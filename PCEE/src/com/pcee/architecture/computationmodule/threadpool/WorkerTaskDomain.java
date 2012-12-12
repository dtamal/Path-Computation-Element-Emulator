/**
 *  This file is part of Path Computation Element Emulator (PCEE).
 *
 *  PCEE is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  PCEE is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with PCEE.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.pcee.architecture.computationmodule.threadpool;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import com.graph.elements.edge.EdgeElement;
import com.graph.elements.edge.params.EdgeParams;
import com.graph.elements.edge.params.impl.ParentVirtualLinkEdgeParams;
import com.graph.elements.vertex.VertexElement;
import com.graph.graphcontroller.Gcontroller;
import com.graph.path.PathElement;
import com.graph.path.algorithms.constraints.Constraint;
import com.graph.path.algorithms.constraints.impl.SimplePathComputationConstraint;
import com.pcee.architecture.ModuleEnum;
import com.pcee.architecture.ModuleManagement;
import com.pcee.architecture.computationmodule.ComputationModuleDomainImpl;
import com.pcee.architecture.computationmodule.ted.TopologyInformationDomain;
import com.pcee.logger.Logger;
import com.pcee.protocol.message.PCEPMessage;
import com.pcee.protocol.message.PCEPMessageFactory;
import com.pcee.protocol.message.objectframe.PCEPObjectFrameFactory;
import com.pcee.protocol.message.objectframe.impl.PCEPBandwidthObject;
import com.pcee.protocol.message.objectframe.impl.PCEPEndPointsObject;
import com.pcee.protocol.message.objectframe.impl.PCEPExplicitRouteObject;
import com.pcee.protocol.message.objectframe.impl.PCEPGenericExplicitRouteObjectImpl;
import com.pcee.protocol.message.objectframe.impl.PCEPNoPathObject;
import com.pcee.protocol.message.objectframe.impl.PCEPRequestParametersObject;
import com.pcee.protocol.message.objectframe.impl.erosubobjects.EROSubobjects;
import com.pcee.protocol.message.objectframe.impl.erosubobjects.PCEPAddress;
import com.pcee.protocol.request.PCEPRequestFrame;
import com.pcee.protocol.request.PCEPRequestFrameFactory;
import com.pcee.protocol.response.PCEPResponseFrame;
import com.pcee.protocol.response.PCEPResponseFrameFactory;

/**
 * Runnable class used by the thread pool to process path computation requests
 * 
 * @author Mohit Chamania
 * @author Marek Drogon
 */
public class WorkerTaskDomain extends WorkerTask {
	// Request to be processed
	private Request request;
	// Graph used for computation of the request
	private Gcontroller graph;
	// Module management object to send the response to the session layer
	private ModuleManagement lm;

	/** Default Constructor */
	public WorkerTaskDomain(ModuleManagement layerManagement, Request request, Gcontroller graph) {
		lm = layerManagement;
		this.request = request;
		this.graph = graph;
	}


	public void processSingleDomainRequest() {
		// Initialize response object and set parameters
		// Compute path
		PathElement element = request.getAlgo().computePath(graph, request.getConstrains());
		if (element != null) {
			localLogger("Computed path is " + element.getVertexSequence());
			Iterator<EdgeElement> iter = element.getTraversedEdges().iterator();
			while(iter.hasNext()){
				EdgeElement temp = iter.next();
				localLogger (temp.getEdgeID() + "\t" + temp.getSourceVertex().getVertexID() + "\t" + temp.getDestinationVertex().getVertexID());
			}
			Iterator<VertexElement> iter1 = element.getTraversedVertices().iterator();
			while(iter1.hasNext()){
				localLogger(iter1.next().getVertexID());
			}

			// return response
			String requestID = request.getRequestID();
			ArrayList<PCEPAddress> vertexList = getTraversedVertexes(element.getTraversedVertices(), graph);

			ArrayList<EROSubobjects> newVertexList = new ArrayList<EROSubobjects>();

			for (int i = 0; i < vertexList.size(); i++) {
				newVertexList.add(vertexList.get(i));
			}

			PCEPRequestParametersObject RP = PCEPObjectFrameFactory.generatePCEPRequestParametersObject("1", "0", "0", "0", "0", "1", requestID);
			PCEPExplicitRouteObject ERO = PCEPObjectFrameFactory.generatePCEPExplicitRouteObject("1", "0", newVertexList);

			PCEPResponseFrame responseFrame = PCEPResponseFrameFactory.generatePathComputationResponseFrame(RP);
			responseFrame.insertExplicitRouteObject(ERO);

			if (request.getBandwidth()>0) {
				PCEPBandwidthObject bw = PCEPObjectFrameFactory.generatePCEPBandwidthObject("1", "0", (float) element.getPathParams().getAvailableCapacity());
				responseFrame.insertBandwidthObject(bw);
			}
			
			PCEPMessage message = PCEPMessageFactory.generateMessage(responseFrame);
			message.setAddress(request.getAddress());

			// Send response message from the computation layer to the session layer
			lm.getComputationModule().sendMessage(message, ModuleEnum.SESSION_MODULE);
		}
		else {
			localDebugger("Error in Computing Path from " + request.getConstrains().getSource().getVertexID() + " to " + request.getConstrains().getDestination().getVertexID());
			returnNoPathMessage();

		}

	}


	/**
	 * Function to process the path computation request 
	 */
	public void processRequest() {
		if (request instanceof SingleDomainRequest) {
			processSingleDomainRequest();
		} else {
			processMultiDomainRequest();
		}
	}

	/** Function to process multi-domain request
	 * 
	 */
	private void processMultiDomainRequest() {
		if (graph.vertexExists(request.getSourceRouterIP())) {
			processMultiDomainSourceRequest();
		} else {
			processMultiDomainDestinationRequest();
		}


	}


	private void processMultiDomainDestinationRequest() {
		//For destination request, compute paths to the destination (not a border node) from all border nodes in the domain 
		Set<String> bnIDs = TopologyInformationDomain.getInstance().getBorderNodes();
		if (bnIDs.contains(request.getDestRouterIP())){
			localLogger("Path to be computed to border node, not to be included in request");
		} else {
			//Start computation of paths from border nodes to the destination
			Iterator<String> iter = bnIDs.iterator();
			ArrayList<PCEPExplicitRouteObject> eroList = new ArrayList<PCEPExplicitRouteObject>();
			ArrayList<PCEPBandwidthObject> bwList = new ArrayList<PCEPBandwidthObject>();
			while(iter.hasNext()){
				String sourceID = iter.next();
				Constraint constr;
				if (request.getBandwidth()>0){
					constr = new SimplePathComputationConstraint(graph.getVertex(sourceID), graph.getVertex(request.getDestRouterIP()), request.getBandwidth());
				} else {
					constr = new SimplePathComputationConstraint(graph.getVertex(sourceID), graph.getVertex(request.getDestRouterIP()));
				}


				PathElement element = request.getAlgo().computePath(graph, constr);
				if (element !=null) {
					localLogger("Computed path is " + element.getVertexSequence());
					// return response
					ArrayList<PCEPAddress> vertexList = getTraversedVertexes(element.getTraversedVertices(), graph);
					ArrayList<EROSubobjects> newVertexList = new ArrayList<EROSubobjects>();
					for (int i = 0; i < vertexList.size(); i++) {
						newVertexList.add(vertexList.get(i));
					}
					//Add Ero to the ERO List
					PCEPExplicitRouteObject ERO = PCEPObjectFrameFactory.generatePCEPExplicitRouteObject("1", "0", newVertexList);
					eroList.add(ERO);
					
					//Add bandwidth to the bwList if the request contains the bandwidth object
					if (request.getBandwidth() > 0) {
						PCEPBandwidthObject bw = PCEPObjectFrameFactory.generatePCEPBandwidthObject("1", "0", (float) element.getPathParams().getAvailableCapacity());
						bwList.add(bw);
					}

				}
			}

			//Generate response frame
			if (eroList.size()>0) {
				//atleast one path was computed
				PCEPRequestParametersObject RP = PCEPObjectFrameFactory.generatePCEPRequestParametersObject("1", "0", "0", "0", "0", "1", request.getRequestID());

				PCEPResponseFrame responseFrame = PCEPResponseFrameFactory.generatePathComputationResponseFrame(RP);

				Iterator<PCEPExplicitRouteObject> eroIter = eroList.iterator();
				while(eroIter.hasNext()) 
					responseFrame.insertExplicitRouteObject(eroIter.next());

				//Add bandwidth to the response message if the request contains the bandwidth object
				if (request.getBandwidth() > 0) {
					Iterator<PCEPBandwidthObject> bwIter = bwList.iterator();
					while(bwIter.hasNext()) 
						responseFrame.insertBandwidthObject(bwIter.next());
				}
				PCEPMessage message = PCEPMessageFactory.generateMessage(responseFrame);
				message.setAddress(request.getAddress());

				// Send response message from the computation layer to the session layer
				lm.getComputationModule().sendMessage(message, ModuleEnum.SESSION_MODULE);


			} else {
				//no path found send no path object to parent PCE
				returnNoPathMessage();
			}
		}
	}


	private void processMultiDomainSourceRequest() {
		// Generate request to be sent to the sourcePCE and wait for response
		ComputationModuleDomainImpl cmp = (ComputationModuleDomainImpl)lm.getComputationModule();

		lm.getSessionModule().registerConnection(cmp.getParentPCEAddress(), false, true, true);



		LinkedBlockingQueue<PCEPMessage> inQueue = new LinkedBlockingQueue<PCEPMessage>();

		String requestID = request.getRequestID();

		//Check if request can be sent to remote peer with the current request ID or if another request should be made
		if (cmp.isValidRequestToRemotePeer(cmp.getParentPCEAddress(), requestID) == false) {
			//Need to generate a new request ID for parent PCE
			do {
				Random generator = new Random();
				String temp = Integer.toString(generator.nextInt(3000) + 1);
				if (cmp.isValidRequestToRemotePeer(cmp.getParentPCEAddress(), temp)) {
					requestID = temp;
					break;
				}
			} while(true);
		}
		//Register request with the computation module
		cmp.registerRequestToRemotePeer(cmp.getParentPCEAddress(), requestID, inQueue);


		// Send information to the parent PCE
		String endPointsPFlag ="1";
		String endPointsIFlag = "0";

		PCEPAddress sourceAddress = new PCEPAddress(request.getSourceRouterIP(), false);
		PCEPAddress destinationAddress = new PCEPAddress(request.getDestRouterIP(), false);

		PCEPRequestParametersObject RP = PCEPObjectFrameFactory.generatePCEPRequestParametersObject("1", "0", "0", "0", "0", "1", requestID);
		PCEPEndPointsObject endPoints = PCEPObjectFrameFactory.generatePCEPEndPointsObject(endPointsPFlag, endPointsIFlag, sourceAddress, destinationAddress);




		PCEPRequestFrame requestMessage = PCEPRequestFrameFactory.generatePathComputationRequestFrame(RP, endPoints);

		//If bandwidth is non zero include bandwidth object
		if (request.getBandwidth()>0) {
			PCEPBandwidthObject bw = PCEPObjectFrameFactory.generatePCEPBandwidthObject("1", "0", (float)request.getBandwidth());
			requestMessage.insertBandwidthObject(bw);
		}

		PCEPMessage message = PCEPMessageFactory.generateMessage(requestMessage);

		// Address destAddress = new Address(serverAddressTextField.getText());
		PCEPAddress remotePeerAddress = new PCEPAddress(cmp.getParentPCEAddress().getIPv4Address(false), cmp.getParentPCEAddress().getPort());
		message.setAddress(remotePeerAddress);

		lm.getComputationModule().sendMessage(message, ModuleEnum.SESSION_MODULE);

		localLogger("Request Sent to Parent PCE");

		//Wait for response from remote peer (parent PCE)
		try {
			PCEPMessage in = inQueue.take();
			//Response received from Server
			PCEPResponseFrame responseFrame = PCEPResponseFrameFactory.getPathComputationResponseFrame(in);

			//Check if a single path exists
			if (responseFrame.containsNoPathObject()) {
				//No path received from Parent PCE return No Path to Domain PCE
				localLogger("Received No path From Parent PCE");				
				returnNoPathMessage();
			} else {
				//if source is one of the border nodes, use the EROs sent by the parent PCE and send it back to the client
				if (TopologyInformationDomain.getInstance().getBorderNodes().contains(request.getSourceRouterIP())) {
					//Source Node is actually a Border Node so the Parent PCE computed a path to the source directly
					RP = PCEPObjectFrameFactory.generatePCEPRequestParametersObject("1", "0", "0", "0", "0", "1", request.getRequestID());
					PCEPResponseFrame respFrame = PCEPResponseFrameFactory.generatePathComputationResponseFrame(RP);
					respFrame.insertExplicitRouteObjectList(responseFrame.extractExplicitRouteObjectList());

					//Add Bandwidth Information to the response if available in the response from the Parent PCE
					if (responseFrame.containsBandwidthObjectList()) {
						respFrame.insertBandwidthObjectList(responseFrame.extractBandwidthObjectList());
					}
					PCEPMessage mesg = PCEPMessageFactory.generateMessage(respFrame);
					mesg.setAddress(request.getAddress());

					// Send response message from the computation layer to the session layer
					lm.getComputationModule().sendMessage(mesg, ModuleEnum.SESSION_MODULE);
				}
				else {
					//compute paths from the source of the request to the source of all EROs, append EROs to make destintaion 
					
					localLogger("response Received from Parent PCE computing path in local PCE");
					Iterator <PCEPExplicitRouteObject> iter = responseFrame.extractExplicitRouteObjectList().iterator();
					LinkedList<PCEPBandwidthObject> bwList = null;
					if (responseFrame.containsBandwidthObjectList())
						bwList = responseFrame.extractBandwidthObjectList();
					
					//Create a copy of the domain graph and add the edges from the destination the border nodes based on the computed graph
					Gcontroller tempGraph = graph.createCopy();
					tempGraph.addVertex(new VertexElement (request.getDestRouterIP(), tempGraph, 0,0));
					
					int count =0;
					while(iter.hasNext()) {
						localLogger("Inserting Edges in local topology");
						double bw =0;
						if (bwList!=null)
							bw = bwList.get(count).getBandwidthFloatValue();
						PCEPGenericExplicitRouteObjectImpl ero= (PCEPGenericExplicitRouteObjectImpl)iter.next();
						String sourceID = ((PCEPAddress)ero.getTraversedVertexList().get(0)).getIPv4Address(false);
						String destID = ((PCEPAddress)ero.getTraversedVertexList().get(ero.getTraversedVertexList().size()-1)).getIPv4Address(false);
						EdgeElement edge = new EdgeElement(sourceID+"-" + destID, tempGraph.getVertex(sourceID), tempGraph.getVertex(destID), tempGraph);
						ArrayList<String> vertexSequence = new ArrayList<String>();
						for (int i=0; i< ero.getTraversedVertexList().size();i++)
							vertexSequence.add(((PCEPAddress)ero.getTraversedVertexList().get(i)).getIPv4Address(false));
						EdgeParams params = new ParentVirtualLinkEdgeParams(edge, 1, responseFrame.extractExplicitRouteObjectList().size(), bw, vertexSequence);
						edge.setEdgeParams(params);
						tempGraph.addEdge(edge);
						count++;
					}

					
					Constraint constr;
					if (request.getBandwidth()>0){
						constr = new SimplePathComputationConstraint(tempGraph.getVertex(request.getSourceRouterIP()), tempGraph.getVertex(request.getDestRouterIP()), request.getBandwidth());
					} else {
						constr = new SimplePathComputationConstraint(tempGraph.getVertex(request.getSourceRouterIP()), tempGraph.getVertex(request.getDestRouterIP()));
					}
					PathElement element = request.getAlgo().computePath(tempGraph, constr);
					if (element !=null) {
						localLogger("Computed path is " + element.getVertexSequence());
						// return response
						ArrayList<PCEPAddress> vertexList = getTraversedVertexes(element.getTraversedVertices(), tempGraph);
						ArrayList<EROSubobjects> newVertexList = new ArrayList<EROSubobjects>();

						//Add Computed path to the NewVertexList
						for (int i = 0; i < vertexList.size(); i++) {
							newVertexList.add(vertexList.get(i));
						}

						PCEPExplicitRouteObject ERO = PCEPObjectFrameFactory.generatePCEPExplicitRouteObject("1", "0", newVertexList);
						//atleast one path was computed
						RP = PCEPObjectFrameFactory.generatePCEPRequestParametersObject("1", "0", "0", "0", "0", "1", request.getRequestID());

						PCEPResponseFrame respFrame = PCEPResponseFrameFactory.generatePathComputationResponseFrame(RP);

						respFrame.insertExplicitRouteObject(ERO);

						if (request.getBandwidth()>0) {
							PCEPBandwidthObject bw = PCEPObjectFrameFactory.generatePCEPBandwidthObject("1", "0", (float) element.getPathParams().getAvailableCapacity());
							respFrame.insertBandwidthObject(bw);
						}
						
						PCEPMessage mesg = PCEPMessageFactory.generateMessage(respFrame);
						mesg.setAddress(request.getAddress());

						// Send response message from the computation layer to the session layer
						lm.getComputationModule().sendMessage(mesg, ModuleEnum.SESSION_MODULE);


					} else {
						//No path Found in the source domain return no path Object
						returnNoPathMessage();
					}
				}
			}
		} catch (InterruptedException e) {
			localLogger("Domain WorkerTask while waiting for response from Parent PCE : " + e.getMessage());
		}
	}


	/** Function to update the graph instance used for computation */
	public void updateGraph(Gcontroller newGraph) {
		this.graph = newGraph;
	}

	/** Function to implement the path computation operations */
	public void run() {
		localDebugger("Start processing of Request for path from " + request.getSourceRouterIP() + " to " + request.getDestRouterIP());
		localLogger("Processing of Request: " + request.getRequestID());
		processRequest();
	}


	/**
	 * Function to get the list of traversed vertices from the response object. Used to create ERO
	 * 
	 * @param resp
	 * @return
	 */
	private ArrayList<PCEPAddress> getTraversedVertexes(ArrayList<VertexElement> vertexArrayList, Gcontroller graph) {

		ArrayList<PCEPAddress> traversedVertexesList = new ArrayList<PCEPAddress>();
		
		//Add source address to the traversedVertexesList
		traversedVertexesList.add(new PCEPAddress(vertexArrayList.get(0).getVertexID(), false));
		
		
		for (int i = 0; i < vertexArrayList.size()-1; i++) {
			//find the edge in the graph and extract the vertexSequence
			String sourceID = vertexArrayList.get(i).getVertexID();
			String destID = vertexArrayList.get(i+1).getVertexID();
			EdgeElement edge = graph.getConnectingEdge(sourceID, destID);
			ArrayList<String> vertexIDs = edge.getEdgeParams().getVertexSequence(sourceID, destID);
			for (int j=1;j<vertexIDs.size();j++) {
				traversedVertexesList.add(new PCEPAddress(vertexIDs.get(j), false));
			}
		}

		return traversedVertexesList;
	}

	/**
	 * Function to log events inside the WorkerTask
	 * 
	 * @param event
	 */
	private void localLogger(String event) {
		Logger.logSystemEvents("[WorkerTask]     " + event);
	}

	/**
	 * Function to log debugging information inside the worker task
	 * 
	 * @param event
	 */
	private void localDebugger(String event) {
		Logger.debugger("[WorkerTask]     " + event);
	}


	/**Function to return the no Path message to the Client*/
	private void returnNoPathMessage() {
		//Generate a No path object
		PCEPRequestParametersObject RP = PCEPObjectFrameFactory.generatePCEPRequestParametersObject("1", "0", "0", "0", "0", "1", request.getRequestID());
		PCEPNoPathObject noPath = PCEPObjectFrameFactory.generatePCEPNoPathObject("1", "0", 1, "o");
		PCEPResponseFrame responseFrame = PCEPResponseFrameFactory.generatePathComputationResponseFrame(RP);
		responseFrame.insertNoPathObject(noPath);
		PCEPMessage mesg = PCEPMessageFactory.generateMessage(responseFrame);
		mesg.setAddress(request.getAddress());
		lm.getComputationModule().sendMessage(mesg, ModuleEnum.SESSION_MODULE);
	}

}

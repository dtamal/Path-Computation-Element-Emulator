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
import com.pcee.architecture.computationmodule.ComputationModule;

import com.pcee.architecture.computationmodule.ted.TopologyInformationParent;
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
public class WorkerTaskParent extends WorkerTask {
	// Request to be processed
	private Request request;
	// Graph used for computation of the request
	private Gcontroller graph;
	// Module management object to send the response to the session layer
	private ModuleManagement lm;

	/** Default Constructor */
	public WorkerTaskParent(ModuleManagement layerManagement, Request request, Gcontroller graph) {
		lm = layerManagement;
		this.request = request;
		this.graph = graph;
	}



	/**
	 * Function to process the path computation request 
	 */
	public void processRequest() {
		//Check if the destimation is a border node (i.e. is on the virtual graph)
		if (graph.vertexExists(request.getDestRouterIP())) {
			localLogger("Destination exists in the Parent Virtual Graph");
			//Destination exists in the virtual graph
			if (graph.vertexExists(request.getSourceRouterIP())) {
				//Source also exists on the virtual graph
				//Compute single end-to-end path and send it to the source domain
				Constraint constr;
				if (request.getBandwidth()>0){
					constr = new SimplePathComputationConstraint(graph.getVertex(request.getSourceRouterIP()), graph.getVertex(request.getDestRouterIP()), request.getBandwidth());
				} else {
					constr = new SimplePathComputationConstraint(graph.getVertex(request.getSourceRouterIP()), graph.getVertex(request.getDestRouterIP()));
				}

				PathElement element = request.getAlgo().computePath(graph, constr);

				//Initialize Response Message
				PCEPRequestParametersObject RP = PCEPObjectFrameFactory.generatePCEPRequestParametersObject("1", "0", "0", "0", "0", "1", request.getRequestID());
				PCEPResponseFrame responseFrame = PCEPResponseFrameFactory.generatePathComputationResponseFrame(RP);
				if (element !=null) {
					//Path Found 
					localLogger("Computed path is " + element.getVertexSequence());
					// GENERATE ERO

					//TODO FIX THIS
					ArrayList<PCEPAddress> vertexList = getTraversedVertexes(element.getTraversedVertices(), graph);
					ArrayList<EROSubobjects> newVertexList = new ArrayList<EROSubobjects>();
					for (int i = 0; i < vertexList.size(); i++) {
						newVertexList.add(vertexList.get(i));
					}


					//Insert ERO into Response
					PCEPExplicitRouteObject ERO = PCEPObjectFrameFactory.generatePCEPExplicitRouteObject("1", "0", newVertexList);
					responseFrame.insertExplicitRouteObject(ERO);



					//Insert bandwidth Object if found in request
					if (request.getBandwidth() > 0) {
						PCEPBandwidthObject bw = PCEPObjectFrameFactory.generatePCEPBandwidthObject("1", "0", (float) element.getPathParams().getAvailableCapacity());
						//						bwList.add(bw);
						responseFrame.insertBandwidthObject(bw);
					}

					PCEPMessage message = PCEPMessageFactory.generateMessage(responseFrame);
					message.setAddress(request.getAddress());

					// Send response message from the computation layer to the session layer
					lm.getComputationModule().sendMessage(message, ModuleEnum.SESSION_MODULE);
				}	else {
					//Return a no path object
					returnNoPathMessage();
				}

			} else {
				//Source does not exist in the virtual Topology 
				//Get the border nodes for the source domain

				String domainID = TopologyInformationParent.getInstance().getDomainID(request.getSourceRouterIP());

				if (domainID==null) {
					localLogger("Domain Mapping not found for source node : " + request.getSourceRouterIP());
					returnNoPathMessage();
				} else {
					//Get the set of border nodes associated with the domain
					Set<String> bnIDs = TopologyInformationParent.getInstance().getBorderNodes(domainID);
					if (bnIDs==null) {
						localLogger("Border nodes not found for domain : " + domainID);
						returnNoPathMessage();
					} else {
						Iterator<String> iter = bnIDs.iterator();
						ArrayList<PCEPBandwidthObject> bwList = new ArrayList<PCEPBandwidthObject>();
						ArrayList<PCEPExplicitRouteObject> eroList = new ArrayList<PCEPExplicitRouteObject>();
						while(iter.hasNext()) {
							//Compute a path to this destination 
							String sourceID = iter.next();

							if (graph.vertexExists(sourceID)) {
								//compute a path from the destination to the Border node in the source domain
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
									PCEPExplicitRouteObject ERO = PCEPObjectFrameFactory.generatePCEPExplicitRouteObject("1", "0", newVertexList);
									eroList.add(ERO);

									if (request.getBandwidth() > 0) {
										PCEPBandwidthObject bw = PCEPObjectFrameFactory.generatePCEPBandwidthObject("1", "0", (float) element.getPathParams().getAvailableCapacity());
										bwList.add(bw);
									}

								}

							} else {
								localLogger("Border node " + sourceID + " not found in the topology of the parent");
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
							
							//Insert Bandwidth in response if found in request
							if (request.getBandwidth()>0) {
								Iterator<PCEPBandwidthObject> bwIter = bwList.iterator();
								while(bwIter.hasNext()) {
									responseFrame.insertBandwidthObject(bwIter.next());
								}
							}
							PCEPMessage message = PCEPMessageFactory.generateMessage(responseFrame);
							message.setAddress(request.getAddress());
							// Send response message from the computation layer to the session layer
							lm.getComputationModule().sendMessage(message, ModuleEnum.SESSION_MODULE);
						} else {
							returnNoPathMessage();
						}
					}
				}

			}


		} else {
			localLogger ("Destination Address not in Parent Virtual Topology ");

			//get the domain ID of the domain in which the destination IP exists
			String domainID = TopologyInformationParent.getInstance().getDomainID(request.getDestRouterIP());
			if (domainID==null) {
				//Invalid destination 
				localLogger("Domain ID for remote destination not found");
				returnNoPathMessage();
			} else {
				//Get the PCEP address for the destination Domain 
				PCEPAddress remoteDomainAddress = TopologyInformationParent.getInstance().getDomainPCEAddress(domainID);
				if (remoteDomainAddress==null) {
					localLogger("PCE address for remote domain not found");
					returnNoPathMessage();					
				} else {
					//Make a connection to the remote domaini 
					localLogger ("Connecting to PCE in domain " + domainID);

					lm.getSessionModule().registerConnection(remoteDomainAddress, false, true, true);

					LinkedBlockingQueue<PCEPMessage> inQueue = new LinkedBlockingQueue<PCEPMessage>();

					String requestID = request.getRequestID();

					ComputationModule cmp = lm.getComputationModule();
					//Check if request can be sent to remote peer with the current request ID or if another request should be made
					if (cmp.isValidRequestToRemotePeer(remoteDomainAddress, requestID) == false) {
						//Need to generate a new request ID for parent PCE
						do {
							Random generator = new Random();
							String temp = Integer.toString(generator.nextInt(3000) + 1);
							if (cmp.isValidRequestToRemotePeer(remoteDomainAddress, temp)) {
								requestID = temp;
								break;
							}
						} while(true);
					}
					//Register request with the computation module
					cmp.registerRequestToRemotePeer(remoteDomainAddress, requestID, inQueue);


					// Send information to the destination PCE
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

					message.setAddress(remoteDomainAddress);

					lm.getComputationModule().sendMessage(message, ModuleEnum.SESSION_MODULE);


					//Message sent to the client domain waiting for response

					//Wait for response from remote peer (parent PCE)
					try {
						PCEPMessage in = inQueue.take();
						//Generate new Graph
						//Response received from Server
						PCEPResponseFrame responseFrame = PCEPResponseFrameFactory.getPathComputationResponseFrame(in);

						//Check if a single path exists
						if (responseFrame.containsNoPathObject()) {

							localLogger("Response from Domain PCE in the destination domain contains a no Path Object");
							//No path received from Parent PCE return No Path to Domain PCE
							returnNoPathMessage();
						} else {
							Gcontroller graph = TopologyInformationParent.getInstance().getGraph().createCopy();
							graph.addVertex(new VertexElement(request.getDestRouterIP(), graph, 0,0));

							LinkedList<PCEPBandwidthObject> bwList = null;
							if (responseFrame.containsBandwidthObjectList()) {
								bwList = responseFrame.extractBandwidthObjectList();
							}
							int count =0;
							Iterator <PCEPExplicitRouteObject> iter = responseFrame.extractExplicitRouteObjectList().iterator();
							while(iter.hasNext()) {
								double bw =0;
								if (bwList!=null)
									bw = bwList.get(count).getBandwidthFloatValue();
								PCEPGenericExplicitRouteObjectImpl ero= (PCEPGenericExplicitRouteObjectImpl)iter.next();
								String sourceID = ((PCEPAddress)ero.getTraversedVertexList().get(0)).getIPv4Address(false);
								String destID = ((PCEPAddress)ero.getTraversedVertexList().get(ero.getTraversedVertexList().size()-1)).getIPv4Address(false);
								EdgeElement edge = new EdgeElement(sourceID+"-" + destID, graph.getVertex(sourceID), graph.getVertex(destID), graph);
								ArrayList<String> vertexSequence = new ArrayList<String>();
								for (int i=0; i< ero.getTraversedVertexList().size();i++)
									vertexSequence.add(((PCEPAddress)ero.getTraversedVertexList().get(i)).getIPv4Address(false));
								EdgeParams params = new ParentVirtualLinkEdgeParams(edge, 1, responseFrame.extractExplicitRouteObjectList().size(), bw, vertexSequence);
								edge.setEdgeParams(params);
								graph.addEdge(edge);
								count++;
							}

							//The new Graph has been populated with links to the destination based on the incoming EROs

							//Add code for path computation Here 

							if (graph.vertexExists(request.getSourceRouterIP())) {
								//compute a path from the destination to the Border node in the source domain
								Constraint constr;
								if (request.getBandwidth()>0){
									constr = new SimplePathComputationConstraint(graph.getVertex(request.getSourceRouterIP()), graph.getVertex(request.getDestRouterIP()), request.getBandwidth());
								} else {
									constr = new SimplePathComputationConstraint(graph.getVertex(request.getSourceRouterIP()), graph.getVertex(request.getDestRouterIP()));
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
									PCEPExplicitRouteObject ERO = PCEPObjectFrameFactory.generatePCEPExplicitRouteObject("1", "0", newVertexList);

									PCEPRequestParametersObject rp = PCEPObjectFrameFactory.generatePCEPRequestParametersObject("1", "0", "0", "0", "0", "1", request.getRequestID());
									PCEPResponseFrame respFrame = PCEPResponseFrameFactory.generatePathComputationResponseFrame(rp);


									respFrame.insertExplicitRouteObject(ERO);

									if (request.getBandwidth() > 0) {
										PCEPBandwidthObject bw = PCEPObjectFrameFactory.generatePCEPBandwidthObject("1", "0", (float) element.getPathParams().getAvailableCapacity());
										//										bwList.add(bw);
										respFrame.insertBandwidthObject(bw);
									}
									PCEPMessage outMessage = PCEPMessageFactory.generateMessage(respFrame);
									outMessage.setAddress(request.getAddress());

									// Send response message from the computation layer to the session layer
									lm.getComputationModule().sendMessage(outMessage, ModuleEnum.SESSION_MODULE);
								} else {
									//Path not found 
									returnNoPathMessage();
								}


							} else {
								//Path should be computed from the destination router ID to all the border nodes in the source Domain
								domainID = TopologyInformationParent.getInstance().getDomainID(request.getSourceRouterIP());

								if (domainID==null) {
									localLogger("Domain Mapping not found for source node : " + request.getSourceRouterIP());
									returnNoPathMessage();
								} else {
									//Get the set of border nodes associated with the domain
									Set<String> bnIDs = TopologyInformationParent.getInstance().getBorderNodes(domainID);
									if (bnIDs==null) {
										localLogger("Border nodes not found for domain : " + domainID);
										returnNoPathMessage();
									} else {
										Iterator<String> iter1 = bnIDs.iterator();

										ArrayList<PCEPExplicitRouteObject> eroList = new ArrayList<PCEPExplicitRouteObject>();
										bwList = new LinkedList<PCEPBandwidthObject> ();
										while(iter1.hasNext()) {
											//Compute a path to this destination 
											String sourceID = iter1.next();

											if (graph.vertexExists(sourceID)) {
												//compute a path from the destination to the Border node in the source domain
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
													PCEPExplicitRouteObject ERO = PCEPObjectFrameFactory.generatePCEPExplicitRouteObject("1", "0", newVertexList);
													eroList.add(ERO);
													if (request.getBandwidth() > 0) {
														PCEPBandwidthObject bw = PCEPObjectFrameFactory.generatePCEPBandwidthObject("1", "0", (float) element.getPathParams().getAvailableCapacity());
														bwList.add(bw);
													}
												}

											} else {
												localLogger("Border node " + sourceID + " not found in the topology of the parent");
											}

										}
										//Generate response frame
										if (eroList.size()>0) {
											//atleast one path was computed
											PCEPRequestParametersObject rp = PCEPObjectFrameFactory.generatePCEPRequestParametersObject("1", "0", "0", "0", "0", "1", request.getRequestID());
											PCEPResponseFrame respFrame = PCEPResponseFrameFactory.generatePathComputationResponseFrame(rp);
											Iterator<PCEPExplicitRouteObject> eroIter = eroList.iterator();
											while(eroIter.hasNext()) 
												respFrame.insertExplicitRouteObject(eroIter.next());

											//Insert Bandwidth of computed Paths
											if (request.getBandwidth() > 0) {
												Iterator<PCEPBandwidthObject> bwIter = bwList.iterator();
												while(bwIter.hasNext()) 
													respFrame.insertBandwidthObject(bwIter.next());
											}

											PCEPMessage outMessage = PCEPMessageFactory.generateMessage(respFrame);
											outMessage.setAddress(request.getAddress());
											// Send response message from the computation layer to the session layer
											lm.getComputationModule().sendMessage(outMessage, ModuleEnum.SESSION_MODULE);
										} else {
											returnNoPathMessage();
										}
									}
								}

							}
						}
					} catch (InterruptedException e) {
						localLogger("Interrupted when waiting for message from remote domain : " + e.getMessage());
					}


				}


			}

		}
	}


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
		Logger.logSystemEvents("[WorkerTaskParent]     " + event);
	}

	/**
	 * Function to log debugging information inside the worker task
	 * 
	 * @param event
	 */
	private void localDebugger(String event) {
		Logger.debugger("[WorkerTaskParent]     " + event);
	}

}

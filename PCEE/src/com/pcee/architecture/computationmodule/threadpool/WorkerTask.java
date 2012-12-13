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

import com.graph.elements.edge.EdgeElement;
import com.graph.elements.vertex.VertexElement;
import com.graph.graphcontroller.Gcontroller;
import com.graph.path.PathElement;
import com.graph.path.algorithms.PathComputationAlgorithm;
import com.graph.path.algorithms.constraints.Constraint;
import com.graph.path.algorithms.constraints.impl.SimplePathComputationConstraint;
import com.graph.path.algorithms.impl.MaxBandwidthShortestPathComputationAlgorithm;
import com.graph.path.algorithms.impl.SimplePathComputationAlgorithm;
import com.pcee.architecture.ModuleEnum;
import com.pcee.architecture.ModuleManagement;
import com.pcee.logger.Logger;
import com.pcee.protocol.message.PCEPMessage;
import com.pcee.protocol.message.PCEPMessageFactory;
import com.pcee.protocol.message.objectframe.PCEPObjectFrameFactory;
import com.pcee.protocol.message.objectframe.impl.PCEPBandwidthObject;
import com.pcee.protocol.message.objectframe.impl.PCEPExplicitRouteObject;
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
public class WorkerTask implements Runnable {
	// Request to be processed
	private PCEPMessage request;
	// Graph used for computation of the request
	private Gcontroller graph;
	// Module management object to send the response to the session layer
	private ModuleManagement lm;

	/** Default Constructor */
	public WorkerTask(ModuleManagement layerManagement, PCEPMessage request, Gcontroller graph) {
		lm = layerManagement;
		this.request = request;
		this.graph = graph;
	}

	/** Function to update the graph instance used for computation */
	public void updateGraph(Gcontroller newGraph) {
		this.graph = newGraph;
	}

	/** Function to implement the path computation operations */
	public void run() {
		PCEPRequestFrame requestFrame = PCEPRequestFrameFactory.getPathComputationRequestFrame(request);
		localLogger("Starting Processing of Request: " + requestFrame.getRequestID());
		processSingleDomainRequest(requestFrame);
		localLogger("Completed Processing of Request: " + requestFrame.getRequestID());		
	}


	private void processSingleDomainRequest(PCEPRequestFrame requestFrame) {
		//Check if source and destination domain are available in the graph, if not send a no path object 
		String sourceID = requestFrame.getSourceAddress().getIPv4Address(false).trim();
		String destID = requestFrame.getDestinationAddress().getIPv4Address(false).trim();
		if (graph.vertexExists(sourceID) && graph.vertexExists(destID)) {
			//begin path computation
			//Check if bandwidth objecy exists in the request frame
			Constraint constr = null;
			PathComputationAlgorithm algo = null;
			if (requestFrame.containsBandwidthObject()) {
				localLogger("Request Contains bandwidth Object");
				constr = new SimplePathComputationConstraint (graph.getVertex(sourceID), graph.getVertex(destID), requestFrame.extractBandwidthObject().getBandwidthFloatValue());
				algo = new MaxBandwidthShortestPathComputationAlgorithm();
			} else {
				constr = new SimplePathComputationConstraint (graph.getVertex(sourceID), graph.getVertex(destID));
				algo = new SimplePathComputationAlgorithm();
			}
			//Start Path Computation
			PathElement element = algo.computePath(graph, constr);
			if (element !=null) {
				localLogger("Computed path is " + element.getVertexSequence());
				// return response
				ArrayList<EROSubobjects> vertexList = getTraversedVertexes(element.getTraversedVertices());

				//Generate ERO Object
				PCEPExplicitRouteObject ERO = PCEPObjectFrameFactory.generatePCEPExplicitRouteObject("1", "0", vertexList);
				//atleast one path was computed
				PCEPRequestParametersObject RP = PCEPObjectFrameFactory.generatePCEPRequestParametersObject("1", "0", "0", "0", "0", "1", Integer.toString(requestFrame.getRequestID()));

				PCEPResponseFrame respFrame = PCEPResponseFrameFactory.generatePathComputationResponseFrame(RP);

				respFrame.insertExplicitRouteObject(ERO);

				if (requestFrame.containsBandwidthObject()) {
					PCEPBandwidthObject bw = PCEPObjectFrameFactory.generatePCEPBandwidthObject("1", "0", (float) element.getPathParams().getAvailableCapacity());
					respFrame.insertBandwidthObject(bw);
				}

				PCEPMessage mesg = PCEPMessageFactory.generateMessage(respFrame);
				mesg.setAddress(request.getAddress());

				localLogger("Path found in the domain. Sending back to client");
				// Send response message from the computation layer to the session layer
				lm.getComputationModule().sendMessage(mesg, ModuleEnum.SESSION_MODULE);


			} else {
				//No path Found in the source domain return no path Object
				returnNoPathMessage(requestFrame.getRequestID());
			}

		} else {
			//Source and/or destination not present in the PCE
			if (graph.vertexExists(sourceID))
				localLogger("Destination IP address " + destID + " not in the topology. Returning a no path object");
			else if (graph.vertexExists(destID)) 
				localLogger("Source IP address " + sourceID + " not in the topology. Returning a no path object");
			else {
				localLogger("Both source IP address " + sourceID + " and destination IP address " + destID + " not in the topology. Returning a no path object");
			}
			returnNoPathMessage(requestFrame.getRequestID());
		}
	}


	/**Function to return the no Path message to the Client*/
	private void returnNoPathMessage(int requestID) {
		//Generate a No path object
		PCEPRequestParametersObject RP = PCEPObjectFrameFactory.generatePCEPRequestParametersObject("1", "0", "0", "0", "0", "1", Integer.toString(requestID));
		PCEPNoPathObject noPath = PCEPObjectFrameFactory.generatePCEPNoPathObject("1", "0", 1, "0");
		PCEPResponseFrame responseFrame = PCEPResponseFrameFactory.generatePathComputationResponseFrame(RP);
		responseFrame.insertNoPathObject(noPath);
		PCEPMessage mesg = PCEPMessageFactory.generateMessage(responseFrame);
		mesg.setAddress(request.getAddress());
		lm.getComputationModule().sendMessage(mesg, ModuleEnum.SESSION_MODULE);
	}

	/**
	 * Function to get the list of traversed vertices as PCEP addresses from the List of vertices in the graph. Used to create ERO
	 *
	 * @param resp
	 * @return
	 */
	private ArrayList<EROSubobjects> getTraversedVertexes(ArrayList<VertexElement> vertexArrayList) {

		ArrayList<EROSubobjects> traversedVertexesList = new ArrayList<EROSubobjects>();

		for (int i=0;i<vertexArrayList.size();i++) {
			traversedVertexesList.add(new PCEPAddress(vertexArrayList.get(i).getVertexID(), false));			
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

}

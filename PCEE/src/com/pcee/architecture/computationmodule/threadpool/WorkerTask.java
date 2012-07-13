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
import com.pcee.architecture.ModuleEnum;
import com.pcee.architecture.ModuleManagement;
import com.pcee.logger.Logger;
import com.pcee.protocol.message.PCEPMessage;
import com.pcee.protocol.message.PCEPMessageFactory;
import com.pcee.protocol.message.objectframe.PCEPObjectFrameFactory;
import com.pcee.protocol.message.objectframe.impl.PCEPExplicitRouteObject;
import com.pcee.protocol.message.objectframe.impl.PCEPRequestParametersObject;
import com.pcee.protocol.message.objectframe.impl.erosubobjects.EROSubobjects;
import com.pcee.protocol.message.objectframe.impl.erosubobjects.PCEPAddress;
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
	private Request request;
	// Graph used for computation of the request
	private Gcontroller graph;
	// Module management object to send the response to the session layer
	private ModuleManagement lm;

	/** Default Constructor */
	public WorkerTask(ModuleManagement layerManagement, Request request, Gcontroller graph) {
		lm = layerManagement;
		this.request = request;
		this.graph = graph;
	}

	/**
	 * Function to process the path computation request and create a response object from the same
	 * 
	 * @return Response Object
	 */
	public Response processRequest() {
		// Initialize response object and set parameters
		Response response = new Response();
		response.setRequestID(request.getRequestID());
		response.setAddress(request.getAddress());
		// Compute path
		PathElement element = request.getAlgo().computePath(graph, request.getConstrains());
		if (element != null) {
			response.setElement(element);
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

		}
		else
			localDebugger("Error in Computing Path from " + request.getConstrains().getSource().getVertexID() + " to " + request.getConstrains().getDestination().getVertexID());

		// return response
		return response;
	}

	/** Function to update the graph instance used for computation */
	public void updateGraph(Gcontroller newGraph) {
		this.graph = newGraph;
	}

	/** Function to implement the path computation operations */
	public void run() {
		localDebugger("Start processing of Request for path from " + request.getConstrains().getSource().getVertexID() + " to " + request.getConstrains().getDestination().getVertexID());
		localLogger("Processing of Request: " + request.getRequestID());
		Response response = processRequest();
		processResponseMessage(response);
	}

	/** Function to create a PCEPResponse message from the response object */
	private void processResponseMessage(Response resp) {

		String requestID = resp.getRequestID();
		ArrayList<PCEPAddress> vertexList = getTraversedVertexes(resp);

		ArrayList<EROSubobjects> newVertexList = new ArrayList<EROSubobjects>();

		for (int i = 0; i < vertexList.size(); i++) {
			newVertexList.add(vertexList.get(i));
		}

		PCEPRequestParametersObject RP = PCEPObjectFrameFactory.generatePCEPRequestParametersObject("1", "0", "0", "0", "0", "1", requestID);
		PCEPExplicitRouteObject ERO = PCEPObjectFrameFactory.generatePCEPExplicitRouteObject("1", "0", newVertexList);

		PCEPResponseFrame responseFrame = PCEPResponseFrameFactory.generatePathComputationRequestFrame(RP);
		responseFrame.insertExplicitRouteObject(ERO);

		PCEPMessage message = PCEPMessageFactory.generateMessage(responseFrame);
		message.setAddress(resp.getAddress());

		// Send response message from the computation layer to the session layer
		lm.getComputationModule().sendMessage(message, ModuleEnum.SESSION_MODULE);
	}

	/**
	 * Function to get the list of traversed vertices from the response object. Used to create ERO
	 * 
	 * @param resp
	 * @return
	 */
	private ArrayList<PCEPAddress> getTraversedVertexes(Response resp) {

		ArrayList<VertexElement> vertexArrayList = resp.getElement().getTraversedVertices();
		ArrayList<PCEPAddress> traversedVertexesList = new ArrayList<PCEPAddress>();

		for (int i = 0; i < vertexArrayList.size(); i++) {
			VertexElement vertex = vertexArrayList.get(i);

			PCEPAddress address = new PCEPAddress(vertex.getVertexID(), false); // FIXME
			traversedVertexesList.add(address);
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

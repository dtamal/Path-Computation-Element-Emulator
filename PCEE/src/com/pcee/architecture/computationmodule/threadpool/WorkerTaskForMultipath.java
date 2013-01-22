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

import java.io.IOException;
import java.util.ArrayList;

import com.benchmark.ResultLogger;
import com.graph.elements.vertex.VertexElement;
import com.graph.graphcontroller.Gcontroller;
import com.graph.path.PathElement;
import com.graph.path.algorithms.MultiPathComputationAlgorithm;
import com.graph.path.algorithms.constraints.MultiPathConstraint;
import com.graph.path.algorithms.constraints.impl.SimplePathComputationConstraint;
import com.graph.path.algorithms.impl.SimplePathComputationAlgorithm;
import com.pcee.architecture.ModuleEnum;
import com.pcee.architecture.ModuleManagement;
import com.pcee.architecture.computationmodule.ComputationModuleMLImpl;
import com.pcee.client.ClientTest;
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
import com.pcee.protocol.response.PCEPResponseFrame;
import com.pcee.protocol.response.PCEPResponseFrameFactory;

/**
 * Runnable class used by the thread pool to process path computation requests
 * 
 * @author Mohit Chamania
 * @author Marek Drogon
 */
public class WorkerTaskForMultipath implements Runnable {
	// Request to be processed
	private Request request;
	// Graph used for computation of the request
	private Gcontroller graph;
	// Module management object to send the response to the session layer
	private ModuleManagement lm;

	/** Default Constructor */
	public WorkerTaskForMultipath(ModuleManagement layerManagement,
			Request request, Gcontroller graph) {
		lm = layerManagement;
		this.request = request;
		this.graph = graph;
	}

	/**
	 * Function to process the path computation request and create a response
	 * object from the same
	 * 
	 * @return Response Object
	 */
	public Response processRequest() {
		// Initialize response object and set parameters
		Response response = new Response();
		response.setRequestID(request.getRequestID());
		response.setAddress(request.getAddress());

		if (ComputationModuleMLImpl.singlePath) {
			SimplePathComputationAlgorithm algo = (SimplePathComputationAlgorithm) request
					.getAlgo();
			SimplePathComputationConstraint constr = (SimplePathComputationConstraint) request
					.getConstrains();

			ClientTest.enterTheComputation.add(System.nanoTime());
			PathElement element = algo.computePath(graph, constr);
			ClientTest.leaveTheComputation.add(System.nanoTime());
			ArrayList<PathElement> elements = new ArrayList<PathElement>();

			if (element != null && element.getPathParams().getAvailableCapacity()>= constr.getBw()) {
				elements.add(element);
				response.setPathElements(elements);
			}
		} else {
			// Compute path
			MultiPathComputationAlgorithm malgo = request.getMAlgo();
			MultiPathConstraint mc = request.getMConstraints();

			// set begin of path computation time stamp before computePath() and
			// end of path computation time stamp after computePath()
			ClientTest.enterTheComputation.add(System.nanoTime());
			ArrayList<PathElement> elements = malgo.computePath(graph, mc);
			ClientTest.leaveTheComputation.add(System.nanoTime());

			if (elements != null) {
				response.setPathElements(elements);
				String loggerString = "computed paths are: \n ";
				for (int i = 0; i < elements.size(); i++)
					loggerString += "                        Path[" + i
							+ "] : " + elements.get(i).getVertexSequence()
							+ " \n";
				localLogger(loggerString);
			} else
				localDebugger("Error in Computing Path from "
						+ request.getMConstraints().getSource().getVertexID()
						+ " to "
						+ request.getMConstraints().getDestination()
								.getVertexID());
		}
		return response;
	}

	/** Function to update the graph instance used for computation */
	public void updateGraph(Gcontroller newGraph) {
		this.graph = newGraph;
	}

	/** Function to implement the path computation operations */
	public void run() {
		Response response = processRequest();
		processResponseMessage(response);

	}

	/** Function to create a PCEPResponse message from the response object */
	private void processResponseMessage(Response resp) {
		String requestID = resp.getRequestID();
		PCEPRequestParametersObject RP = PCEPObjectFrameFactory
				.generatePCEPRequestParametersObject("1", "0", "0", "0", "0",
						"1", requestID);
		PCEPResponseFrame responseFrame = PCEPResponseFrameFactory
				.generatePathComputationRequestFrame(RP);

		if (resp.getPathElements() == null) {
			// return no path object in PCEP Response Message
			PCEPNoPathObject noPath = PCEPObjectFrameFactory
					.generatePCEPNoPathObject("1", "0", 1, "0");
			responseFrame.insertNoPathObject(noPath);
			PCEPMessage message = PCEPMessageFactory
					.generateMessage(responseFrame);
			message.setAddress(resp.getAddress());
			lm.getComputationModule().sendMessage(message,
					ModuleEnum.SESSION_MODULE);
		} else {
			ArrayList<ArrayList<PCEPAddress>> vertexLists = getTraversedVertexes(resp);

			for (int j = 0; j < vertexLists.size(); j++) {
				ArrayList<PCEPAddress> vertexList = vertexLists.get(j);

				ArrayList<EROSubobjects> newVertexList = new ArrayList<EROSubobjects>();

				for (int i = 0; i < vertexList.size(); i++) {
					newVertexList.add(vertexList.get(i));
				}

				PCEPExplicitRouteObject ERO = PCEPObjectFrameFactory
						.generatePCEPExplicitRouteObject("1", "0",
								newVertexList);
				 PCEPBandwidthObject object =
				 PCEPObjectFrameFactory.generatePCEPBandwidthObject("1",
				 "0",((float)resp.getPathElements().get(j).getPathParams().getReserve()));
				responseFrame.insertExplicitRouteObject(ERO);
				 responseFrame.insertBandwidthObject(object);
			}
			PCEPMessage message = PCEPMessageFactory
					.generateMessage(responseFrame);
			message.setAddress(resp.getAddress());

			try {
				ResultLogger.logResult(message);// FIXME remove after test
			} catch (IOException e) {
				e.printStackTrace();
			}

			// Send response message from the computation layer to the session
			// layer
			lm.getComputationModule().sendMessage(message,
					ModuleEnum.SESSION_MODULE);
		}
	}

	/**
	 * Function to get the list of traversed vertices from the response object.
	 * Used to create ERO
	 * 
	 * @param resp
	 * @return
	 */
	private ArrayList<ArrayList<PCEPAddress>> getTraversedVertexes(Response resp) {

		ArrayList<ArrayList<PCEPAddress>> traversedVertexesLists = new ArrayList<ArrayList<PCEPAddress>>();
		for (PathElement element : resp.getPathElements()) {
			ArrayList<VertexElement> vertexArrayList = element
					.getTraversedVertices();

			ArrayList<PCEPAddress> traversedVertexesList = new ArrayList<PCEPAddress>();

			for (int i = 0; i < vertexArrayList.size(); i++) {
				PCEPAddress address = new PCEPAddress(vertexArrayList.get(i)
						.getVertexID(), false); // FIXME
				traversedVertexesList.add(address);
			}
			traversedVertexesLists.add(traversedVertexesList);
		}
		return traversedVertexesLists;
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

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

package com.pcee.architecture.computationmodule;

import java.util.concurrent.LinkedBlockingQueue;

import com.graph.graphcontroller.Gcontroller;
import com.graph.path.algorithms.constraints.impl.SimplePathComputationConstraint;
import com.graph.path.algorithms.impl.SimplePathComputationAlgorithm;
import com.pcee.architecture.ModuleEnum;
import com.pcee.architecture.ModuleManagement;
import com.pcee.architecture.computationmodule.ted.TopologyInformation;
import com.pcee.architecture.computationmodule.threadpool.Request;
import com.pcee.architecture.computationmodule.threadpool.ThreadPool;
import com.pcee.common.Address;
import com.pcee.logger.Logger;
import com.pcee.protocol.message.PCEPMessage;
import com.pcee.protocol.request.PCEPRequestFrame;
import com.pcee.protocol.request.PCEPRequestFrameFactory;

/**
 * 
 * @author Marek Drogon
 * @author Mohit Chamania
 */
public class ComputationModuleImpl extends ComputationModule {

	//Management Object used to forward communications between the different modules
	private ModuleManagement lm;

	//Thread Pool Implementation to compute incoming requests
	private ThreadPool threadPool;
	
	//Used by the ThreadPool class to initialize the given amount of Threads
	private int computationThreads;

	//Thread-safe queue to store requests to be used by the thread pool
	private LinkedBlockingQueue<Request> requestQueue;

	//Object to retrieve current topology information 
	private TopologyInformation topologyInstance = TopologyInformation.getInstance();

	//Graph library used for implementing path computation 
	private Gcontroller graph;

	/**Default Constructor
	 * 
	 * @param layerManagement
	 */
	public ComputationModuleImpl(ModuleManagement layerManagement) {
		lm = layerManagement;
		computationThreads = 5;
		start();
	}
	
	public ComputationModuleImpl(ModuleManagement layerManagement, int computationThreads) {
		lm = layerManagement;
		this.computationThreads = computationThreads; 
		start();
	}

	public void stop() {
		threadPool.stop();
		requestQueue.clear();
	}

	public void start() {
		//Get the current Graph Instance
		graph = topologyInstance.getGraph();
		//Initialize a new request Queue
		requestQueue = new LinkedBlockingQueue<Request>();
		//Initialize the thread pool used for computing requests
		threadPool = new ThreadPool(lm, computationThreads, requestQueue);
	}

	public void closeConnection(Address address) {
		// TODO Auto-generated method stub
		//Explicit removal of requests from closed connections not implemented
	}

	public void registerConnection(Address address, boolean connected,
			boolean connectionInitialized) {
		// TODO Auto-generated method stub
		//Explicit registration of connections not implemented

	}


	public  void receiveMessage(PCEPMessage message, ModuleEnum sourceLayer) {
		localDebugger("Entering: receiveMessage(PCEPMessage message)");
		localDebugger("| message: " + message.contentInformation());
		localDebugger("| sourceLayer: " + sourceLayer);
		switch (sourceLayer) {
		case SESSION_MODULE:
			computeRequest(message);
			break;
		default:
			localLogger("Error in receiveMessage(PCEPMessage message, LayerEnum targetLayer)");
			localLogger("Wrong sourceLayer");
			break;
		}


	}

	public void sendMessage(PCEPMessage message, ModuleEnum targetLayer) {
		switch (targetLayer) {
		case NETWORK_MODULE:
			// undefined
			break;
		case SESSION_MODULE:
			lm.getSessionModule().receiveMessage(message, ModuleEnum.COMPUTATION_MODULE);
			break;
		case COMPUTATION_MODULE:
			// undefined
			break;
		case CLIENT_MODULE:
			// Not possible
			break;
		default:
			localLogger("Error in sendMessage(PCEPMessage message, LayerEnum targetLayer)");
			localLogger("Wrong target Layer");
			break;
		}

	}

	/**Function to create the Request Object used by the thread pool to perform path computation
	 * 
	 * @param message
	 */
	private void computeRequest(PCEPMessage message) {
		localDebugger("Entering: computeRequest(PCEPMessage message)");
		try{
			//Extract Request Frame from the incoming message
			PCEPRequestFrame requestFrame = PCEPRequestFrameFactory.getPathComputationRequestFrame(message);
			Address address = message.getAddress();

			//Extract request parameters to be used for computing the path
			String requestID = Long.toString(requestFrame.getRequestID());
			String source = requestFrame.getSourceAddress().getAddress();
			String destination = requestFrame.getDestinationAddress().getAddress();

			//Creating new request to be assigned to the Thread Pool 
			Request req = new Request();
			req.setRequestID(requestID);
			req.setAddress(address);
			localLogger(source);
			localLogger(destination);
			localLogger("Size = " + graph.getVertexIDSet().size());
			req.setConstrains(new SimplePathComputationConstraint(graph.getVertex(source.trim()), graph.getVertex(destination.trim())));
			req.setAlgo(new SimplePathComputationAlgorithm());

			localLogger("Adding Request ID " + requestID + " to the Queue");
			requestQueue.add(req);
		}catch (NullPointerException e){
			localDebugger("Problem in generating request. Please check request parameters");
		}
	}

	/**Function to log events in the Computation layer
	 * 
	 * @param event
	 */
	private void localLogger(String event) {
		Logger.logSystemEvents("[MessageHandler] " + event);
	}

	/**Function to log debugging information in the computation layer
	 * 
	 * @param event
	 */
	private void localDebugger(String event) {
		Logger.debugger("[MessageHandler] " + event);
	}


}

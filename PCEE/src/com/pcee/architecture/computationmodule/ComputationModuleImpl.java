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

import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

import com.graph.graphcontroller.Gcontroller;
import com.graph.path.algorithms.constraints.impl.SimplePathComputationConstraint;
import com.graph.path.algorithms.impl.SimplePathComputationAlgorithm;
import com.pcee.architecture.ModuleEnum;
import com.pcee.architecture.ModuleManagement;
import com.pcee.architecture.computationmodule.ted.TopologyInformation;
import com.pcee.architecture.computationmodule.threadpool.Request;
import com.pcee.architecture.computationmodule.threadpool.ThreadPool;
import com.pcee.logger.Logger;
import com.pcee.protocol.message.PCEPMessage;
import com.pcee.protocol.message.objectframe.impl.erosubobjects.PCEPAddress;
import com.pcee.protocol.request.PCEPRequestFrame;
import com.pcee.protocol.request.PCEPRequestFrameFactory;
import com.pcee.protocol.response.PCEPResponseFrame;
import com.pcee.protocol.response.PCEPResponseFrameFactory;

/**
 * 
 * @author Marek Drogon
 * @author Mohit Chamania
 */
public class ComputationModuleImpl extends ComputationModule {

	// Management Object used to forward communications between the different
	// modules
	private ModuleManagement lm;

	// Thread Pool Implementation to compute incoming requests
	private ThreadPool threadPool;

	// Used by the ThreadPool class to initialize the given amount of Threads
	private int computationThreads;

	// Thread-safe queue to store requests to be used by the thread pool
	private LinkedBlockingQueue<Request> requestQueue;

	// Object to retrieve current topology information
	private TopologyInformation topologyInstance = TopologyInformation
			.getInstance();

	// Graph library used for implementing path computation
	private Gcontroller graph;

	//HashMap for keeping track of requests made to remote peers and the associated worker tasks
	private HashMap <String, LinkedBlockingQueue<PCEPMessage>> remotePeerResponseAssociationHashMap;
	
	/**
	 * Default Constructor
	 * 
	 * @param layerManagement
	 */
	public ComputationModuleImpl(ModuleManagement layerManagement) {
		lm = layerManagement;
		computationThreads = 5;
		start();
	}

	public ComputationModuleImpl(ModuleManagement layerManagement,
			int computationThreads) {
		lm = layerManagement;
		this.computationThreads = computationThreads;
		start();
	}

	public void stop() {
		threadPool.stop();
		requestQueue.clear();
	}

	public void start() {
		//Innitialize the map that will record the responses coming from remote peers
		remotePeerResponseAssociationHashMap = new HashMap<String, LinkedBlockingQueue<PCEPMessage>>();
		// Get the current Graph Instance
		graph = topologyInstance.getGraph();
		// Initialize a new request Queue
		requestQueue = new LinkedBlockingQueue<Request>();
		// Initialize the thread pool used for computing requests
		threadPool = new ThreadPool(lm, computationThreads, requestQueue);
	}

	public void closeConnection(PCEPAddress address) {
		// TODO Auto-generated method stub
		// Explicit removal of requests from closed connections not implemented
	}

	public void registerConnection(PCEPAddress address, boolean connected,
			boolean connectionInitialized, boolean forceClient) {
		// TODO Auto-generated method stub
		// Explicit registration of connections not implemented

	}

	public void receiveMessage(PCEPMessage message, ModuleEnum sourceLayer) {
		localDebugger("Entering: receiveMessage(PCEPMessage message)");
		localDebugger("| message: " + message.contentInformation());
		localDebugger("| sourceLayer: " + sourceLayer);
		switch (sourceLayer) {
		case SESSION_MODULE:
			//If message is a path computation request process message 
			if (message.getMessageHeader().getTypeDecimalValue()==3)
				computeRequest(message);
			else if (message.getMessageHeader().getTypeDecimalValue()==4) 
				//Path computation response received from another PCE server /// needs to be sent to a worker in the computataion module
				processResponseFromRemotePeer(message);
			break;
		default:
			localLogger("Error in receiveMessage(PCEPMessage message, LayerEnum targetLayer)");
			localLogger("Wrong sourceLayer");
			break;
		}

	}

	private String getKeyForRemotePeerAssociation(PCEPAddress address, String requestID) {
		return address.getIPv4Address(true) + "-" + requestID;
	}
	
	public synchronized boolean  isValidRequestToRemotePeer(PCEPAddress address, String requestID){
		//If the particular combination of remote PCE peer and request ID already exist do not make a new association
		String key = getKeyForRemotePeerAssociation(address, requestID);
		if (remotePeerResponseAssociationHashMap.containsKey(key)) 
			return false;
		return true;
	}
	
	public synchronized void registerRequestToRemotePeer(PCEPAddress address, String requestID, LinkedBlockingQueue<PCEPMessage> queue){
		if (isValidRequestToRemotePeer(address, requestID)) {
			String key = getKeyForRemotePeerAssociation(address, requestID);
			remotePeerResponseAssociationHashMap.put(key, queue);
		}
		else
			localLogger("registerRequestToRemotePeer: Not a valid request");
	}
	
	//Function to implement a mechanism where a response from another server (hierarchical or PCE peer) is sent to the correct worker task
	protected synchronized void processResponseFromRemotePeer(PCEPMessage message) {
		PCEPAddress address = message.getAddress();
		//Message is of type PCEP Response
		PCEPResponseFrame responseFrame = PCEPResponseFrameFactory
				.getPathComputationResponseFrame(message);
		String requestID = Integer.toString(responseFrame.getRequestID());
		String key = getKeyForRemotePeerAssociation(address, requestID);
		if (remotePeerResponseAssociationHashMap.containsKey(key)) {
			localLogger("Path Computation Response Received by the computation Module, adding to queue from worker task");
			remotePeerResponseAssociationHashMap.get(key).add(message);
			remotePeerResponseAssociationHashMap.remove(key);
		} else {
			localLogger("Response for Peer-requestID combnation that is not registered with the computation module");
		}
		
		
	}

	public void sendMessage(PCEPMessage message, ModuleEnum targetLayer) {
		switch (targetLayer) {
		case NETWORK_MODULE:
			// undefined
			break;
		case SESSION_MODULE:
			lm.getSessionModule().receiveMessage(message,
					ModuleEnum.COMPUTATION_MODULE);
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

	/**
	 * Function to create the Request Object used by the thread pool to perform
	 * path computation
	 * 
	 * @param message
	 */
	private void computeRequest(PCEPMessage message) {
		localDebugger("Entering: computeRequest(PCEPMessage message)");
		try {
			// Extract Request Frame from the incoming message
			PCEPRequestFrame requestFrame = PCEPRequestFrameFactory
					.getPathComputationRequestFrame(message);
			PCEPAddress address = message.getAddress();

			// Extract request parameters to be used for computing the path
			String requestID = Long.toString(requestFrame.getRequestID());
			String source = requestFrame.getSourceAddress().getIPv4Address(
					false);
			String destination = requestFrame.getDestinationAddress()
					.getIPv4Address(false);

			// Creating new request to be assigned to the Thread Pool
			Request req = new Request();
			req.setRequestID(requestID);
			req.setAddress(address);
			localLogger(source);
			localLogger(destination);
			localLogger("Size = " + graph.getVertexIDSet().size());
			req.setConstrains(new SimplePathComputationConstraint(graph
					.getVertex(source.trim()), graph.getVertex(destination
							.trim())));
			req.setAlgo(new SimplePathComputationAlgorithm());

			localLogger("Adding Request ID " + requestID + " to the Queue");
			requestQueue.add(req);
		} catch (NullPointerException e) {
			localDebugger("Problem in generating request. Please check request parameters");
		} 
	}

	/**
	 * Function to log events in the Computation layer
	 * 
	 * @param event
	 */
	private void localLogger(String event) {
		Logger.logSystemEvents("[MessageHandler] " + event);
	}

	/**
	 * Function to log debugging information in the computation layer
	 * 
	 * @param event
	 */
	private void localDebugger(String event) {
		Logger.debugger("[MessageHandler] " + event);
	}

}

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

package com.pcee.architecture.sessionmodule;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pcee.architecture.ModuleEnum;
import com.pcee.architecture.ModuleManagement;
import com.pcee.architecture.sessionmodule.statemachine.StateMachine;
import com.pcee.architecture.sessionmodule.statemachine.StateMachineClientImpl;
import com.pcee.architecture.sessionmodule.statemachine.StateMachineServerImpl;
import com.pcee.protocol.message.PCEPMessage;
import com.pcee.protocol.message.objectframe.impl.erosubobjects.PCEPAddress;

/**
 * Session Layer Implementation with multiple worker threads to process incoming
 * messages
 * 
 * @author Marek Drogon
 * @author Mohit Chamania
 */
public class SessionModuleImpl extends SessionModule {

	private static Logger logger = LoggerFactory.getLogger(SessionModuleImpl.class);
	
	// Management Object used to forward communications between the different
	// modules
	private ModuleManagement lm;

	// Static to define the number of worker threads processing incoming
	// messages from different modules
	final int sessionThreads;

	// Array Of Worker Threads to process incoming messages
	private ReadingQueueThreadImpl[] readingQueueThread;

	// HashMap to store association between address and the corresponding state
	// machine
	private HashMap<String, StateMachine> addressToStateMachineHashMap;

	// A single Timer to manage timeout events for all state machines
	private Timer stateMachineTimer;

	/**
	 * Default Constructor
	 * 
	 * @param layerManagement
	 */
	public SessionModuleImpl(ModuleManagement layerManagement) {
		logger.debug("Entering: SessionModuleImpl(ModuleManagement layerManagement)");

		lm = layerManagement;
		sessionThreads = 1;
		start();
	}

	public SessionModuleImpl(ModuleManagement layerManagement,
			int sessionThreads) {
		logger.debug("Entering: SessionModuleImpl(ModuleManagement layerManagement, int sessionThreads)");

		lm = layerManagement;
		this.sessionThreads = 1;
		start();
	}

	private class ReadingQueueBuffer {
		PCEPMessage message;
		ModuleEnum sourceLayer;

		public ReadingQueueBuffer(PCEPMessage message, ModuleEnum sourceLayer) {
			this.message = message;
			this.sourceLayer = sourceLayer;
		}
	}

	/** Worker threads to process incoming messages from different modules */
	private class ReadingQueueThreadImpl extends Thread {

		// A blocking Queue to store messages for the thread to process
		LinkedBlockingQueue<ReadingQueueBuffer> readingQueue = new LinkedBlockingQueue<ReadingQueueBuffer>();

		/**
		 * Function to add a message to the queue for the worker thread to read
		 * 
		 * @param message
		 */
		public void addMessage(PCEPMessage message, ModuleEnum sourceLayer) {
			logger.debug("Entering: addMessage(PCEPMessage message)");

			readingQueue.add(new ReadingQueueBuffer(message, sourceLayer));
		}

		// Main function of the worker thread to process incoming messages
		public void run() {
			logger.debug("Entering: run()");

			while (true) {
				ReadingQueueBuffer temp = null;
				try {
					logger.info("Waiting for new Messages");
					temp = readingQueue.take();
				} catch (InterruptedException e) {
					break;
				}
				processMessage(temp.message, temp.sourceLayer);
				if (Thread.currentThread().isInterrupted())
					break;

			}
		}

	}

	public void stop() {
		logger.debug("Entering: stop()");

		Iterator<StateMachine> iter = addressToStateMachineHashMap.values()
				.iterator();
		while (iter.hasNext()) {

			StateMachine sm = iter.next();
			closeConnection(sm.getAddress());
		}
		for (int i = 0; i < sessionThreads; i++)
			readingQueueThread[i].interrupt();
		stateMachineTimer.cancel();

	}

	public void start() {
		logger.debug("Entering: start()");

		// Initialize the timer object
		stateMachineTimer = new Timer();
		// Create a new map for storing associations between address and state
		// machines
		addressToStateMachineHashMap = new HashMap<String, StateMachine>();
		// Initialize the reading worker threads
		readingQueueThread = new ReadingQueueThreadImpl[sessionThreads];
		for (int i = 0; i < sessionThreads; i++) {
			readingQueueThread[i] = new ReadingQueueThreadImpl();
			readingQueueThread[i].setName("SessionLayerThread" + i);
			readingQueueThread[i].start();
		}
	}

	public void receiveMessage(PCEPMessage message, ModuleEnum sourceLayer) {
		logger.debug("Entering: receiveMessage(PCEPMessage message, ModuleEnum sourceLayer)");
		logger.debug("| message: " + message.contentInformation());
		logger.debug("| sourceLayer: " + sourceLayer);

		int x;
		switch (sourceLayer) {
		case NETWORK_MODULE:
			// Simple hash function to determine to which worker thread a
			// message should be assigned
			// TODO
			x = Integer.parseInt(message.getAddress().getIPv4Address()
					.split(":")[1])
					% sessionThreads;
			readingQueueThread[x].addMessage(message, sourceLayer);
			break;
		case COMPUTATION_MODULE:
			 x =
			 Integer.parseInt(message.getAddress().getIPv4Address().split(":")[1])
			 % sessionThreads;
			 readingQueueThread[x].addMessage(message, sourceLayer);
			//sendMessage(message, ModuleEnum.NETWORK_MODULE);
			break;
		case CLIENT_MODULE:
			x = Integer.parseInt(message.getAddress().getIPv4Address()
					.split(":")[1])
					% sessionThreads;
			readingQueueThread[x].addMessage(message, sourceLayer);
			// TODO
			// x =
			// Integer.parseInt(message.getAddress().getAddress().split(":")[1])
			// % readingThreadCount;
			// readingQueueThread[x].addMessage(message);
			break;
		default:
			logger.info("Error in recieveMessage(PCEPMessage message, LayerEnum sourceLayer)");
			logger.info("Wrong source Layer");
			break;
		}

	}

	public void sendMessage(PCEPMessage message, ModuleEnum targetLayer) {
		logger.debug("Entering: sendMessage(PCEPMessage message, ModuleEnum targetLayer)");
		logger.debug("| message: " + message.contentInformation());
		logger.debug("| targetLayer: " + targetLayer);

		switch (targetLayer) {
		case NETWORK_MODULE:
			lm.getNetworkModule().receiveMessage(message,
					ModuleEnum.SESSION_MODULE);
			break;
		case COMPUTATION_MODULE:
			lm.getComputationModule().receiveMessage(message,
					ModuleEnum.SESSION_MODULE);
			break;
		case CLIENT_MODULE:
			lm.getClientModule().receiveMessage(message,
					ModuleEnum.SESSION_MODULE);
			break;
		default:
			logger.info("Error in sendMessage(PCEPMessage message, LayerEnum targetLayer)");
			logger.info("Wrong target Layer 11");
			break;
		}

	}

	public void registerConnection(PCEPAddress address, boolean connected,
			boolean connectionInitialized, boolean forceClient) {
		logger.debug("Entering: registerConnection(Address address, boolean connected, boolean connectionInitialized)");
		logger.debug("| address: " + address.getIPv4Address());
		logger.debug("| connected: " + connected);
		logger.debug("| connectionInitialized: " + connectionInitialized);

		// if state machine exists do nothing
		if (!addressToStateMachineHashMap.containsKey(address.getIPv4Address())) {
			// Function for the client side, where the node is responsible for
			// initializing the connection and is not connected initially
			if ((connectionInitialized == true) && (connected == false)) {
				lm.getNetworkModule().registerConnection(address, connected,
						connectionInitialized, forceClient);
			}

			// If the connection is connected, register the new state machine
			// for the connection
			if (connected == true) {
				createNewStateMachine(address, connectionInitialized, forceClient);
			}
		}
	}

	public void closeConnection(PCEPAddress address) {
		logger.debug("Entering: closeConnection(PCEPAddress address)");
		logger.debug("| address: " + address.getIPv4Address());

		if (addressToStateMachineHashMap.containsKey(address.getIPv4Address())) {
			// Releasing resources from the state machine
			StateMachine stateMachine = getStateMachineFromHashMap(address);
			stateMachine.releaseResources();
			// removing state machine from hash map
			removeStateMachineFromHashMap(address);
			// closing connection in the network layer
			lm.getNetworkModule().closeConnection(address);
		} else {
			logger.debug("Could not find a StateMachine for "
					+ address.getIPv4Address());
		}
	}

	/**
	 * Function to create new state machine
	 * 
	 * @param address
	 * @param connectionInitialized
	 */
	private void createNewStateMachine(PCEPAddress address,
			boolean connectionInitialized, boolean forceClient) {
		logger.debug("Entering: createNewStateMachine(PCEPAddress address, boolean connectionInitialized)");
		logger.debug("| address: " + address.getIPv4Address());
		logger.debug("| connectionInitialized: " + connectionInitialized);

		logger.info("New StateMachine for " + address.getIPv4Address());
		// Creating new state machine
		// If LM is of type client, create a client state machine or else create a server state machine (by default) 
		StateMachine stateMachine;
		if (lm.isServer()) {
			//If in the server a state machine is registered forcefully as a client then we create a client state machine or else we make 
			// a server state machine
			if (forceClient)
				stateMachine = new StateMachineClientImpl(lm, address,stateMachineTimer, connectionInitialized);
			else
				stateMachine = new StateMachineServerImpl(lm, address,stateMachineTimer, connectionInitialized);
		} else {
			stateMachine = new StateMachineClientImpl(lm, address,stateMachineTimer, connectionInitialized);
		}
		
		// adding state machine to hash map
		insertStateMachineToHashMap(address, stateMachine);
	}

	/**
	 * Function to process and incoming message
	 * 
	 * @param message
	 */
	private void processMessage(PCEPMessage message, ModuleEnum sourceLayer) {
		logger.debug("Entering: processMessage(PCEPMessage message)");
		logger.debug("| message: " + message.contentInformation());
		logger.debug("| address: " + message.getAddress().getIPv4Address());

		logger.info("Processing Message from "
				+ message.getAddress().getIPv4Address());
		StateMachine machine = getStateMachineFromHashMap(message.getAddress());
		if (machine == null) {
			logger.info("State Machine for connection from "
					+ message.getAddress().getIPv4Address()
					+ " does not exist. Discarding Message");
		} else
			machine.updateState(message, sourceLayer);
	}

	/**
	 * Function to get the State machine from map using the address object
	 * 
	 * @param address
	 * @return
	 */
	private StateMachine getStateMachineFromHashMap(PCEPAddress address) {
		logger.debug("Entering: getStateMachineFromHashMap(PCEPAddress address)");
		logger.debug("| address: " + address.getIPv4Address());

		logger.info("Getting StateMachine for " + address.getIPv4Address());
		return addressToStateMachineHashMap.get(address.getIPv4Address());
	}

	/**
	 * Function to register the state machine inside the hash map
	 * 
	 * @param address
	 * @param stateMachine
	 */
	private void insertStateMachineToHashMap(PCEPAddress address,
			StateMachine stateMachine) {
		logger.debug("Entering: insertStateMachineToHashMap(Address address, StateMachineImpl stateMachine)");
		logger.debug("| address: " + address.getIPv4Address());
		logger.debug("| stateMachine: " + stateMachine.toString());

		logger.info("Inserting StateMachine for " + address.getIPv4Address());
		addressToStateMachineHashMap
				.put(address.getIPv4Address(), stateMachine);
		logger.info("| StateMachines active: "
				+ addressToStateMachineHashMap.size());
	}

	/**
	 * Function to remove the state machine from the hash Map
	 * 
	 * @param address
	 */
	private void removeStateMachineFromHashMap(PCEPAddress address) {
		logger.debug("Entering: removeStateMachineFromHashMap(PCEPAddress address)");
		logger.debug("| address: " + address.getIPv4Address());

		logger.info("Removing StateMachine for " + address.getIPv4Address());
		addressToStateMachineHashMap.remove(address.getIPv4Address());
		logger.info("| StateMachines active: "
				+ addressToStateMachineHashMap.size());
	}

}

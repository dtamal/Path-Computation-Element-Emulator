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

import com.pcee.architecture.ModuleEnum;
import com.pcee.architecture.ModuleManagement;
import com.pcee.architecture.sessionmodule.statemachine.StateMachine;
import com.pcee.architecture.sessionmodule.statemachine.StateMachineImpl;
import com.pcee.common.Address;
import com.pcee.logger.Logger;
import com.pcee.protocol.message.PCEPMessage;

/** Session Layer Implementation with multiple worker threads to process incoming messages
 * @author Marek Drogon
 * @author Mohit Chamania
 */
public class SessionModuleImpl extends SessionModule {

	//Management Object used to forward communications between the different modules
	private ModuleManagement lm;

	//Static to define the number of worker threads processing incoming messages from different modules
	final int sessionThreads;

	//Array Of Worker Threads to process incoming messages
	private ReadingQueueThreadImpl[] readingQueueThread;

	//HashMap to store association between address and the corresponding state machine
	private HashMap<String, StateMachine> addressToStateMachineHashMap;

	//A single Timer to manage timeout events for all state machines
	private Timer stateMachineTimer;

	/**Default Constructor
	 * 
	 * @param layerManagement
	 */
	public SessionModuleImpl(ModuleManagement layerManagement) {
		lm = layerManagement;
		sessionThreads = 5;
		start();
	}
	
	public SessionModuleImpl(ModuleManagement layerManagement, int sessionThreads) {
		lm = layerManagement;
		this.sessionThreads = sessionThreads;
		start();
	}
	
	/**Worker threads to process incoming messages from different modules */
	private class ReadingQueueThreadImpl extends Thread{
		//A blocking Queue to store messages for the thread to process
		LinkedBlockingQueue<PCEPMessage> readingQueue = new LinkedBlockingQueue<PCEPMessage> ();

		/** Function to add a message to the queue for the worker thread to read
		 * 
		 * @param message
		 */
		public void addMessage(PCEPMessage message){
			readingQueue.add(message);
		}

		//Main function of the worker thread to process incoming messages
		public void run() {
			while(true){
				PCEPMessage message;
				try {
					localLogger("Waiting for new Messages");
					message = readingQueue.take();
				} catch (InterruptedException e) {
					break;
				}
				processMessage(message);
				if (Thread.currentThread().isInterrupted())
					break;

			}
		}

	}


	public void stop() {
		Iterator<StateMachine> iter = addressToStateMachineHashMap.values().iterator();
		while(iter.hasNext()){

			StateMachine sm = iter.next();
			closeConnection(sm.getAddress());
		}
		for (int i=0;i<sessionThreads;i++)
			readingQueueThread[i].interrupt();
		stateMachineTimer.cancel();

	}

	public void start() {
		//Initialize the timer object
		stateMachineTimer = new Timer();
		//Create a new map for storing associations between address and state machines
		addressToStateMachineHashMap = new HashMap<String, StateMachine>();
		//Initialize the reading worker threads
		readingQueueThread = new ReadingQueueThreadImpl[sessionThreads];
		for (int i=0;i<sessionThreads; i++){
			readingQueueThread[i] = new ReadingQueueThreadImpl();
			readingQueueThread[i].setName("SessionLayerThread" + i);
			readingQueueThread[i].start();
		}
	}

	public void receiveMessage(PCEPMessage message, ModuleEnum sourceLayer) {
		localDebugger("Entering: receiveMessage(PCEPMessage message)");
		localDebugger("| message: " + message.contentInformation());
		localDebugger("| sourceLayer: " + sourceLayer);
		int x;
		switch (sourceLayer) {
		case NETWORK_MODULE:
			//Simple hash function to determine to which worker thread a message should be assigned
			//TODO
			x=Integer.parseInt(message.getAddress().getAddress().split(":")[1]) % sessionThreads;
			readingQueueThread[x].addMessage(message);
			break;
		case COMPUTATION_MODULE:
			x = Integer.parseInt(message.getAddress().getAddress().split(":")[1]) % sessionThreads;
			readingQueueThread[x].addMessage(message);
			break;
		case CLIENT_MODULE:
			sendMessage(message, ModuleEnum.NETWORK_MODULE);
			//TODO
//			x = Integer.parseInt(message.getAddress().getAddress().split(":")[1]) % readingThreadCount;
//			readingQueueThread[x].addMessage(message);

			break;
		default:
			localLogger("Error in recieveMessage(PCEPMessage message, LayerEnum sourceLayer)");
			localLogger("Wrong source Layer");
			break;
		}


	}
	
	public void sendMessage(PCEPMessage message, ModuleEnum targetLayer) {
		localDebugger("Entering: sendMessage(PCEPMessage message");
		localDebugger("| message: " + message.contentInformation());
		localDebugger("| targetLayer: " + targetLayer);

		switch (targetLayer) {
		case NETWORK_MODULE:
			lm.getNetworkModule().receiveMessage(message, ModuleEnum.SESSION_MODULE);
			break;
		case COMPUTATION_MODULE:
			lm.getComputationModule().receiveMessage(message, ModuleEnum.SESSION_MODULE);
			break;
		case CLIENT_MODULE:
			lm.getClientModule().receiveMessage(message, ModuleEnum.SESSION_MODULE);
			break;
		default:
			localLogger("Error in sendMessage(PCEPMessage message, LayerEnum targetLayer)");
			localLogger("Wrong target Layer");
			break;
		}

	}

	public void registerConnection(Address address, boolean connected, boolean connectionInitialized) {
		localDebugger("Entering: registerConnection(Address address, boolean connected, boolean connectionInitialized)");
		localDebugger("| address: " + address.getAddress());
		localDebugger("| connected: " + connected);
		localDebugger("| connectionInitialized: " + connectionInitialized);

		//Function for the client side, where the node is responsible for initializing the connection and is not connected initially
		if ((connectionInitialized==true) && (connected==false)){
			lm.getNetworkModule().registerConnection(address, connected, connectionInitialized);
		}

		// If the connection is connected, register the new state machine for the connection
		if (connected == true) {
			createNewStateMachine(address, connectionInitialized);
		}
	}

	public void closeConnection(Address address) {
		localDebugger("Entering: closeConnection(Address address)");
		localDebugger("| address: " + address.getAddress());

		if (addressToStateMachineHashMap.containsKey(address.getAddress())) {
			//Releasing resources from the state machine
			StateMachine stateMachine = getStateMachineFromHashMap(address);
			stateMachine.releaseResources();
			//removing state machine from hash map
			removeStateMachineFromHashMap(address);
			//closing connection in the network layer
			lm.getNetworkModule().closeConnection(address);
		} else {
			localDebugger("Could not find a StateMachine for " + address.getAddress());
		}
	}


	/**Function to create new state machine
	 * @param address
	 * @param connectionInitialized
	 */
	private void createNewStateMachine(Address address, boolean connectionInitialized) {
		localDebugger("Entering: createNewStateMachine(Address address, boolean connectionInitialized)");
		localDebugger("| address: " + address.getAddress());
		localDebugger("| connectionInitialized: " + connectionInitialized);

		localLogger("New StateMachine for " + address.getAddress());
		//Creating new state machine
		StateMachine stateMachine = new StateMachineImpl(lm, address, stateMachineTimer, connectionInitialized);
		//adding state machine to hash map
		insertStateMachineToHashMap(address, stateMachine);
	}

	/**Function to process and incoming message 
	 * 
	 * @param message
	 */
	private void processMessage(PCEPMessage message) {
		localDebugger("Entering: processMessage(PCEPMessage message)");
		localDebugger("| message: " + message.contentInformation());
		localDebugger("| address: " + message.getAddress().getAddress());

		localLogger("Processing Message from " + message.getAddress().getAddress());
		StateMachine machine = getStateMachineFromHashMap(message.getAddress());
		if (machine==null){
			localLogger("State Machine for connection from "+ message.getAddress().getAddress() + " does not exist. Discarding Message");
		}
		else
			machine.updateState(message);
	}

	/**Function to get the State machine from map using the address object
	 * 
	 * @param address
	 * @return
	 */	
	private StateMachine getStateMachineFromHashMap(Address address) {
		localDebugger("Entering: getStateMachineFromHashMap(Address address)");
		localDebugger("| address: " + address.getAddress());

		localLogger("Getting StateMachine for " + address.getAddress());
		return addressToStateMachineHashMap.get(address.getAddress());
	}

	
	/**Function to register the state machine inside the hash map 
	 * 
	 * @param address
	 * @param stateMachine
	 */
	private void insertStateMachineToHashMap(Address address, StateMachine stateMachine) {
		localDebugger("Entering: insertStateMachineToHashMap(Address address, StateMachineImpl stateMachine)");
		localDebugger("| address: " + address.getAddress());
		localDebugger("| stateMachine: " + stateMachine.toString());

		localLogger("Inserting StateMachine for " + address.getAddress());
		addressToStateMachineHashMap.put(address.getAddress(), stateMachine);
		localLogger("| StateMachines active: " + addressToStateMachineHashMap.size());
	}

	/**Function to remove the state machine from the hash Map
	 * 
	 * @param address
	 */
	private void removeStateMachineFromHashMap(Address address) {
		localDebugger("Entering: removeStateMachineFromHashMap(Address address)");
		localDebugger("| address: " + address.getAddress());

		localLogger("Removing StateMachine for " + address.getAddress());
		addressToStateMachineHashMap.remove(address.getAddress());
		localLogger("| StateMachines active: " + addressToStateMachineHashMap.size());
	}

	/**Function for logging events inside the Session Management module
	 * 
	 * @param event
	 */
	private void localLogger(String event) {
		Logger.logSystemEvents("[SessionLayer]     " + event);
	}

	/**Function for logging debug information inside the session management module
	 * 
	 * @param event
	 */
	private void localDebugger(String event) {
//		Logger.debugger("[SessionLayer]     " + event);
	}

}

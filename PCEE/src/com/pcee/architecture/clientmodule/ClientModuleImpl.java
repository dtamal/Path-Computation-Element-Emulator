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

package com.pcee.architecture.clientmodule;

import java.util.concurrent.LinkedBlockingQueue;

import com.pcee.architecture.ModuleEnum;
import com.pcee.architecture.ModuleManagement;
import com.pcee.common.Address;
import com.pcee.logger.Logger;
import com.pcee.protocol.message.PCEPMessage;
import com.pcee.protocol.response.PCEPResponseFrame;
import com.pcee.protocol.response.PCEPResponseFrameFactory;

public class ClientModuleImpl extends ClientModule {

	//Module Management Variable to facilitate inter-module communication
	private ModuleManagement lm;

	//Local Thread which buffers path computation requests until PCEP session is in SessionUP state
	private Thread sendingThread;
	
	//Message queue used to store buffered path computation requests till session up state is achieved
	private LinkedBlockingQueue<PCEPMessage> sendingQueue;
	
	//Boolean to indicate shutdown of sendingThread
	private boolean sendingThreadIsActive;

	/**Function to initialize the thread which sends buffered path computation messages */
	private void initSendingThread() {
		//Initialize new thread object
		sendingThread = new Thread() {
			public void run() {
				localLogger("Sending thread stated, processing buffered path compuaton requests");
				//boolean to check if thread is active
				while (sendingThreadIsActive) {
					try {
						PCEPMessage message = sendingQueue.take();
						sendMessage(message, ModuleEnum.SESSION_MODULE);
						localDebugger("Send message to Session layer");
						
					} catch (InterruptedException e) {
						// localLogger("Sending Thread Interrupted.");
					}
				}
			}
		};
	}

	public ClientModuleImpl(ModuleManagement layerManagement) {
		lm = layerManagement;
		this.start();
	}

	public void stop() {
		sendingThreadIsActive = false;
		sendingThread.interrupt();
		sendingQueue.clear();
	}

	public void start() {
		//Initialize new sending queue
		sendingQueue = new LinkedBlockingQueue<PCEPMessage>();
		//Set sending thread to active
		sendingThreadIsActive = true;
		//We initialize the Thread object definition  but do not start the sending thread here
		initSendingThread();		
	}

	
	public void closeConnection(Address address) {
		lm.getSessionModule().closeConnection(address);
	}

	public void registerConnection(Address address, boolean connected, boolean connectionInitialized) {
		localDebugger("Entering: registerConnection(Address address, boolean connected, boolean connectionInitialized)");
		localDebugger("| address: " + address.getAddress());
		localDebugger("| connected" + connected);
		localDebugger("| connectionInitialized" + connectionInitialized);

		if (connected == false) {
			lm.getSessionModule().registerConnection(address, connected, connectionInitialized);
		}
	}

	public synchronized void receiveMessage(PCEPMessage message, ModuleEnum sourceLayer) {
		localDebugger("Entering: receiveMessage(PCEPMessage message)");
		localDebugger("| message: " + message.contentInformation());
		localDebugger("| sourceLayer: " + sourceLayer);

		switch (sourceLayer) {
		case SESSION_MODULE:
			if (message.getMessageHeader().getTypeDecimalValue() == 2){
				//Wait till we receive a keepalive message (session in session up state) before starting sending thread
				sendingThread.start();
			}
			else if (message.getMessageHeader().getTypeDecimalValue() == 4) {
				//Path Computation Ressponse received
				PCEPResponseFrame responseFrame = PCEPResponseFrameFactory.getPathComputationResponseFrame(message);
				localLogger("Computation Received:  " + responseFrame.getTraversedVertexes());
			}
			break;
		default:
			localLogger("Error in sendMessage(PCEPMessage message, LayerEnum targetLayer)");
			localLogger("Wrong target Layer");
			break;
		}

	}

	public synchronized void sendMessage(PCEPMessage message, ModuleEnum targetLayer) {
		localDebugger("Entering: sendMessage(PCEPMessage message)");
		localDebugger("| message: " + message.contentInformation());
		localDebugger("| targetLayer: " + targetLayer);

		switch (targetLayer) {
		case SESSION_MODULE:
			lm.getSessionModule().receiveMessage(message, ModuleEnum.CLIENT_MODULE);
			break;
		case CLIENT_MODULE:
			//Special Case used by the User to send messages to the client layer 
			//Typically these are high level messages and are sent only after the session is in session up state
			if (message.getMessageHeader().getTypeDecimalValue() == 3)
				if (sendingThreadIsActive == false) {
					sendingThreadIsActive = true;
					initSendingThread();
					sendingThread.start();
				}
			sendingQueue.add(message);
			break;
		default:
			localLogger("Error in sendMessage(PCEPMessage message, LayerEnum targetLayer)");
			localLogger("Wrong target Layer");
			break;
		}
	}

	/**Function to log events in the Client Layer
	 * 
	 * @param event
	 */
	private void localLogger(String event) {
		Logger.logSystemEvents("[ClientLayer]     " + event);
	}

	/**Function to log debugging information in the client layer
	 * 
	 * @param event
	 */
	private void localDebugger(String event) {
		Logger.debugger("[ClientLayer]     " + event);
	}

	

}

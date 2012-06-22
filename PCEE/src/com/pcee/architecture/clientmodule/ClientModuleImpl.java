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
import com.pcee.client.ClientTest;
import com.pcee.logger.Logger;
import com.pcee.protocol.message.PCEPMessage;
import com.pcee.protocol.message.objectframe.impl.erosubobjects.PCEPAddress;
import com.pcee.protocol.response.PCEPResponseFrame;
import com.pcee.protocol.response.PCEPResponseFrameFactory;

public class ClientModuleImpl extends ClientModule {

	// Module Management Variable to facilitate inter-module communication
	private ModuleManagement lm;

	// Local Thread which buffers path computation requests until PCEP session
	// is in SessionUP state
	private Thread sendingThread;

	// Message queue used to store buffered path computation requests till
	// session up state is achieved
	private LinkedBlockingQueue<PCEPMessage> sendingQueue;

	// Boolean to indicate shutdown of sendingThread
	private boolean sendingThreadIsActive;

	public LinkedBlockingQueue<PCEPMessage> receiveQueue;

	/**
	 * Function to initialize the thread which sends buffered path computation
	 * messages
	 */
	private void initSendingThread() {
		localDebugger("|");
		localLogger("Entering: initSendingThread()");

		// Initialize new thread object
		sendingThread = new Thread() {
			public void run() {
				localLogger("| Sending thread stated, processing buffered path compuaton requests");
				// boolean to check if thread is active
				while (sendingThreadIsActive) {
					if (Thread.currentThread().isInterrupted())
						break;
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
		localDebugger("|");
		localLogger("Entering: ClientModuleImpl(ModuleManagement layerManagement)");
		localDebugger("| layerManagement: " + layerManagement);

		lm = layerManagement;
		this.start();
	}

	public void stop() {
		localDebugger("|");
		localLogger("Entering: stop()");

		sendingThreadIsActive = false;
		sendingThread.interrupt();
		sendingQueue.clear();
	}

	public void start() {
		localDebugger("|");
		localLogger("Entering: start()");

		receiveQueue = new LinkedBlockingQueue<PCEPMessage>();

		// Initialize new sending queue
		sendingQueue = new LinkedBlockingQueue<PCEPMessage>();
		// Set sending thread to active
		sendingThreadIsActive = false;
		// We initialize the Thread object definition but do not start the
		// sending thread here
		initSendingThread();
	}

	public void closeConnection(PCEPAddress address) {
		localDebugger("|");
		localLogger("Entering: closeConnection(Address address)");
		localDebugger("| address: " + address.getIPv4Address(true));

		lm.getSessionModule().closeConnection(address);
	}

	public void registerConnection(PCEPAddress address, boolean connected,
			boolean connectionInitialized) {
		localDebugger("|");
		localLogger("Entering: registerConnection(Address address, boolean connected, boolean connectionInitialized)");
		localDebugger("| address: " + address.getIPv4Address(true));
		localDebugger("| connected" + connected);
		localDebugger("| connectionInitialized" + connectionInitialized);

		if (connected == false) {
			lm.getSessionModule().registerConnection(address, connected,
					connectionInitialized);
		}
	}

	public synchronized void receiveMessage(PCEPMessage message,
			ModuleEnum sourceLayer) {
		localDebugger("|");
		localDebugger("Entering: receiveMessage(PCEPMessage message, ModuleEnum sourceLayer)");
		localDebugger("| message: " + message.contentInformation());
		localDebugger("| sourceLayer: " + sourceLayer);

		switch (sourceLayer) {
		case SESSION_MODULE:
			if (message.getMessageHeader().getTypeDecimalValue() == 2) {
				// Wait till we receive a keepalive message (session in session
				// up state) before starting sending thread
				if (sendingThreadIsActive==false){
					sendingThread.start();
					sendingThreadIsActive=true;
				}
			} else if (message.getMessageHeader().getTypeDecimalValue() == 4) {
				// Path Computation Ressponse received
				ClientTest.messageQueue.add(message);
				PCEPResponseFrame responseFrame = PCEPResponseFrameFactory
						.getPathComputationResponseFrame(message);
				localLogger("| COMPUTATION RECEIVED: "
						+ responseFrame.getTraversedVertexes());
				
				
/*				TopologyUpdateLauncher.responseFrame = responseFrame;
				TopologyUpdateLauncher.objectList = responseFrame.extractExplicitRouteObjectList();
				TopologyUpdateLauncher.bwList = responseFrame.extractBandwidthObjectList();
				TopologyUpdateLauncher.nopath = responseFrame.extractNoPathObject();
*///				TopologyUpdateLauncher.executeReserveAndRelease();
			} else if (message.getMessageHeader().getTypeDecimalValue() == 5) {
				receiveQueue.add(message);
				localLogger("| Notification message received");
			}
			break;
		default:
			localLogger("| Error: Wrong target Layer");
			break;
		}

	}

	public synchronized void sendMessage(PCEPMessage message,
			ModuleEnum targetLayer) {
		localDebugger("|");
		localDebugger("Entering: sendMessage(PCEPMessage message, ModuleEnum targetLayer)");
		localDebugger("| message: " + message.contentInformation());
		localDebugger("| targetLayer: " + targetLayer);

		switch (targetLayer) {
		case SESSION_MODULE:
			lm.getSessionModule().receiveMessage(message,
					ModuleEnum.CLIENT_MODULE);
			break;
		case CLIENT_MODULE:
			// Special Case used by the User to send messages to the client
			// layer
			// Typically these are high level messages and are sent only after
			// the session is in session up state
			
			  sendingQueue.add(message);
			 
//			lm.getSessionModule().receiveMessage(message,
//					ModuleEnum.CLIENT_MODULE);
			break;
		default:
			localLogger("| Error: Wrong target Layer");
			break;
		}
	}

	/**
	 * Function to log events in the Client Layer
	 * 
	 * @param event
	 */
	private void localLogger(String event) {
		Logger.logSystemEvents("[ClientLayer]     " + event);
	}

	/**
	 * Function to log debugging information in the client layer
	 * 
	 * @param event
	 */
	private void localDebugger(String event) {
		Logger.debugger("[ClientModule]     " + event);
	}

}

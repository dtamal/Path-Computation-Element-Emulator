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
import com.pcee.logger.Logger;
import com.pcee.protocol.message.PCEPMessage;
import com.pcee.protocol.message.objectframe.impl.erosubobjects.PCEPAddress;
import com.pcee.protocol.response.PCEPResponseFrame;
import com.pcee.protocol.response.PCEPResponseFrameFactory;

public class ClientModuleImpl extends ClientModule {

	// Module Management Variable to facilitate inter-module communication
	private ModuleManagement lm;

	public LinkedBlockingQueue<PCEPMessage> receiveQueue;



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
	}

	public void start() {
		localDebugger("|");
		localLogger("Entering: start()");
		receiveQueue = new LinkedBlockingQueue<PCEPMessage>();
	}

	public void closeConnection(PCEPAddress address) {
		localDebugger("|");
		localLogger("Entering: closeConnection(Address address)");
		localDebugger("| address: " + address.getIPv4Address(true));

		lm.getSessionModule().closeConnection(address);
	}

	public void registerConnection(PCEPAddress address, boolean connected,
			boolean connectionInitialized, boolean forceClient) {
		localDebugger("|");
		localLogger("Entering: registerConnection(Address address, boolean connected, boolean connectionInitialized)");
		localDebugger("| address: " + address.getIPv4Address(true));
		localDebugger("| connected" + connected);
		localDebugger("| connectionInitialized" + connectionInitialized);

		if (connected == false) {
			lm.getSessionModule().registerConnection(address, connected,
					connectionInitialized, true);
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
			if (message.getMessageHeader().getTypeDecimalValue() == 4) {
				// Path Computation Ressponse received
				//ClientTest.messageQueue.add(message);
				PCEPResponseFrame responseFrame = PCEPResponseFrameFactory
						.getPathComputationResponseFrame(message);
				localLogger("| COMPUTATION RECEIVED: "
						+ responseFrame.getTraversedVertexes());
				receiveQueue.add(message);
				
			} else if (message.getMessageHeader().getTypeDecimalValue() == 5) {
				receiveQueue.add(message);
				localLogger("| Notification message received");
			} else if (message.getMessageHeader().getTypeDecimalValue() == 6) {
				
				receiveQueue.add(message);
				localLogger("| Error message received");
				
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

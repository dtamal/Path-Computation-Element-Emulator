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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pcee.architecture.ModuleEnum;
import com.pcee.architecture.ModuleManagement;
import com.pcee.protocol.message.PCEPMessage;
import com.pcee.protocol.message.objectframe.impl.erosubobjects.PCEPAddress;
import com.pcee.protocol.response.PCEPResponseFrame;
import com.pcee.protocol.response.PCEPResponseFrameFactory;

public class ClientModuleImpl extends ClientModule {
	
	private static Logger logger = LoggerFactory.getLogger(ClientModuleImpl.class);
	
	// Module Management Variable to facilitate inter-module communication
	private ModuleManagement lm;

	//public LinkedBlockingQueue<PCEPMessage> receiveQueue;



	public ClientModuleImpl(ModuleManagement layerManagement) {
		logger.debug("|");
		logger.info("Entering: ClientModuleImpl(ModuleManagement layerManagement)");
		logger.debug("| layerManagement: " + layerManagement);

		lm = layerManagement;
		this.start();
	}

	public void stop(boolean graceful) {
		logger.info("Entering: stop(" + (graceful ? "true":"false") + ")");
	}

	public void start() {
		logger.info("Entering: start()");
		receiveQueue = new LinkedBlockingQueue<PCEPMessage>();
	}

	public void closeConnection(PCEPAddress address) {
		logger.debug("|");
		logger.info("Entering: closeConnection(Address address)");
		logger.debug("| address: " + address.getIPv4Address(true));

		lm.getSessionModule().closeConnection(address);
	}

	public void registerConnection(PCEPAddress address, boolean connected,
			boolean connectionInitialized, boolean forceClient) {
		logger.debug("|");
		logger.info("Entering: registerConnection(Address address, boolean connected, boolean connectionInitialized)");
		logger.debug("| address: " + address.getIPv4Address(true));
		logger.debug("| connected" + connected);
		logger.debug("| connectionInitialized" + connectionInitialized);

		if (connected == false) {
			lm.getSessionModule().registerConnection(address, connected,
					connectionInitialized, true);
		}
	}

	public synchronized void receiveMessage(PCEPMessage message,
			ModuleEnum sourceLayer) {
		logger.debug("|");
		logger.debug("Entering: receiveMessage(PCEPMessage message, ModuleEnum sourceLayer)");
		logger.debug("| message: " + message.contentInformation());
		logger.debug("| sourceLayer: " + sourceLayer);

		switch (sourceLayer) {
		case SESSION_MODULE:
			if (message.getMessageHeader().getTypeDecimalValue() == 4) {
				// Path Computation Ressponse received
				//ClientTest.messageQueue.add(message);
				PCEPResponseFrame responseFrame = PCEPResponseFrameFactory
						.getPathComputationResponseFrame(message);
				logger.info("| COMPUTATION RECEIVED: "
						+ responseFrame.getTraversedVertexes());
				receiveQueue.add(message);
				
			} else if (message.getMessageHeader().getTypeDecimalValue() == 5) {
				receiveQueue.add(message);
				logger.info("| Notification message received");
			} else if (message.getMessageHeader().getTypeDecimalValue() == 6) {
				
				receiveQueue.add(message);
				logger.info("| Error message received");
				
			}
			break;
		default:
			logger.info("| Error: Wrong target Layer");
			break;
		}

	}

	public synchronized void sendMessage(PCEPMessage message,
			ModuleEnum targetLayer) {
		logger.debug("|");
		logger.debug("Entering: sendMessage(PCEPMessage message, ModuleEnum targetLayer)");
		logger.debug("| message: " + message.contentInformation());
		logger.debug("| targetLayer: " + targetLayer);

		switch (targetLayer) {
		case SESSION_MODULE:
			lm.getSessionModule().receiveMessage(message,
					ModuleEnum.CLIENT_MODULE);
			break;
		default:
			logger.info("| Error: Wrong target Layer");
			break;
		}
	}

}

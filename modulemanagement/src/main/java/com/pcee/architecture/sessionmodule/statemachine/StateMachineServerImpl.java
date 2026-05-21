package com.pcee.architecture.sessionmodule.statemachine;

import java.util.Timer;

import com.pcee.architecture.ModuleEnum;
import com.pcee.architecture.ModuleManagement;
import com.pcee.protocol.message.PCEPMessage;
import com.pcee.protocol.message.PCEPMessageAnalyser;
import com.pcee.protocol.message.objectframe.impl.erosubobjects.PCEPAddress;

public class StateMachineServerImpl extends StateMachineImpl{

	public StateMachineServerImpl(ModuleManagement layerManagement, PCEPAddress Address, Timer stateTimer, boolean connectionInitialized) {
		super(layerManagement, Address, stateTimer, connectionInitialized);
	}

	@Override
	public void updateState(PCEPMessage message, ModuleEnum sourceModule) {
		logger.debug("Entering: updateState(PCEPMessage message, ModuleEnum targetLayer)");
		logger.debug("| message: " + message.contentInformation());
		switch (state) {
		case 0: {
			enterIdleState();
			break;
		}
		case 1: {
			// enterTCPPendingState();
			logger.info("You should not see me!");
			System.out.println("Message Arrived Before State Was Updated");
			break;
		}
		case 2: {
			enterOpenWaitState(message);
			break;
		}
		case 3: {
			enterKeepWaitState(message);
			break;
		}
		case 4: {
			enterSessionUPState(message, sourceModule);
			break;
		}
		default: {
			logger.info("Lost in a completely not reachable state. wtf?");
		}
		}
	}


	private void enterSessionUPState(PCEPMessage message, ModuleEnum sourceModule) {
		logger.debug("Entering: enterSessionUPState(PCEPMessage message)");
		logger.debug("| message: " + message.contentInformation());

		// System.out.println("[StateMachine: " + address.getAddress() +
		// "] entering Session up State" );

		// flushBuffer();

		boolean noErrorsDetected = PCEPMessageAnalyser
				.checkMessageFormat(message);

		if (noErrorsDetected == false) {
			logger.info("Message Format Error detected");
			sendCloseMessage();
			// releaseResources();
			closeTCPConnection();
		}

		this.checkMultipleConnections();

		logger.debug("Entering: analyzeMessage(PCEPMessage message)");

		int messageType = message.getMessageHeader().getTypeDecimalValue();

		switch (messageType) {

		case 1: {
			logger.info("Received Open Message");
			logger.info("Waiting to be implemented. Do something with this Message!");

			break;
		}
		case 2: {
			logger.info("Received KeepAlive Message");

			restartDeadTimer();
			logger.info("Processing Information: "
					+ message.contentInformation());

			break;
		}
		case 3: {
			logger.info("Received Path Computation Request Message");
			// System.out.println("Received Path Computation Request Message");

			restartDeadTimer();
			if (sourceModule.compareTo(ModuleEnum.NETWORK_MODULE) == 0) {
				lm.getSessionModule().sendMessage(message,
						ModuleEnum.COMPUTATION_MODULE);
			} else {
				logger.info("Server State Machine should not receive Path Computation Requests from any module other than Network");
			}
			break;
		}
		case 4: {
			logger.info("Received Path Computation Response Message");
			restartDeadTimer();
			// MessageHandler.readResponseMessage(message);
			if (sourceModule.compareTo(ModuleEnum.COMPUTATION_MODULE) == 0) {
				lm.getSessionModule().sendMessage(message,
						ModuleEnum.NETWORK_MODULE);
			} else {
				logger.info("Server State Machine should not receive Path Computation Responses from any module other than Computation Module");				
			}

			break;
		}
		case 5: {
			logger.info("Received Notification Message");


			break;
		}
		case 6: {
			logger.info("Received Error Message");

			break;
		}
		case 7: {
			logger.info("Received Close Message");

			this.closeTCPConnection();

			break;
		}
		default: {
			logger.info("ERROR: Unkown Message");

			break;
		}
		}

	}

}

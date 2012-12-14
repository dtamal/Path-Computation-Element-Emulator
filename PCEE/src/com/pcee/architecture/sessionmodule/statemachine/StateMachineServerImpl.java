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
		localDebugger("Entering: updateState(PCEPMessage message, ModuleEnum targetLayer)");
		localDebugger("| message: " + message.contentInformation());
		switch (state) {
		case 0: {
			enterIdleState();
			break;
		}
		case 1: {
			// enterTCPPendingState();
			localLogger("You should not see me!");
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
			localLogger("Lost in a completely not reachable state. wtf?");
		}
		}
	}


	private void enterSessionUPState(PCEPMessage message, ModuleEnum sourceModule) {
		localDebugger("Entering: enterSessionUPState(PCEPMessage message)");
		localDebugger("| message: " + message.contentInformation());

		// System.out.println("[StateMachine: " + address.getAddress() +
		// "] entering Session up State" );

		// flushBuffer();

		boolean noErrorsDetected = PCEPMessageAnalyser
				.checkMessageFormat(message);

		if (noErrorsDetected == false) {
			localLogger("Message Format Error detected");
			sendCloseMessage();
			// releaseResources();
			closeTCPConnection();
		}

		this.checkMultipleConnections();

		localDebugger("Entering: analyzeMessage(PCEPMessage message)");

		int messageType = message.getMessageHeader().getTypeDecimalValue();

		switch (messageType) {

		case 1: {
			localLogger("Received Open Message");
			localLogger("Waiting to be implemented. Do something with this Message!");

			break;
		}
		case 2: {
			localLogger("Received KeepAlive Message");

			restartDeadTimer();
			localLogger("Processing Information: "
					+ message.contentInformation());

			break;
		}
		case 3: {
			localLogger("Received Path Computation Request Message");
			// System.out.println("Received Path Computation Request Message");

			restartDeadTimer();
			if (sourceModule.compareTo(ModuleEnum.NETWORK_MODULE) == 0) {
				lm.getSessionModule().sendMessage(message,
						ModuleEnum.COMPUTATION_MODULE);
			} else {
				localLogger("Server State Machine should not receive Path Computation Requests from any module other than Network");
			}
			break;
		}
		case 4: {
			localLogger("Received Path Computation Response Message");
			restartDeadTimer();
			// MessageHandler.readResponseMessage(message);
			if (sourceModule.compareTo(ModuleEnum.COMPUTATION_MODULE) == 0) {
				lm.getSessionModule().sendMessage(message,
						ModuleEnum.NETWORK_MODULE);
			} else {
				localLogger("Server State Machine should not receive Path Computation Responses from any module other than Computation Module");				
			}

			break;
		}
		case 5: {
			localLogger("Received Notification Message");


			break;
		}
		case 6: {
			localLogger("Received Error Message");

			break;
		}
		case 7: {
			localLogger("Received Close Message");

			this.closeTCPConnection();

			break;
		}
		default: {
			localLogger("ERROR: Unkown Message");

			break;
		}
		}

	}

}

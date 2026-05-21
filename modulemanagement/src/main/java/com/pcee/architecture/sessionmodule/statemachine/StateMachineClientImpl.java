package com.pcee.architecture.sessionmodule.statemachine;

import java.util.Timer;
import java.util.concurrent.LinkedBlockingQueue;

import com.pcee.architecture.ModuleEnum;
import com.pcee.architecture.ModuleManagement;
import com.pcee.protocol.message.PCEPMessage;
import com.pcee.protocol.message.PCEPMessageAnalyser;
import com.pcee.protocol.message.objectframe.impl.erosubobjects.PCEPAddress;

public class StateMachineClientImpl extends StateMachineImpl {

	
	private LinkedBlockingQueue<PCEPMessage> sendingQueue;

	public StateMachineClientImpl(ModuleManagement layerManagement, PCEPAddress Address, Timer stateTimer, boolean connectionInitialized) {
		super(layerManagement, Address, stateTimer, connectionInitialized);
		sendingQueue = new LinkedBlockingQueue<PCEPMessage>();

	}



	//	flushBuffer();

	public synchronized void flushBuffer() {

		int size = sendingQueue.size();

		if (size > 0) {
			logger.info("Found " + size
					+ " Messages waiting  in Buffer for Connection "
					+ address.getIPv4Address());
			while (sendingQueue.size() > 0) {
				try {
					PCEPMessage message = sendingQueue.take();
					sendMessageToPeer(message, ModuleEnum.NETWORK_MODULE);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}


	@Override
	public void updateState(PCEPMessage message, ModuleEnum sourceModule) {
		logger.debug("Entering: updateState(PCEPMessage message, ModuleEnum targetLayer)");
		logger.debug("| message: " + message.contentInformation());
		//Client module should have a Buffer for messages not coming from the network (Client or Computation Module) that shoudl be sent once the 
		//State Machine comes to the session up state. This is done so that the client module/computation module does not need to buffer
		//Path Computation Requests or Notification messages before the state machine reaches the session up state

		if ((this.state != 4) && (sourceModule.compareTo(ModuleEnum.NETWORK_MODULE)!=0)) {
			sendingQueue.add(message);
		} else {
			//Process the messages normally

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
				//After the keep wait state, if the connection goes into the session up state 
				//the buffer with additional connections should be flushed 
				if (state==4) {
					flushBuffer();
				}
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
			if ((sourceModule.compareTo(ModuleEnum.CLIENT_MODULE)==0) || (sourceModule.compareTo(ModuleEnum.COMPUTATION_MODULE)==0)) {
				lm.getSessionModule().sendMessage(message,
						ModuleEnum.NETWORK_MODULE);
			} else {
				logger.info("Client State Machine should not receive Path Computation Requests from any module other than Network");
			}
			break;
		}
		case 4: {
			logger.info("Received Path Computation Response Message");
			restartDeadTimer();
			if (sourceModule.compareTo(ModuleEnum.NETWORK_MODULE) == 0) {
				//if the state machine is launched in a client, the response should be sent to the Client module 
				//or else in case of a server it should be sent to the computation module 
				if (lm.isServer()==false)
					lm.getSessionModule().sendMessage(message, ModuleEnum.CLIENT_MODULE);
				else
					lm.getSessionModule().sendMessage(message, ModuleEnum.COMPUTATION_MODULE);
			} else {
				logger.info("Client State Machine should not receive Path Computation Responses from any module other than Network Module");				
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

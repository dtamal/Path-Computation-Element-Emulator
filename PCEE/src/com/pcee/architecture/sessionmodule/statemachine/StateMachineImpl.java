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

package com.pcee.architecture.sessionmodule.statemachine;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pcee.architecture.ModuleEnum;
import com.pcee.architecture.ModuleManagement;
import com.pcee.common.SessionID;
import com.pcee.protocol.close.PCEPCloseFrame;
import com.pcee.protocol.close.PCEPCloseFrameFactory;
import com.pcee.protocol.keepalive.PCEPKeepaliveFrame;
import com.pcee.protocol.keepalive.PCEPKeepaliveFrameFactory;
import com.pcee.protocol.message.PCEPConstantValues;
import com.pcee.protocol.message.PCEPMessage;
import com.pcee.protocol.message.PCEPMessageAnalyser;
import com.pcee.protocol.message.PCEPMessageFactory;
import com.pcee.protocol.message.objectframe.impl.erosubobjects.PCEPAddress;
import com.pcee.protocol.open.PCEPOpenFrame;
import com.pcee.protocol.open.PCEPOpenFrameFactory;

//Example of the PCEPStateMachine 
//+-+-+-+-+-+-+<------+
//+------| SessionUP |<---+  |
//|      +-+-+-+-+-+-+    |  |
//|                       |  |
//|   +->+-+-+-+-+-+-+    |  |
//|   |  | KeepWait  |----+  |
//|   +--|           |<---+  |
//|+-----+-+-+-+-+-+-+    |  |
//||          |           |  |
//||          |           |  |
//||          V           |  |
//||  +->+-+-+-+-+-+-+----+  |
//||  |  | OpenWait  |-------+
//||  +--|           |<------+
//||+----+-+-+-+-+-+-+<---+  |
//|||         |           |  |
//|||         |           |  |
//|||         V           |  |
//||| +->+-+-+-+-+-+-+    |  |
//||| |  |TCPPending |----+  |
//||| +--|           |       |
//|||+---+-+-+-+-+-+-+<---+  |
//||||        |           |  |
//||||        |           |  |
//||||        V           |  |
//|||+--->+-+-+-+-+       |  |
//||+---->| Idle  |-------+  |
//|+----->|       |----------+
//+------>+-+-+-+-+

//Message Types
//Value     Meaning                          
//1        Open                          
//2        Keepalive                     
//3        Path Computation Request      
//4        Path Computation Reply        
//5        Notification                  
//6        Error                         
//7        Close                         

/**
 * Implementation of the basic PCEP State Machine
 * 
 * @author Marek Drogon
 */
public abstract class StateMachineImpl extends StateMachine {

	protected static Logger logger = LoggerFactory.getLogger(StateMachineImpl.class);

	// Module Management Variable to facilitate communication between the
	// different modules
	ModuleManagement lm;

	// Random Session ID instance used in this implementation
	protected int sessionID = SessionID.getInstance().getID(); // TODO read from
	// message
	// Remote address of the PCEP connection
	protected PCEPAddress address;

	// Boolean to indicate if the peer initialized the connection
	protected boolean connectionInitialized;

	// Boolean to indicate if the TCP connection was established
	protected boolean connectionEstablished;

	// Reference variable to the session layer global timer
	protected Timer stateTimer;

	// /Timer tasks defined to implement operations for different timeout
	// operations
	protected TimerTask connectTimerTask;
	protected TimerTask openWaitTimerTask;
	protected TimerTask keepWaitTimerTask;
	protected TimerTask keepAliveTimerTask;
	protected TimerTask deadTimerTask;

	// Boolean variables to check if timers are running
	protected boolean connectTimerRunning;
	protected boolean keepAliveTimerRunning;
	protected boolean deadTimerRunning;

	// Indicator to see if the Session UP state is achieved for the first time, for logging in GUI
	protected boolean firstTimeSessionUP = true;

	// Variable to store the current state of the state machine
	protected int state;

	// Int to store the connection retry count
	protected int connectRetry;

	// final int to store the maximum connection retries
	protected final int connectMaxRetry = 5;

	// FIXME change to non-debugging values back
	protected final int connect = PCEPConstantValues.CONNECT_TIMER;
	protected int openWait = PCEPConstantValues.OPENWAIT_TIMER;
	protected int keepWait = PCEPConstantValues.KEEPWAIT_TIMER;
	protected int keepAlive = PCEPConstantValues.KEEPALIVE_TIMER;
	protected int deadTimer = PCEPConstantValues.DEAD_TIMER;

	protected int openRetry;

	protected boolean remoteOk;
	protected boolean localOk;

	public StateMachineImpl(ModuleManagement layerManagement,
			PCEPAddress Address, Timer stateTimer, boolean connectionInitialized) {
		// logger.debug("Entering: StateMachineImpl(ModuleManagement layerManagement, PCEPAddress Address, Timer stateTimer, boolean connectionInitialized)");

		lm = layerManagement;

		address = Address;
		this.stateTimer = stateTimer;
		this.connectionInitialized = connectionInitialized;
		this.setState(0);

		this.connectRetry = 0;
		this.localOk = false;
		this.remoteOk = false;
		this.openRetry = 0;

		keepAliveTimerRunning = false;
		deadTimerRunning = false;

		this.enterIdleState();
		if (connectionInitialized == true)
			this.updateState(true);
	}

	protected void setState(int state) {
		logger.debug("Entering: setState(int state)");
		switch (state) {
		case 0: {
			logger.info("Entering Idle State");
			this.state = state;
			break;
		}
		case 1: {
			logger.info("Entering TCPPending State");
			this.state = state;
			break;
		}
		case 2: {
			logger.info("Entering OpenWait State");
			this.state = state;
			break;
		}
		case 3: {
			logger.info("Entering KeepWait State");
			this.state = state;
			break;
		}
		case 4: {
			logger.info("Entering SessionUP State");
			if (firstTimeSessionUP) {
				logger.info("Entering SessionUp State");
				logger.info("Connection established");
				firstTimeSessionUP = false;
			}
			this.state = state;

			if (keepAliveTimerRunning == false) {
				startKeepAliveTimer();
			}

			startDeadTimer();

			break;
		}
		default: {
			logger.info("ERROR: Unkown State");
			this.setState(0);
			break;
		}
		}


	}

	public synchronized void updateState(boolean connectionEstablished) {
		logger.debug("Entering: updateState(boolean connectionEstablished)");
		logger.debug("| connectionEstablished: " + connectionEstablished);

		if (state != 1) {
			logger.info("Received connectionEstablished Update in the wrong state! Should have been state 1, received it in state"
					+ state);
		}

		this.connectionEstablished = connectionEstablished;
		enterTCPPendingState();
	}



	public abstract void updateState(PCEPMessage message, ModuleEnum sourceModule);
	


	/**
	 * enterIdleState
	 */
	protected void enterIdleState() {
		logger.debug("Entering: enterIdleState()");

		// this.connectRetry = 0;
		// this.localOk = false;
		// this.remoteOk = false;
		// this.openRetry = 0;

		if (connectionInitialized == true) {
			logger.info("System initiated the Connection");
			startConnectTimer();
			setState(1);
		}

		if (connectionInitialized == false) {
			logger.info("System received the Connection");

			this.sendOpenMessage(keepAlive, deadTimer);
			this.startOpenWaitTimer();

			this.setState(2);
		}

	}

	/**
	 * enterTCPPendingState
	 */
	protected void enterTCPPendingState() {
		logger.debug("Entering: enterTCPPendingState()");

		if (connectionEstablished == true) { // (a)
			logger.info("Connection established with Peer");

			sendOpenMessage(keepAlive, deadTimer);
			cancelConnectTimer();
			startOpenWaitTimer();
			setState(2);
		}
		if (connectionEstablished == false) { // (b)
			logger.info("Connection establishment failed with Peer");

			checkPendingStatus();
		}

	}

	/**
	 * enterOpenWaitState
	 * 
	 * @param message
	 */
	protected void enterOpenWaitState(PCEPMessage message) {
		logger.debug("Entering: enterOpenWaitState(PCEPMessage message)");
		logger.debug("| message: " + message.contentInformation());

		// PCEPMessageFrame frame =
		// PCEPMessageFactory.getPCEPMessageFrame(message);
		int messageType = message.getMessageHeader().getTypeDecimalValue();

		if (messageType != 1) { // (h)
			logger.info("Received wrong Message Type. Should have been type 1. Got: "
					+ messageType);

			this.releaseResources();
		}

		this.checkMultipleConnections(); // (a)

		boolean noErrorsDetected = PCEPMessageAnalyser
				.checkMessageFormat(message);

		if (!noErrorsDetected) {// (b)
			logger.info("Message Format Error detected");

			this.sendErrorMessage(1, 1);
			// this.releaseResources();
			this.closeTCPConnection();
		}

		int sessionCharacteristics = PCEPMessageAnalyser
				.checkSessionCharacteristics(message);

		if (noErrorsDetected && sessionCharacteristics == 0) { // (c)
			logger.info("No Error detected, but Session Characteristics are not acceptable, but negotiable");

			this.openRetry = 1;
			this.sendErrorMessage(1, 5);
			this.releaseResources();
		}

		if (noErrorsDetected && sessionCharacteristics == 1) { // (d)
			logger.info("No Error detected and Session Characteristics are acceptable");

			this.sendKeepAliveMessage();
			this.setRemoteOk(true);

			if (localOk == true) {
				logger.info("localOk is true");

				this.cancelOpenWaitTimer();
				this.setState(4);
			}

			if (localOk == false) {
				logger.info("localOk is false");

				this.cancelOpenWaitTimer();
				this.startKeepWaitTimer();
				this.setState(3);
			}
		}

		if (noErrorsDetected && sessionCharacteristics == -1) { // (e)
			logger.info("No Error detected, but Session Characteristics are not acceptable, and not negotiable");

			this.sendErrorMessage(1, 3);
			this.releaseResources();
		}

		if (noErrorsDetected && openRetry == 0 && sessionCharacteristics == 0) { // (f)
			logger.info("No Error detected, openRetry==0, and Session Characteristics are not acceptable, but negotiable");

			this.incrementOpenRetry();
			this.sendErrorMessage(1, 4); // TODO No Proposed Objects, yet!

			if (localOk == true) {
				logger.info("localOk is true");

				this.restartOpenWaitTimer();
				this.setState(2);
			}

			if (localOk == false) {
				logger.info("localOk is false");

				this.cancelOpenWaitTimer();
				this.startKeepWaitTimer();
				this.setState(3);

			}
		}

	}

	/**
	 * enterKeepWaitState
	 * 
	 * @param message
	 */
	protected void enterKeepWaitState(PCEPMessage message) {
		logger.debug("Entering: enterKeepWaitState(PCEPMessage message)");
		logger.debug("| message: " + message.contentInformation());

		int messageType = message.getMessageHeader().getTypeDecimalValue();

		if (messageType != 2 && messageType != 6) { // (e)
			logger.info("Received wrong Message Type. Should have been type 2,6. Got: "
					+ messageType);
			this.releaseResources();
		}

		boolean noErrorsDetected = PCEPMessageAnalyser
				.checkMessageFormat(message);

		if (!noErrorsDetected) {// (b)
			logger.info("Message Format Error detected");

			this.sendErrorMessage(1, 1);
			this.closeTCPConnection();
		}

		if (messageType == 2) {
			logger.info("Processing KeepAlive Message");
			setLocalOk(true);

			if (remoteOk == true) {
				logger.info("remoteOk is true");

				this.cancelKeepWaitTimer();
				this.setState(4);
			}
			if (remoteOk == false) {
				logger.info("remoteOk is false");

				this.cancelKeepWaitTimer();
				this.startOpenWaitTimer();
				this.setState(2);
			}
		}

		if (messageType == 6) {
			logger.info("Processing Error Message");

			// TODO New Proposal through error msg
			int sessionCharacteristics = PCEPMessageAnalyser
					.checkSessionCharacteristics(message);

			if (sessionCharacteristics == -1) {
				logger.info("Session Characteristics are not acceptable, and not negotiable");

				this.sendErrorMessage(1, 6);
				// this.releaseResources();
				this.closeTCPConnection();
			}

			if (sessionCharacteristics == 1) {
				logger.info("Session Characteristics are acceptable");

				this.adjustSessionCharacteristics();
				this.restartKeepWaitTimer();
				this.sendOpenMessage(this.keepAlive, this.deadTimer);
			}

			if (remoteOk == true) {
				logger.info("remoteOk is true");

				this.restartKeepWaitTimer();
				this.setState(3);
			}
			if (remoteOk == false) {
				logger.info("remoteOk is false");

				this.cancelKeepWaitTimer();
				this.startOpenWaitTimer();
				this.setState(2);
			}

		}

	}

	/**
	 * enterSessionUPState
	 * 
	 * @param message
	 */
	protected void enterSessionUPState(PCEPMessage message) {
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

		this.analyzeMessage(message);

	}

	private void analyzeMessage(PCEPMessage message) {
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
			if (message.getAddress().getPort() == 4189) {
				// System.out.println("\n\n\n\n\n ------------------------------------ Message Send Check ------------------------------");
				lm.getSessionModule().sendMessage(message,
						ModuleEnum.NETWORK_MODULE);
			} else
				lm.getSessionModule().sendMessage(message,
						ModuleEnum.COMPUTATION_MODULE);

			break;
		}
		case 4: {
			logger.info("Received Path Computation Response Message");
			restartDeadTimer();
			// MessageHandler.readResponseMessage(message);
			if (message.getAddress().getPort() == 4189) {
				// System.out.println("\n\n\n\n\n ------------------------------------ Message Recieve Check ------------------------------");

				lm.getSessionModule().sendMessage(message,
						ModuleEnum.CLIENT_MODULE);
			} else if (lm.isServer()) {
				lm.getSessionModule().sendMessage(message,
						ModuleEnum.NETWORK_MODULE);
			} else {
				lm.getSessionModule().sendMessage(message,
						ModuleEnum.CLIENT_MODULE);
			}

			break;
		}
		case 5: {
			logger.info("Received Notification Message");

			// MessageHandler.processRequestMessage(message);

			break;
		}
		case 6: {
			logger.info("Received Error Message");
			if (lm.isServer()==false)
				lm.getClientModule().receiveMessage(message, ModuleEnum.SESSION_MODULE);

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

	/**************************************************************************************************
	 * Set/Get
	 */

	protected void setLocalOk(boolean value) {
		logger.debug("Entering: setLocalOk(boolean value)");

		logger.info("Setting LocalOk to: " + value);
		this.localOk = value;
	}

	protected void setRemoteOk(boolean value) {
		logger.debug("Entering: setRemoteOk(boolean value)");

		logger.info("Setting RemoteOk to: " + value);
		this.remoteOk = value;
	}

	protected void incrementOpenRetry() {
		logger.debug("Entering: incrementOpenRetry()");

		openRetry++;
		logger.info("Incrementing openRetry. Value is now: " + openRetry);
	}

	/**************************************************************************************************
	 * Various
	 */

	protected void sendMessageToPeer(PCEPMessage message, ModuleEnum targetLayer) {
		logger.debug("Entering: sendMessageToPeer(PCEPMessage message)");
		logger.debug("| message: " + message.contentInformation());

		message.setAddress(address);
		lm.getSessionModule().sendMessage(message, targetLayer);

	}

	protected void checkMultipleConnections() {
		logger.debug("Entering: checkMultipleConnections()");
		// logger.info("Waiting to be implemented! checkMultipleConnections()");
		// // TODO
	}

	protected void adjustSessionCharacteristics() {
		logger.debug("Entering: adjustSessionCharacteristics()");

		logger.info("Waiting to be implemented! adjustSessionCharacteristics()"); // TODO
	}

	protected void retryTCPConnection() {
		logger.debug("Entering: retryTCPConnection()");

		logger.info("Waiting to be implemented! retryTCPConnection()"); // TODO
	}

	protected void checkPendingStatus() {
		logger.debug("Entering: checkPendingStatus()");

		if (connectRetry == connectMaxRetry) {
			logger.info("Maximum number of connection retries reached");
			releaseResources();
		}
		if (connectRetry < connectMaxRetry) {
			retryTCPConnection();
			connectRetry++;
			restartConnectTimer();
		}
	}

	protected void terminateTimerTasks() {
		logger.debug("Entering: terminateTimerTasks()");

		logger.info("TERMINATING TIMERS");

		if (connectTimerTask != null)
			cancelConnectTimer();
		if (openWaitTimerTask != null)
			cancelOpenWaitTimer();
		if (keepWaitTimerTask != null)
			cancelKeepWaitTimer();
		if (deadTimerTask != null)
			cancelDeadTimer();
		if (keepAliveTimerTask != null)
			cancelKeepAliveTimer();
	}

	public void releaseResources() {
		logger.debug("Entering: releaseResources()");
		logger.info("RELEASING RESSOURCES");

		terminateTimerTasks();
		this.setState(0);
	}

	// TODO UPDATE
	protected void closeTCPConnection() {
		logger.debug("Entering: closeTCPConnection()");

		logger.info("Closing Connection");

		lm.getSessionModule().closeConnection(address);
	}

	/**************************************************************************************************
	 * Messages
	 */
	protected void sendOpenMessage(int keepalive, int deadTimer) {
		logger.debug("Entering: sendOpenMessage(int keepalive, int deadTimer)");

		logger.info("Sending Open Message to" + address.getIPv4Address());

		PCEPOpenFrame openFrame = PCEPOpenFrameFactory.generateOpenFrame(
				keepalive, deadTimer, "1", "1"); // TODO
		PCEPMessage openMessage = PCEPMessageFactory.generateMessage(openFrame);

		sendMessageToPeer(openMessage, ModuleEnum.NETWORK_MODULE);
	}

	protected void sendKeepAliveMessage() {
		logger.debug("Entering: sendKeepAliveMessage()");

		logger.info("Sending KeepAlive Message to" + address.getIPv4Address());

		PCEPKeepaliveFrame keepaliveFrame = PCEPKeepaliveFrameFactory
				.generateKeepaliveFrame();
		PCEPMessage keepaliveMessage = PCEPMessageFactory
				.generateMessage(keepaliveFrame);

		sendMessageToPeer(keepaliveMessage, ModuleEnum.NETWORK_MODULE);

		if (keepAliveTimerRunning == true) {
			this.restartKeepAliveTimer();
		}
	}

	protected void sendCloseMessage() {
		logger.debug("Entering: sendCloseMessage()");

		logger.info("Sending Close Message to" + address.getIPv4Address());

		PCEPCloseFrame closeFrame = PCEPCloseFrameFactory.generateCloseFrame(1,
				"1", "1");
		PCEPMessage closeMessage = PCEPMessageFactory
				.generateMessage(closeFrame);

		sendMessageToPeer(closeMessage, ModuleEnum.NETWORK_MODULE);
	}

	protected void sendErrorMessage(int type, int value) {
		logger.debug("Entering: sendErrorMessage(int type, int value)");

		logger.info("Sending Error Message to" + address.getIPv4Address());

		PCEPMessage errorMessage = PCEPMessageFactory
				.generateSIMPLEErrorMessage(type, value, "1", "0");
		sendMessageToPeer(errorMessage, ModuleEnum.NETWORK_MODULE);
	}

	/**************************************************************************************************
	 * Connect
	 */
	protected void startConnectTimer() {
		logger.debug("Entering: startConnectTimer()");

		logger.info("Starting Connect Timer");

		connectTimerRunning = true;

		connectTimerTask = new TimerTask() {
			public void run() {
				logger.info("ConnectTimer Expired");
				checkPendingStatus();
			}
		};
		stateTimer.schedule(connectTimerTask, connect * 1000);
	}

	protected void restartConnectTimer() {
		logger.debug("Entering: restartConnectTimer()");

		logger.info("Restarting Connect Timer");

		this.connectTimerTask.cancel();
		connectTimerTask = new TimerTask() {
			public void run() {
				logger.info("Connect Timer Expired");
				checkPendingStatus();
			}
		};
		stateTimer.schedule(connectTimerTask, connect * 1000);
	}

	protected void cancelConnectTimer() {
		logger.debug("Entering: cancelConnectTimer()");

		if (connectTimerRunning == true) {
			logger.info("Cancelling Connect Timer");

			this.connectTimerTask.cancel();
			connectTimerRunning = false;
		}

	}

	/**************************************************************************************************
	 * OpenWait
	 */
	protected void startOpenWaitTimer() {
		logger.debug("Entering: startOpenWaitTimer()");

		logger.info("Starting OpenWait Timer");

		openWaitTimerTask = new TimerTask() {
			public void run() {
				logger.info("OpenWaitTimer Expired");
				sendErrorMessage(1, 2);
				// releaseResources();
				closeTCPConnection();
			}
		};
		stateTimer.schedule(openWaitTimerTask, openWait * 1000);

	}

	protected void restartOpenWaitTimer() {
		logger.debug("Entering: restartOpenWaitTimer()");

		logger.info("Restarting OpenWait Timer");

		this.openWaitTimerTask.cancel();
		openWaitTimerTask = new TimerTask() {
			public void run() {
				logger.info("OpenWait Timer Expired");
				sendErrorMessage(1, 2);
				// releaseResources();
				closeTCPConnection();
			}
		};
		stateTimer.schedule(openWaitTimerTask, openWait * 1000);
	}

	protected void cancelOpenWaitTimer() {
		logger.debug("Entering: cancelOpenWaitTimer()");

		logger.info("Cancelling OpenWait Timer");

		this.openWaitTimerTask.cancel();
	}

	/**************************************************************************************************
	 * KeepWait
	 */
	protected void startKeepWaitTimer() {
		logger.debug("Entering: startKeepWaitTimer()");

		logger.info("Starting KeepWait Timer");

		keepWaitTimerTask = new TimerTask() {
			public void run() {
				logger.info("KeepWaitTimer Expired");
				sendErrorMessage(1, 7);
				// releaseResources();
				closeTCPConnection();
			}
		};
		stateTimer.schedule(keepWaitTimerTask, keepWait * 1000);
	}

	protected void restartKeepWaitTimer() {
		logger.debug("Entering: restartKeepWaitTimer()");

		logger.info("Restarting KeepWait Timer");

		this.keepWaitTimerTask.cancel();
		keepWaitTimerTask = new TimerTask() {
			public void run() {
				logger.info("KeepWait Timer Expired");
				sendErrorMessage(1, 7);
				// releaseResources();
				closeTCPConnection();
			}
		};
		stateTimer.schedule(keepWaitTimerTask, keepWait * 1000);
	}

	protected void cancelKeepWaitTimer() {
		logger.debug("Entering: cancelKeepWaitTimer()");

		logger.info("Cancelling KeepWait Timer");

		this.keepWaitTimerTask.cancel();
	}

	/**************************************************************************************************
	 * KeepAlive
	 */
	protected void startKeepAliveTimer() {
		logger.debug("Entering: startKeepAliveTimer()");

		logger.info("Starting KeepAlive Timer");

		keepAliveTimerRunning = true;

		keepAliveTimerTask = new TimerTask() {
			public void run() {
				sendKeepAliveMessage();
			}
		};
		stateTimer.schedule(keepAliveTimerTask, keepAlive * 1000);
	}

	protected void restartKeepAliveTimer() {
		logger.debug("Entering: restartKeepAliveTimer()");

		logger.info("Restarting KeepAlive Timer");

		this.keepAliveTimerTask.cancel();
		keepAliveTimerTask = new TimerTask() {
			public void run() {
				sendKeepAliveMessage();
			}
		};
		stateTimer.schedule(keepAliveTimerTask, keepAlive * 1000);
	}

	protected void cancelKeepAliveTimer() {
		logger.debug("Entering: cancelKeepAliveTimer()");

		logger.info("Cancelling KeepAlive Timer");

		this.keepAliveTimerTask.cancel();
	}

	/**************************************************************************************************
	 * DeadTimer
	 */
	protected void startDeadTimer() {
		logger.debug("Entering: startDeadTimer()");

		logger.info("Starting Dead Timer");

		deadTimerRunning = true;

		deadTimerTask = new TimerTask() {
			public void run() {

				cancelKeepAliveTimer(); // TODO remove after closing the
				// connection is implemented
				sendCloseMessage();
				// releaseResources();
				closeTCPConnection();
			}
		};
		stateTimer.schedule(deadTimerTask, deadTimer * 1000);

	}

	protected void restartDeadTimer() {
		logger.debug("Entering: restartDeadTimer()");

		logger.info("Restarting Dead Timer");
		if (deadTimerRunning == false) {
			logger.debug("DeadTimer was not running. Restarting task");
		} else
			this.deadTimerTask.cancel();
		deadTimerRunning = true;
		deadTimerTask = new TimerTask() {
			public void run() {

				cancelKeepAliveTimer(); // TODO remove after closing the
				// connection is implemented
				sendCloseMessage();
				// releaseResources();
				closeTCPConnection();
			}
		};
		stateTimer.schedule(deadTimerTask, deadTimer * 1000);
	}

	protected void cancelDeadTimer() {
		logger.debug("Entering: cancelDeadTimer()");

		logger.info("Cancelling Dead Timer");

		this.deadTimerTask.cancel();
	}

	public PCEPAddress getAddress() {
		logger.debug("Entering: getAddress()");

		return address;
	}

	public String toString() {
		return "Address: " + address.getIPv4Address() + " sessionID: "
				+ sessionID + " State: " + state + " ConnectionInitialized: "
				+ connectionInitialized;
	}

	protected String prefixGenerator() {
		String prefix;

		switch (state) {
		case 0: {
			prefix = "*IDLE*";
			break;
		}
		case 1: {
			prefix = "*TCP PENDING*";
			break;
		}
		case 2: {
			prefix = "*OPENWAIT*";
			break;
		}
		case 3: {
			prefix = "*KEEPWAIT*";
			break;
		}
		case 4: {
			prefix = "*SESSION UP*";
			break;
		}
		default: {
			prefix = "UNKNOWN STATE - ERROR";
			break;
		}
		}

		return prefix;
	}
}

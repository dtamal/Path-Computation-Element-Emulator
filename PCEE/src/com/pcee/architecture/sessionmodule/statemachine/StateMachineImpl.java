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

import com.pcee.architecture.ModuleEnum;
import com.pcee.architecture.ModuleManagement;
import com.pcee.common.SessionID;
import com.pcee.logger.Logger;
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
		// localDebugger("Entering: StateMachineImpl(ModuleManagement layerManagement, PCEPAddress Address, Timer stateTimer, boolean connectionInitialized)");

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
		localDebugger("Entering: setState(int state)");
		switch (state) {
		case 0: {
			localLogger("Entering Idle State");
			this.state = state;
			break;
		}
		case 1: {
			localLogger("Entering TCPPending State");
			this.state = state;
			break;
		}
		case 2: {
			localLogger("Entering OpenWait State");
			this.state = state;
			break;
		}
		case 3: {
			localLogger("Entering KeepWait State");
			this.state = state;
			break;
		}
		case 4: {
			localLogger("Entering SessionUP State");
			if (firstTimeSessionUP) {
				localLogger("Entering SessionUp State");
				guiLogger("Connection established");
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
			localLogger("ERROR: Unkown State");
			this.setState(0);
			break;
		}
		}


	}

	public synchronized void updateState(boolean connectionEstablished) {
		localDebugger("Entering: updateState(boolean connectionEstablished)");
		localDebugger("| connectionEstablished: " + connectionEstablished);

		if (state != 1) {
			localLogger("Received connectionEstablished Update in the wrong state! Should have been state 1, received it in state"
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
		localDebugger("Entering: enterIdleState()");

		// this.connectRetry = 0;
		// this.localOk = false;
		// this.remoteOk = false;
		// this.openRetry = 0;

		if (connectionInitialized == true) {
			localLogger("System initiated the Connection");
			startConnectTimer();
			setState(1);
		}

		if (connectionInitialized == false) {
			localLogger("System received the Connection");

			this.sendOpenMessage(keepAlive, deadTimer);
			this.startOpenWaitTimer();

			this.setState(2);
		}

	}

	/**
	 * enterTCPPendingState
	 */
	protected void enterTCPPendingState() {
		localDebugger("Entering: enterTCPPendingState()");

		if (connectionEstablished == true) { // (a)
			localLogger("Connection established with Peer");

			sendOpenMessage(keepAlive, deadTimer);
			cancelConnectTimer();
			startOpenWaitTimer();
			setState(2);
		}
		if (connectionEstablished == false) { // (b)
			localLogger("Connection establishment failed with Peer");

			checkPendingStatus();
		}

	}

	/**
	 * enterOpenWaitState
	 * 
	 * @param message
	 */
	protected void enterOpenWaitState(PCEPMessage message) {
		localDebugger("Entering: enterOpenWaitState(PCEPMessage message)");
		localDebugger("| message: " + message.contentInformation());

		// PCEPMessageFrame frame =
		// PCEPMessageFactory.getPCEPMessageFrame(message);
		int messageType = message.getMessageHeader().getTypeDecimalValue();

		if (messageType != 1) { // (h)
			localLogger("Received wrong Message Type. Should have been type 1. Got: "
					+ messageType);

			this.releaseResources();
		}

		this.checkMultipleConnections(); // (a)

		boolean noErrorsDetected = PCEPMessageAnalyser
				.checkMessageFormat(message);

		if (!noErrorsDetected) {// (b)
			localLogger("Message Format Error detected");

			this.sendErrorMessage(1, 1);
			// this.releaseResources();
			this.closeTCPConnection();
		}

		int sessionCharacteristics = PCEPMessageAnalyser
				.checkSessionCharacteristics(message);

		if (noErrorsDetected && sessionCharacteristics == 0) { // (c)
			localLogger("No Error detected, but Session Characteristics are not acceptable, but negotiable");

			this.openRetry = 1;
			this.sendErrorMessage(1, 5);
			this.releaseResources();
		}

		if (noErrorsDetected && sessionCharacteristics == 1) { // (d)
			localLogger("No Error detected and Session Characteristics are acceptable");

			this.sendKeepAliveMessage();
			this.setRemoteOk(true);

			if (localOk == true) {
				localLogger("localOk is true");

				this.cancelOpenWaitTimer();
				this.setState(4);
			}

			if (localOk == false) {
				localLogger("localOk is false");

				this.cancelOpenWaitTimer();
				this.startKeepWaitTimer();
				this.setState(3);
			}
		}

		if (noErrorsDetected && sessionCharacteristics == -1) { // (e)
			localLogger("No Error detected, but Session Characteristics are not acceptable, and not negotiable");

			this.sendErrorMessage(1, 3);
			this.releaseResources();
		}

		if (noErrorsDetected && openRetry == 0 && sessionCharacteristics == 0) { // (f)
			localLogger("No Error detected, openRetry==0, and Session Characteristics are not acceptable, but negotiable");

			this.incrementOpenRetry();
			this.sendErrorMessage(1, 4); // TODO No Proposed Objects, yet!

			if (localOk == true) {
				localLogger("localOk is true");

				this.restartOpenWaitTimer();
				this.setState(2);
			}

			if (localOk == false) {
				localLogger("localOk is false");

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
		localDebugger("Entering: enterKeepWaitState(PCEPMessage message)");
		localDebugger("| message: " + message.contentInformation());

		int messageType = message.getMessageHeader().getTypeDecimalValue();

		if (messageType != 2 && messageType != 6) { // (e)
			localLogger("Received wrong Message Type. Should have been type 2,6. Got: "
					+ messageType);
			this.releaseResources();
		}

		boolean noErrorsDetected = PCEPMessageAnalyser
				.checkMessageFormat(message);

		if (!noErrorsDetected) {// (b)
			localLogger("Message Format Error detected");

			this.sendErrorMessage(1, 1);
			this.closeTCPConnection();
		}

		if (messageType == 2) {
			localLogger("Processing KeepAlive Message");
			setLocalOk(true);

			if (remoteOk == true) {
				localLogger("remoteOk is true");

				this.cancelKeepWaitTimer();
				this.setState(4);
			}
			if (remoteOk == false) {
				localLogger("remoteOk is false");

				this.cancelKeepWaitTimer();
				this.startOpenWaitTimer();
				this.setState(2);
			}
		}

		if (messageType == 6) {
			localLogger("Processing Error Message");

			// TODO New Proposal through error msg
			int sessionCharacteristics = PCEPMessageAnalyser
					.checkSessionCharacteristics(message);

			if (sessionCharacteristics == -1) {
				localLogger("Session Characteristics are not acceptable, and not negotiable");

				this.sendErrorMessage(1, 6);
				// this.releaseResources();
				this.closeTCPConnection();
			}

			if (sessionCharacteristics == 1) {
				localLogger("Session Characteristics are acceptable");

				this.adjustSessionCharacteristics();
				this.restartKeepWaitTimer();
				this.sendOpenMessage(this.keepAlive, this.deadTimer);
			}

			if (remoteOk == true) {
				localLogger("remoteOk is true");

				this.restartKeepWaitTimer();
				this.setState(3);
			}
			if (remoteOk == false) {
				localLogger("remoteOk is false");

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

		this.analyzeMessage(message);

	}

	private void analyzeMessage(PCEPMessage message) {
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
			localLogger("Received Path Computation Response Message");
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
			localLogger("Received Notification Message");

			// MessageHandler.processRequestMessage(message);

			break;
		}
		case 6: {
			localLogger("Received Error Message");
			if (lm.isServer()==false)
				lm.getClientModule().receiveMessage(message, ModuleEnum.SESSION_MODULE);

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

	/**************************************************************************************************
	 * Set/Get
	 */

	protected void setLocalOk(boolean value) {
		localDebugger("Entering: setLocalOk(boolean value)");

		localLogger("Setting LocalOk to: " + value);
		this.localOk = value;
	}

	protected void setRemoteOk(boolean value) {
		localDebugger("Entering: setRemoteOk(boolean value)");

		localLogger("Setting RemoteOk to: " + value);
		this.remoteOk = value;
	}

	protected void incrementOpenRetry() {
		localDebugger("Entering: incrementOpenRetry()");

		openRetry++;
		localLogger("Incrementing openRetry. Value is now: " + openRetry);
	}

	/**************************************************************************************************
	 * Various
	 */

	protected void sendMessageToPeer(PCEPMessage message, ModuleEnum targetLayer) {
		localDebugger("Entering: sendMessageToPeer(PCEPMessage message)");
		localDebugger("| message: " + message.contentInformation());

		message.setAddress(address);
		lm.getSessionModule().sendMessage(message, targetLayer);

	}

	protected void checkMultipleConnections() {
		localDebugger("Entering: checkMultipleConnections()");
		// localLogger("Waiting to be implemented! checkMultipleConnections()");
		// // TODO
	}

	protected void adjustSessionCharacteristics() {
		localDebugger("Entering: adjustSessionCharacteristics()");

		localLogger("Waiting to be implemented! adjustSessionCharacteristics()"); // TODO
	}

	protected void retryTCPConnection() {
		localDebugger("Entering: retryTCPConnection()");

		localLogger("Waiting to be implemented! retryTCPConnection()"); // TODO
	}

	protected void checkPendingStatus() {
		localDebugger("Entering: checkPendingStatus()");

		if (connectRetry == connectMaxRetry) {
			localLogger("Maximum number of connection retries reached");
			releaseResources();
		}
		if (connectRetry < connectMaxRetry) {
			retryTCPConnection();
			connectRetry++;
			restartConnectTimer();
		}
	}

	protected void terminateTimerTasks() {
		localDebugger("Entering: terminateTimerTasks()");

		localLogger("TERMINATING TIMERS");

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
		localDebugger("Entering: releaseResources()");
		localLogger("RELEASING RESSOURCES");

		terminateTimerTasks();
		this.setState(0);
	}

	// TODO UPDATE
	protected void closeTCPConnection() {
		localDebugger("Entering: closeTCPConnection()");

		localLogger("CLOSING CONNECTION");
		guiLogger("Closing Connection");

		lm.getSessionModule().closeConnection(address);
	}

	/**************************************************************************************************
	 * Messages
	 */
	protected void sendOpenMessage(int keepalive, int deadTimer) {
		localDebugger("Entering: sendOpenMessage(int keepalive, int deadTimer)");

		localLogger("Sending Open Message to" + address.getIPv4Address());

		PCEPOpenFrame openFrame = PCEPOpenFrameFactory.generateOpenFrame(
				keepalive, deadTimer, "1", "1"); // TODO
		PCEPMessage openMessage = PCEPMessageFactory.generateMessage(openFrame);

		sendMessageToPeer(openMessage, ModuleEnum.NETWORK_MODULE);
	}

	protected void sendKeepAliveMessage() {
		localDebugger("Entering: sendKeepAliveMessage()");

		localLogger("Sending KeepAlive Message to" + address.getIPv4Address());

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
		localDebugger("Entering: sendCloseMessage()");

		localLogger("Sending Close Message to" + address.getIPv4Address());

		PCEPCloseFrame closeFrame = PCEPCloseFrameFactory.generateCloseFrame(1,
				"1", "1");
		PCEPMessage closeMessage = PCEPMessageFactory
				.generateMessage(closeFrame);

		sendMessageToPeer(closeMessage, ModuleEnum.NETWORK_MODULE);
	}

	protected void sendErrorMessage(int type, int value) {
		localDebugger("Entering: sendErrorMessage(int type, int value)");

		localLogger("Sending Error Message to" + address.getIPv4Address());

		PCEPMessage errorMessage = PCEPMessageFactory
				.generateSIMPLEErrorMessage(type, value, "1", "0");
		sendMessageToPeer(errorMessage, ModuleEnum.NETWORK_MODULE);
	}

	/**************************************************************************************************
	 * Connect
	 */
	protected void startConnectTimer() {
		localDebugger("Entering: startConnectTimer()");

		localLogger("Starting Connect Timer");

		connectTimerRunning = true;

		connectTimerTask = new TimerTask() {
			public void run() {
				localLogger("ConnectTimer Expired");
				checkPendingStatus();
			}
		};
		stateTimer.schedule(connectTimerTask, connect * 1000);
	}

	protected void restartConnectTimer() {
		localDebugger("Entering: restartConnectTimer()");

		localLogger("Restarting Connect Timer");

		this.connectTimerTask.cancel();
		connectTimerTask = new TimerTask() {
			public void run() {
				localLogger("Connect Timer Expired");
				checkPendingStatus();
			}
		};
		stateTimer.schedule(connectTimerTask, connect * 1000);
	}

	protected void cancelConnectTimer() {
		localDebugger("Entering: cancelConnectTimer()");

		if (connectTimerRunning == true) {
			localLogger("Cancelling Connect Timer");

			this.connectTimerTask.cancel();
			connectTimerRunning = false;
		}

	}

	/**************************************************************************************************
	 * OpenWait
	 */
	protected void startOpenWaitTimer() {
		localDebugger("Entering: startOpenWaitTimer()");

		localLogger("Starting OpenWait Timer");

		openWaitTimerTask = new TimerTask() {
			public void run() {
				localLogger("OpenWaitTimer Expired");
				sendErrorMessage(1, 2);
				// releaseResources();
				closeTCPConnection();
			}
		};
		stateTimer.schedule(openWaitTimerTask, openWait * 1000);

	}

	protected void restartOpenWaitTimer() {
		localDebugger("Entering: restartOpenWaitTimer()");

		localLogger("Restarting OpenWait Timer");

		this.openWaitTimerTask.cancel();
		openWaitTimerTask = new TimerTask() {
			public void run() {
				localLogger("OpenWait Timer Expired");
				sendErrorMessage(1, 2);
				// releaseResources();
				closeTCPConnection();
			}
		};
		stateTimer.schedule(openWaitTimerTask, openWait * 1000);
	}

	protected void cancelOpenWaitTimer() {
		localDebugger("Entering: cancelOpenWaitTimer()");

		localLogger("Cancelling OpenWait Timer");

		this.openWaitTimerTask.cancel();
	}

	/**************************************************************************************************
	 * KeepWait
	 */
	protected void startKeepWaitTimer() {
		localDebugger("Entering: startKeepWaitTimer()");

		localLogger("Starting KeepWait Timer");

		keepWaitTimerTask = new TimerTask() {
			public void run() {
				localLogger("KeepWaitTimer Expired");
				sendErrorMessage(1, 7);
				// releaseResources();
				closeTCPConnection();
			}
		};
		stateTimer.schedule(keepWaitTimerTask, keepWait * 1000);
	}

	protected void restartKeepWaitTimer() {
		localDebugger("Entering: restartKeepWaitTimer()");

		localLogger("Restarting KeepWait Timer");

		this.keepWaitTimerTask.cancel();
		keepWaitTimerTask = new TimerTask() {
			public void run() {
				localLogger("KeepWait Timer Expired");
				sendErrorMessage(1, 7);
				// releaseResources();
				closeTCPConnection();
			}
		};
		stateTimer.schedule(keepWaitTimerTask, keepWait * 1000);
	}

	protected void cancelKeepWaitTimer() {
		localDebugger("Entering: cancelKeepWaitTimer()");

		localLogger("Cancelling KeepWait Timer");

		this.keepWaitTimerTask.cancel();
	}

	/**************************************************************************************************
	 * KeepAlive
	 */
	protected void startKeepAliveTimer() {
		localDebugger("Entering: startKeepAliveTimer()");

		localLogger("Starting KeepAlive Timer");

		keepAliveTimerRunning = true;

		keepAliveTimerTask = new TimerTask() {
			public void run() {
				sendKeepAliveMessage();
			}
		};
		stateTimer.schedule(keepAliveTimerTask, keepAlive * 1000);
	}

	protected void restartKeepAliveTimer() {
		localDebugger("Entering: restartKeepAliveTimer()");

		localLogger("Restarting KeepAlive Timer");

		this.keepAliveTimerTask.cancel();
		keepAliveTimerTask = new TimerTask() {
			public void run() {
				sendKeepAliveMessage();
			}
		};
		stateTimer.schedule(keepAliveTimerTask, keepAlive * 1000);
	}

	protected void cancelKeepAliveTimer() {
		localDebugger("Entering: cancelKeepAliveTimer()");

		localLogger("Cancelling KeepAlive Timer");

		this.keepAliveTimerTask.cancel();
	}

	/**************************************************************************************************
	 * DeadTimer
	 */
	protected void startDeadTimer() {
		localDebugger("Entering: startDeadTimer()");

		localLogger("Starting Dead Timer");

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
		localDebugger("Entering: restartDeadTimer()");

		localLogger("Restarting Dead Timer");
		if (deadTimerRunning == false) {
			localDebugger("DeadTimer was not running. Restarting task");
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
		localDebugger("Entering: cancelDeadTimer()");

		localLogger("Cancelling Dead Timer");

		this.deadTimerTask.cancel();
	}

	public PCEPAddress getAddress() {
		localDebugger("Entering: getAddress()");

		return address;
	}

	public String toString() {
		return "Address: " + address.getIPv4Address() + " sessionID: "
				+ sessionID + " State: " + state + " ConnectionInitialized: "
				+ connectionInitialized;
	}

	protected static void guiLogger(String event) {
		// Logger.logGUINotifications(event);
	}

	protected void localLogger(String event) {

		String prefix = prefixGenerator();
		Logger.logSystemEvents("[StateMachine: " + address.getIPv4Address() +
				"]" + prefix + " " + event);
	}

	protected void localDebugger(String event) {
		// String prefix = prefixGenerator();
		// Logger.debugger("[StateMachine: " + address.getIPv4Address() + "]" +
		// prefix + " " + event);
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

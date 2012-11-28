package com.pcee.architecture.computationmodule.ted;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.global.GlobalCfg;
import com.pcee.architecture.ModuleManagement;
import com.pcee.logger.Logger;

public class TopologyUpdateListener extends Thread {

	private static ModuleManagement lm;

	public TopologyUpdateListener(ModuleManagement lm) {
		TopologyUpdateListener.lm = lm;
	}

	public void run() {
		ServerSocket server;
		try {
			server = new ServerSocket(GlobalCfg.prrPort);
			localLogger("Listening for topology updates");

			while (true) {
				Socket s = server.accept();
				SocketProcessing socketWorker = new SocketProcessing(s);
				socketWorker.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void localLogger(String event) {
		Logger.logSystemEvents("[TopologyUpdateListener]     " + event);
	}

	/**
	 * Function to log debugging events
	 * 
	 * @param event
	 */
	private static void localDebugger(String event) {
		//Logger.debugger("[TopologyUpdateListener]     " + event);
	}

}

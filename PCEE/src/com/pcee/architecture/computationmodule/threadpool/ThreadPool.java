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

package com.pcee.architecture.computationmodule.threadpool;


import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;
import com.graph.graphcontroller.Gcontroller;
import com.pcee.architecture.ModuleManagement;
import com.pcee.architecture.computationmodule.ted.TopologyInformation;
import com.pcee.logger.Logger;
import com.pcee.protocol.message.PCEPMessage;

/**
 * ThreadPool implementation to support multiple path computations, also includes an update server to support topology updates
 * 
 * @author Mohit Chamania
 * @author Marek Drogon
 */
public class ThreadPool {


	// Integer to define the number of threads used
	private int threadCount;

	// boolean to check if the the thread pool has been initialized
	private boolean isInitialized;

	// Map association to store worker threads against their IDs
	private HashMap<String, Worker> threadHashMap;

	// Blocking queue used by workers to read incoming requests
	private LinkedBlockingQueue<PCEPMessage> requestQueue;

	// Graph instance used by workers to perform path computations
	private Gcontroller graph;

	// Module management instance to send response to the computation layer
	private ModuleManagement lm;

	// TopologyInformation used to retrieve topology information from file
	private static TopologyInformation topologyInstance = TopologyInformation.getInstance();

	/**
	 * default Constructor
	 * 
	 * @param layerManagement
	 * @param threadCount
	 * @param requestQueue
	 */
	public ThreadPool(ModuleManagement layerManagement, int threadCount, LinkedBlockingQueue<PCEPMessage> requestQueue) {
		lm = layerManagement;
		this.threadCount = threadCount;
		isInitialized = false;
		this.requestQueue = requestQueue;
		graph = topologyInstance.getGraph();
		// initialize the worker threads
		initThreadPool();
	}

	/**
	 * Function to initialize the thread pool
	 * 
	 * @return
	 */
	private boolean initThreadPool() {
		localLogger("Initializing Thread Pool, size = " + threadCount);
		if (isInitialized == false) {
			threadHashMap = new HashMap<String, Worker>();
			for (int i = 0; i < threadCount; i++) {
				String id = "Thread-" + Integer.toString(i);
				Worker worker = new Worker(lm, this, id, requestQueue, graph);
				worker.setName("WorkerThread-" + i);
				threadHashMap.put(id, worker);
				worker.start();
				localLogger("Worker Thread " + i + " initialized");
			}
			isInitialized = true;
			localDebugger("Thread Pool Initialized");
			return true;
		} else
			return false;
	}

	/**
	 * Function to get the new graph instance
	 * 
	 * @return
	 */
	protected Gcontroller getUpdatedGraph() {
		return topologyInstance.getGraph();
	}



	/** Function to stop the thread pool */
	public void stop() {
		Iterator<String> iter = threadHashMap.keySet().iterator();
		while (iter.hasNext()) {
			String id = iter.next();
			threadHashMap.get(id).setTerminate(true);
			threadHashMap.get(id).interrupt();
		}

	}

	/**
	 * Function to log events inside the thread pool implementation
	 * 
	 * @param event
	 */
	private void localLogger(String event) {
		Logger.logSystemEvents("[ThreadPool]     " + event);
	}

	/**
	 * Function to log debugging events inside the thread pool implementation
	 * 
	 * @param event
	 */
	private void localDebugger(String event) {
		Logger.debugger("[ThreadPool]     " + event);
	}

}

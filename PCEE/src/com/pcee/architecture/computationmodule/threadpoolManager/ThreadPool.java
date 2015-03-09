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

package com.pcee.architecture.computationmodule.threadpoolManager;


import com.pcee.architecture.ModuleManagement;
import com.pcee.architecture.computationmodule.ted.TopoManager;
import com.pcee.protocol.message.PCEPMessage;
import com.topology.primitives.TopologyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * ThreadPool implementation to support multiple path computations, also includes an update server to support topology updates
 * 
 * @author Mohit Chamania
 * @author Marek Drogon
 */
public class ThreadPool {

	private static Logger logger = LoggerFactory.getLogger(ThreadPool.class);
	
	// Integer to define the number of threads used
	private int threadCount;

	// boolean to check if the the thread pool has been initialized
	private boolean isInitialized;

	// Map association to store worker threads against their IDs
	private HashMap<String, Worker> threadHashMap;

	// Blocking queue used by workers to read incoming requests
	private LinkedBlockingQueue<PCEPMessage> requestQueue;

	// Graph instance used by workers to perform path computations
//	private Gcontroller graph;
    private TopologyManager manager;

// Module management instance to send response to the computation layer
	private ModuleManagement lm;

	// TopologyInformation used to retrieve topology information from file
//	private static TopologyInformation topologyInstance = TopologyInformation.getInstance();
    private static TopoManager topologyInstance = TopoManager.get_instance();

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
//		graph = topologyInstance.getGraph();
        manager = topologyInstance.getManager();
		// initialize the worker threads
		initThreadPool();
	}

	/**
	 * Function to initialize the thread pool
	 *
	 * @return
	 */
	private boolean initThreadPool() {
		logger.info("Initializing Thread Pool, size = " + threadCount);
		if (isInitialized == false) {
			threadHashMap = new HashMap<String, Worker>();
			for (int i = 0; i < threadCount; i++) {
				String id = "Thread-" + Integer.toString(i);
//				Worker worker = new Worker(lm, this, id, requestQueue, graph);
                Worker worker = new Worker(lm, this, id, requestQueue);
				worker.setName("WorkerThread-" + i);
				threadHashMap.put(id, worker);
				worker.start();
				logger.info("Worker Thread " + i + " initialized");
			}
			isInitialized = true;
			logger.debug("Thread Pool Initialized");
			return true;
		} else
			return false;
	}

	/**
	 * Function to get the new graph instance
	 * 
	 * @return
	 */
//	protected Gcontroller getUpdatedGraph() {
//		return topologyInstance.getGraph();
//	}

    protected TopologyManager getUpdatedGraph() {
        return topologyInstance.getManager();
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

}

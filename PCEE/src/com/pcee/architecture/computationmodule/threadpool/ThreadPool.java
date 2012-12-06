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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;
import com.graph.graphcontroller.Gcontroller;
import com.graph.graphcontroller.impl.GcontrollerImpl;
import com.graph.topology.importers.ImportTopology;
import com.pcee.architecture.ModuleManagement;
import com.pcee.architecture.computationmodule.ted.TopologyInformationDomain;
import com.pcee.logger.Logger;

/**
 * ThreadPool implementation to support multiple path computations, also includes an update server to support topology updates
 * 
 * @author Mohit Chamania
 * @author Marek Drogon
 */
public class ThreadPool {

    // port used to receive topology updates
    private int port = 1337;

    // Integer to define the number of threads used
    private int threadCount;

    // boolean to check if the the thread pool has been initialized
    private boolean isInitialized;

    // Map association to store worker threads against their IDs
    private HashMap<String, WorkerThread> threadHashMap;

    // Blocking queue used by workers to read incoming requests
    private LinkedBlockingQueue<Request> requestQueue;

    // Module management instance to send response to the computation layer
    private ModuleManagement lm;

    /**
     * default Constructor
     * 
     * @param layerManagement
     * @param threadCount
     * @param requestQueue
     */
    public ThreadPool(ModuleManagement layerManagement, int threadCount, LinkedBlockingQueue<Request> requestQueue) {
	lm = layerManagement;
	this.threadCount = threadCount;
	isInitialized = false;
	this.requestQueue = requestQueue;
	// initialize the worker threads
	initThreadPool();
	// start thread to listen for new topology updates
    }

    /**
     * Function to initialize the thread pool
     * 
     * @return
     */
    private boolean initThreadPool() {
	localLogger("Initializing Thread Pool, size = " + threadCount);
	if (isInitialized == false) {
	    threadHashMap = new HashMap<String, WorkerThread>();
	    for (int i = 0; i < threadCount; i++) {
		String id = "Thread-" + Integer.toString(i);
		WorkerThread worker = new WorkerThread(lm, this, id, requestQueue);
		worker.setName("WorkerThread-" + i);
		threadHashMap.put(id, worker);
		worker.start();
		System.out.println("Worker Thread " + i + " initialized");
	    }
	    isInitialized = true;
	    localDebugger("Thread Pool Initialized");
	    return true;
	} else
	    return false;
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

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
import com.pcee.architecture.computationmodule.ted.TopologyInformation;
import com.pcee.logger.Logger;

/**ThreadPool implementation to support multiple path computations, also includes an update server to support topology updates
 * @author Mohit Chamania
 * @author Marek Drogon
 */
public class ThreadPool {

	//port used to receive topology updates 
	private int port = 1337;

	//Integer to define the number of threads used
	private int threadCount;

	//boolean to check if the the thread pool has been initialized
	private boolean isInitialized;

	//Map association to store worker threads against their IDs
	private HashMap<String, Worker> threadHashMap;

	//Blocking queue used by workers to read incoming requests 
	private LinkedBlockingQueue<Request> requestQueue;

	//Graph instance used by workers to perform path computations
	private Gcontroller graph;

	//Module management instance to send response to the computation layer
	private ModuleManagement lm;

	//TopologyInformation used to retrieve topology information from file 
	private static TopologyInformation topologyInstance = TopologyInformation.getInstance();


	/**default Constructor
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
		graph = topologyInstance.getGraph();
		//initialize the worker threads
		initThreadPool();
		//start thread to listen for new topology updates
		startTopologyUpdateListner();
	}

	/**Function to initialize the thread pool 
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
			}
			isInitialized = true;
			localDebugger("Thread Pool Initialized");
			return true;
		} else
			return false;
	}

	/**Function to get the new graph instance
	 * @return
	 */
	protected Gcontroller getUpdatedGraph() {
		return topologyInstance.getGraph();
	}

	/**Function to update the graph inside the controller
	 * 
	 * @param graph
	 */
	private void updateGraph(Gcontroller graph) {
		topologyInstance.updateGraph(graph);
		this.graph = topologyInstance.getGraph();
		Iterator<String> iter = threadHashMap.keySet().iterator();
		while (iter.hasNext()) {
			String id = iter.next();
			threadHashMap.get(id).interrupt();
		}
	}

	/**Function to initialize a thread to listen for topology updates*/
	private void startTopologyUpdateListner() {
		Thread thread = new Thread() {
			//Override the run() method to implement a simple server socket to listen for topology updates
			public void run() {
				ServerSocket serverSocket;
				try {
					serverSocket = new ServerSocket(port);

					while (true) {
						try {

							Socket clientSocket = serverSocket.accept();
							//Remote connection sends topology in the form of a string with a delimiter "@" used for each line
							BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
							String text = "";
							String line = "";
							while ((line = bufferedReader.readLine()) != null) {
								text = text + line;
							}

							//Convert received string into text array and use importers to get graph instance
							String[] temp = text.split("@");
							ImportTopology importTopology = TopologyInformation.getInstance().getTopologyImporter();
							Gcontroller newGraph = new GcontrollerImpl();
							importTopology.importTopologyFromString(newGraph, temp);

							//update the graph instance in the Thread pool
							updateGraph(newGraph);

							//Close the socket
							bufferedReader.close();
							clientSocket.close();
						} catch (IOException e) {
							localDebugger("IOException during read for new connections. Discarding update");
							continue;
						}
					}
				} catch (IOException e1) {
					localDebugger("Could not open server socket to listen for topology updates on port:" + port);

				}

			}

		};
		thread.setName("ThreadPoolThread");
		thread.start();
	}

	/**Function to stop the thread pool*/
	public void stop(){
		Iterator<String> iter = threadHashMap.keySet().iterator();
		while (iter.hasNext()) {
			String id = iter.next();
			threadHashMap.get(id).setTerminate(true);
			threadHashMap.get(id).interrupt();
		}

	}

	
	/**Function to log events inside the thread pool implementation
	 * 
	 * @param event
	 */
	private void localLogger(String event) {
		Logger.logSystemEvents("[ThreadPool]     " + event);
	}

	/**Function to log debugging events inside the thread pool implementation 
	 * 
	 * @param event
	 */
	private void localDebugger(String event) {
		Logger.debugger("[ThreadPool]     " + event);
	}

}

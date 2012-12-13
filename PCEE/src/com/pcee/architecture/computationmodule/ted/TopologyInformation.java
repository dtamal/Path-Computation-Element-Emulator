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

package com.pcee.architecture.computationmodule.ted;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.graph.elements.edge.EdgeElement;
import com.graph.elements.edge.params.EdgeParams;
import com.graph.elements.edge.params.impl.BasicEdgeParams;
import com.graph.graphcontroller.Gcontroller;
import com.graph.graphcontroller.impl.GcontrollerImpl;
import com.graph.topology.importers.ImportTopology;
import com.graph.topology.importers.impl.BRITEImportTopology;
import com.graph.topology.importers.impl.SNDLibImportTopology;
import com.pcee.logger.Logger;

/**
 * Class to provide Topology Instances to the computation layer
 * 
 * @author Marek Drogon
 * 
 */
public class TopologyInformation {


	//JSON Parser
	private static Gson json = new Gson();

	//Port on which to listen for topology Information Updates
	private static int topologyUpdatePort = 5555;

	//Thread for topology Update Listener
	private static Thread topologyUpdateThread;

	// Static oject instance of the TopologyInformation Class
	static private TopologyInformation _instance;

	// Graph Instance
	private Gcontroller graph;

	// Topology Importer used to populate the graph instance
	private static ImportTopology topology;

	// path to the topology description file
	private static String topoPath = ".//atlanta.txt";


	/**Function to set the port for topology Updates
	 *
	 * @param port
	 */
	public static void setTopologyUpdatePort(int port){
		topologyUpdatePort = port;
	}

	/**
	 * @param input
	 *            the topoPath to be used by the TED
	 */
	public static void setTopoPath(String input) {
		topoPath = input;
	}

	/**
	 * @param importer
	 *            ONLY SNDLib or BRITE supported as input
	 */
	public static void setImporter(String importer) {
		if (importer.equals("SNDLib")) {
			topology = new SNDLibImportTopology();
		} else if (importer.equals("BRITE")) {
			topology = new BRITEImportTopology();
		} else {
			topology = new SNDLibImportTopology();
		}
	}

	/** default constructor */
	private TopologyInformation() {
		topology = new SNDLibImportTopology();
		graph = new GcontrollerImpl();

		// Source file used to instantiate the topology
		File file = new File(topoPath);

		// Function to import the topology stored in the text file into the
		// graph object
		topology.importTopology(graph, file.getAbsolutePath());
		if (graph == null)
			localDebugger("Error in loading graph from file");
		else
			localLogger("NetworkSize: " + networkSize());

		//Start Topology Update Listener
		localLogger ("Starting thread to listen for topology updates on port " + topologyUpdatePort);
		startTopologyUpdateListner();
	}

	/**
	 * Function to update the graph instance used inside the Topology
	 * Information object
	 * 
	 * @param newGraph
	 */
	public synchronized void updateGraph(Gcontroller newGraph) {
		graph = newGraph;
	}

	/** Function to determine the network size */
	public int networkSize() {
		return graph.getVertexSet().size();
	}

	/** Function to get the instance of the TopologyInformation class */
	public static TopologyInformation getInstance() {
		if (_instance == null)
			_instance = new TopologyInformation();
		return _instance;
	}

	/** Function to get the graph object used */
	public Gcontroller getGraph() {
		return graph;
	}

	/**
	 * Function to get the topology importer used in the implementation
	 * 
	 * @return topology importer
	 */
	public ImportTopology getTopologyImporter() {
		return topology;
	}

	/**
	 * Function for logging events
	 * 
	 * @param event
	 */
	private void localLogger(String event) {
		// Logger.logSystemEvents("[TopologyInformation]     " + event);
	}

	/**
	 * Function for logging debug information
	 * 
	 * @param event
	 */
	private void localDebugger(String event) {
		// Logger.debugger("[TopologyInformation]     " + event);
	}

	/**
	 * test case
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(new File(topoPath).getAbsolutePath());
	}



	/** Function to initialize a thread to listen for topology updates */
	private void startTopologyUpdateListner() {
		topologyUpdateThread = new Thread() {

			//Function to parse and implement incoming topology Updates
			@SuppressWarnings("rawtypes")
			public void parseInput(String text) {
				try {
					Map input = json.fromJson(text, Map.class);
					if (input.containsKey("operation")) {
						if (input.get("operation").toString().equalsIgnoreCase("reserve")) {
							//Request to reserve capacity on a sequence of nodes
							double capacity = Double.parseDouble(input.get("capacity").toString());
							ArrayList vertexSequence = ((ArrayList)input.get("vertexSequence"));
							synchronized(graph) {
								for (int i=0;i<vertexSequence.size()-1;i++){
									String sourceID = (String)vertexSequence.get(i);
									String destID = (String)vertexSequence.get(i+1);
									if (graph.aConnectingEdge(sourceID, destID)) {
										if (graph.getConnectingEdge(sourceID, destID).getEdgeParams().reserveCapacity(capacity)) {
											localLogger("Cannot reserve requested capacity between " + sourceID +" and " + destID);
											for (int j=0;j<i;j++) {
												//Releasing capacity that was reserved till before i
												String srcID = (String)vertexSequence.get(j);
												String dstID = (String)vertexSequence.get(j+1);
												graph.getConnectingEdge(srcID, dstID).getEdgeParams().releaseCapacity(capacity);
											}
											break;

										}
									} else {
										localLogger("Invalid Vertex Sequence sent, no edge found between " + sourceID +" and " + destID);
										for (int j=0;j<i;j++) {
											//Releasing capacity that was reserved till before i
											String srcID = (String)vertexSequence.get(j);
											String dstID = (String)vertexSequence.get(j+1);
											graph.getConnectingEdge(srcID, dstID).getEdgeParams().releaseCapacity(capacity);
										}
										break;
									}
								}
							}

						} else if (input.get("operation").toString().equalsIgnoreCase("release")) {
							//Request to reserve capacity on a sequence of nodes
							double capacity = Double.parseDouble(input.get("capacity").toString());
							ArrayList vertexSequence = ((ArrayList)input.get("vertexSequence"));
							synchronized(graph) {
								for (int i=0;i<vertexSequence.size()-1;i++){
									String sourceID = (String)vertexSequence.get(i);
									String destID = (String)vertexSequence.get(i+1);
									if (graph.aConnectingEdge(sourceID, destID)) {
										if (!graph.getConnectingEdge(sourceID, destID).getEdgeParams().releaseCapacity(capacity)) {
											localLogger("Cannot release additional capacity between " + sourceID +" and " + destID);
											for (int j=0;j<i;j++) {
												//Releasing capacity that was reserved till before i
												String srcID = (String)vertexSequence.get(j);
												String dstID = (String)vertexSequence.get(j+1);
												graph.getConnectingEdge(srcID, dstID).getEdgeParams().reserveCapacity(capacity);
											}
											break;

										}
									} else {
										localLogger("Invalid Vertex Sequence sent, no edge found between " + sourceID +" and " + destID);
										for (int j=0;j<i;j++) {
											//Releasing capacity that was reserved till before i
											String srcID = (String)vertexSequence.get(j);
											String dstID = (String)vertexSequence.get(j+1);
											graph.getConnectingEdge(srcID, dstID).getEdgeParams().reserveCapacity(capacity);
										}
										break;
									}
								}
							}

						} else 	if (input.get("operation").toString().equalsIgnoreCase("updateEdgeDefinition")) {
							//Request to update the Edge Definition
							double capacity = Double.parseDouble(input.get("capacity").toString());
							double avcapacity = Double.parseDouble(input.get("avcapacity").toString());
							double weight = Double.parseDouble(input.get("weight").toString());
							double delay = Double.parseDouble(input.get("delay").toString());
							ArrayList vSequence = ((ArrayList)input.get("vertexSequence"));
							synchronized(graph) {
								ArrayList<String> vertexSequence = new ArrayList<String>();
								for (int i=0;i<vSequence.size();i++) {
									vertexSequence.add(vSequence.get(i).toString());
								}
								String sourceID = vertexSequence.get(0);
								String destID = vertexSequence.get(vertexSequence.size()-1);
								if (graph.aConnectingEdge(sourceID, destID)) {
									EdgeElement edge = graph.getConnectingEdge(sourceID, destID);
									EdgeParams params = new BasicEdgeParams(edge, delay, weight, capacity);
									params.setAvailableCapacity(avcapacity);
									edge.setEdgeParams(params);
									localLogger("Updated Edge from " + sourceID + " to " + destID);
								}


							}

						}
					}
				} catch (JsonSyntaxException e) {
					localLogger("Malformed Json sent from Client" + e.getMessage());
				} catch (Exception e) {
					localLogger("Invalid parameters sent in the JSON from Client : " + e.getMessage());

				}

			}


			// Override the run() method to implement a simple server socket to listen for topology updates
			public void run() {
				ServerSocket serverSocket;
				try {
					serverSocket = new ServerSocket(topologyUpdatePort);

					while (true) {
						try {
							Socket clientSocket = serverSocket.accept();
							// Remote connection sends topology in the form of a string with a delimiter "@" used for each line
							BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
							String text = "";
							String line = "";
							while ((line = bufferedReader.readLine()) != null) {
								text = text + line;
							}
							parseInput(text);
							// Close the socket
							bufferedReader.close();
							clientSocket.close();
						} catch (IOException e) {
							localDebugger("IOException during read for new connections. Discarding update");
							continue;
						} 
					}
				} catch (IOException e1) {
					localDebugger("Could not open server socket to listen for topology updates on port:" + topologyUpdatePort);

				}

			}

		};
		topologyUpdateThread.setName("TopologyUpdateThread");
		topologyUpdateThread.start();
	}
}

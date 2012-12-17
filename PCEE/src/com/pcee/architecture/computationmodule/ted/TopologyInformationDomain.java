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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.graph.elements.edge.EdgeElement;
import com.graph.elements.edge.params.EdgeParams;
import com.graph.elements.edge.params.impl.PathElementEdgeParams;
import com.graph.elements.vertex.VertexElement;
import com.graph.graphcontroller.Gcontroller;
import com.graph.graphcontroller.impl.GcontrollerImpl;
import com.graph.path.PathElement;
import com.graph.path.algorithms.PathComputationAlgorithm;
import com.graph.path.algorithms.constraints.Constraint;
import com.graph.path.algorithms.constraints.impl.SimplePathComputationConstraint;
import com.graph.path.algorithms.impl.MaxBandwidthShortestPathComputationAlgorithm;
import com.graph.topology.importers.ImportTopology;
import com.graph.topology.importers.impl.BRITEImportTopology;
import com.graph.topology.importers.impl.SNDLibImportTopology;
import com.pcee.architecture.computationmodule.ted.client.TopologyUpdateParentClient;
import com.pcee.logger.Logger;

/**
 * Class to provide Topology Instances to the computation layer
 * 
 * @author Marek Drogon
 * 
 */
public class TopologyInformationDomain {

	//JSON Parser
	private static Gson json = new Gson();

	//Port on which to listen for topology Information Updates
	private static int topologyUpdatePort = 5555;

	//Port on which to Parent PCE listens for topology Information Updates
	private static int topologyUpdateParentPort = 5555;

	//Port on which to Parent PCE listens for topology Information Updates
	private static String topologyUpdateParentIP = "127.0.0.1";

	
	// Static oject instance of the TopologyInformation Class
	static private TopologyInformationDomain _instance;

	// Graph Instance
	private Gcontroller graph;

	// Set with list of Border Nodes (IDs)
	private Set<String> bnID;

	// Topology Importer used to populate the graph instance
	private static ImportTopology topology;

	// path to the topology description file
	private static String topoPath = "atlantaDomain1.txt";

	//path to the file with Border Node information 
	private static String bnPath = "atlantaBNDomain1.txt";

	//Graph Controller for the virtual Graph of the domain
	private Gcontroller virtualGraph;


	
	/** Function to set the port on which to send topology Updates to the Parent PCE
	 * 
	 * @param topologyUpdateParentPort
	 */
	public static void setTopologyUpdateParentPort(int topologyUpdateParentPort) {
		TopologyInformationDomain.topologyUpdateParentPort = topologyUpdateParentPort;
	}

	/**IP address on which to send the topology update to the Parent PCE
	 * 
	 * @param topologyUpdateParentIP
	 */
	public static void setTopologyUpdateParentIP(String topologyUpdateParentIP) {
		TopologyInformationDomain.topologyUpdateParentIP = topologyUpdateParentIP;
	}

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


	/** Function to set the list of border nodes to be used for path computation
	 * 
	 * @param input The location of the file with the list of border nodes
	 */
	public static void setBnListPath(String input) {
		bnPath = input;
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

	/** Function to import the border node information from the file with the border nodes */
	private void importBorderNodes(){
		bnID = new HashSet<String>();
		try {
			BufferedReader reader = new BufferedReader (new FileReader(bnPath));
			String temp;
			while((temp=reader.readLine())!=null){
				String y = temp.trim();
				if (graph.vertexExists(y)) {
					bnID.add(y);
				} else {
					localLogger("Border Node " + y + " not found in the topology check topology information");
				}
			}
			localLogger("Border Node Import Complete : Total Border Nodes = " + bnID.size());
			reader.close();
		} catch (FileNotFoundException e) {
			localLogger("Could not Initialize the Border Nodes - File Not Found : " + e.getMessage());
		} catch (IOException e) {
			localLogger("Could not Initialize the Border Nodes - IO Exception when reading file : " + e.getMessage());
		}

	}

	/** default constructor */
	private TopologyInformationDomain() {

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

		//Function to import the border nodes into the TopologyInformation function
		importBorderNodes();

		//Function to generate the virtualGraph of the topology
		generateVirtualGraph();
		
		//startTopologyUpdateListner
		startTopologyUpdateListner();
	}

	/** Function to generate the virtual Graph for the domain
	 * 
	 */
	private void generateVirtualGraph() {
		virtualGraph = new GcontrollerImpl();
		Iterator <String> iter =  bnID.iterator();
		while(iter.hasNext()) {
			String temp = iter.next();	
			//Get the vertex from the original graph
			VertexElement x = graph.getVertex(temp);
			//Add the vertex to the virtual graph
			virtualGraph.addVertex(new VertexElement (temp, virtualGraph, x.getXCoord(), x.getYCoord()));
		}
		//Compute maximum bandwidth path between all vertex pairs and add them as edges in the domain topology
		String[] vertices = bnID.toArray(new String[1]);
		PathComputationAlgorithm algo = new MaxBandwidthShortestPathComputationAlgorithm();
		for (int i=0;i<vertices.length-1;i++) {
			for (int j=i+1; j< vertices.length; j++) {
				Constraint constr = new SimplePathComputationConstraint (graph.getVertex(vertices[i]), graph.getVertex(vertices[j]));
				PathElement temp = algo.computePath(graph, constr);
				if (temp != null) {
					EdgeElement edge = new EdgeElement(vertices[i] + "-" + vertices[j], graph.getVertex(vertices[i]), graph.getVertex(vertices[j]), virtualGraph);
					EdgeParams params = new PathElementEdgeParams(edge, temp);
					edge.setEdgeParams(params);
					virtualGraph.addEdge(edge);
					
					//Update the virtual Topology of the Parent
					TopologyUpdateParentClient.updateEdge(topologyUpdateParentIP, topologyUpdateParentPort, temp);
				}
			}
		}

	}


	/**
	 * Function to update the graph instance used inside the Topology
	 * Information object
	 * 
	 * @param newGraph
	 */
	public synchronized void updateGraph(Gcontroller newGraph) {
		graph = newGraph;
		generateVirtualGraph();
	}

	/**
	 * Function to update the Border Node Instance used inside the Topology
	 * Information object
	 * 
	 * @param newBnId
	 */
	public synchronized void updateBorderNodes(Set<String> newBnID) {
		bnID = newBnID;
	}


	/** Function to determine the network size */
	public int networkSize() {
		return graph.getVertexSet().size();
	}

	/** Function to get the instance of the TopologyInformation class */
	public static TopologyInformationDomain getInstance() {
		if (_instance == null)
			_instance = new TopologyInformationDomain();
		return _instance;
	}

	/** Function to get the graph object used */
	public Gcontroller getGraph() {
		return graph;
	}

	/** Function to get the list of border nodes used */
	public Set<String> getBorderNodes(){
		return bnID;
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
		TopologyInformationDomain info = TopologyInformationDomain.getInstance();

		Iterator<EdgeElement> iter = info.virtualGraph.getEdgeSet().iterator();
		while(iter.hasNext()) {
			System.out.println(iter.next().getEdgeID());
		}

		iter = info.virtualGraph.getEdgeSet().iterator();
		while(iter.hasNext()) {
			EdgeElement y = iter.next();
			EdgeParams x = y.getEdgeParams();
			Iterator<String> iter1 = x.getVertexSequence(y.getDestinationVertex().getVertexID(), y.getSourceVertex().getVertexID()).iterator();
			System.out.println(y.getDestinationVertex().getVertexID() + "-" + y.getSourceVertex().getVertexID());
			while(iter1.hasNext())
				System.out.println("\t" + iter1.next());
		}

		//		System.out.println(new File(topoPath).getAbsolutePath());
	}


	/** Function to initialize a thread to listen for topology updates */
	private void startTopologyUpdateListner() {
		Thread thread = new Thread() {

			//Function to parse and implement incoming topology Updates
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
										if (!graph.getConnectingEdge(sourceID, destID).getEdgeParams().reserveCapacity(capacity)) {
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
							
						} else 	if (input.get("operation").toString().equalsIgnoreCase("release")) {
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
							
						} else if (input.get("operation").toString().equalsIgnoreCase("updateVirtualTopologyBandwidth")) {
							Iterator<EdgeElement> iter = virtualGraph.getEdgeSet().iterator();
							while(iter.hasNext()) {
								PathElementEdgeParams temp = (PathElementEdgeParams) iter.next().getEdgeParams();
								TopologyUpdateParentClient.updateEdge(topologyUpdateParentIP, topologyUpdateParentPort, temp.getPath());
							}
						} else if (input.get("operation").toString().equalsIgnoreCase("recomputeVirtualTopology")) {
							//Computes a new virtual Graph 
							//Update to parent sent within the generation code
							generateVirtualGraph();
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
		thread.setName("ThreadPoolThread");
		thread.start();
	}
}

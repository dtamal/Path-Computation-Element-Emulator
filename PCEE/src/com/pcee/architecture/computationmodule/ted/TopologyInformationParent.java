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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.graph.elements.edge.EdgeElement;
import com.graph.elements.edge.params.EdgeParams;
import com.graph.elements.edge.params.impl.ParentVirtualLinkEdgeParams;
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
import com.pcee.logger.Logger;
import com.pcee.protocol.message.objectframe.impl.erosubobjects.PCEPAddress;

/**
 * Class to provide Topology Instances to the computation layer
 * 
 * @author Marek Drogon
 * 
 */
public class TopologyInformationParent {

	//JSON Parser
	private static Gson json = new Gson();
	
	//Port on which to listen for topology Information Updates
	private static int topologyUpdatePort = 5555;
	
	// Static oject instance of the TopologyInformation Class
	static private TopologyInformationParent _instance;

	// Graph Instance
	private Gcontroller graph;

	//HashMap to indicate the set of border nodes associated with a domain ID
	private HashMap<String, Set<String>> domainBnMapping;
	
	//HashMap to indicate the domain ID associated with a node address
	private HashMap<String, String> nodeDomainMapping;
	
	//HashMap to indicate the location of the Domain PCE servers based on the domain ID
	private HashMap<String, PCEPAddress> domainPCEinfo;

	// Topology Importer used to populate the graph instance
	private static ImportTopology topology;

	// path to the topology description file
	private static String topoPath = ".//atlantaDomain1.txt";

	// path to the topology description file
	private static String multiDomainInfoPath = ".//multiDomainInfo.txt";

	
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
	
	/** Function to set the path of the multi-domain configuration file
	 * 
	 * @param input location of the file
	 */
	public static void setMultiDomainInfoPath (String input) {
		multiDomainInfoPath = input;
	}

	
	
	/**Function to parse the multi-domain topology file and extract information about the border nodes associated with a domain
	 */
	private void parseDomainBnMapping() {
		try {
			BufferedReader reader = new BufferedReader ( new FileReader(multiDomainInfoPath));
			String temp;
			int flag=0;
			while((temp=reader.readLine())!=null) {
				if (temp.trim().compareTo("BORDERNODES (")==0) {
					flag=1;
					break;
				}
			}
			if (flag==0) {
				localLogger("Incorrect Multi-Domian Information File");
				System.exit(0);
			}
			domainBnMapping = new HashMap<String, Set<String>>();
			while((temp=reader.readLine())!=null) {
				//End Loop when parsing of block is complete
				temp = temp.trim();
				if (temp.compareTo(")")==0) 
					break;
				//Not an empty line
				if (temp.length()!=0) {
					String[] temp1 = temp.split(":");
					if (temp1.length==2) {
						temp1[1] = temp1[1].trim();
						temp1[0] = temp1[0].trim();
						if (domainBnMapping.containsKey(temp1[1])) {
							domainBnMapping.get(temp1[1]).add(temp1[0]);
						}
						else {
							Set <String> temp2 = new HashSet<String>();
							temp2.add(temp1[0]);
							domainBnMapping.put(temp1[1], temp2);
						}
					}
				}
			}	
			reader.close();
			
		} catch (FileNotFoundException e) {
			localLogger ("Multi-domain Info File not found : " + e.getMessage());
		} catch (IOException e) {
			localLogger ("IOException when reading from Multi-DOmain Info File : " + e.getMessage());
		}
	}

	
	
	
	/**Function to parse the multi-domain topology file and extract information about the nodes in the topology and the corresponding domains
	 */
	private void parseNodeDomainMapping() {
		try {
			BufferedReader reader = new BufferedReader ( new FileReader(multiDomainInfoPath));
			String temp;
			int flag=0;
			while((temp=reader.readLine())!=null) {
				if (temp.trim().compareTo("NODES (")==0) {
					flag=1;
					break;
				}
			}
			if (flag==0) {
				localLogger("Incorrect Multi-Domian Information File");
				System.exit(0);
			}
			nodeDomainMapping = new HashMap<String, String>();
			while((temp=reader.readLine())!=null) {
				//End Loop when parsing of block is complete
				temp = temp.trim();
				if (temp.compareTo(")")==0) 
					break;
				//Not an empty line
				if (temp.length()!=0) {
					String[] temp1 = temp.split(":");
					if (temp1.length==2) {
						nodeDomainMapping.put(temp1[0].trim(), temp1[1].trim());
					}
				}
			}	
			reader.close();
			
		} catch (FileNotFoundException e) {
			localLogger ("Multi-domain Info File not found : " + e.getMessage());
		} catch (IOException e) {
			localLogger ("IOException when reading from Multi-DOmain Info File : " + e.getMessage());
		}
		

	}
	
	
	
	/**Function to parse the multi-domain topology file and extract information about the Domain PCEs
	 */
	private void parseDomainPCEInfo(){
		try {
			BufferedReader reader = new BufferedReader ( new FileReader(multiDomainInfoPath));
			String temp;
			int flag=0;
			while((temp=reader.readLine())!=null) {
				if (temp.trim().compareTo("DOMAINPCE (")==0) {
					flag=1;
					break;
				}
			}
			if (flag==0) {
				localLogger("Incorrect Multi-Domian Information File");
				System.exit(0);
			}
			domainPCEinfo = new HashMap<String, PCEPAddress>();
			while((temp=reader.readLine())!=null) {
				temp = temp.trim();
				//End Loop when parsing of block is complete
				if (temp.compareTo(")")==0) 
					break;
				//Not an empty line
				if (temp.length()!=0) {
					String[] temp1 = temp.split(":");
					if (temp1.length==3) {
						domainPCEinfo.put(temp1[0].trim(), new PCEPAddress(temp1[1].trim(), Integer.parseInt(temp1[2].trim())));
					}
				}
			}	
			reader.close();
			
		} catch (FileNotFoundException e) {
			localLogger ("Multi-domain Info File not found : " + e.getMessage());
		} catch (IOException e) {
			localLogger ("IOException when reading from Multi-DOmain Info File : " + e.getMessage());
		}
		
	}
	
	private void parseMultiDomainInfo(){
		parseDomainPCEInfo();
		parseNodeDomainMapping();
		parseDomainBnMapping();
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
	private TopologyInformationParent() {

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
		parseMultiDomainInfo();

		//startTopologyUpdateListner
		startTopologyUpdateListner();

	}

	
	/**Function to get the domain ID associated with a node 
	 * 
	 * @param nodeID IP address of the node for which the domain should be looked up
	 * @return Corresponding Domain ID, or null value if mapping does not exist
	 */
	public String getDomainID(String nodeID){
		if (nodeDomainMapping.containsKey(nodeID)){
			return nodeDomainMapping.get(nodeID);
		}
		else
			return null;
	}

	
	/**Function to get the PCE associated with a particular Domain ID
	 * 
	 * @param domainID Domain ID for which the PCE address is request
	 * @return PCEPAddress object for the corresponding domain, or null if mapping does not exist
	 */
	public PCEPAddress getDomainPCEAddress(String domainID) {
		if (domainPCEinfo.containsKey(domainID)) {
			return domainPCEinfo.get(domainID);
		} else
			return null;
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
	public static TopologyInformationParent getInstance() {
		if (_instance == null)
			_instance = new TopologyInformationParent();
		return _instance;
	}

	/** Function to get the graph object used */
	public Gcontroller getGraph() {
		return graph;
	}

	/** Function to get the list of border nodes used */
	public Set<String> getBorderNodes(String bnID){
		if (domainBnMapping.containsKey(bnID))
			return domainBnMapping.get(bnID);
		else
			return null;
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
		Logger.logSystemEvents("[TopologyInformation]     " + event);
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
		TopologyInformationParent info = TopologyInformationParent.getInstance();
		

		
		Iterator<String> iter = info.domainPCEinfo.keySet().iterator();
		while(iter.hasNext()) {
			String key = iter.next();
			System.out.println(key + "\t" + info.domainPCEinfo.get(key).getIPv4Address());
		}

		iter = info.nodeDomainMapping.keySet().iterator();
		while(iter.hasNext()) {
			String key = iter.next();
			System.out.println(key + "\t" + info.nodeDomainMapping.get(key));
		}

		iter = info.domainBnMapping.keySet().iterator();
		while(iter.hasNext()) {
			String key = iter.next();
			System.out.println("Domain = " + key);
			Iterator<String> iter1 = info.getBorderNodes(key).iterator();
			while(iter1.hasNext()){
				System.out.println(iter1.next());
			}
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
						if (input.get("operation").toString().equalsIgnoreCase("updateEdgeDefinition")) {
							//Request to update the Edge Definition
							double capacity = Double.parseDouble(input.get("capacity").toString());
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
									EdgeParams params = new ParentVirtualLinkEdgeParams(edge, delay, weight, capacity, vertexSequence);
									edge.setEdgeParams(params);
									localLogger("Updated Edge New Edge = " + edge.getEdgeParams().getVertexSequence(edge.getSourceVertex().getVertexID(), edge.getDestinationVertex().getVertexID()));
								}
								
								
							}
							
						}else if (input.get("operation").toString().equalsIgnoreCase("reserve")) {
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
							localLogger("Received Topology Update : " + text);
							//read the text till the 
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

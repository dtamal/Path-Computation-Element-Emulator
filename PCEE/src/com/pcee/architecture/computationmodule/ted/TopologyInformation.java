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

import java.io.File;

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

	// Static oject instance of the TopologyInformation Class
	static private TopologyInformation _instance;

	// Graph Instance
	private Gcontroller graph;

	// Topology Importer used to populate the graph instance
	private static ImportTopology topology;

	// path to the topology description file
	private static String topoPath = ".//atlanta.txt";

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
}

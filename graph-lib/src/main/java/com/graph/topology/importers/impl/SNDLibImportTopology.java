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

package com.graph.topology.importers.impl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.graph.elements.edge.EdgeElement;
import com.graph.elements.edge.params.EdgeParams;
import com.graph.elements.edge.params.impl.BasicEdgeParams;
import com.graph.elements.vertex.VertexElement;
import com.graph.graphcontroller.Gcontroller;
import com.graph.logger.GraphLogger;
import com.graph.topology.importers.ImportTopology;

public class SNDLibImportTopology extends ImportTopology {

	private static final String classIdentifier = "SNDLibImportTopology";

	@Override
	public void importTopology(Gcontroller graph, String filename) {
		// add vertices to the graph
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			String temp;
			VertexElement vertex1, vertex2;

			// Read till we get to Node definition)
			while ((temp = reader.readLine()).trim().compareTo("NODES (") != 0) {
			}

			// read till we reach the end of node definitions
			while ((temp = reader.readLine()) != null) {
				temp = temp.trim();
				// System.out.println(temp);
				// if (temp.length()==1){
				// break;
				// }
				if (temp.trim().compareTo(")") == 0) {
					break;
				}

				Pattern p;
				Matcher m;

				String sourceID = "";
				p = Pattern.compile("[a-zA-Z0-9\\.]+");
				m = p.matcher(temp);
				if (m.find()) {
					sourceID = m.group(0);
				}

				// p = Pattern.compile("[0-9\\.]+");
				// m = p.matcher(temp);
				double[] temp1 = new double[2];
				int count = 0;
				while (m.find()) {
					temp1[count] = Double.parseDouble(m.group(0));
					count++;
					if (count == 2)
						break;
				}

				vertex1 = new VertexElement(sourceID, graph, temp1[0], temp1[1]);
				graph.addVertex(vertex1);
				// System.out.println("Vertex Added: VertexID=" +
				// vertex1.getVertexID()+ ", X=" + vertex1.getXCoord() + ", Y="
				// + vertex1.getYCoord());
			}

			// Read till we get to Edge definition)
			while ((temp = reader.readLine()).trim().compareTo("LINKS (") != 0) {
			}

			// read till we reach the end of the edge definition
			while ((temp = reader.readLine()) != null) {
				temp = temp.trim();
				if (temp.length() == 1) {
					break;
				}

				Pattern p;
				Matcher m;

				p = Pattern.compile("[a-zA-Z0-9\\.]+");
				m = p.matcher(temp);
				String[] temp1 = new String[3];
				int count = 0;
				while (m.find()) {
					temp1[count] = m.group(0);
					count++;
					if (count == 3)
						break;
				}

				vertex1 = graph.getVertex(temp1[1]);
				vertex2 = graph.getVertex(temp1[2]);

				EdgeElement edge = new EdgeElement(temp1[0], vertex1, vertex2,
						graph);

				// System.out.println("Edge Added: Edge ID=" + edge.getEdgeID()
				// + ", sourceID=" + vertex1.getVertexID() +
				// ", destinationID = " + vertex2.getVertexID());
				// Compute delay using X and Y Coords from Vertices
				double distance = Math.sqrt(Math.pow(vertex1.getXCoord()
						- vertex2.getXCoord(), 2)
						+ Math
								.pow(vertex1.getYCoord() - vertex2.getYCoord(),
										2));

				double delay = distance / 29.9792458; // (in ms)
				// @TODO import parameters for link weight and delay from brite
				EdgeParams params = new BasicEdgeParams(edge, delay, 1, 40);
				edge.setEdgeParams(params);
				graph.addEdge(edge);
			}
			reader.close();

			// Iterator <VertexElement> iter = graph.getVertexSet().iterator();
			// while(iter.hasNext()){
			// iter.next().updateNeighbourMap();
			// }

		} catch (FileNotFoundException e) {
			GraphLogger.logError(
					"The file " + filename + " could not be found",
					classIdentifier);
			e.printStackTrace();
		} catch (IOException e) {
			GraphLogger.logError("IO Exception while reading file ",
					classIdentifier);
			e.printStackTrace();
		}

	}

	@Override
	public void importTopologyFromString(Gcontroller graph, String[] topology) {
		// TODO Auto-generated method stub
		// add vertices to the graph
		String temp;
		VertexElement vertex1, vertex2;

		int counter = 0;
		int flag = 0;
		while (counter < topology.length) {
			if (topology[counter].trim().compareTo("NODES (") == 0) {
				flag = 1;
				break;
			}
			counter++;
		}
		if (flag == 0) {
			GraphLogger.logError("Invalid Topology Information",
					classIdentifier);
			System.exit(-1);
		}
		counter++;
		// read till we reach the end of node definitions
		while (counter < topology.length) {
			temp = topology[counter].trim();
			// System.out.println(temp);
			// if (temp.length()==1){
			// break;
			// }
			if (temp.trim().compareTo(")") == 0) {
				break;
			}

			Pattern p;
			Matcher m;

			String sourceID = "";
			p = Pattern.compile("[a-zA-Z0-9\\.]+");
			m = p.matcher(temp);
			if (m.find()) {
				sourceID = m.group(0);
			}

			// p = Pattern.compile("[0-9\\.]+");
			// m = p.matcher(temp);
			double[] temp1 = new double[2];
			int count = 0;
			while (m.find()) {
				temp1[count] = Double.parseDouble(m.group(0));
				count++;
				if (count == 2)
					break;
			}

			vertex1 = new VertexElement(sourceID, graph, temp1[0], temp1[1]);
			graph.addVertex(vertex1);
			System.out.println("Vertex Added: VertexID="
					+ vertex1.getVertexID() + ", X=" + vertex1.getXCoord()
					+ ", Y=" + vertex1.getYCoord());
			counter++;
		}

		// Read till we get to Edge definition)
		flag = 0;
		while (counter < topology.length) {
			if (topology[counter].trim().compareTo("LINKS (") == 0) {
				flag = 1;
				break;
			}
			counter++;
		}

		if (flag == 0) {
			GraphLogger.logError("Invalid Topology Information",
					classIdentifier);
			System.exit(-1);
		}

		counter++;
		// read till we reach the end of the edge definition
		while (counter < topology.length) {
			temp = topology[counter].trim();
			if (temp.length() == 1) {
				break;
			}

			Pattern p;
			Matcher m;

			p = Pattern.compile("[a-zA-Z0-9\\.]+");
			m = p.matcher(temp);
			String[] temp1 = new String[3];
			int count = 0;
			while (m.find()) {
				temp1[count] = m.group(0);
				count++;
				if (count == 3)
					break;
			}

			vertex1 = graph.getVertex(temp1[1]);
			vertex2 = graph.getVertex(temp1[2]);

			EdgeElement edge = new EdgeElement(temp1[0], vertex1, vertex2,
					graph);

			System.out.println("Edge Added: Edge ID=" + edge.getEdgeID()
					+ ", sourceID=" + vertex1.getVertexID()
					+ ", destinationID = " + vertex2.getVertexID());
			// Compute delay using X and Y Coords from Vertices
			double distance = Math.sqrt(Math.pow(vertex1.getXCoord()
					- vertex2.getXCoord(), 2)
					+ Math.pow(vertex1.getYCoord() - vertex2.getYCoord(), 2));

			double delay = distance / 29.9792458; // (in ms)
			// @TODO import parameters for link weight and delay from brite
			EdgeParams params = new BasicEdgeParams(edge, delay, 1, 40);
			edge.setEdgeParams(params);
			graph.addEdge(edge);
			counter++;
		}

		// Iterator <VertexElement> iter = graph.getVertexSet().iterator();
		// while(iter.hasNext()){
		// iter.next().updateNeighbourMap();
		// }

	}

}

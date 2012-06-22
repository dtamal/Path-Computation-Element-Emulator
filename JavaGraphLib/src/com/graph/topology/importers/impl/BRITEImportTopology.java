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


import com.graph.elements.edge.EdgeElement;
import com.graph.elements.edge.params.EdgeParams;
import com.graph.elements.edge.params.impl.BasicEdgeParams;
import com.graph.elements.vertex.VertexElement;
import com.graph.graphcontroller.Gcontroller;
import com.graph.logger.GraphLogger;
import com.graph.topology.importers.ImportTopology;


public class BRITEImportTopology extends ImportTopology{
	private static final String classIdentifier = "BRITEImportTopology";

	public void importTopology(Gcontroller graph, String filename) {
		//add vertices to the graph
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			String temp;
			VertexElement vertex1, vertex2;

			int nodes=0;

			//Read till we get to Node definition)
			while ((temp=reader.readLine())!=null){
				String[] temp1 = temp.split(":");
				if (temp1.length==2){
					if (temp1[0].compareTo("Nodes")==0){
						temp1[1]= temp1[1].trim();
						temp1[1] = temp1[1].substring(1, temp1[1].length()-1);
						temp1[1] = temp1[1].trim();
						nodes = Integer.parseInt(temp1[1]);
						break;
					}
				}
			}			

			for (int i=0;i<nodes;i++){
				temp=reader.readLine();
				String[] temp1 = temp.split("\t");
				vertex1 = new VertexElement(temp1[0], graph, Double.parseDouble(temp1[1]), Double.parseDouble(temp1[2]));
				graph.addVertex(vertex1);
			}

			int edges=0;
			//Read till we get to Edge definition)
			while ((temp=reader.readLine())!=null){
				String[] temp1 = temp.split(":");
				if (temp1.length==2){
					if (temp1[0].compareTo("Edges")==0){
						temp1[1]= temp1[1].trim();
						temp1[1] = temp1[1].substring(1, temp1[1].length()-1);
						temp1[1] = temp1[1].trim();
						edges = Integer.parseInt(temp1[1]);
						break;
					}
				}
			}			

			for (int i=0;i<edges;i++){
				temp=reader.readLine();
				String[] temp1 = temp.split("\t");

				vertex1 = graph.getVertex(temp1[1].trim());
				vertex2 = graph.getVertex(temp1[2].trim());

				EdgeElement edge = new EdgeElement(temp1[0], vertex1, vertex2, graph);

				//Compute delay using X and Y Coords from Vertices
				double distance = Math.sqrt(Math.pow(vertex1.getXCoord() - vertex2.getXCoord(), 2) + Math.pow(vertex1.getYCoord() - vertex2.getYCoord(), 2));

				double delay = distance  / 29.9792458; //(in ms)
				//@TODO import parameters for link weight and delay from brite
				EdgeParams params = new BasicEdgeParams(edge, delay, 1, 100);
				edge.setEdgeParams(params);
				graph.addEdge(edge);
			}
			reader.close();
		} catch (FileNotFoundException e) {
			GraphLogger.logError("The file " + filename + " could not be found", classIdentifier);
			e.printStackTrace();
		} catch (IOException e) {
			GraphLogger.logError("IO Exception while reading file ", classIdentifier);
			e.printStackTrace();
		}
	}

	@Override
	public void importTopologyFromString(Gcontroller graph, String[] topology) {
		
		int counter=0;
		
		String temp;
		VertexElement vertex1, vertex2;

		int nodes=0;

		//Read till we get to Node definition)
		while (counter<topology.length){
			temp = topology[counter++];
			String[] temp1 = temp.split(":");
			if (temp1.length==2){
				if (temp1[0].compareTo("Nodes")==0){
					temp1[1]= temp1[1].trim();
					temp1[1] = temp1[1].substring(1, temp1[1].length()-1);
					temp1[1] = temp1[1].trim();
					nodes = Integer.parseInt(temp1[1]);
					break;
				}
			}
		}			
		

		for (int i=0;i<nodes;i++){
			temp=topology[counter++];
			String[] temp1 = temp.split("\t");
			vertex1 = new VertexElement(temp1[0], graph, Double.parseDouble(temp1[1]), Double.parseDouble(temp1[2]));
			graph.addVertex(vertex1);
		}

		int edges=0;
		//Read till we get to Edge definition)
		while (counter<topology.length){
			temp = topology[counter++];
			String[] temp1 = temp.split(":");
			if (temp1.length==2){
				if (temp1[0].compareTo("Edges")==0){
					temp1[1]= temp1[1].trim();
					temp1[1] = temp1[1].substring(1, temp1[1].length()-1);
					temp1[1] = temp1[1].trim();
					edges = Integer.parseInt(temp1[1]);
					break;
				}
			}
		}			

		for (int i=0;i<edges;i++){
			temp = topology[counter++];
			String[] temp1 = temp.split("\t");

			vertex1 = graph.getVertex(temp1[1].trim());
			vertex2 = graph.getVertex(temp1[2].trim());

			EdgeElement edge = new EdgeElement(temp1[0], vertex1, vertex2, graph);

			//Compute delay using X and Y Coords from Vertices
			double distance = Math.sqrt(Math.pow(vertex1.getXCoord() - vertex2.getXCoord(), 2) + Math.pow(vertex1.getYCoord() - vertex2.getYCoord(), 2));

			double delay = distance  / 29.9792458; //(in ms)
			//@TODO import parameters for link weight and delay from brite
			EdgeParams params = new BasicEdgeParams(edge, delay, 1, 100);
			edge.setEdgeParams(params);
			graph.addEdge(edge);
		}
	}
}

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

public class TxtImportTopology extends ImportTopology{

	private static final String classIdentifier = "TxtImportTopology";

	public void importTopology(Gcontroller graph, String filename) {
		//add vertices to the graph
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			String temp;
			VertexElement vertex1, vertex2;

			while ((temp=reader.readLine())!=null){
				String[] temp1 = temp.split("-");
				if (temp1.length==4){
					//Check the first node to confirm if it exists in the graph
					if (graph.vertexExists(temp1[0])==false){
						vertex1 = new VertexElement(temp1[0], graph);
						graph.addVertex(vertex1);
					}
					else {
						vertex1 = graph.getVertex(temp1[0]);
					}


					//Check the second node to confirm if it exists in the graph
					if (graph.vertexExists(temp1[1])==false){
						vertex2 = new VertexElement(temp1[1], graph);
						graph.addVertex(vertex2);
					}
					else {
						vertex2 = graph.getVertex(temp1[1]);
					}

					//Insert Edge in the graph and the vertexElements
					EdgeElement edge = new EdgeElement(temp1[0]+"-"+temp1[1], vertex1, vertex2, graph);
					EdgeParams params = new BasicEdgeParams(edge, Double.parseDouble(temp1[2]), Double.parseDouble(temp1[2]), Double.parseDouble(temp1[3]));
					edge.setEdgeParams(params);
					graph.addEdge(edge);
				}
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
		// TODO Auto-generated method stub
		
	}

}

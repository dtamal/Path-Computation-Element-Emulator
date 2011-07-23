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

package com.graph.graphcontroller.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


import com.graph.elements.edge.EdgeElement;
import com.graph.elements.vertex.VertexElement;
import com.graph.graphcontroller.Gcontroller;
import com.graph.jdsl.impl.JDSLGraphImpl;
import com.graph.logger.GraphLogger;

public class GcontrollerImpl implements Gcontroller{

	/**JDSL Graph Implementation*/
	protected JDSLGraphImpl graph;

	public GcontrollerImpl(){
		graph= new JDSLGraphImpl();
	}
	
	private static final String classIdentifier = "Gcontroller";


	
	public boolean aConnectingEdge(String vertexID1, String vertexID2) {
		return graph.edgeExists(vertexID1, vertexID2);
	}

	
	public boolean aConnectingEdge(VertexElement vertex1, VertexElement vertex2) {
		return graph.edgeExists(vertex1.getVertexID(), vertex2.getVertexID());
	}

	
	public EdgeElement getConnectingEdge(String sourceID, String destinationID) {
		return graph.getConnectingEdge(sourceID, destinationID);
	}

	
	public EdgeElement getConnectingEdge(VertexElement source, VertexElement destination) {
		return graph.getConnectingEdge(source.getVertexID(), destination.getVertexID());
	}

	
	public EdgeElement getEdge(String edgeID) {
		return graph.getEdgeElement(edgeID);
	}


	
	public VertexElement getVertex(String vertexID) {
		return graph.getVertexElement(vertexID);
	}

	
	public Set<EdgeElement> getEdgeSet() {
		Set<EdgeElement> edgeSet = new HashSet<EdgeElement>();
		Iterator<String> iter = getEdgeIDSet().iterator();
		while (iter.hasNext()){
			edgeSet.add(graph.getEdgeElement(iter.next()));
		}
		return edgeSet;
	}


	
	public Set<VertexElement> getVertexSet() {
		Set<VertexElement> vertexSet = new HashSet<VertexElement>();
		Iterator<String> iter = getVertexIDSet().iterator();
		while (iter.hasNext()){
			vertexSet.add(graph.getVertexElement(iter.next()));
		}
		return vertexSet;
	}

	
	public Set<String> getEdgeIDSet() {
		return graph.getEdgeIdSet();
	}

	
	public Set<String> getVertexIDSet() {
		return graph.getVertexIdSet();
	}

	
	public void addVertex(VertexElement vertex) {
		if (this.vertexExists(vertex)==false){
			this.graph.addVertex(vertex);
		}
		else
			GraphLogger.logMsg("Vertex already exists", classIdentifier);
	}

	
	/**Function to add an edge in the graph. Also responsible for inserting the edges in the VertexElements*/
	public void addEdge(EdgeElement edge) {
//		if (this.aConnectingEdge(edge.getSourceVertex(), edge.getDestinationVertex())==false){
			if (this.graph.addEdge(edge))
			{
				edge.getSourceVertex().insertConnectedEdge(edge);
				edge.getDestinationVertex().insertConnectedEdge(edge);
			}
			else
				GraphLogger.logMsg("Error inserting edge in the graph", classIdentifier);			
//		}
//		else
//			Logger.logMsg("Edge already exists", classIdentifier);
	}

	
	public boolean vertexExists(String vertexID) {
		return this.getVertexIDSet().contains(vertexID);
	}

	
	public boolean vertexExists(VertexElement vertex) {
		return this.getVertexIDSet().contains(vertex.getVertexID());
	}

	
	public Gcontroller createCopy() {
		Gcontroller newController = new GcontrollerImpl();
		Iterator<VertexElement> iter = this.getVertexSet().iterator();
		while(iter.hasNext()){
			VertexElement newVertex = iter.next().copyVertexElement(newController);
			newController.addVertex(newVertex);
		}

		Iterator<EdgeElement> iter1 = this.getEdgeSet().iterator();
		while(iter1.hasNext()){
			EdgeElement newEdge = iter1.next().copyEdgeElement(newController);
			newController.addEdge(newEdge);
		}
		return newController;
	}

	
	public ArrayList<EdgeElement> allConnectingEdges(String vertexID1,
			String vertexID2) {
		return graph.allConnectingEdges(vertexID1, vertexID2);
	}

	
	public ArrayList<EdgeElement> allConnectingEdges(VertexElement vertexID1,
			VertexElement vertexID2) {
		return graph.allConnectingEdges(vertexID1.getVertexID(), vertexID2.getVertexID());
	}

}

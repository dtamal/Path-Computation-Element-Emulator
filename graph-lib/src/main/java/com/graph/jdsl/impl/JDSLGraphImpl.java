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

package com.graph.jdsl.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import com.graph.elements.edge.EdgeElement;
import com.graph.elements.vertex.VertexElement;
import com.graph.logger.GraphLogger;

import jdsl.graph.api.Edge;
import jdsl.graph.api.EdgeIterator;
import jdsl.graph.api.Vertex;
import jdsl.graph.ref.IncidenceListGraph;


public class JDSLGraphImpl extends IncidenceListGraph {

	/**Static variable to be used as class identifier*/
	private static final String classIdentifier = "JDSLGraphImpl";

	/**HashMap containing mapping against vertex ID and vertices*/
	private HashMap<String, Vertex> vertexMap;
	
	/**HashMap Containints mapping against Edge ID and Edge*/
	private HashMap<String, Edge> edgeMap;
	
	/**default constructor*/
	public JDSLGraphImpl(){
		vertexMap  = new HashMap<String, Vertex>();
		edgeMap = new HashMap<String, Edge>();
	}

	
	
	/**Function to add a vertex to the graph*/
	public void addVertex(VertexElement vertex){
		if (vertex==null){
			GraphLogger.logError("Cannot insert a null vertexElement into graph", classIdentifier);
			
		}
		Vertex temp = this.insertVertex(vertex);
		if (temp==null){
			GraphLogger.logError("Error while inserting vertex in JDSL Graph Library", classIdentifier);
		}
		else
			vertexMap.put(vertex.getVertexID(), temp);
	}
	
	/**Function to get a vertex element from a vertex ID*/
	private Vertex getVertex(String vertexID){
		if (vertexMap.containsKey(vertexID)){
			return vertexMap.get(vertexID);
		}
		else{
			GraphLogger.logError("Vertex :" + vertexID + " not Found in vertex Map", classIdentifier);
			return null;
		}
	}

	/**Function to get an edge element from the Edge ID*/
	private Edge getEdge(String edgeID){
		if (edgeMap.containsKey(edgeID)){
			return edgeMap.get(edgeID);
		}
		else{
			GraphLogger.logError("Edge :" + edgeID + " not Found in Edge Map", classIdentifier);
			return null;
		}
	}

	/**Function to get the vertex element from the vertex ID*/
	public VertexElement getVertexElement(String vertexID){
		Vertex vertex = this.getVertex(vertexID);
		if (vertex!=null)
			return ((VertexElement)vertex.element());
		else{
			GraphLogger.logError("Vertex with ID :" + vertexID + " not found in vertexMap.", classIdentifier);
			return null;
		}
	}

	/**Function to get the edge element from the edgeID*/
	public EdgeElement getEdgeElement(String edgeID){
		Edge edge = this.getEdge(edgeID);
		if (edge!=null)
			return ((EdgeElement)edge.element());
		else{
			GraphLogger.logError("Edge with ID :" + edgeID + " not found in edgeMap.", classIdentifier);
			return null;
		}
	}

	
	/**Function to add an edge to the graph*/
	public boolean addEdge(EdgeElement edge){
		Edge temp = this.insertEdge(getVertex(edge.getSourceVertex().getVertexID()), getVertex(edge.getDestinationVertex().getVertexID()), edge);
		if (temp==null){
			GraphLogger.logError("Error while inserting edge in JDSL Graph Library", classIdentifier);
			return false;
		}
		else
		{
			edgeMap.put(edge.getEdgeID(), temp);
			return true;
		}
	}
	
	/**Function to get an edge connected to two vertices*/
	private Edge aConnectingEdge(String sourceID, String destinationID){
		return this.aConnectingEdge(getVertex(sourceID), getVertex(destinationID));
	}
	
	/**Function to get EdgeElement when provided with source and destination IDs*/
	public EdgeElement getConnectingEdge(String sourceID, String destinationID){
		Edge edge = aConnectingEdge(sourceID, destinationID);
		if (edge==null)
		{
			GraphLogger.logError("No Edge connecting " + sourceID + " to " + destinationID + " in the JDSL Graph", classIdentifier);
			return null;
		}
		return ((EdgeElement)edge.element());
	}
	
	/**Boolean to check if an edge exists between two vertices*/
	public boolean edgeExists(String sourceID, String destinationID){
		return this.areAdjacent(getVertex(sourceID), getVertex(destinationID));
	}

	/**Function to get the set of vertex IDs*/
	public Set<String> getVertexIdSet(){
		return vertexMap.keySet();
	}

	/**Function to get the set of edge IDs*/
	public Set<String> getEdgeIdSet(){
		return edgeMap.keySet();
	}

	public ArrayList<EdgeElement> allConnectingEdges (String vertexID1, String vertexID2){
		EdgeIterator iter = this.connectingEdges(this.getVertex(vertexID1), this.getVertex(vertexID2));
		ArrayList<EdgeElement> temp = new ArrayList<EdgeElement>();
		while(iter.hasNext()){
			temp.add((EdgeElement)iter.nextEdge().element());
		}
		if (temp.size()==0)
			return null;
		else
			return temp;
	}
}

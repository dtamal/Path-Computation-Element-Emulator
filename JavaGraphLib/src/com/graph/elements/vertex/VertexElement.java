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

package com.graph.elements.vertex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.graph.elements.edge.EdgeElement;
import com.graph.elements.vertex.params.VertexParams;
import com.graph.graphcontroller.Gcontroller;
import com.graph.logger.GraphLogger;

public class VertexElement implements Comparable<VertexElement> {
	/**Static variable to be used as class identifier*/
	private static final String classIdentifier = "VertexElement";

	/**vertex Identifier*/
	private String vertexID;

	/**coordinates for the vertex*/
	private double xCoord, yCoord;
	
	private boolean isBorderNode = false;

	/**Pointer to the Graph Controller associated with the vertex*/
	protected Gcontroller graph;

	private HashMap<VertexElement, ArrayList<EdgeElement>> neighbourMap;

	private VertexParams params;
	
	/**Define set of edges that should be excluded from getNeighboringEdges function*/
	private HashSet<EdgeElement> excludedEdges;

	/**default constructor*/
	public VertexElement(String vertexID, Gcontroller graph){
		this.vertexID = vertexID; 
		this.graph = graph;
		this.neighbourMap = new HashMap<VertexElement, ArrayList<EdgeElement>>();
		excludedEdges = new HashSet<EdgeElement>();
	}

	/**default constructor*/
	public VertexElement(String vertexID, Gcontroller graph, double xCoord, double yCoord){
		this.vertexID = vertexID; 
		this.graph = graph;
		this.neighbourMap = new HashMap<VertexElement, ArrayList<EdgeElement>>();
		this.xCoord = xCoord;
		this.yCoord = yCoord;
		excludedEdges = new HashSet<EdgeElement>();
	}

	/**Function to get the x Coordinate of the vertex*/
	public double getXCoord(){
		return this.xCoord;
	}

	/**Function to get the y Coordinate of the vertex*/
	public double getYCoord(){
		return this.yCoord;
	}

	/**Function to set the x Coordinate of the vertex*/
	public void setXCoord(double coord){
		this.xCoord = coord;
	}

	/**Function to set the y Coordinate of the vertex*/
	public void setYCoord(double coord){
		this.yCoord = coord;
	}


	/**Function to get the Pointer to the corresponding Graph */
	public Gcontroller getGraphController(){
		return graph;
	}

	/**Function to get the vertex Identifier*/
	public String getVertexID(){
		return this.vertexID;
	}

	/**Function to update the neighbour map*/
	public void updateNeighbourMap(){
		//TODO Check this function for bugs
		this.neighbourMap.clear();
		Set<String> vertexSet = this.graph.getVertexIDSet();
		vertexSet.remove(this.getVertexID());
		Iterator<String> iter = vertexSet.iterator();
		while(iter.hasNext()){
			String remoteNodeID = iter.next();
			if (this.graph.aConnectingEdge(this.vertexID, remoteNodeID)==true){
				this.neighbourMap.put(this.graph.getVertex(remoteNodeID), this.graph.allConnectingEdges(this.vertexID, remoteNodeID));
			}
		}
	}

	/**Boolean function to evaluate if the neighbour map contains sufficient keys*/
	private boolean validateNeighbourMap(){
		if (neighbourMap==null)
		{
			GraphLogger.logError("No neighbouring nodes in the neighbour map", classIdentifier);
			return false;
		}
		else if (neighbourMap.keySet().size()==0){
			GraphLogger.logError("No neighbouring nodes in the neighbour map", classIdentifier);
			return false;			
		}
		return true;
	}

	/**Function to get the list of connected edges*/
	public Set<EdgeElement> getConnectedEdges(){
		if (validateNeighbourMap())
		{
			HashSet<EdgeElement> temp = new HashSet<EdgeElement>();
			Iterator<ArrayList<EdgeElement>> iter = neighbourMap.values().iterator();
			while(iter.hasNext()){
				Iterator <EdgeElement> iter1 = iter.next().iterator();
				while (iter1.hasNext()){
					//temp.add();
					EdgeElement edge = iter1.next();
					if (excludedEdges.contains(edge)==false){
						temp.add(edge);
					}
				}
			}
			return temp;
		}
		else
			return null;
	}

	/**Function to get the list of neighbouring vetrices*/
	public Set<VertexElement> getNeighbouringVertices(){
		if (validateNeighbourMap())
			return neighbourMap.keySet();
		else
			return null;
	}

	/**Function to populate the neighbour list of the vertex*/
	public void insertConnectedEdge(EdgeElement edge){
		//Check if the given edge belongs to the same graph
		if (edge.graph!=this.graph){
			GraphLogger.logError("The edge to be inserted does not belong to the same graph", classIdentifier);
		}
		else
		{
			//check if one of the vertices is same as the current vertex
			VertexElement destination = null; 
			if (edge.getSourceVertex()==this){
				destination = edge.getDestinationVertex();
			}
			else if (edge.getDestinationVertex()==this){
				destination = edge.getSourceVertex();
			}
			else{
				GraphLogger.logError("The edge to be inserted is not connected to the vertex " + this.vertexID, classIdentifier);				
			}
			if (destination !=null){
				if (neighbourMap.containsKey(destination)){
					this.neighbourMap.get(destination).add(edge);
				}
				else 
				{
					ArrayList<EdgeElement> temp = new ArrayList<EdgeElement>();
					temp.add(edge);
					this.neighbourMap.put(destination, temp);
				}
			}
		}
	}


	
	public int compareTo(VertexElement temp) {
		if ((temp.getVertexID().compareTo(this.vertexID)==0))
			return 0;
		return 1;
	}
	
	public void setIsBorderNode(boolean isBorderNode){
	    this.isBorderNode = isBorderNode;
	}
	
	public boolean isBorderNode(){
	    return this.isBorderNode;
	}
	
	public VertexParams getVertexParams(){
		return params;
	}

	
	
	public void setVertexParams(VertexParams params){
		this.params = params;
	}

	/**Function to copt the vertex Element into a new graph*/
	public VertexElement copyVertexElement (Gcontroller newGraph){
		VertexElement element = new VertexElement (vertexID, newGraph, xCoord, yCoord);
		element.setIsBorderNode(isBorderNode);
		VertexParams params = null;
		if (this.getVertexParams()!=null)
			params = this.getVertexParams().copyVertexParams(element);
		element.setVertexParams(params);
		return element;
	}

	
	/**Flush the set of edges that should be excluded in the getConnectedEdges() function*/
	public void flushExcludedEdges(){
		excludedEdges.clear();
	}
	
	/**Add an edge that should be excluded in the getConnectedEdges() function*/
	public void excludeEdge(EdgeElement edge){
		if (this.getConnectedEdges().contains(edge))
			this.excludedEdges.add(edge);
	}
	
}

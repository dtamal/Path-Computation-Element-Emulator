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

package com.graph.elements.edge;

import com.graph.elements.edge.params.EdgeParams;
import com.graph.elements.vertex.VertexElement;
import com.graph.graphcontroller.Gcontroller;
import com.graph.logger.GraphLogger;

public class EdgeElement {
	/**Static variable to be used as class identifier*/
	private static final String classIdentifier = "EdgeElement";

	/**Edge Identifier*/
	private String edgeID;

	/**Vertex Elements for the source and destination vertices*/
	private VertexElement source, destination;

	/**Reference to store the Edge parameters*/
	private EdgeParams edgeParams;
	
	/**Reference to the parent graph controller*/
	public Gcontroller graph;
	
	/**default Constructor*/
	public EdgeElement(String edgeID, VertexElement source, VertexElement destination, Gcontroller graph, EdgeParams params){
		this.edgeID = edgeID;
		this.source = source;
		this.destination = destination;
		this.graph = graph;
		this.edgeParams = params;
	}

	/**default Constructor*/
	public EdgeElement(String edgeID, VertexElement source, VertexElement destination, Gcontroller graph){
		this.edgeID = edgeID;
		this.source = source;
		this.destination = destination;
		this.graph = graph;
	}

	
	/**Function to get the Pointer to the corresponding Graph */
	public Gcontroller getGraphController(){
		return graph;
	}

	/**Function to get the EdgeID*/
	public String getEdgeID(){
		return this.edgeID;
	}

	/**Function to get the Source Vertex*/
	public VertexElement getSourceVertex(){
		if (source!=null)
			return this.source;
		else
		{
			GraphLogger.logError("Source VertexElement not set", classIdentifier);
			return null;
		}
	}

	/**Function to get the Destination Vertex*/
	public VertexElement getDestinationVertex(){
		if (destination!=null)
			return this.destination;
		else
		{
			GraphLogger.logError("Destination VertexElement not set", classIdentifier);
			return null;
		}
	}	
	
	/**Object containing the parameters for any given Edge*/
	public EdgeParams getEdgeParams(){
		return edgeParams;
	}
	
	/**Function to manually set the EdgeElement object*/
	public void setEdgeParams(EdgeParams element){
		this.edgeParams = element;
	}
	
	/**Function to copy the edge element onto a new graph*/
	public EdgeElement copyEdgeElement(Gcontroller newGraph){
		EdgeElement element = new EdgeElement (edgeID, newGraph.getVertex(source.getVertexID()), newGraph.getVertex(destination.getVertexID()), newGraph);
		EdgeParams params = this.getEdgeParams().copyEdgeParams(element);
		element.setEdgeParams(params);
		return element;
	}
}

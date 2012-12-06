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

package com.graph.path;

import java.util.ArrayList;

import com.graph.elements.edge.EdgeElement;
import com.graph.elements.vertex.VertexElement;
import com.graph.graphcontroller.Gcontroller;
import com.graph.logger.GraphLogger;
import com.graph.path.params.PathParams;

public abstract class PathElement {

	/**Static variable to be used as class identifier*/
	private static final String classIdentifier = "PathElement";

	/**reference to the corresponding graph controller*/
	protected Gcontroller graph;
	
	/**Source and Destination Vertex Element for the path*/
	protected VertexElement source, destination;
	
	/**Path Paramteres Element*/
	protected PathParams params;
	
	/**Function to get the Pointer to the corresponding Graph */
	public Gcontroller getGraphController(){
		return graph;
	}
	
	/**Function to get the sourceID of a path*/
	public String getSourceID(){
		if (source==null)
		{
			GraphLogger.logError("The source VertexElement in PathElement is not set (null)", classIdentifier);
			return null;
		}
		return source.getVertexID();
	}

	/**Function to get the source of a path*/
	public VertexElement getSource()
	{
		if (source==null)
		{
			GraphLogger.logError("The source VertexElement in PathElement is not set (null)", classIdentifier);
		}
		return source;	
	}

	/**Function to get the destination ID of a path*/
	public String getDestinationID(){
		if (destination==null)
		{
			GraphLogger.logError("The destination VertexElement in PathElement is not set (null)", classIdentifier);
			return null;
		}
		return destination.getVertexID();
	}

	/**Function to get the Destination of a path*/
	public VertexElement getDestination(){
		if (destination==null)
		{
			GraphLogger.logError("The destination VertexElement in PathElement is not set (null)", classIdentifier);
		}
		return destination;
	}
	
	/**Function to get the ordered arraylist of Vertices in the path*/
	public abstract ArrayList<VertexElement> getTraversedVertices();

	/**Function to get the ordered arraylist of Edges in the path*/
	public abstract ArrayList<EdgeElement> getTraversedEdges();
	
	/**Boolean function to check if the vertex belongs to the path*/
	public boolean containsVertex(String vertexID){
		return this.containsVertex(this.getGraphController().getVertex(vertexID));
	}
	
	/**Boolean function to check if the vertex belongs to the path*/
	public abstract boolean containsVertex(VertexElement vertex);

	/**Boolean function to check if the Edge belongs to the path*/
	public boolean containsEdge(String edgeID){
		return this.containsEdge(this.getGraphController().getEdge(edgeID));
	}
	
	/**Boolean function to check if the Edge belongs to the path*/
	public abstract boolean containsEdge(EdgeElement vertex);

	/**Function to get the PathParameters Object*/
	public PathParams getPathParams(){
		return params;
	}
	
	/**Function to set the PathParameters Object*/
	public void setPathParams(PathParams params){
		this.params=params;
	}
	
	/**Funcion to get the vertex sequence for a path element*/
	public String getVertexSequence(){
		String temp=this.getSourceID();
		ArrayList<VertexElement> vertices = this.getTraversedVertices();
		for (int i=1;i<vertices.size();i++){
			temp=temp + "-" + vertices.get(i).getVertexID();
		}
		return temp;
	}
	
	/**Function to reserve Bandwidth on a path*/
	public abstract boolean resvBandwidth(double bw);
	
	/**Function to release Bandwidth on a path*/
	public abstract boolean releaseBandwidth(double bw);

	public abstract void setEdgeSequence(ArrayList<EdgeElement> edges);
	
	public abstract PathElement createCopy();
}

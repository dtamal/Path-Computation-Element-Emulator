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

package com.graph.path.algorithms.constraints.multipath.impl;

import com.graph.elements.vertex.VertexElement;
import com.graph.logger.GraphLogger;
import com.graph.path.algorithms.constraints.MultiPathConstraint;

public class SimpleMultiPathComputationConstraint extends MultiPathConstraint{
	
	public static final String classIdentifier = "SimpleMultiPathComputationConstraint";
	
	/**bandwidth for connection **/
	private double bw;
	

	/**Vertex Elements to define source and destination*/
	private VertexElement source, destination;
	
	/**variable to store the number of paths requested, if 0 then all possible paths required*/
	private int pathCount;
	
	/**Function to get the source of the requested path*/
	public VertexElement getSource(){
		return this.source;
	}

	/**Function to get the destination of the requested path*/
	public VertexElement getDestination(){
		return this.destination;
	}
	
	/**Constructor to set the source and destination*/
	public SimpleMultiPathComputationConstraint(VertexElement source, VertexElement destination){
		this.source = source;
		this.destination = destination;
		this.pathCount=0;
	}

	/**Constructor to set the source and destination*/
	public SimpleMultiPathComputationConstraint(VertexElement source, VertexElement destination, int pathCount, double bw){
		if (source==null){
			GraphLogger.logError("null source", classIdentifier);
		}
		if (destination==null){
			GraphLogger.logError("null destination", classIdentifier);
		}
		this.source = source;
		this.destination = destination;
		this.pathCount=pathCount;
		this.bw = bw;
	}

	/**Function to get the max paths to be computed*/
	public int getPathCount() {
		return this.pathCount;
	}

	/**
	 * @return the bw
	 */
	public double getBw() {
		return bw;
	}

	/**
	 * @param bw the bw to set
	 */
	public void setBw(double bw) {
		this.bw = bw;
	}

	
}

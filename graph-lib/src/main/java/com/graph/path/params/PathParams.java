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

package com.graph.path.params;

import java.util.Iterator;

import com.graph.elements.edge.EdgeElement;
import com.graph.logger.GraphLogger;
import com.graph.path.PathElement;

public class PathParams {

	/**Static variable to be used as class identifier*/
	private static final String classIdentifier = "PathParams";

	/**Pointer to the path element*/
	private PathElement path;
	
	private double reserve;
	
	public PathParams(PathElement path){
		this.path = path;
	}
	
	/**Function to get the associated Path Element*/
	public PathElement getPathElement(){
		return this.path;
	}
	
	
	/**
	 * @return the capacity
	 */
	public double getReserve() {
		return reserve;
	}

	/**
	 * @param capacity the capacity to set
	 */
	public void setReserve(double reserve) {
		this.reserve = reserve;
	}

	/**Function to check if iterator over edges is valid*/
	private boolean validateEdgeIterator(Iterator<EdgeElement> iter){
		if (iter==null){
			GraphLogger.logError("The path description does not have any edges", classIdentifier);
			return false;
		}
		else if (iter.hasNext()==false)
		{
			GraphLogger.logError("The path description does not have any edges", classIdentifier);
			return false;
		}
		return true;
	}
	
	
	/**Function to get the path delay*/
	public double getPathDelay(){
		double delay=0;
		Iterator<EdgeElement> iter = this.path.getTraversedEdges().iterator();
		
		//validate the edge iterator to check if it is null or empty
		if (validateEdgeIterator(iter)==false)
			return -1;
		
		while(iter.hasNext()==true)
			delay += iter.next().getEdgeParams().getDelay();
		return delay;
	}

	/**Function to get the path cost*/
	public double getPathWeight(){
		double cost=0;
		Iterator<EdgeElement> iter = this.path.getTraversedEdges().iterator();
		
		//validate the edge iterator to check if it is null or empty
		if (validateEdgeIterator(iter)==false)
			return -1;

		while(iter.hasNext()==true)
			cost += iter.next().getEdgeParams().getWeight();
		return cost;
	}

	
	/**Function to get the available path capacity*/
	public double getAvailableCapacity(){
		double capacity, temp;
		Iterator<EdgeElement> iter = this.path.getTraversedEdges().iterator();

		//validate the edge iterator to check if it is null or empty
		if (validateEdgeIterator(iter)==false)
			return -1;
			
		capacity = iter.next().getEdgeParams().getAvailableCapacity();
		while(iter.hasNext()==true)
		{
			temp = iter.next().getEdgeParams().getAvailableCapacity();
			if (temp<capacity)
				capacity=temp;
		}
		return capacity;
	}

	/**Function to get the Max possible path capacity*/
	public double getMaxCapacity(){
		double capacity, temp;
		Iterator<EdgeElement> iter = this.path.getTraversedEdges().iterator();

		//validate the edge iterator to check if it is null or empty
		if (validateEdgeIterator(iter)==false)
			return -1;
			
		capacity = iter.next().getEdgeParams().getMaxCapacity();
		while(iter.hasNext()==true)
		{
			temp = iter.next().getEdgeParams().getMaxCapacity();
			if (temp<capacity)
				capacity=temp;
		}
		return capacity;
	}	
}

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

package com.graph.elements.edge.params.impl;

import java.util.ArrayList;

import com.graph.elements.edge.EdgeElement;
import com.graph.elements.edge.params.EdgeParams;
import com.graph.logger.GraphLogger;

public class ParentVirtualLinkEdgeParams extends EdgeParams{

	public static final String classIdentifier = "ParentVirtualLinkEdgeParams";
	
	
	/**Delay of a link*/
	private double delay=0;
	
	/**Weight of the link*/
	private double weight=0;
	
	/**Maximum capacity of the link*/
	private double maxCapacity=0;
	
	/**Available capacity on the link*/
	private double availableCapacity=0;

	private boolean isDynamicLink= false;
	
	private ArrayList<String> vertexSequence; 
	
	public ParentVirtualLinkEdgeParams(EdgeElement edge){
		this.setEdgeElement(edge);
	}

	public ParentVirtualLinkEdgeParams(EdgeElement edge, double delay, double weight, double maxCapacity, ArrayList<String> vertexSequence){
		this.setEdgeElement(edge);
		this.setDelay(delay);
		this.setWeight(weight);
		this.setMaxCapacity(maxCapacity);
		this.setAvailableCapacity(maxCapacity);
		this.isDynamicLink = false;
		this.vertexSequence = vertexSequence;
	}

	public boolean isDynamicLink() {
	    return isDynamicLink;
	}

	public void setDynamicLink(boolean isDynamicLink) {
	    this.isDynamicLink = isDynamicLink;
	}

	public ParentVirtualLinkEdgeParams(double delay, double weight, double maxCapacity){
		//this.setEdgeElement(edge);
		this.setDelay(delay);
		this.setWeight(weight);
		this.setMaxCapacity(maxCapacity);
		this.setAvailableCapacity(maxCapacity);
		this.isDynamicLink=false;
	}
	
	
	/**Function to get the delay of the link*/
	public double getDelay(){
		return delay;
	}
	
	/**Function to set the delay of the link*/
	protected void setDelay(double delay){
		this.delay = delay;
	}
	
	/**Function to get the weight of the link*/
	public double getWeight(){
		return weight;
	}

	/**Function to set the weight of the link*/
	public void setWeight(double w){
		weight=w;
		if (w<0)
			GraphLogger.logMsg("Notification: Weight of edge " + this.getEdgeElement().getEdgeID() + " < 0, weight = " + Double.toString(w), classIdentifier);
	}
	
	/**Function to get the total capacity of the link*/
	public double getMaxCapacity(){
		return maxCapacity;
	}

	/**Function to set the total capacity of the link*/
	public void setMaxCapacity(double capacity){
		this.maxCapacity=capacity;
	}

	/**Function to set the available capacity of the link*/
	public void setAvailableCapacity(double capacity){
		this.availableCapacity=capacity;
	}

	/**Function to get the used capacity of the link*/
	public double getUsedCapacity(){
		return maxCapacity-availableCapacity;
	}

	/**Function to get the available capacity of the link*/
	public double getAvailableCapacity(){
		return availableCapacity;
	}

	
	public boolean releaseCapacity(double capacity) {
		if (this.getUsedCapacity()<capacity){
			GraphLogger.logError("Capacity release requested is greater than total used capacity", classIdentifier);
			return false;
		}
		else{
			this.setAvailableCapacity(this.getAvailableCapacity()+ capacity);
			return true;
		}
	}

	public boolean reserveCapacity(double capacity) {
		if (this.getAvailableCapacity()<capacity){
			GraphLogger.logError("Not Enough Capacity left for reservation", classIdentifier);
			return false;
		}
		else{
			this.availableCapacity -= capacity;
			return true;
		}
	}

	@Override
	public EdgeParams copyEdgeParams(EdgeElement newElement) {
		EdgeParams params = new ParentVirtualLinkEdgeParams(newElement, delay, weight, maxCapacity, vertexSequence);
		params.setAvailableCapacity(this.availableCapacity);
		params.setDynamicLink(isDynamicLink);
		return params;
	}

	@Override
	public ArrayList<String> getVertexSequence(String sourceID, String destID) {
		String edgeSourceID = this.getEdgeElement().getSourceVertex().getVertexID();
		String edgeDestID = this.getEdgeElement().getDestinationVertex().getVertexID();
		if ((sourceID.compareTo(edgeSourceID)!=0) && (sourceID.compareTo(edgeDestID)!=0))
			return null;
		if ((destID.compareTo(edgeSourceID)!=0) && (destID.compareTo(edgeDestID)!=0))
			return null;
		
		if ((sourceID.compareTo(vertexSequence.get(0))==0) && (destID.compareTo(vertexSequence.get(vertexSequence.size()-1))==0)) {
			return vertexSequence;
		} else if ((destID.compareTo(vertexSequence.get(0))==0) && (sourceID.compareTo(vertexSequence.get(vertexSequence.size()-1))==0)) {
			//Reverse the vertex Sequence
			ArrayList<String> out = new ArrayList<String>();
			for (int i=vertexSequence.size()-1; i>=0;i--) {
				out.add(vertexSequence.get(i));
			} 
			return out;
		} else {
			System.out.println("Error in the way vertex Sequence is defined");
			return null;
		}
		
	}

}

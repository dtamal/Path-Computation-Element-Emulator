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
import java.util.Iterator;

import com.graph.elements.edge.EdgeElement;
import com.graph.elements.edge.params.EdgeParams;
import com.graph.elements.vertex.VertexElement;
import com.graph.logger.GraphLogger;
import com.graph.path.PathElement;

public class PathElementEdgeParams extends EdgeParams{



	public static final String classIdentifier = "PathElementEdgeParams";

	/**PathElement based on which Edge params are computed */
	private PathElement path;

	private boolean isDynamicLink= true;

	public PathElementEdgeParams(EdgeElement edge, PathElement path){
		this.setEdgeElement(edge);
		this.path = path;
	}

	public boolean isDynamicLink() {
		return isDynamicLink;
	}

	public void setDynamicLink(boolean isDynamicLink) {
		this.isDynamicLink = isDynamicLink;
	}


	/**Function to get the delay of the link*/
	public double getDelay(){
		return path.getPathParams().getPathDelay();
	}

	/**Function to set the delay of the link*/
	protected void setDelay(double delay){
	}

	/**Function to get the weight of the link*/
	public double getWeight(){
		return path.getPathParams().getPathWeight();
	}

	/**Function to set the weight of the link*/
	public void setWeight(double w){
	}

	/**Function to get the total capacity of the link*/
	public double getMaxCapacity(){
		return path.getPathParams().getMaxCapacity();
	}

	/**Function to set the total capacity of the link*/
	public void setMaxCapacity(double capacity){
	}

	/**Function to set the available capacity of the link*/
	public void setAvailableCapacity(double capacity){
	}

	/**Function to get the used capacity of the link*/
	public double getUsedCapacity(){
		return this.getMaxCapacity()-this.getAvailableCapacity();
	}

	/**Function to get the available capacity of the link*/
	public double getAvailableCapacity(){
		return path.getPathParams().getAvailableCapacity();
	}


	public boolean releaseCapacity(double capacity) {
		if (this.getUsedCapacity()<capacity){
			GraphLogger.logError("Capacity release requested is greater than total used capacity", classIdentifier);
			return false;
		}
		else{
			path.releaseBandwidth(capacity);
			return true;
		}
	}

	public boolean reserveCapacity(double capacity) {
		if (this.getAvailableCapacity()<capacity){
			GraphLogger.logError("Not Enough Capacity left for reservation", classIdentifier);
			return false;
		}
		else{
			path.resvBandwidth(capacity);
			return true;
		}
	}

	@Override
	public EdgeParams copyEdgeParams(EdgeElement newElement) {
		EdgeParams params = new PathElementEdgeParams(newElement, path);
		params.setDynamicLink(isDynamicLink);
		return params;
	}

	public ArrayList<String> getVertexSequence(String sourceID, String destID) {
		String edgeSourceID = this.getEdgeElement().getSourceVertex().getVertexID();
		String edgeDestID = this.getEdgeElement().getDestinationVertex().getVertexID();
		if ((sourceID.compareTo(edgeSourceID)!=0) && (sourceID.compareTo(edgeDestID)!=0))
			return null;
		if ((destID.compareTo(edgeSourceID)!=0) && (destID.compareTo(edgeDestID)!=0))
			return null;
		//Get the sequence of vertices depending on the direction on which the link is being traversed
		ArrayList<String> pathElementSequence = new ArrayList<String>();
		Iterator<VertexElement> iter = path.getTraversedVertices().iterator();
		while(iter.hasNext()) {
			pathElementSequence.add(iter.next().getVertexID());
		}
		if ((pathElementSequence.get(0).compareTo(sourceID)==0) && (pathElementSequence.get(pathElementSequence.size()-1).compareTo(destID)==0)) {
			return pathElementSequence;
		} else if ((pathElementSequence.get(0).compareTo(destID)==0) && (pathElementSequence.get(pathElementSequence.size()-1).compareTo(sourceID)==0)) {
			ArrayList<String> reversed = new ArrayList<String> ();
			for (int i=pathElementSequence.size()-1; i>=0;i--) {
				reversed.add(pathElementSequence.get(i));
			}
			return reversed;
		} else {
			System.out.println("Something wrong with the way path element sequence is defined");
			return null;
		}
	}
	
	/**Function to get the path element associated with the Edge Parameter
	 * 
	 * @return Path Element
	 */
	public PathElement getPath() {
		return path;
	}

}

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

import com.graph.elements.edge.EdgeElement;
import com.graph.elements.edge.params.EdgeParams;
import com.graph.logger.GraphLogger;
import com.graph.path.PathElement;

public class VirtualPathEdgeParams extends EdgeParams{

	public static final String classIdentifier = "VirtualPathEdgeParams";

	//VirtualPathEdgeElements describes a enge in the virtual graph which is linked to a path description in a given graph. 
	//Note that in this scenario, the link capacity has not been reserved beforehand
	
	PathElement virtualLinkDesc;
	
	public VirtualPathEdgeParams(EdgeElement parent, PathElement virtualPath){
		virtualLinkDesc = virtualPath;
		this.setEdgeElement(parent);
	}
	
	public PathElement getPathElement(){
		return virtualLinkDesc;
	}
	
	@Override
	public double getAvailableCapacity() {
		return virtualLinkDesc.getPathParams().getAvailableCapacity();
	}

	@Override
	public double getDelay() {
		return virtualLinkDesc.getPathParams().getPathDelay();
	}

	@Override
	public double getMaxCapacity() {
		return virtualLinkDesc.getPathParams().getMaxCapacity();
	}

	@Override
	public double getUsedCapacity() {
		return virtualLinkDesc.getPathParams().getMaxCapacity()- virtualLinkDesc.getPathParams().getAvailableCapacity();

	}

	@Override
	public double getWeight() {
		return 	virtualLinkDesc.getPathParams().getPathWeight();

	}

	@Override
	public boolean releaseCapacity(double capacity) {
		return virtualLinkDesc.releaseBandwidth(capacity);
	}

	@Override
	public boolean reserveCapacity(double capacity) {
		return virtualLinkDesc.resvBandwidth(capacity);
	}

	@Override
	public void setAvailableCapacity(double capacity) {
		// TODO Auto-generated method stub
		GraphLogger.logError("Cannot set available capacity for a virtual Path link", classIdentifier);
	}

	@Override
	protected void setDelay(double delay) {
		// TODO Auto-generated method stub
		GraphLogger.logError("Cannot set delay for a virtual Path link", classIdentifier);		
	}

	@Override
	public void setMaxCapacity(double capacity) {
		// TODO Auto-generated method stub
		GraphLogger.logError("Cannot set Max Capacity for a virtual Path link", classIdentifier);				
	}

	@Override
	public void setWeight(double w) {
		// TODO Auto-generated method stub
		GraphLogger.logError("Cannot set weight for a virtual Path link", classIdentifier);						
	}

	@Override
	public EdgeParams copyEdgeParams(EdgeElement newElement) {
		EdgeParams params = new VirtualPathEdgeParams(newElement, virtualLinkDesc);
		return params;
	}
	
}

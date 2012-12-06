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

package com.graph.elements.edge.params;

import java.util.ArrayList;

import com.graph.elements.edge.EdgeElement;

public abstract class EdgeParams {
	
	public abstract boolean isDynamicLink();

	public abstract void setDynamicLink(boolean isDynamicLink);
    
	/**Edge Element for the which paramteres are defined*/
	private EdgeElement edge;
	
	/**Function to set the Edge Element */
	public void setEdgeElement(EdgeElement edge){
		this.edge=edge;
	}
	
	/**Function to get the Pointer to the corresponding Edge */
	public EdgeElement getEdgeElement(){
		return edge;
	}

	/**Function to get the delay of the link*/
	public abstract double getDelay();
	
	/**Function to set the delay of the link*/
	protected abstract void setDelay(double delay);
	
	/**Function to get the weight of the link*/
	public abstract double getWeight();

	/**Function to set the weight of the link*/
	public abstract void setWeight(double w);
	
	/**Function to get the total capacity of the link*/
	public abstract double getMaxCapacity();

	/**Function to set the total capacity of the link*/
	public abstract void setMaxCapacity(double capacity);

	/**Function to set the available capacity of the link*/
	public abstract void setAvailableCapacity(double capacity);

	/**Function to get the used capacity of the link*/
	public abstract double getUsedCapacity();

	/**Function to get the available capacity of the link*/
	public abstract double getAvailableCapacity();

	/**Function to reserve capacity on the link*/
	public abstract boolean reserveCapacity(double capacity);

	/**Function to release capacity on the link*/
	public abstract boolean releaseCapacity(double capacity);

	/**Function to copy the edge Parameters onto a new edge element */
	public abstract EdgeParams copyEdgeParams(EdgeElement newElement);

	public int getMaxCarriers() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getCarrierUsage(int i) {
		// TODO Auto-generated method stub
		return 0;
	}

	public abstract ArrayList<String> getVertexSequence(String sourceID, String destID);
	
}

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

package com.graph.elements.vertex.params;

import com.graph.elements.vertex.VertexElement;

public abstract class VertexParams {

	/**Vertex Element for the which paramteres are defined*/
	private VertexElement vertex;
	
	/**Function to set the Edge Element */
	public void setVertexElement(VertexElement vertex){
		this.vertex=vertex;
	}
	
	/**Function to get the Pointer to the corresponding Edge */
	public VertexElement getVertexElement(){
		return vertex;
	}
	
	/**Function to to copy the vertex Params*/
	public abstract VertexParams copyVertexParams(VertexElement newVertex);
	
	public abstract String getType();
	
	public abstract int getFreePorts();
	
}

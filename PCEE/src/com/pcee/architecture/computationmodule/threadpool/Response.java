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

package com.pcee.architecture.computationmodule.threadpool;

import java.util.ArrayList;

import com.graph.elements.vertex.VertexElement;
import com.graph.path.PathElement;
import com.pcee.protocol.message.objectframe.impl.erosubobjects.PCEPAddress;

public class Response {

    // Variable ideintifying the requestID used inside the PCEP protocol
    private String requestID;
    // Variable indicating the computed path
    private PathElement element;
    // Variable indicating the computed paths
    private ArrayList<PathElement> elements;
    // Address to identify the PCEP session
    private PCEPAddress address;
    // Vertex indicating the found vertex
    private VertexElement vertex;
    // indicating whether it's a vertex request or path computation request
    private boolean vertexRequest;

    public boolean isVertexRequest() {
		return vertexRequest;
	}

	public void setVertexRequest(boolean vertexRequest) {
		this.vertexRequest = vertexRequest;
	}

	public VertexElement getVertex() {
		return vertex;
	}

	public void setVertex(VertexElement vertex) {
		this.vertex = vertex;
	}

	/**
     * @return the requestID
     */
    public String getRequestID() {
	return requestID;
    }

    /**
     * @param requestID
     *            the requestID to set
     */
    public void setRequestID(String requestID) {
	this.requestID = requestID;
    }

    /**
     * @return the element
     */
    public PathElement getElement() {
	return element;
    }

    /**
     * @param element
     *            the element to set
     */
    public void setElement(PathElement element) {
	this.element = element;
    }

    /**
     * @return the address
     */
    public PCEPAddress getAddress() {
	return address;
    }

    /**
     * @param address
     *            the address to set
     */
    public void setAddress(PCEPAddress address) {
	this.address = address;
    }

    /**
     * set the computed arraylist containing paths
     * @param elements
     */
    public void setPathElements(ArrayList<PathElement> elements){
    	this.elements = elements;
    }
    
    /**
     * return the computed arraylist of paths
     * @return
     */
    public ArrayList<PathElement> getPathElements(){
    	return this.elements;
    }
}

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

import com.graph.path.algorithms.PathComputationAlgorithm;
import com.graph.path.algorithms.constraints.Constraint;
import com.pcee.common.Address;

/**Class to define a path computation request used by the thread pool
 * 
 * @author Mohit Chamania
 * @author Marek Drogon
 */
public class Request {

	//Variable to store the request ID
	private String requestID;
	//variable to store the address for the PCE response
	private Address address;
	
	//Constraints for the path computation request
	private Constraint constrains;
	
	//Algorithm used for the path computation request 
	private PathComputationAlgorithm algo;

	/**
	 * @return the requestID
	 */
	public String getRequestID() {
		return requestID;
	}

	/**
	 * @param requestID the requestID to set
	 */
	public void setRequestID(String requestID) {
		this.requestID = requestID;
	}

	/**
	 * @return the address
	 */
	public Address getAddress() {
		return address;
	}

	/**
	 * @param address the address to set
	 */
	public void setAddress(Address address) {
		this.address = address;
	}

	/**
	 * @return the constrains
	 */
	public Constraint getConstrains() {
		return constrains;
	}

	/**
	 * @param constrains the constrains to set
	 */
	public void setConstrains(Constraint constrains) {
		this.constrains = constrains;
	}

	/**
	 * @return the algo
	 */
	public PathComputationAlgorithm getAlgo() {
		return algo;
	}

	/**
	 * @param algo the algo to set
	 */
	public void setAlgo(PathComputationAlgorithm algo) {
		this.algo = algo;
	}


}

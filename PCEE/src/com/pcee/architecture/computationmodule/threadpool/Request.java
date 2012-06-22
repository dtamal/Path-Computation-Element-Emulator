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

import com.graph.path.algorithms.MultiPathComputationAlgorithm;
import com.graph.path.algorithms.PathComputationAlgorithm;
import com.graph.path.algorithms.constraints.Constraint;
import com.graph.path.algorithms.constraints.MultiPathConstraint;
import com.pcee.protocol.message.objectframe.impl.erosubobjects.PCEPAddress;

/**
 * Class to define a path computation request used by the thread pool
 * 
 * @author Mohit Chamania
 * @author Marek Drogon
 */
public class Request {

    // Variable to store the request ID
    private String requestID;
    
    // variable to store the address for the PCE response
    private PCEPAddress address;

    // Constraints for the path computation request
    private Constraint constrains;
    
    //Constraints for multipath computation request
    private MultiPathConstraint mconstraints;

    // Algorithm used for the path computation request
    private PathComputationAlgorithm algo;
    
    //Algorithm for multipath computation request
    private MultiPathComputationAlgorithm malgo;

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
     * @return the constrains
     */
    public Constraint getConstrains() {
	return constrains;
    }

    /**
     * @param constrains
     *            the constrains to set
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
     * @param algo
     *            the algo to set
     */
    public void setAlgo(PathComputationAlgorithm algo) {
	this.algo = algo;
    }

    /**
 	 * set contraints for multipath computation
     * @param mconstraints
     */
    public void setMContraints(MultiPathConstraint mconstraints){
    	this.mconstraints = mconstraints;
    }
    
    /**
     * return the contraints for multipath computation
     * @return
     */
    public MultiPathConstraint getMConstraints(){
    	return this.mconstraints;
    }
    
    /**
     * @param malgo
     * 				set the multipath computation algorithm
     */
    public void setMAlgo(MultiPathComputationAlgorithm malgo){
    	this.malgo = malgo;
    }
    
    /**
     * @return Algorithm for multipath computation 
     */
    public MultiPathComputationAlgorithm getMAlgo(){
    	return this.malgo;
    }
}

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

package com.graph.path.algorithms.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import com.graph.elements.edge.EdgeElement;
import com.graph.path.PathElement;
import com.graph.path.algorithms.constraints.Constraint;

public class LinkExcludedPathComputationAlgorithm extends SimplePathComputationAlgorithm {

	private HashSet<EdgeElement> excludedLinks = new HashSet<EdgeElement>();
	
	public void flushExcludedLinks(){
		if (excludedLinks==null)
			excludedLinks= new HashSet<EdgeElement>();
		else
			excludedLinks.clear();
	}
	
	public void addExcludedLinks(ArrayList<EdgeElement> edges){
		Iterator <EdgeElement> iter = edges.iterator();
		while(iter.hasNext()){
			excludedLinks.add(iter.next());
		}
	}
	
	/**Function to check constraint for the inserted edge and existing path*/
	protected int checkConstraint (Constraint constraint, EdgeElement edge, PathElement path){
		if (excludedLinks.contains(edge))
			return 0;
		else
			return 1;
	}

	/**Function to check constraint for the inserted edge and existing path*/
	protected int checkConstraint (Constraint constraint, EdgeElement edge){
		if (excludedLinks.contains(edge))
			return 0;
		else
			return 1;
	}

}

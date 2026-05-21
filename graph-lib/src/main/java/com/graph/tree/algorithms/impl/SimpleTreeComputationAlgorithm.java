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

package com.graph.tree.algorithms.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.graph.elements.edge.EdgeElement;
import com.graph.elements.vertex.VertexElement;
import com.graph.graphcontroller.Gcontroller;
import com.graph.logger.GraphLogger;
import com.graph.path.PathElement;
import com.graph.path.algorithms.constraints.Constraint;
import com.graph.path.algorithms.constraints.impl.SimplePathComputationConstraint;
import com.graph.path.pathelementimpl.PathElementImpl;
import com.graph.tree.algorithms.TreeComputationAlgorithm;

public class SimpleTreeComputationAlgorithm implements TreeComputationAlgorithm {

    private static final String classIdentifier = "SimplePathComputationAlgorithm";

    /** Sort paths by ascending order of weight */
    private ArrayList<PathElement> sortPathsByWeight(ArrayList<PathElement> paths) {
	int flag = 0;
	int count = 0;
	if (paths.size() == 0)
	    return paths;
	while (flag == 0) {
	    flag = 1;
	    for (int i = paths.size() - 1; i > count; i--)
		if (paths.get(i).getPathParams().getPathWeight() < paths.get(i - 1).getPathParams().getPathWeight()) {
		    // swap elements i and i-1
		    PathElement temp = paths.remove(i - 1);
		    paths.add(i, temp);
		    flag = 0;
		}
	    count++;
	}
	return paths;
    }

    public ArrayList<PathElement> computePath(Gcontroller graph, Constraint constr) {
	ArrayList<PathElement> output = new ArrayList<PathElement>();
	// Check if constraint is of type SimplePathComputationConstraint
	if (constr.getClass() != SimplePathComputationConstraint.class) {
	    GraphLogger.logError("Invalid Constraint type used in Algorithm.", classIdentifier);
	    return null;
	}
	SimplePathComputationConstraint constraint = (SimplePathComputationConstraint) constr;
	VertexElement source = constraint.getSource();
	ArrayList<PathElement> list = new ArrayList<PathElement>();
	Iterator<EdgeElement> iter = source.getConnectedEdges().iterator();
	while (iter.hasNext()) {
	    EdgeElement edge = iter.next();
	    PathElementImpl tmp;
	    if (edge.getSourceVertex().compareTo(source) == 0) {
		tmp = new PathElementImpl(graph, constraint.getSource(), edge.getDestinationVertex());
	    } else
		tmp = new PathElementImpl(graph, constraint.getSource(), edge.getSourceVertex());
	    tmp.insertEdge(edge);
	    if (tmp.getPathParams().getAvailableCapacity() >= constr.getBw())
		list.add(tmp);
	}
	list = sortPathsByWeight(list);

	Set<VertexElement> visitedVertices = new HashSet<VertexElement>();
	visitedVertices.add(source);
	// System.out.println("Size of sorted list = " + list.size());

	while (list.size() > 0) {
	    // Extract the min cost path from the list and remove from sorted list
	    PathElementImpl temp = (PathElementImpl) list.get(0);
	    list.remove(0);

	    // Check if destination has already been visited
	    if (visitedVertices.contains(temp.getDestination()) == false) {
		// Add edge to the output array
		output.add(temp);

		// Include the destination into the list of visited vertices
		visitedVertices.add(temp.getDestination());

		VertexElement destination = temp.getDestination();
		// extend temp to its neighbours and insert into the list
		iter = destination.getConnectedEdges().iterator();
		while (iter.hasNext()) {
		    EdgeElement edge = iter.next();
		    PathElementImpl tmp;
		    VertexElement nextDestination;

		    if (edge.getSourceVertex().compareTo(destination) == 0) {
			nextDestination = edge.getDestinationVertex();
		    } else
			nextDestination = edge.getSourceVertex();
		    // Check if the destination vertex already belongs to the list of visited vertices
		    if (visitedVertices.contains(nextDestination) == false) {
			if (temp.containsVertex(nextDestination) == false) {
			    // System.out.println("Size of Edge list for new Path Element = " + temp.getTraversedEdges().size());
			    tmp = new PathElementImpl(graph, constraint.getSource(), nextDestination, temp.getTraversedEdges());
			    tmp.insertEdge(edge);
			    if (tmp.getPathParams().getAvailableCapacity()>=constr.getBw())
				list.add(tmp);
			}
		    }
		}

		// Sort paths be weight metric
		list = sortPathsByWeight(list);
		// System.out.println("Size of sorted list = " + list.size());
	    }
	}
	// Logger.logError("No Path found", classIdentifier);
	return output;
    }

}

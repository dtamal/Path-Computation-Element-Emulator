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
import java.util.Set;

import com.graph.elements.edge.EdgeElement;
import com.graph.elements.vertex.VertexElement;
import com.graph.graphcontroller.Gcontroller;
import com.graph.logger.GraphLogger;
import com.graph.path.PathElement;
import com.graph.path.algorithms.PathComputationAlgorithm;
import com.graph.path.algorithms.constraints.Constraint;
import com.graph.path.algorithms.constraints.impl.SimplePathComputationConstraint;
import com.graph.path.pathelementimpl.PathElementImpl;

public class SimplePathComputationAlgorithm implements PathComputationAlgorithm {

	private static final String classIdentifier = "SimplePathComputationAlgorithm";

	/**Sort paths by ascending order of weight*/
	protected ArrayList<PathElement> sortPaths(ArrayList<PathElement> paths){
//		StaticPathSortImpl.sortPathsByWeight(paths);
		int flag=0;
		int count=0;
		if (paths.size()==0)
			return paths;
		//bubble sorting?
		while(flag==0){
			flag=1;
			for (int i=paths.size()-1;i>count;i--)
				if (paths.get(i).getPathParams().getPathWeight()<paths.get(i-1).getPathParams().getPathWeight()){
					//swap elements i and i-1
					PathElement temp= paths.remove(i-1);
					paths.add(i, temp);
					flag=0;
				}
			count++;
		}
		return paths;
	}

	/**Function to check constraint for the inserted edge and existing path*/
	protected int checkConstraint (Constraint constraint, EdgeElement edge, PathElement path){
		return 1;
	}

	/**Function to check constraint for the inserted edge and existing path*/
	protected int checkConstraint (Constraint constraint, EdgeElement edge){
		return 1;
	}

	
	public PathElement computePath(Gcontroller graph, Constraint constr) {
		//Check if constraint is of type SimplePathComputationConstraint
		if (constr.getClass()!=SimplePathComputationConstraint.class){
			GraphLogger.logError("Invalid Constraint type used in Algorithm.", classIdentifier);
			return null;
		}
		SimplePathComputationConstraint constraint = (SimplePathComputationConstraint) constr;
		VertexElement source = graph.getVertex(constraint.getSource().getVertexID());
		if (source.getConnectedEdges()==null)
			return null;

		ArrayList<PathElement> list = new ArrayList<PathElement> ();
		Iterator<EdgeElement> iter = source.getConnectedEdges().iterator();
		System.out.println("Connected Edges From source = " + source.getConnectedEdges().size());
		while (iter.hasNext()){
			EdgeElement edge = iter.next();
			//Check Constraint for path computation
			if (checkConstraint(constraint, edge)==1){
				PathElementImpl tmp;
				if (edge.getSourceVertex().compareTo(source)==0){
					tmp = new PathElementImpl(graph, graph.getVertex(constraint.getSource().getVertexID()), edge.getDestinationVertex());
				}
				else
					tmp = new PathElementImpl(graph, graph.getVertex(constraint.getSource().getVertexID()), edge.getSourceVertex());
				tmp.insertEdge(edge);
				list.add(tmp);
			}
		}
		list = sortPaths(list);

		Set<VertexElement> visitedVertices = new HashSet<VertexElement>();
		visitedVertices.add(source);

		while(list.size()>0){
			PathElementImpl temp = (PathElementImpl)list.get(0);
			list.remove(0);
			if (visitedVertices.contains(temp.getDestination())==false){
				//Include the destination into the list of visited vertices
				visitedVertices.add(temp.getDestination());
				//If the shortest path terminates at the destination return this path
				VertexElement destination = temp.getDestination();
				//if (destination.compareTo(graph.getVertex(constraint.getDestination().getVertexID()))==0){
				if (destination.compareTo(graph.getVertex(constraint.getDestination().getVertexID()))==0)	
					return temp;
				
				//extend temp to its neighbours and insert into the list
				if (destination.getConnectedEdges()!=null){
					iter = destination.getConnectedEdges().iterator();
					while (iter.hasNext()){
						EdgeElement edge = iter.next();
						PathElementImpl tmp;
						VertexElement nextDestination;

						if (edge.getSourceVertex().compareTo(destination)==0){
							nextDestination = edge.getDestinationVertex();
						}
						else
							nextDestination = edge.getSourceVertex();
						//Check if the destination vertex already belongs to the list of visited vertices
						if (visitedVertices.contains(nextDestination)==false)
						{
							if (temp.containsVertex(nextDestination)==false){
//								System.out.println("Size of Edge list for new Path Element = " + temp.getTraversedEdges().size());
								//Check with constraint if edge can be added
								if (checkConstraint(constraint, edge, temp)==1){
									tmp = new PathElementImpl(graph, constraint.getSource(), nextDestination, temp.getTraversedEdges());
									tmp.insertEdge(edge);
									list.add(tmp);
								}
							}
						}
					}
				}
				//
				list = sortPaths(list);
//				System.out.println("Size of sorted list = " + list.size());
			}
		}
//		GraphLogger.logError("No Path found from " + source.getVertexID() + " to " + constraint.getDestination().getVertexID(), classIdentifier);
		GraphLogger.logError("No Path found from " + constraint.getSource().getVertexID() + " to " + constraint.getDestination().getVertexID(), classIdentifier);
		return null;
	}

}

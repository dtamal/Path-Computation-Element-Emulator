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

package com.graph.path.algorithms.multipath.impl;

import java.util.ArrayList;
import java.util.Iterator;

import com.graph.elements.edge.EdgeElement;
import com.graph.elements.edge.params.EdgeParams;
import com.graph.elements.edge.params.impl.BasicEdgeParams;
import com.graph.elements.vertex.VertexElement;
import com.graph.graphcontroller.Gcontroller;
import com.graph.graphcontroller.impl.GcontrollerImpl;
import com.graph.logger.GraphLogger;
import com.graph.path.PathElement;
import com.graph.path.algorithms.MultiPathComputationAlgorithm;
import com.graph.path.algorithms.common.StaticPathSortImpl;
import com.graph.path.algorithms.constraints.MultiPathConstraint;
import com.graph.path.algorithms.constraints.multipath.impl.SimpleMultiPathComputationConstraint;
import com.graph.path.pathelementimpl.PathElementImpl;
import com.graph.topology.importers.ImportTopology;
import com.graph.topology.importers.impl.SNDLibImportTopology;

public class SimpleMultiPathComputationAlgorithm implements MultiPathComputationAlgorithm {
	private static final String classIdentifier = "SimplePathComputationAlgorithm";

	/**Sort paths by ascending order of weight*/
	protected ArrayList<PathElement> sortPaths(ArrayList<PathElement> paths){
		return StaticPathSortImpl.sortPathsByWeight(paths);
	}

	/**Function to check constraint for the inserted edge and existing path*/
	protected int checkConstraint (MultiPathConstraint constraint, EdgeElement edge, PathElement path){
		return 1;
	}

	
	public ArrayList<PathElement> computePath(Gcontroller graph, MultiPathConstraint constr) {

		//Check if constraint is of type SimplePathComputationConstraint
		if (constr.getClass()!=SimpleMultiPathComputationConstraint.class){
			GraphLogger.logError("Invalid Constraint type used in Algorithm.", classIdentifier);
			return null;
		}
		SimpleMultiPathComputationConstraint constraint = (SimpleMultiPathComputationConstraint) constr;
		VertexElement source = constraint.getSource();
		ArrayList<PathElement> list = new ArrayList<PathElement> ();
		Iterator<EdgeElement> iter = source.getConnectedEdges().iterator();
		while (iter.hasNext()){
			EdgeElement edge = iter.next();
			PathElementImpl tmp;
			if (edge.getSourceVertex().compareTo(source)==0){
				tmp = new PathElementImpl(graph, constraint.getSource(), edge.getDestinationVertex());
				System.out.println(edge.getDestinationVertex().getVertexID());
			}
			else
			{
				tmp = new PathElementImpl(graph, constraint.getSource(), edge.getSourceVertex());
				System.out.println(edge.getSourceVertex().getVertexID());

			}
			tmp.insertEdge(edge);
			list.add(tmp);
		}
		list = sortPaths(list);

		//ArrayList for listing all paths to a destination
		ArrayList<PathElement> output = new ArrayList<PathElement>();

		while(list.size()>0){
			PathElementImpl temp = (PathElementImpl)list.get(0);
			list.remove(0);

			//If the path terminates at the destination return this path
			VertexElement destination = temp.getDestination();
			if (destination.compareTo(constraint.getDestination())==0){
				output.add(temp);
				if ((constraint.getPathCount()>0)&&(output.size()==constraint.getPathCount()))
						return output;
			}
			else 
			{
				//extend temp to its neighbours and insert into the list
				iter = destination.getConnectedEdges().iterator();
				while (iter.hasNext()){
					EdgeElement edge = iter.next();
					PathElementImpl tmp;

					//Check which vertex is the new destination
					VertexElement nextDestination;
					if (edge.getSourceVertex().compareTo(destination)==0){
						nextDestination = edge.getDestinationVertex();
					}
					else
						nextDestination = edge.getSourceVertex();
					
					if (temp.containsVertex(nextDestination)==false){
						//Check with constraint if edge can be added
						if (checkConstraint(constraint, edge, temp)==1){
							tmp = new PathElementImpl(graph, constraint.getSource(), nextDestination, temp.getTraversedEdges());
							tmp.insertEdge(edge);
							list.add(tmp);
						}
					}
				}
			}
			list = sortPaths(list);
		}
		if (output.size()==0)
			GraphLogger.logError("No Path found", classIdentifier);
		output=sortPaths(output);
		return output;
	}
	
	public static void main(String[] args){
		Gcontroller graph = new GcontrollerImpl();
		ImportTopology importTopology = new SNDLibImportTopology();
		importTopology.importTopology(graph, "c:\\germany50.txt");

		EdgeElement element = new EdgeElement("temp", graph.getVertex("Nuernberg"), graph.getVertex("Muenchen"), graph);
		EdgeParams params = new BasicEdgeParams(element, 1, 1, 1);
		element.setEdgeParams(params);
		graph.addEdge(element);
		
		element = new EdgeElement("temp1", graph.getVertex("Nuernberg"), graph.getVertex("Muenchen"), graph);
		params = new BasicEdgeParams(element, 1, 1, 1);
		element.setEdgeParams(params);
		graph.addEdge(element);
		
		SimpleMultiPathComputationAlgorithm algorithm = new SimpleMultiPathComputationAlgorithm();
		MultiPathConstraint constr = new SimpleMultiPathComputationConstraint(graph.getVertex("Nuernberg"), graph.getVertex("Muenchen"), 4);

		ArrayList<PathElement> paths = algorithm.computePath(graph, constr);
		
		for (int i=0;i<paths.size();i++){
			System.out.println(paths.get(i).getVertexSequence());
		}
	}

}

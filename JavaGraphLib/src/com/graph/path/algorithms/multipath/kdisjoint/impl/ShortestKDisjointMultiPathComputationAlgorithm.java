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

package com.graph.path.algorithms.multipath.kdisjoint.impl;

import java.util.ArrayList;
import java.util.Iterator;

import com.graph.elements.edge.EdgeElement;
import com.graph.elements.edge.params.EdgeParams;
import com.graph.elements.vertex.VertexElement;
import com.graph.graphcontroller.Gcontroller;
import com.graph.graphcontroller.impl.GcontrollerImpl;
import com.graph.path.PathElement;
import com.graph.path.algorithms.MultiPathComputationAlgorithm;
import com.graph.path.algorithms.PathComputationAlgorithm;
import com.graph.path.algorithms.constraints.Constraint;
import com.graph.path.algorithms.constraints.MultiPathConstraint;
import com.graph.path.algorithms.constraints.impl.SimplePathComputationConstraint;
import com.graph.path.algorithms.constraints.multipath.impl.SimpleMultiPathComputationConstraint;
import com.graph.path.algorithms.impl.SimplePathComputationAlgorithm;
import com.graph.path.pathelementimpl.PathElementImpl;
import com.graph.topology.importers.ImportTopology;
import com.graph.topology.importers.impl.TxtImportTopology;

public class ShortestKDisjointMultiPathComputationAlgorithm implements MultiPathComputationAlgorithm{


	//check if any two paths in the computed path list have any common edge sequences
	public ArrayList<PathElement> eliminateCommonEdges(ArrayList<PathElement> computedPaths){
		boolean flag=true;
		while(flag){
			flag=false;
			for (int x=0;x<computedPaths.size();x++)
			{
				PathElement currentPath=computedPaths.get(x);
				ArrayList<EdgeElement> edges1 = currentPath.getTraversedEdges();
				int s1, d1, s0, d0;
				PathElement oldPath=null;
				for (int i=0;i<edges1.size();i++){
					EdgeElement currentEdge = edges1.get(i);
					s1=i;
					//Find out already computed path which shares common edge
					int j;
					// check if other paths share a common edge
					boolean flag1=false;
					for (j=0;j<computedPaths.size();j++){
						oldPath= computedPaths.get(j);
						if (oldPath!=currentPath){
							if (oldPath.containsEdge(currentEdge)){
								flag1=true;
								break;
							}
						}
					}

					if (flag1==true)
					{
						System.out.println("Trap Found");
						flag=true;						

						//find number of consequtive links common between oldPath and currentPath
						for (j=1;j<edges1.size()-i;j++){
							if (!oldPath.containsEdge(edges1.get(j+i)))
								break;
						}

						//j indicates the number of common edges starting from i that are common between oldPath and temp1
						d1=i+j-1;

						//get start and end indices of the edges in the oldPath Element
						ArrayList<EdgeElement> edges0 = oldPath.getTraversedEdges();
						s0 = edges0.indexOf(edges1.get(d1));
						d0 = edges0.indexOf(edges1.get(s1));

						//Create new arrays for old and new path objects

						//oldPath
						ArrayList<EdgeElement> edgeSequence = new ArrayList<EdgeElement> ();
						for (j=0;j<s0;j++)
							edgeSequence.add(edges0.get(j));
						for (j=d1+1;j<edges1.size();j++)
							edgeSequence.add(edges1.get(j));
						oldPath.setEdgeSequence(edgeSequence);

						//tempPath
						edgeSequence = new ArrayList<EdgeElement> ();
						for (j=0;j<s1;j++)
							edgeSequence.add(edges1.get(j));
						for (j=d0+1;j<edges0.size();j++)
							edgeSequence.add(edges0.get(j));
						currentPath.setEdgeSequence(edgeSequence);
						x=0;
						
						break;
					}
				}
			}
		}
		return computedPaths;
	}

	public void refreshEdgeElimination(Gcontroller controller, ArrayList<PathElement> computedPaths){
		//Flush eliminationproperties for all vertices
		Iterator<VertexElement> iter = controller.getVertexSet().iterator();
		while(iter.hasNext()){
			iter.next().flushExcludedEdges();
		}
		
		Iterator<EdgeElement> iter1 = controller.getEdgeSet().iterator();
		while(iter1.hasNext()){
			EdgeParams params = iter1.next().getEdgeParams();
			if (params.getWeight()<0)
				params.setWeight(-1.0 * params.getWeight());
		}
		
		for (int i=0;i<computedPaths.size();i++){
			ArrayList<VertexElement> vertices = computedPaths.get(i).getTraversedVertices();
			ArrayList<EdgeElement> edges = computedPaths.get(i).getTraversedEdges();
			for (int j=0;j<edges.size();j++){
				vertices.get(j).excludeEdge(edges.get(j));
				EdgeParams params = edges.get(j).getEdgeParams();
				params.setWeight(-1.0 * params.getWeight());				
			}
		}
		
	}
	
	private ArrayList<PathElement> changeParentGraph(Gcontroller graph, ArrayList<PathElement> paths){
		if (paths==null)
			return null;
		if (paths.size()==0)
			return null;
		ArrayList<PathElement> newPaths = new ArrayList<PathElement>();
		
		for (int i=0;i<paths.size();i++){
			ArrayList<EdgeElement> edges = paths.get(i).getTraversedEdges();
			ArrayList <EdgeElement> newEdges = new ArrayList <EdgeElement> ();
			for (int j=0;j<edges.size();j++){
				newEdges.add(graph.getEdge(edges.get(j).getEdgeID()));
			}
			newPaths.add(new PathElementImpl(graph, graph.getVertex(paths.get(i).getSourceID()), graph.getVertex(paths.get(i).getDestinationID()), newEdges));
		}		
		return newPaths;
	}
	
	
	public ArrayList<PathElement> computePath(Gcontroller controller, MultiPathConstraint constraints) {
		Gcontroller graph =	controller.createCopy();
		
		VertexElement source = graph.getVertex(constraints.getSource().getVertexID());
		VertexElement destination = graph.getVertex(constraints.getDestination().getVertexID());
		ArrayList<PathElement> computedPaths = new ArrayList<PathElement>();

		//compute shortest Path
		Constraint constr = new SimplePathComputationConstraint(source,destination);

		PathComputationAlgorithm algo = new SimplePathComputationAlgorithm();

		for (int i=0;i<constraints.getPathCount();i++){
			PathElement temp = algo.computePath(graph, constr);
			//if single path computation has a result and the bandwidth of the 
			//computed path equals or larger than bandwidth specified in the constraints
			//then it meets the final requirement and can be added to the result arraylist
			if (temp!=null && temp.getPathParams().getAvailableCapacity()>= constraints.getBw()){
				//Modify the neighbor map 
//				System.out.println("Vertex Sequence :" + temp.getVertexSequence());
				computedPaths.add(temp);
				computedPaths = eliminateCommonEdges(computedPaths);
				refreshEdgeElimination(graph, computedPaths);
			}
			else
				break;
		}
		
		//Convert Path Elements computed on graph "graph" to those on graph "controller"
		computedPaths = changeParentGraph(controller, computedPaths);
		return computedPaths;
	}
	
	public static void main(String[] args){
		ImportTopology importT = new TxtImportTopology();
		Gcontroller graph = new GcontrollerImpl();
		importT.importTopology(graph, "c:\\trap.txt");
		
		ShortestKDisjointMultiPathComputationAlgorithm algo = new ShortestKDisjointMultiPathComputationAlgorithm();
		MultiPathConstraint constr = new SimpleMultiPathComputationConstraint(graph.getVertex("1"), graph.getVertex("8"), 2, 10);
		
		ArrayList<PathElement> paths = algo.computePath(graph, constr);
		for (int i=0;i<paths.size();i++){
			System.out.println(paths.get(i).getVertexSequence());
		}
		
		System.out.println((paths.get(0).getGraphController()==graph)?"yes":"no");
		
	}
}

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
import java.util.HashSet;
import java.util.Iterator;

import com.graph.elements.edge.EdgeElement;
import com.graph.elements.edge.params.EdgeParams;
import com.graph.elements.edge.params.impl.VirtualPathEdgeParams;
import com.graph.elements.vertex.VertexElement;
import com.graph.graphcontroller.Gcontroller;
import com.graph.graphcontroller.impl.GcontrollerImpl;
import com.graph.path.PathElement;
import com.graph.path.algorithms.MultiPathComputationAlgorithm;
import com.graph.path.algorithms.constraints.Constraint;
import com.graph.path.algorithms.constraints.MultiPathConstraint;
import com.graph.path.algorithms.constraints.impl.SimplePathComputationConstraint;
import com.graph.path.algorithms.impl.LinkExcludedPathComputationAlgorithm;
import com.graph.path.pathelementimpl.PathElementImpl;

public class KDisjointMultiPathComputationAlgorithm implements MultiPathComputationAlgorithm{



	private ArrayList<PathElement> computeKDPSet(Gcontroller controller, MultiPathConstraint constr){
		ArrayList<PathElement> paths = new ArrayList<PathElement> ();
		LinkExcludedPathComputationAlgorithm algorithm = new LinkExcludedPathComputationAlgorithm();
		Constraint constraint = new SimplePathComputationConstraint(constr.getSource(), constr.getDestination());
		algorithm.flushExcludedLinks();
		PathElement tempPath = null;
		do {
			tempPath = algorithm.computePath(controller, constraint);
			if (tempPath!=null){
				paths.add(tempPath);
				algorithm.addExcludedLinks(tempPath.getTraversedEdges());
			}
			else
				break;
		}while(true);
		return paths;
	}

	private HashSet<VertexElement> computeMergeNodes(ArrayList<PathElement> paths){
		HashSet<VertexElement> vertices = new HashSet<VertexElement>();
		for (int i=0;i<paths.size();i++){
			Iterator<VertexElement> iter = paths.get(i).getTraversedVertices().iterator();
			while(iter.hasNext()){
				VertexElement temp = iter.next();
				for (int j=i+1;j<paths.size();j++){
					if (j!=i){
						if (paths.get(j).getTraversedVertices().contains(temp)){
							vertices.add(temp);
							break;
						}
					}
				}
			}
		}
		return vertices;
	}

	private ArrayList<PathElement> getSubPaths (Gcontroller controller, ArrayList<PathElement> kdpPaths, HashSet<VertexElement> mergeNodes){
		ArrayList<PathElement> paths = new ArrayList<PathElement> ();
//		System.out.println(kdpPaths.size());

		for (int i=0;i<kdpPaths.size();i++){
			
			PathElement temp = kdpPaths.get(i);
			ArrayList<VertexElement> tempVertices = temp.getTraversedVertices();
			ArrayList<EdgeElement> tempEdges = temp.getTraversedEdges();
			ArrayList<EdgeElement> currentList = new ArrayList<EdgeElement>();
			VertexElement source = temp.getSource();
			for (int x=1;x<tempVertices.size();x++){
				currentList.add(tempEdges.get(x-1));
				if (mergeNodes.contains(tempVertices.get(x))){
						PathElement element = new PathElementImpl(controller, source, tempVertices.get(x), currentList);
						paths.add(element);
						currentList.clear();
						source = tempVertices.get(x);
				}
			}
		}
		return paths;
	}

	
	public ArrayList<PathElement> computePath(Gcontroller controller, MultiPathConstraint constraints) {
		ArrayList<PathElement> kdpPaths = computeKDPSet(controller, constraints);

		//Step 1 calculate disjoint paths by eliminating links used be subsequent shortest paths
		System.out.println("Disjoint Paths are: ");		
		for (int i=0;i<kdpPaths.size();i++){
			System.out.println(kdpPaths.get(i).getVertexSequence());
		}


		//Step 2 evaluate the merge nodes in the computed paths
		HashSet<VertexElement> mergeNodes = computeMergeNodes(kdpPaths);
		Iterator<VertexElement> iter = mergeNodes.iterator();
		System.out.println("Merge Nodes Are: ");
		while(iter.hasNext()){
			System.out.println(iter.next().getVertexID());
		}


		//Step 3 evaluate the disjoint sub-paths between the merge nodes
		ArrayList<PathElement> subPaths = getSubPaths(controller, kdpPaths, mergeNodes);
		System.out.println("Sub paths Are: ");

		for (int i=0;i<subPaths.size();i++){
			System.out.println(subPaths.get(i).getVertexSequence());
		}

		//Step 4 Create an auxillary graph using the sub paths and the merge nodes
		Gcontroller auxGraph = new GcontrollerImpl();
		//Add vertices
		iter = mergeNodes.iterator();
		VertexElement vertex;
		while(iter.hasNext()){
			vertex = iter.next().copyVertexElement(auxGraph);
			auxGraph.addVertex(vertex);
		}
		
		//add Edges
		EdgeElement edge;
		EdgeParams params;
		for (int i=0;i<subPaths.size();i++){
			VertexElement source = auxGraph.getVertex(subPaths.get(i).getSourceID());
			VertexElement dest = auxGraph.getVertex(subPaths.get(i).getDestinationID());
			edge= new EdgeElement(Integer.toString(i), source, dest, auxGraph);
			params = new VirtualPathEdgeParams(edge, subPaths.get(i));
			edge.setEdgeParams(params);	
			auxGraph.addEdge(edge);
		}
		
		
		//Get All simple paths
		
		
		return kdpPaths;				
	}


}

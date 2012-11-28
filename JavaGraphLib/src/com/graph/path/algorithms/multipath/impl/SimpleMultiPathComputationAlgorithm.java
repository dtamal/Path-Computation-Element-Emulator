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
import java.util.Random;

import com.graph.elements.edge.EdgeElement;
import com.graph.elements.vertex.VertexElement;
import com.graph.graphcontroller.Gcontroller;
import com.graph.graphcontroller.impl.GcontrollerImpl;
import com.graph.logger.GraphLogger;
import com.graph.path.PathElement;
import com.graph.path.algorithms.MultiPathComputationAlgorithm;
import com.graph.path.algorithms.common.StaticPathSortImpl;
import com.graph.path.algorithms.constraints.Constraint;
import com.graph.path.algorithms.constraints.MultiPathConstraint;
import com.graph.path.algorithms.constraints.impl.SimplePathComputationConstraint;
import com.graph.path.algorithms.constraints.multipath.impl.SimpleMultiPathComputationConstraint;
import com.graph.path.algorithms.impl.MaxBandwidthShortestPathComputationAlgorithm;
import com.graph.path.pathelementimpl.PathElementImpl;
import com.graph.resv.ResvElement;
import com.graph.topology.importers.ImportTopology;
import com.graph.topology.importers.impl.MLSNDLibImportTopology;

public class SimpleMultiPathComputationAlgorithm implements
		MultiPathComputationAlgorithm {
	private static final String classIdentifier = "SimplePathComputationAlgorithm";

	/** Sort paths by ascending order of weight */
	protected ArrayList<PathElement> sortPaths(ArrayList<PathElement> paths) {
		return StaticPathSortImpl.sortPathsByBandwidth(paths);
	}


	protected int checkConstraint(MultiPathConstraint contraint, EdgeElement edge, PathElement path){
		if(path.getTraversedEdges().size()>=8)
			return 0;
		if((edge.getEdgeParams().getAvailableCapacity()>=1)&&(path.getPathParams().getAvailableCapacity()>=1))
			return 1;
		else return 0;
	}
	
	/** Function to check constraint for the inserted edge and existing path */
	protected int checkConstraint(MultiPathConstraint constraint,
			EdgeElement edge) {
		if (edge.getEdgeParams().getAvailableCapacity() >= 1)
			return 1;
		else
			return 0;
	}

	public ArrayList<PathElement> computePath(Gcontroller graph,
			MultiPathConstraint constr) {
		// Check if constraint is of type SimplePathComputationConstraint
		if (constr.getClass() != SimpleMultiPathComputationConstraint.class) {
			GraphLogger.logError("Invalid Constraint type used in Algorithm.",
					classIdentifier);
			return null;
		}

		SimpleMultiPathComputationConstraint constraint = (SimpleMultiPathComputationConstraint) constr;

		if (constr.getPathCount() == 1) {
			MaxBandwidthShortestPathComputationAlgorithm algo = new MaxBandwidthShortestPathComputationAlgorithm();
			Constraint constr1 = new SimplePathComputationConstraint(
					constr.getSource(), constr.getDestination(), constr.getBw());
			PathElement temp = algo.computePath(graph, constr1);
			if (temp == null)
				return null;
			else
				temp.getPathParams().setReserve(constr1.getBw());
			ArrayList<PathElement> temp1 = new ArrayList<PathElement>();
			temp1.add(temp);
			return temp1;
		}
		VertexElement source = graph.getVertex(constraint.getSource()
				.getVertexID());
		if (constraint.getSource() == null) {
			System.out.println("error");
			System.exit(-1);
		}
		
		System.out.println("Source not NULL");
		
		ArrayList<PathElement> list = new ArrayList<PathElement>();
		Iterator<EdgeElement> iter = source.getConnectedEdges().iterator();
		while (iter.hasNext()) {
			EdgeElement edge = iter.next();
			PathElementImpl tmp;
			if (edge.getSourceVertex().compareTo(source) == 0) {
				tmp = new PathElementImpl(graph, constraint.getSource(),
						edge.getDestinationVertex());
			} else {
				tmp = new PathElementImpl(graph, constraint.getSource(),
						edge.getSourceVertex());
			}
			tmp.insertEdge(edge);
			list.add(tmp);
		}
		list = sortPaths(list);

		// ArrayList for listing all paths to a destination
		ArrayList<PathElement> output = new ArrayList<PathElement>();
		while (list.size() > 0) {
			PathElementImpl temp = (PathElementImpl) list.get(0);
			list.remove(0);

			// If the path terminates at the destination return this path
			VertexElement destination = temp.getDestination();
			if (destination.compareTo(graph.getVertex(constraint
					.getDestination().getVertexID())) == 0) {
				output.add(temp);
				if ((constraint.getPathCount() > 0)
						&& (output.size() == constraint.getPathCount()))
					return modifyPathElements(output, constr.getBw());
			} else {
				// extend temp to its neighbours and insert into the list
				iter = destination.getConnectedEdges().iterator();
				
				while (iter.hasNext()) {
					
					EdgeElement edge = iter.next();
					PathElementImpl tmp;

					// Check which vertex is the new destination
					VertexElement nextDestination;
					if (edge.getSourceVertex().compareTo(destination) == 0) {
						nextDestination = edge.getDestinationVertex();
					} else
						nextDestination = edge.getSourceVertex();

					if (temp.containsVertex(nextDestination) == false) {
						// Check with constraint if edge can be added
						if (checkConstraint(constraint, edge, temp) == 1) {
							tmp = new PathElementImpl(graph,
									constraint.getSource(), nextDestination,
									temp.getTraversedEdges());
							tmp.insertEdge(edge);
							list.add(tmp);
						}
					}
				}
			}
			list = sortPaths(list);
		}
		if (output.size() == 0) {
			GraphLogger.logError("No Path found", classIdentifier);
			return null;
		}
		output = sortPaths(output);
		return modifyPathElements(output, constr.getBw());
	}

	public ArrayList<PathElement> modifyPathElements(
			ArrayList<PathElement> paths, double bw) {
		
		ArrayList<PathElement> finalOutput = null;
		ArrayList<ResvElement> resv;

		boolean delayFlag = true;
		double totCapacityReq = bw;
		double totCapacity;
		if (paths.get(0).getPathParams().getAvailableCapacity() >= totCapacityReq) {
			PathElement element = paths.get(0);
			element.getPathParams().setReserve(totCapacityReq);
			finalOutput = new ArrayList<PathElement>();
			finalOutput.add(element);
		} else {
			for (int i = 0; i < paths.size(); i++) {
				resv = new ArrayList<ResvElement>();

				totCapacity = paths.get(i).getPathParams()
						.getAvailableCapacity();
				ResvElement temp = new ResvElement(paths.get(i), paths.get(i)
						.getPathParams().getAvailableCapacity());
				temp.resv();
				resv.add(temp);
				for (int j = 0; j < paths.size(); j++) {
					if (j == i)
						continue;
					// Check if some other path still has available capacity
					// after the first path would have been reserved
					if (paths.get(j).getPathParams().getAvailableCapacity() > 0) {
						// check if the about to be added path has a DD to all
						// other path, which is already in the resvList, is
						// larger than 128
						// if yes, this can not be added to the resvList.
						for (int k = 0; k < resv.size(); k++) {
							if (Math.abs(paths.get(j).getPathParams()
									.getPathDelay()
									- resv.get(k).getElement().getPathParams()
											.getPathDelay()) > 128) {
								delayFlag = false;
								break;
							}
						}
						// if the path has larger DD to any path in the
						// resvList, continue the outer for loop to check other
						// paths.
						if (!delayFlag)
							continue;

						if (paths.get(j).getPathParams().getAvailableCapacity() < totCapacityReq
								- totCapacity) {
							totCapacity += paths.get(j).getPathParams()
									.getAvailableCapacity();
							ResvElement temp1 = new ResvElement(paths.get(j),
									paths.get(j).getPathParams()
											.getAvailableCapacity());
							temp1.resv();
							resv.add(temp1);
						} else {
							ResvElement temp1 = new ResvElement(paths.get(j),
									totCapacityReq - totCapacity);
							totCapacity += totCapacityReq - totCapacity;
							temp1.resv();
							resv.add(temp1);
						}
					}
					if (totCapacity >= totCapacityReq)
						break;
				}
				if (totCapacity >= totCapacityReq) {
					// reservation found
					finalOutput = new ArrayList<PathElement>();
					for (int k = 0; k < resv.size(); k++) {
						PathElement element = resv.get(k).getElement();
						element.getPathParams().setReserve(resv.get(k).getBw());
						finalOutput.add(element);
						resv.get(k).release();
					}
					break;
				} else {
					// reservation not found
					for (int k = 0; k < resv.size(); k++) {
						resv.get(k).release();
					}
				}
			}
		}
		return finalOutput;
	}

	public static void main(String[] args) {
		Gcontroller graph = new GcontrollerImpl();
		ImportTopology importTopology = new MLSNDLibImportTopology();
		importTopology.importTopology(graph, ".//germany50.txt");
		Random random = new Random();
		int src,dest;
		int j = 0;
		int loopCount = 0;
		while (j<=loopCount) {
			src = random.nextInt(50)+1;
			dest = random.nextInt(50)+1;
			while(dest == src){
				dest = random.nextInt(51);
			}
			src = 24;
			dest = 8;
			
			j++;
		}
	}

}

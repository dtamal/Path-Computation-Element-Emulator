package com.graph.elements.vertex.algorithms;

import com.graph.elements.vertex.VertexElement;
import com.graph.elements.vertex.algorithms.contraints.VertexConstraint;
import com.graph.graphcontroller.Gcontroller;

/**
 * Algorithm for searching vertex element(s)
 * 
 * @author Yuesheng Zhong
 *
 */
public interface VertexAlgorithm {
	
	/**
	 * @param controller
	 * @param constraint
	 * @return
	 */
	public VertexElement searchVertex(Gcontroller controller, VertexConstraint constraint);
}

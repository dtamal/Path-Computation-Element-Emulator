package com.graph.elements.vertex.algorithms.impl;

import java.util.ArrayList;
import java.util.Set;

import com.graph.elements.vertex.VertexElement;
import com.graph.elements.vertex.algorithms.VertexAlgorithm;
import com.graph.elements.vertex.algorithms.contraints.VertexConstraint;
import com.graph.elements.vertex.params.ITResourceVertexParams;
import com.graph.graphcontroller.Gcontroller;

/**
 * Algorithm for searching single vertex element using a certain constraint
 * 
 * @author Yuesheng Zhong
 *
 */
public class SingleVertexAlgorithmImpl implements
		VertexAlgorithm {

	/* (non-Javadoc)
	 * @see com.graph.elements.vertex.algorithms.VertexAlgorithm#searchVertex(com.graph.graphcontroller.Gcontroller, com.graph.elements.vertex.algorithms.contraints.VertexConstraint)
	 */
	@Override
	public VertexElement searchVertex(Gcontroller controller,
			VertexConstraint constraint) {
		// TODO Auto-generated method stub
		ITResourceVertexParams params = null;
		ArrayList<VertexElement> elements = controller.getBorderNodeVertexElements();
		for(VertexElement testElement : elements){
			params = (ITResourceVertexParams)testElement.getVertexParams();
			System.out.println("availabe CPU : " + params.getAvailableCPU()+", required CPU : " + constraint.getCPU());
			System.out.println("availabe RAM : " + params.getAvailableRAM()+", required RAM : " + constraint.getRAM());
			System.out.println("availabe STO : " + params.getAvailableSTORAGE()+", required STO : " + constraint.getSTORAGE());
			if((params.getAvailableCPU() >= constraint.getCPU()) && (params.getAvailableRAM() >= constraint.getRAM()) && (params.getAvailableSTORAGE() >= constraint.getSTORAGE())){
				return testElement;
			}
		}
		return null;
	}

}

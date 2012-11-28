package com.graph.elements.vertex.algorithms.contraints.impl;

import com.graph.elements.vertex.algorithms.contraints.VertexConstraint;

/**
 * Constraint for searching single vertex element meeting requirement(constraint)
 * @author Yuesheng Zhong
 *
 */
public class SingleVertexConstraint implements VertexConstraint {

	private int cpu;
	private int ram;
	private int storage;
	
	public SingleVertexConstraint(){}
	
	public SingleVertexConstraint(int cpu, int ram, int storage){
		this.cpu = cpu;
		this.ram = ram;
		this.storage = storage;
	}
	@Override
	public int getCPU() {
		// TODO Auto-generated method stub
		return this.cpu;
	}

	@Override
	public int getRAM() {
		// TODO Auto-generated method stub
		return this.ram;
	}

	@Override
	public int getSTORAGE() {
		// TODO Auto-generated method stub
		return this.storage;
	}
	@Override
	public void setCPU(int cpu) {
		// TODO Auto-generated method stub
		this.cpu = cpu;
	}
	@Override
	public void setRAM(int ram) {
		// TODO Auto-generated method stub
		this.ram = ram;
	}
	@Override
	public void setSTORAGE(int storage) {
		// TODO Auto-generated method stub
		this.storage = storage;
	}

}

package com.graph.elements.vertex.algorithms.contraints;

/**
 * Constraint for searching vertex 
 * @author Yuesheng Zhong
 *
 */
public interface VertexConstraint {
	/** CPU Constraint */
	public void setCPU(int cpu);
	public int getCPU();
	
	/** RAM Constraint */
	public void setRAM(int ram);
	public int getRAM();
	
	/** STORAGE Constraint*/
	public void setSTORAGE(int storage);
	public int getSTORAGE();
}

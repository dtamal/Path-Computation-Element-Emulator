package com.graph.elements.vertex.params;

import com.graph.elements.vertex.VertexElement;

/**
 * Extension of VertexParams with ITResource support
 * 
 * @author Yuesheng Zhong
 *
 */
public class ITResourceVertexParams extends VertexParams{
	
	private VertexElement element;
	
	private String switchID;
	private int freePorts;
	 
	private double bw;
	private int cpu;
	private int ram;
	private int storage;
	
	private int availableCPU;
	private int availableRAM;
	private int availableSTORAGE;
	
	
	
	/**
	 * @param element
	 * @param switchID
	 * @param freePorts
	 * @param cpu
	 * @param ram
	 * @param storage
	 * @param bw
	 */
	public ITResourceVertexParams(VertexElement element, String switchID, int freePorts, int cpu, int ram, int storage, double bw){
		this.element = element;
		this.switchID = switchID;
		this.freePorts = freePorts;
		this.cpu = cpu;
		this.availableCPU = cpu;
		this.ram = ram;
		this.availableRAM = ram;
		this.storage = storage;
		this.availableSTORAGE = storage;
		this.bw = bw;
	}
	
	/* (non-Javadoc)
	 * @see com.graph.elements.vertex.params.VertexParams#setVertexElement(com.graph.elements.vertex.VertexElement)
	 */
	public void setVertexElement(VertexElement element){
		this.element = element;
	}
	
	/**
	 * @param switchID
	 */
	public void setSwitchID(String switchID) {
		this.switchID = switchID;
	}
	
	/**
	 * @return
	 */
	public String getSwitchID() {
		return switchID;
	}
	
	/* (non-Javadoc)
	 * @see com.graph.elements.vertex.params.VertexParams#getVertexElement()
	 */
	public VertexElement getVertexElement(){
		return this.element;
	}
	
	/**
	 * @param freePorts
	 */
	public void setFreePorts(int freePorts){
		this.freePorts = freePorts;
	}
	
	/* (non-Javadoc)
	 * @see com.graph.elements.vertex.params.VertexParams#getFreePorts()
	 */
	public int getFreePorts(){
		return this.freePorts;
	}
	
	/**
	 * @param cpu
	 */
	public void setCpu(int cpu){
		this.cpu = cpu;
	}
	
	/**
	 * @return
	 */
	public int getCpu(){
		return this.cpu;
	}
	
	/**
	 * @param ram
	 */
	public void setRam(int ram){
		this.ram = ram;
	}
	
	/**
	 * @return
	 */
	public int getRam(){
		return this.ram;
	}
	
	/**
	 * @param storage
	 */
	public void setStorage(int storage){
		this.storage = storage;
	}
	
	/**
	 * @return
	 */
	public int getStorage(){
		return this.storage;
	}

	/**
	 * @param bw
	 */
	public void setBw(double bw){
		this.bw = bw;
	}
	
	/**
	 * @return
	 */
	public double getBw(){
		return this.bw;
	}
	
	/**
	 * @return
	 */
	public int getAvailableCPU(){
		return this.availableCPU;
	}
	
	/**
	 * @return
	 */
	public int getAvailableRAM(){
		return this.availableRAM;
	}
	
	/**
	 * @return
	 */
	public int getAvailableSTORAGE(){
		return this.availableSTORAGE;
	}
	
	/**
	 * @return
	 */
	public int getUsedCPU(){
		return this.cpu - this.availableCPU;
	}
	
	/**
	 * @return
	 */
	public int getUsedRAM(){
		return this.ram - this.availableRAM;
	}
	
	/**
	 * @return
	 */
	public int getUsedSTORAGE(){
		return this.storage - this.availableSTORAGE;
	}
	
	/**
	 * @param cpu
	 * @param ram
	 * @param storage
	 * @return
	 */
	public boolean reserveITResource(int cpu, int ram, int storage){
		if(this.availableCPU < cpu){
			System.out.println("request cpu is larger than available cpu on this vertex, request denied!");
			return false;
		}
		if(this.availableRAM < ram){
			System.out.println("request ram is larger than available ram on this vertex, request denied!");
			return false;
		}
		if(this.availableSTORAGE < storage){
			System.out.println("request storage is larger than available storage on this vertex, request denied!");
			return false;
		}
		this.availableCPU -= cpu;
		this.availableRAM -= ram;
		this.availableSTORAGE -= storage;
		
		return true;
	}
	
	/**
	 * @param cpu
	 * @param ram
	 * @param storage
	 * @return
	 */
	public boolean releaseITResource(int cpu, int ram, int storage){
		if(this.getUsedCPU() < cpu){
			System.out.println("request cpu is larger than TOTAL USED CPU on this vertex, request denied!");
			return false;
		}
		if(this.getUsedRAM() < ram){
			System.out.println("request RAM is larger than TOTAL USED RAM on this vertex, request denied!");
			return false;
		}
		if(this.getUsedSTORAGE() < storage){
			System.out.println("request STORAGE is larger than TOTAL USED STORAGE on this vertex, request denied!");
			return false;
		}
		
		this.availableCPU += cpu;
		this.availableRAM += ram;
		this.availableSTORAGE += storage;
		
		return true;
	}
	
	/* (non-Javadoc)
	 * @see com.graph.elements.vertex.params.VertexParams#copyVertexParams(com.graph.elements.vertex.VertexElement)
	 */
	@Override
	public VertexParams copyVertexParams(VertexElement newVertex) {
		// TODO Auto-generated method stub
		ITResourceVertexParams copy = new ITResourceVertexParams(newVertex,this.switchID,this.freePorts,this.cpu,this.ram,this.storage,this.bw);
		return copy;
	}

	/* (non-Javadoc)
	 * @see com.graph.elements.vertex.params.VertexParams#getType()
	 */
	@Override
	public String getType() {
		return "itVertexParams";
	}
}

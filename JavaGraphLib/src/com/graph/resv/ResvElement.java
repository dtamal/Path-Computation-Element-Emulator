package com.graph.resv;

import com.graph.path.PathElement;

public class ResvElement {
	private PathElement element;
	
	private double bw;
	
	public ResvElement (PathElement element, double bw){
		this.bw = bw;
		this.element = element;
	}

	/**
	 * @return the element
	 */
	public PathElement getElement() {
		return element;
	}

	/**
	 * @param element the element to set
	 */
	public void setElement(PathElement element) {
		this.element = element;
	}

	/**
	 * @return the bw
	 */
	public double getBw() {
		return bw;
	}

	/**
	 * @param bw the bw to set
	 */
	public void setBw(double bw) {
		this.bw = bw;
	}
	
	public boolean resv(){
		return element.resvBandwidth(bw);
	}
	
	public boolean release(){
		return element.releaseBandwidth(bw);
	}
}

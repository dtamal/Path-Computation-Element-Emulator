package com.graph.elements.vertex.params;

import com.graph.elements.vertex.VertexElement;

public class BasicVertexParams extends VertexParams{

	public String getSwitchID() {
		return switchID;
	}
 
	public void setSwitchID(String switchID) {
		this.switchID = switchID;
	}

	public int getFreePorts() {
		return freePorts;
	}

	public void setFreePorts(int freePorts) {
		this.freePorts = freePorts;
	}

	private String switchID;

	private int freePorts;
	
	@Override
	public VertexParams copyVertexParams(VertexElement newVertex) {
		BasicVertexParams params = new BasicVertexParams();
		params.setVertexElement(newVertex);
		params.setFreePorts(this.freePorts);
		params.setSwitchID(switchID);
		return params;
	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return "BasicVertexParams";
	}
	

}

package com.pcee.client.resv;

import java.util.LinkedList;

import com.graph.graphcontroller.Gcontroller;
import com.pcee.protocol.message.objectframe.impl.PCEPExplicitRouteObject;

public class SinglePathResvElement extends ResvElement {

	public LinkedList<PCEPExplicitRouteObject> objectList;
	
	public SinglePathResvElement(String nextID, Gcontroller controller,
			String string, String string2, double startTime, double endTime,
			double bw) {
		graph = controller;
		this.ID = nextID;
		this.bw= bw;
		this.startTime = startTime;
		this.endTime = endTime;
		this.sourceID = string;
		this.destID = string2;	
	}

	@Override
	public boolean reserveConnection() {
		// TODO Auto-generated method stub
		
		return false;
	}

	@Override
	public boolean releaseConnection() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public double getPathDelay() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean reserveConnection1P1() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean releaseConnectionAndITResource() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean reserveConnectionWithITResource() {
		// TODO Auto-generated method stub
		return false;
	}

}

package com.pcee.client.connectionsource;

import java.util.Random;

import com.graph.graphcontroller.Gcontroller;
import com.pcee.client.event.eventhandler.EventHandler;
import com.pcee.client.event.impl.ReserveConnection;
import com.pcee.client.resv.ResvElement;
import com.pcee.client.resv.SinglePathResvElement;


public class SinglePathExponentialSource extends Source{

	int ID = 0;
	int bwLow;
	
	int bwHigh;
	public String getNextID(){
		ID++;
		return Integer.toString(ID);
	}

	@Override
	public void initSource() {      
		// TODO Auto-generated method stub

	}

	@Override
	public void nextConnection(boolean currentResvStatus) {
		double bw = random.nextInt(bwHigh-bwLow +1) + bwLow;
		
		String[] nodeArray = controller.getVertexIDSet().toArray(new String[1]);
		//generate 2 random numbers
		int source = random.nextInt(nodeArray.length);
		int destination;
		do {
			destination = random.nextInt(nodeArray.length);
		}while(destination==source);

		double startTime = EventHandler.getTime() + this.getInterArrivalTime();
		double endTime = startTime + this.getHoldingTime();

		ResvElement element = new SinglePathResvElement(getNextID(), controller, nodeArray[source], nodeArray[destination], startTime , endTime, bw);

		EventHandler.addEvent(new ReserveConnection(element, this));
	}

	int totConnections = 0;

	int blockedConnections = 0;



	private double interArrivalTime, holdingTime;


	private Random random;

	public SinglePathExponentialSource(Gcontroller controller, double interArrivalTime, double holdingTime, String logFile, int bwLow, int bwHigh){
		this.controller = controller;
		this.random = new Random(10);
		this.interArrivalTime = interArrivalTime;
		this.holdingTime = holdingTime;
		this.logFile = logFile;
		this.bwLow = bwLow;
		this.bwHigh = bwHigh;
	}




	public double getHoldingTime(){
		double u = random.nextDouble();
		return -1.0 * Math.log(u) * holdingTime;
	}



	public double getInterArrivalTime(){
		double u = random.nextDouble();
		return -1.0 * Math.log(u) * interArrivalTime;
	}


	@Override
	public void connectionBlocked() {
		if (EventHandler.getTime() > 3000) {
			this.blockedConnections ++;
			this.totConnections++;
		}
		//		System.out.println("Blocked Connections = " + blockedConnections);
		//		System.out.println("Total Connections = " + totConnections);
	}

	@Override
	public void connectionReserved() {
		if (EventHandler.getTime() > 3000) {
			this.totConnections++;		
		}
		//		System.out.println(s"Total Connections = " + totConnections);
	}

	@Override
	public int getTotConnections() {
		return totConnections;
	}

	@Override
	public double getBlockingProbability() {
		return (double)blockedConnections / (double) totConnections ;
	}

	@Override
	public int getBlockedConnections() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void nextRequest(boolean currentResvStatus) {
		// TODO Auto-generated method stub
		
	}


}

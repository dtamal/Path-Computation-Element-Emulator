package com.pcee.client.connectionsource;

import java.util.Random;

import com.graph.graphcontroller.Gcontroller;
import com.pcee.client.event.eventhandler.EventHandler;
import com.pcee.client.event.impl.ReserveConnection;
import com.pcee.client.resv.MultiPathResvElement;
import com.pcee.client.resv.ResvElement;

public class MultiPathExponentialSource extends Source {

	int ID = 0;
	int bwLow;
	int bwHigh;

	public String getNextID() {
		ID++;
		return Integer.toString(ID);
	}

	@Override
	public void initSource() {
		// TODO Auto-generated method stub
	}

	@Override
	public void nextConnection(boolean currentResvStatus) {
		double bw = random.nextInt(bwHigh - bwLow + 1) + bwLow;
		System.out.println("New request :" + getNextID() + " inside nextConnection()");
		String[] nodeArray = controller.getVertexIDSet().toArray(new String[1]);

		// generate 2 random numbers with the length of node numbers in the
		// topology in order to get random node using index numbers
		int source = random.nextInt(nodeArray.length);
		int destination;
		do {
			destination = random.nextInt(nodeArray.length);
		} while (destination == source);

		// Prepare the Start and End Time of each Reserve Connection Request
		double startTime = EventHandler.getTime() + this.getInterArrivalTime();
		double endTime = startTime + this.getHoldingTime();

		// get a ResvElement for the reserve request
		ResvElement element = new MultiPathResvElement(getNextID(), controller, nodeArray[source], nodeArray[destination], startTime, endTime, bw);

		// And the generated Element into the event queue waiting its execution
		EventHandler.addEvent(new ReserveConnection(element, this));
	}

	int totConnections = 0;
	int blockedConnections = 0;

	private double interArrivalTime, holdingTime;
	private Random random;

	public MultiPathExponentialSource(Gcontroller controller, double interArrivalTime, double holdingTime, String logFile, int bwLow, int bwHigh) {
		this.controller = controller;
		this.random = new Random();
		this.interArrivalTime = interArrivalTime;
		this.holdingTime = holdingTime;
		this.logFile = logFile;
		this.bwLow = bwLow;
		this.bwHigh = bwHigh;
	}

	public double getHoldingTime() {
		double u = random.nextDouble();
		return -1.0 * Math.log(u) * holdingTime;
	}

	public double getInterArrivalTime() {
		double u = random.nextDouble();
		return -1.0 * Math.log(u) * interArrivalTime;
	}

	@Override
	public void connectionBlocked() {
		this.blockedConnections++;
		this.totConnections++;
		System.out.println("====================================");
		System.out.println(" Blocked Connections: " + this.blockedConnections);
		System.out.println("====================================");
	}

	@Override
	public void connectionReserved() {
		this.totConnections++;
		System.out.println("Total Connection: " + this.totConnections);
	}

	@Override
	public int getTotConnections() {
		return totConnections;
	}

	public int getBlockedConnections() {
		return blockedConnections;
	}

	@Override
	public double getBlockingProbability() {
		return (double) blockedConnections / (double) totConnections;
	}

	@Override
	public void nextRequest(boolean currentResvStatus) {
		// TODO Auto-generated method stub

	}
}

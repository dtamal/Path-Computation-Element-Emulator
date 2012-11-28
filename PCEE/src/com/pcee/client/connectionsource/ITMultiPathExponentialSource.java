package com.pcee.client.connectionsource;

import java.util.Random;

import com.global.GlobalCfg;
import com.graph.graphcontroller.Gcontroller;
import com.pcee.client.ClientTest;
import com.pcee.client.event.eventhandler.EventHandler;
import com.pcee.client.event.impl.ReserveConnection;
import com.pcee.client.resv.ITMultiPathResvElement;
import com.pcee.client.resv.MultiPathResvElement;
import com.pcee.client.resv.ResvElement;
import com.pcee.protocol.message.objectframe.impl.PCEPGenericExplicitRouteObjectImpl;
import com.pcee.protocol.message.objectframe.impl.erosubobjects.PCEPAddress;
import com.pcee.protocol.response.PCEPResponseFrame;

/**
 * ExponentialSource with ITResource support
 * 
 * @author Yuesheng Zhong
 * 
 */
public class ITMultiPathExponentialSource extends Source {

	int ID = 0;
	int bwLow;
	int bwHigh;

	int cpu, ram, storage;
	int totConnections = 0;
	int blockedConnections = 0;

	private double interArrivalTime, holdingTime;
	private Random random;

	/**
	 * @param controller
	 * @param interArrivalTime
	 * @param holdingTime
	 * @param logFile
	 * @param bwLow
	 * @param bwHigh
	 */
	public ITMultiPathExponentialSource(Gcontroller controller, double interArrivalTime, double holdingTime, String logFile, int bwLow, int bwHigh) {
		this.controller = controller;
		this.random = new Random();
		this.interArrivalTime = interArrivalTime;
		this.holdingTime = holdingTime;
		this.logFile = logFile;
		this.bwLow = bwLow;
		this.bwHigh = bwHigh;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.pcee.client.connectionsource.Source#nextConnection(boolean)
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.pcee.client.connectionsource.Source#nextRequest(boolean)
	 */
	/** prepare for the next path computation request with it resource support */
	public void nextRequest(boolean currentResvStatus) {
		double bw = random.nextInt(bwHigh - bwLow + 1) + bwLow;
		cpu = random.nextInt(GlobalCfg.cpuHigh - GlobalCfg.cpuLow + 1) + GlobalCfg.cpuLow;
		ram = random.nextInt(GlobalCfg.ramHigh - GlobalCfg.ramLow + 1) + GlobalCfg.ramLow;
		storage = random.nextInt(GlobalCfg.storageHigh - GlobalCfg.storageLow + 1) + GlobalCfg.storageLow;

		System.out.println("New request :" + getNextID() + " inside nextConnection()");
		String[] nodeArray = controller.getVertexIDSet().toArray(new String[1]);

		// generate 2 random numbers with the length of node numbers in the
		// topology in order to get random node using index numbers
		String source;
		String destination;

		// Call function in ITResourceLauncher to get vertex meeting it resource
		// requirement
		System.out.println("Making Vertex finding request to server with it resource requirement!");
		System.out.println("\n\n\n");
		PCEPResponseFrame vertexFrame = ClientTest.getVertex(cpu, ram, storage);
		while (vertexFrame.containsNoVertexObject()) {
			System.out.println("ERROR: Request to get vertex with required it resource failed: CPU-" + cpu + " , RAM-" + ram + " , STORAGE-" + storage);
			cpu = random.nextInt(GlobalCfg.cpuHigh - GlobalCfg.cpuLow + 1) + GlobalCfg.cpuLow;
			ram = random.nextInt(GlobalCfg.ramHigh - GlobalCfg.ramLow + 1) + GlobalCfg.ramLow;
			storage = random.nextInt(GlobalCfg.storageHigh - GlobalCfg.storageLow + 1) + GlobalCfg.storageLow;
			vertexFrame = ClientTest.getVertex(cpu, ram, storage);
		}
		
		
		System.out.println("Vertex with address " + ((PCEPAddress) ((PCEPGenericExplicitRouteObjectImpl) vertexFrame.extractExplicitRouteObjectList().getFirst()).getTraversedVertexList().get(0)).getIPv4Address(false) + " exists, preparing to make path computation request...");

		// get the returned vertex address as destination address
		
		PCEPGenericExplicitRouteObjectImpl ERO = (PCEPGenericExplicitRouteObjectImpl) vertexFrame.extractExplicitRouteObjectList().get(0);
		PCEPAddress destAddress = (PCEPAddress) ERO.getTraversedVertexList().get(0);
		destination = destAddress.getIPv4Address(false);

		// determine the source address as self destination, source address
		// should be different from destination address
		do {
			source = nodeArray[random.nextInt(nodeArray.length)];
		} while (source.equals(destination));

		// Prepare the Start and End Time of each Reserve Connection Request
		double startTime = EventHandler.getTime() + this.getInterArrivalTime();
		double endTime = startTime + this.getHoldingTime();

		// get a ResvElement for the reserve request
		ResvElement element = new ITMultiPathResvElement(getNextID(), controller, source, destination, cpu, ram, storage, startTime, endTime, bw);

		// And the generated Element into the event queue waiting its execution
		EventHandler.addEvent(new ReserveConnection(element, this));
	}

	/**
	 * @return
	 */
	public double getHoldingTime() {
		double u = random.nextDouble();
		return -1.0 * Math.log(u) * holdingTime;
	}

	/**
	 * @return
	 */
	public double getInterArrivalTime() {
		double u = random.nextDouble();
		return -1.0 * Math.log(u) * interArrivalTime;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.pcee.client.connectionsource.Source#connectionBlocked()
	 */
	@Override
	public void connectionBlocked() {
		this.blockedConnections++;
		this.totConnections++;
		System.out.println("====================================");
		System.out.println(" Blocked Connections: " + this.blockedConnections);
		System.out.println("====================================");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.pcee.client.connectionsource.Source#connectionReserved()
	 */
	@Override
	public void connectionReserved() {
		this.totConnections++;
		System.out.println("Total Connection: " + this.totConnections);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.pcee.client.connectionsource.Source#getTotConnections()
	 */
	@Override
	public int getTotConnections() {
		return totConnections;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.pcee.client.connectionsource.Source#getBlockedConnections()
	 */
	public int getBlockedConnections() {
		return blockedConnections;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.pcee.client.connectionsource.Source#getBlockingProbability()
	 */
	@Override
	public double getBlockingProbability() {
		return (double) blockedConnections / (double) totConnections;
	}

	/**
	 * @param logString
	 */
	public void log(String logString) {
		System.out.println("ITSinglePathExponentialSource::: " + logString);
	}

	/**
	 * @return
	 */
	public String getNextID() {
		ID++;
		return Integer.toString(ID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.pcee.client.connectionsource.Source#initSource()
	 */
	@Override
	public void initSource() {
		// TODO Auto-generated method stub
	}

}

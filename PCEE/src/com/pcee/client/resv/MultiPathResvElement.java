package com.pcee.client.resv;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;

import com.graph.graphcontroller.Gcontroller;
import com.pcee.client.ClientTest;
import com.pcee.client.connectionsource.Source;
import com.pcee.protocol.message.objectframe.impl.PCEPBandwidthObject;
import com.pcee.protocol.message.objectframe.impl.PCEPExplicitRouteObject;
import com.pcee.protocol.message.objectframe.impl.PCEPGenericExplicitRouteObjectImpl;
import com.pcee.protocol.message.objectframe.impl.erosubobjects.EROSubobjects;
import com.pcee.protocol.message.objectframe.impl.erosubobjects.PCEPAddress;
import com.pcee.protocol.response.PCEPResponseFrame;

public class MultiPathResvElement extends ResvElement {

	public LinkedList<PCEPExplicitRouteObject> objectList;
	public LinkedList<PCEPBandwidthObject> bwList;

	/**
	 * Without ITResource Support
	 * 
	 * @param nextID
	 * @param controller
	 * @param source
	 * @param destination
	 * @param startTime
	 * @param endTime
	 * @param bw
	 */
	public MultiPathResvElement(String nextID, Gcontroller controller, String source, String destination, double startTime, double endTime, double bw) {
		graph = controller;
		this.ID = nextID;
		this.bw = bw;
		this.startTime = startTime;
		this.endTime = endTime;
		this.sourceID = source;
		this.destID = destination;
	}

	/**
	 * With ITResource Support
	 * 
	 * @param nextID
	 * @param controller
	 * @param source
	 * @param destination
	 * @param cpu
	 * @param ram
	 * @param storage
	 * @param startTime
	 * @param endTime
	 * @param bw
	 */
	public MultiPathResvElement(String nextID, Gcontroller controller, String source, String destination, int cpu, int ram, int storage, double startTime, double endTime, double bw) {
		graph = controller;
		this.ID = nextID;
		this.bw = bw;
		this.startTime = startTime;
		this.endTime = endTime;
		this.sourceID = source;
		this.destID = destination;
		this.cpu = cpu;
		this.ram = ram;
		this.storage = storage;
	}

	/** for including it resource scenario */
	public boolean reserveConnectionWithITResource() {
		if (!reserveConnection1P1())
			return false;

		try {// do the reservation work with specified bandwidth on returned
				// EROs
			System.out.println("||||Reserving ITResource in vertex \n::" + destID + " with ITResource \n\t:: CPU-" + cpu + "\n\t:: RAM-" + ram + "\n\t:: STORAGE-" + storage);
			Source.writer.write(reserveVertex(destID, cpu, ram, storage) + "\n");
			Source.writer.flush();

			Thread.sleep(100);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return true;
	}

	/** for including it resource scenario */
	public boolean releaseConnectionAndITResource() {
		if (!releaseConnection())
			return false;

		try {// do the release work with specified bandwidth on EROs that have
				// been reserved
			System.out.println("||||Releasing ITResource in vertex \n:: " + destID + ".");
			Source.writer.write(releaseVertex(destID, cpu, ram, storage) + "\n");
			Source.writer.flush();

			Thread.sleep(100);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return true;
	}

	/** for 1+1 secure scenario */
	public boolean reserveConnection1P1() {
		// Call function to PCE client to compute path from source to dest
		System.out.println("Making path computation request to server from : " + sourceID + " to" + destID);
		System.out.println("\n\n\n\n\n\n");
		PCEPResponseFrame frame = ClientTest.getPath(sourceID, destID, (float) bw);
		if (frame.containsNoPathObject()) {
			System.out.println("ERROR: Resquest " + this.ID + " , Path from " + sourceID + " to " + destID + " with specified requirements does not exist. \n Request " + this.ID + " has been blocked");
			return false;
		}
		System.out.println("Path exist, making reservation from : " + sourceID + " to " + destID);
		this.objectList = frame.extractExplicitRouteObjectList();
		try {// do the reservation work with specified bandwidth on returned
				// EROs
			for (int i = 0; i < objectList.size(); i++) {
				ArrayList<EROSubobjects> subobjects = ((PCEPGenericExplicitRouteObjectImpl) objectList.get(i)).getTraversedVertexList();
				for (int j = 0; j < subobjects.size() - 1; j++) {
					String ingress = ((PCEPAddress) (subobjects.get(j))).getIPv4Address(false);
					String egress = ((PCEPAddress) (subobjects.get(j + 1))).getIPv4Address(false);
					System.out.println("||||Making reservation between " + ingress + " and " + egress + ".");
					Source.writer.write(reserveCapacity(ingress, egress, Double.toString(bw)) + "\n");
					Source.writer.flush();
				}
			}
			Thread.sleep(100);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return true;
	}

	/* (non-Javadoc)
	 * @see com.pcee.client.resv.ResvElement#reserveConnection()
	 */
	@Override
	/** for multipath with different bandwidth scenario */
	public boolean reserveConnection() {
		// Call function to PCE client to compute path from source to dest
		System.out.println("Making path computation request to server from : " + sourceID + " to" + destID);
		System.out.println("\n\n\n\n\n\n");
		PCEPResponseFrame frame = ClientTest.getPath(sourceID, destID, (float) bw);
		if (frame.containsNoPathObject()) {
			System.out.println("ERROR: Resquest " + this.ID + " , Path from " + sourceID + " to " + destID + " with specified requirements does not exist. \n Request " + this.ID + " has been blocked");
			return false;
		}
		System.out.println("Path exist, making reservation from : " + sourceID + " to " + destID);
		this.objectList = frame.extractExplicitRouteObjectList();
		this.bwList = frame.extractBandwidthObjectList();
		try {// do the reservation work with specified bandwidth on returned
				// EROs
			for (int i = 0; i < objectList.size(); i++) {
				ArrayList<EROSubobjects> subobjects = ((PCEPGenericExplicitRouteObjectImpl) objectList.get(i)).getTraversedVertexList();
				double bw = bwList.get(i).getBandwidthFloatValue();
				for (int j = 0; j < subobjects.size() - 1; j++) {
					String ingress = ((PCEPAddress) (subobjects.get(j))).getIPv4Address(false);
					String egress = ((PCEPAddress) (subobjects.get(j + 1))).getIPv4Address(false);
					System.out.println("||||Making reservation between " + ingress + " and " + egress + ".");
					Source.writer.write(reserveCapacity(ingress, egress, Double.toString(bw)) + "\n");
					Source.writer.flush();
				}
			}
			Thread.sleep(100);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return true;
	}

	/* (non-Javadoc)
	 * @see com.pcee.client.resv.ResvElement#releaseConnection()
	 */
	@Override
	public boolean releaseConnection() {
		System.out.println("release bandwidth from " + sourceID + " to " + destID);
		try {// do the release work with specified bandwidth on EROs that have
				// been reserved
			for (int i = 0; i < objectList.size(); i++) {
				ArrayList<EROSubobjects> subobjects = ((PCEPGenericExplicitRouteObjectImpl) objectList.get(i)).getTraversedVertexList();
				for (int j = 0; j < subobjects.size() - 1; j++) {
					String ingress = ((PCEPAddress) (subobjects.get(j))).getIPv4Address(false);
					String egress = ((PCEPAddress) (subobjects.get(j + 1))).getIPv4Address(false);
					System.out.println("||||Making release between " + ingress + " and " + egress + ".");
					Source.writer.write(releaseCapacity(ingress, egress, Double.toString(bw)) + "\n");
					Source.writer.flush();
				}
			}
			Thread.sleep(100);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see com.pcee.client.resv.ResvElement#getPathDelay()
	 */
	@Override
	public double getPathDelay() {
		return 0;
	}

	/**
	 * @param sourceVector
	 * @param destVector
	 * @param bw
	 * @return
	 */
	public static String reserveCapacity(String sourceVector, String destVector, String bw) {
		String s = "RESERVE:";
		s += sourceVector + ":" + destVector + ":" + bw;

		return s;
	}

	/**
	 * @param sourceVector
	 * @param destVector
	 * @param bw
	 * @return
	 */
	public static String releaseCapacity(String sourceVector, String destVector, String bw) {
		String s = "RELEASE:";
		s += sourceVector + ":" + destVector + ":" + bw;

		return s;
	}

	/**
	 * @param vertexAddress
	 * @param cpu
	 * @param ram
	 * @param storage
	 * @return
	 */
	public static String reserveVertex(String vertexAddress, int cpu, int ram, int storage) {
		String s = "RESERVEVERTEX:";
		s += vertexAddress + ":" + cpu + ":" + ram + ":" + storage;
		return s;
	}

	/**
	 * @param vertexAddress
	 * @param cpu
	 * @param ram
	 * @param storage
	 * @return
	 */
	public static String releaseVertex(String vertexAddress, int cpu, int ram, int storage) {
		String s = "RELEASEVERTEX:";
		s += vertexAddress + ":" + cpu + ":" + ram + ":" + storage;
		return s;
	}
}

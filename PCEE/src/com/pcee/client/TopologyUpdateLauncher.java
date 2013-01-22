package com.pcee.client;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Timer;

import com.pcee.architecture.clientmodule.ClientTimer;
import com.pcee.client.reserve_release.ResourceReserver;
import com.pcee.logger.Logger;
import com.pcee.protocol.message.objectframe.impl.PCEPBandwidthObject;
import com.pcee.protocol.message.objectframe.impl.PCEPExplicitRouteObject;
import com.pcee.protocol.message.objectframe.impl.PCEPGenericExplicitRouteObjectImpl;
import com.pcee.protocol.message.objectframe.impl.PCEPNoPathObject;
import com.pcee.protocol.message.objectframe.impl.erosubobjects.EROSubobjects;
import com.pcee.protocol.message.objectframe.impl.erosubobjects.PCEPAddress;
import com.pcee.protocol.response.PCEPResponseFrame;

public class TopologyUpdateLauncher {

	public static void main(String[] args) {
		String temp = "";
		String temp1 = "";
		for (int i = 0; i < 40; i++) {

			temp += reserveCapacity("192.169.2.1", "192.169.2.26", "1") + "\n";
			temp1 += releaseCapacity("192.169.2.1", "192.169.2.26", "1") + "\n";
		}
		sendUpdate("127.0.0.1", 4190, temp + temp1);
	}

	// public static String address = "127.0.0.1";
	public static String address = "134.169.115.127";
	public static int port = 4190;
	public static LinkedList<PCEPExplicitRouteObject> objectList;
	public static PCEPResponseFrame responseFrame;
	public static LinkedList<PCEPBandwidthObject> bwList;
	public static PCEPNoPathObject nopath;
	public static int bw = 4;
	public static int total = 0;
	public static int blocked = 0;
	public static int requestCount = 0;
	public static int requestDelay = 500;

	public static ArrayList<Long> timeStampsSentMilli = new ArrayList<Long>();
	public static ArrayList<Long> timeStampsSentNano = new ArrayList<Long>();
	public static ArrayList<Long> timeStampsReceivedMilli = new ArrayList<Long>();
	public static ArrayList<Long> timeStampsReceivedNano = new ArrayList<Long>();
	
	public static ArrayList<Long> timeStampsVertexSentMilli = new ArrayList<Long>();
	public static ArrayList<Long> timeStampsVertexReceivedMilli = new ArrayList<Long>();
	public static ArrayList<Long> timeStampsVertexSentNano = new ArrayList<Long>();
	public static ArrayList<Long> timeStampsVertexReceivedNano = new ArrayList<Long>();

	public static long startTime;
	public static int duration = 30;
	public static Timer timer = new Timer();

	public static void executeReserveAndRelease() {

		if (requestCount <= 30) {

			timer.schedule(new ClientTimer(), requestDelay);

			total++;

			if (responseFrame.extractNoPathObject() != null) {
				blocked++;
			} else {
				ResourceReserver reserver = new ResourceReserver(objectList, bwList);
				timer.schedule(reserver, 500);
			}
		} else {
			Logger.logBlockingRate(total, blocked, (double) blocked / (double) total);
			reset();
		}
	}

	public static void sendUpdate(String address, int port, String update) {
		try {
			Socket socket = new Socket(address, port);
			BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			bufferedWriter.write(update);
			bufferedWriter.flush();
			socket.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * function used to reserve specified bandwidth on the returned EROs
	 */
	public static void reserve() {
		try {
			Socket socket = new Socket(TopologyUpdateLauncher.address, TopologyUpdateLauncher.port);
			BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			for (int i = 0; i < objectList.size(); i++) {
				ArrayList<EROSubobjects> subobjects = ((PCEPGenericExplicitRouteObjectImpl) objectList.get(i)).getTraversedVertexList();
				double bw = bwList.get(i).getBandwidthFloatValue();
				for (int j = 0; j < subobjects.size() - 1; j++) {
					String ingress = ((PCEPAddress) (subobjects.get(j))).getIPv4Address(false);
					String egress = ((PCEPAddress) (subobjects.get(j + 1))).getIPv4Address(false);
					bufferedWriter.write(reserveCapacity(ingress, egress, Double.toString(bw)));
					bufferedWriter.flush();
				}
			}
			socket.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * function to release bandwidth reserved by reserve() function on each
	 * returned EROs
	 */
	public static void release() {
		try {
			Socket socket = new Socket(TopologyUpdateLauncher.address, TopologyUpdateLauncher.port);
			BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			for (int i = 0; i < objectList.size(); i++) {
				ArrayList<EROSubobjects> subobjects = ((PCEPGenericExplicitRouteObjectImpl) objectList.get(i)).getTraversedVertexList();
				double bw = bwList.get(i).getBandwidthFloatValue();
				for (int j = 0; j < subobjects.size() - 1; j++) {
					String ingress = ((PCEPAddress) (subobjects.get(j))).getIPv4Address(false);
					String egress = ((PCEPAddress) (subobjects.get(j + 1))).getIPv4Address(false);
					bufferedWriter.write(releaseCapacity(ingress, egress, Double.toString(bw)));
					bufferedWriter.flush();
				}
			}
			socket.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void reset() {
		total = 0;
		blocked = 0;
		startTime = System.currentTimeMillis();
		requestCount = 0;
		timeStampsReceivedMilli.clear();
		timeStampsSentMilli.clear();
	}

	public static String reserveCapacity(String sourceVector, String destVector, String bw) {
		String s = "RESERVE:";
		s += sourceVector + ":" + destVector + ":" + bw;

		return s;
	}

	public static String releaseCapacity(String sourceVector, String destVector, String bw) {
		String s = "RELEASE:";
		s += sourceVector + ":" + destVector + ":" + bw;

		return s;
	}

	public static String getCapacity(String sourceVector, String destVector) {
		String s = "GET:";
		s += sourceVector + ":" + destVector;

		return s;
	}

	public static String createLink(String sourceVector, String destVector, String bw) {
		String s = "CREATE:";
		s += sourceVector + ":" + destVector + ":" + bw;

		return s;
	}

	public void printStringArray(String[] arr) {
		for (int i = 0; i < arr.length; i++) {
			System.out.println(arr[i]);
		}
	}

}

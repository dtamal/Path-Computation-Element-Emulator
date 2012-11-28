package com.globalGraph;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * One Place for All Configuration
 * 
 * @author Yuesheng Zhong
 * 
 */ 
public class TopoGlobal {
    /** Path Computation Request Address */
	// public static String pcrAddress = "134.169.174.112";
	public static String pcrAddress = "127.0.0.1";
	public static int pcrPort = 4189;

	/** Reserve and Release Address */
	// public static String prrAddress = "134.169.174.112";
	public static String prrAddress = "127.0.0.1";
	public static int prrPort = 4190;

	
	/** Define Path Count when doing Path Computation */
	public static int pathCount = 2; // Need to change after each scenario and
	// should be equal to logFileName's
	// count
	
	/** Define the source of topology information */
	public static String projectSourcePath = "source/";
	public static String projectLogPath = "log/";
	public static String configFilePath = "init.cfg";
	public static String topology = "germany50.txt";


	/** define size of blocking request queue */
	public static int queueSize = 100;
	
	/** Start and Stop time in EventHandler */
	public static int endTime = 10; // 1500,2500,4500
	public static int startTime = 0;

	/** Configuration Value in Launcher */
	public static double holdingTime = 50;// Need to change after each test
	public static int roundCount = 1;
	public static int bwLow = 3;
	public static int bwHigh = 6;
	public static int cpuLow = 1;
	public static int cpuHigh = 4;
	public static int ramLow = 128;
	public static int ramHigh = 1024;
	public static int storageLow = 10;
	public static int storageHigh = 100;
	public static int interArrivalTime = 1;
	public static int initCPU = 20;
	public static int initRAM = 10240;
	public static int initStorage = 3200;
	public static int initBandwidth = 40;

	/** Flag indicating that Queuing and Computation are finished */
	public static boolean isQueuingAndComputationDone = false;
	
	public static void main(String[] args){
	   // System.out.println(TopoGlobal.logQueuingAndComputationFile);
	}

	public static void init() {
		try {
		    BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(TopoGlobal.configFilePath)));

		    String data = null;
		    String[] tmp = null;
		    String[] tmp1 = null;
		    while ((data = reader.readLine()) != null) {
			tmp = data.split("=");
			if (tmp[0].trim().equals("TotalRequests")) {
			    TopoGlobal.endTime = Integer.parseInt(tmp[1].trim());
			    System.out.println("TotalRequests=" + TopoGlobal.endTime);
			} else if (tmp[0].trim().equals("INITBandwidth")) {
			    TopoGlobal.initBandwidth = Integer.parseInt(tmp[1].trim());
			    System.out.println("INITBandwidth=" + TopoGlobal.initBandwidth);
			} else if (tmp[0].trim().equals("INITCPU")) {
			    TopoGlobal.initCPU = Integer.parseInt(tmp[1].trim());
			    System.out.println("INITCPU=" + TopoGlobal.initCPU);
			} else if (tmp[0].trim().equals("INITRAM")) {
			    TopoGlobal.initRAM = Integer.parseInt(tmp[1].trim());
			    System.out.println("INITRAM=" + TopoGlobal.initRAM);
			} else if (tmp[0].trim().equals("INITStorage")) {
			    TopoGlobal.initStorage = Integer.parseInt(tmp[1].trim());
			    System.out.println("INITStorage=" + TopoGlobal.initStorage);
			} else if (tmp[0].trim().equals("ServerAddressForPathComputation")) {
			    TopoGlobal.pcrAddress = tmp[1].trim();
			    TopoGlobal.prrAddress = TopoGlobal.pcrAddress;
			    System.out.println("ServerAddressForPathComputation=" + TopoGlobal.pcrAddress);
			} else if (tmp[0].trim().equals("ServerPortForPathComputation")) {
			    TopoGlobal.pcrPort = Integer.parseInt(tmp[1].trim());
			    System.out.println("ServerPortForPathComputation=" + TopoGlobal.pcrPort);
			} else if (tmp[0].trim().equals("ServerPortForResourceReserveAndRelease")) {
			    TopoGlobal.prrPort = Integer.parseInt(tmp[1].trim());
			    System.out.println("ServerPortForResourceReserveAndRelease=" + TopoGlobal.prrPort);
			} else if (tmp[0].trim().equals("PathCount")) {
			    TopoGlobal.pathCount = Integer.parseInt(tmp[1].trim());
			    System.out.println("PathCount=" + TopoGlobal.pathCount);
			} else if (tmp[0].trim().equals("Topology")) {
			    TopoGlobal.topology = tmp[1].trim();
			    System.out.println("Topology=" + TopoGlobal.topology);
			} else if (tmp[0].trim().equals("ErlangValue")) {
			    TopoGlobal.holdingTime = Integer.parseInt(tmp[1].trim());
			    System.out.println("ErlangValue=" + TopoGlobal.holdingTime);
			} else if (tmp[0].trim().equals("Bandwidth")) {
			    tmp1 = tmp[1].trim().split("-");
			    TopoGlobal.bwLow = Integer.parseInt(tmp1[0].trim());
			    TopoGlobal.bwHigh = Integer.parseInt(tmp1[1].trim());
			    System.out.println("Bandwidth=" + TopoGlobal.bwLow);
			} else if (tmp[0].trim().equals("CPU")) {
			    tmp1 = tmp[1].trim().split("-");
			    TopoGlobal.cpuLow = Integer.parseInt(tmp1[0].trim());
			    TopoGlobal.cpuHigh = Integer.parseInt(tmp1[1].trim());
			    System.out.println("CPU=" + TopoGlobal.cpuHigh);
			} else if (tmp[0].trim().equals("RAM")) {
			    tmp1 = tmp[1].trim().split("-");
			    TopoGlobal.ramLow = Integer.parseInt(tmp1[0].trim());
			    TopoGlobal.ramHigh = Integer.parseInt(tmp1[1].trim());
			    System.out.println("RAM=" + TopoGlobal.ramHigh);
			} else if (tmp[0].trim().equals("Storage")) {
			    tmp1 = tmp[1].trim().split("-");
			    TopoGlobal.storageLow = Integer.parseInt(tmp1[0].trim());
			    TopoGlobal.storageHigh = Integer.parseInt(tmp1[1].trim());
			    System.out.println("Storage=" + TopoGlobal.storageHigh);
			} else if (tmp[0].trim().equals("InterArrivalTime")) {
			    TopoGlobal.interArrivalTime = Integer.parseInt(tmp[1].trim());
			    System.out.println("IntegerArrivaltime=" + TopoGlobal.interArrivalTime);
			} else if (tmp[0].trim().equals("RequestQueueSize")){
			    TopoGlobal.queueSize = Integer.parseInt(tmp[1].trim());
			    System.out.println("RequestQueueSize=" + TopoGlobal.queueSize);
			}
		    }
		    reader.close();
		    // }
		} catch (IOException e) {
		    e.printStackTrace();
		    System.out.println("ERROR in parsing user input! Default Configuration will be used!");
		}
	    }
}

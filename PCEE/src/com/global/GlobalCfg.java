package com.global;

/**
 * One Place for All Configuration
 * 
 * @author Yuesheng Zhong
 * 
 */ 
public class GlobalCfg {
	/** Path Computation Request Address */
	// public static String pcrAddress = "134.169.174.112";
	public static String pcrAddress = "127.0.0.1";
	public static int pcrPort = 4189;

	public static boolean singlePath = true;
	/** Reserve and Release Address */
	// public static String prrAddress = "134.169.174.112";
	public static String prrAddress = "127.0.0.1";
	public static int prrPort = 4190;

	
	/** Define Path Count when doing Path Computation */
	public static int pathCount = 1; // Need to change after each scenario and
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
	   // System.out.println(GlobalCfg.logQueuingAndComputationFile);
	}

}

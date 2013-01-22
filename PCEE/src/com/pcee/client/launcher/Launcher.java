package com.pcee.client.launcher;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import com.global.GlobalCfg;
import com.globalGraph.TopoGlobal;
import com.graph.graphcontroller.Gcontroller;
import com.graph.graphcontroller.impl.GcontrollerImpl;
import com.graph.topology.importers.ImportTopology;
import com.graph.topology.importers.impl.MLSNDLibImportTopology;
import com.pcee.client.ClientTest;
import com.pcee.client.TopologyUpdateLauncher;
import com.pcee.client.connectionsource.MultiPathExponentialSource;
import com.pcee.client.connectionsource.Source;
import com.pcee.client.event.eventhandler.EventHandler;
import com.pcee.logger.Logger;

public class Launcher {

    public static void main(String[] args) {

	String defaultDir = ".//results//";
	System.out.println("Before init()");
	init();
	System.out.println("After init()");

	/** Init variables */
	int roundCount = GlobalCfg.roundCount;
	int bwLow = GlobalCfg.bwLow;
	int bwHigh = GlobalCfg.bwHigh;

	double interArrivalTime = GlobalCfg.interArrivalTime;
	double holdingTime = GlobalCfg.holdingTime;

	/** Establish the connection to the server using OPEN Object */
	ClientTest.initClient();

	/** Initialize the socket */
	Source.initSocket();

	Logger.logging = false;
	Logger.debugging = false;

	for (int i = 0; i < roundCount; i++) {

	    String utilizationLogFile = defaultDir + "//singlePath-" + holdingTime + "-iter-" + i + ".txt";
	    ImportTopology importer = new MLSNDLibImportTopology();
	    Gcontroller graph = new GcontrollerImpl();
	    importer.importTopology(graph, GlobalCfg.projectSourcePath + GlobalCfg.topology);
	    if (graph.getEdgeSet().size() == 0) {
		System.out.println("fail by import topology");
		System.exit(0);
	    }
	    EventHandler.initEventHandler();
	    Source source;
	    source = new MultiPathExponentialSource(graph, interArrivalTime, holdingTime, utilizationLogFile, bwLow, bwHigh);
	    // source.nextConnection(true);
	    source.nextRequest(true);
	    EventHandler.startEventHandler();

	    Logger.logBlockingRate(source.getTotConnections(), source.getBlockedConnections(), source.getBlockingProbability());

	    // do the log and reset work with jonesir style at the end of each
	    // round

	    Logger.logTimeStampsVertex(TopologyUpdateLauncher.timeStampsVertexSentMilli, TopologyUpdateLauncher.timeStampsVertexReceivedMilli, TopologyUpdateLauncher.timeStampsVertexSentNano, TopologyUpdateLauncher.timeStampsVertexReceivedNano);
	    Logger.logTimeStamps(TopologyUpdateLauncher.timeStampsSentMilli, TopologyUpdateLauncher.timeStampsReceivedMilli, TopologyUpdateLauncher.timeStampsSentNano, TopologyUpdateLauncher.timeStampsReceivedNano);
	    TopologyUpdateLauncher.sendUpdate(GlobalCfg.prrAddress, GlobalCfg.prrPort, "FINISHED");
	    TopologyUpdateLauncher.reset();
	}
	System.out.println("Emulation Finished!");
	System.exit(0);
    }

    public static void init() {
	try {
	    BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(GlobalCfg.configFilePath)));

	    String data = null;
	    String[] tmp = null;
	    String[] tmp1 = null;
	    while ((data = reader.readLine()) != null) {
		tmp = data.split("=");
		if (tmp[0].trim().equals("TotalRequests")) {
		    GlobalCfg.endTime = Integer.parseInt(tmp[1].trim());
		    System.out.println("TotalRequests=" + GlobalCfg.endTime);
// Initial resources ---------------------------------------------------------------------------------
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
// Initial resources ----------------------------------------------------------------------------------
		} else if (tmp[0].trim().equals("ServerAddressForPathComputation")) {
		    GlobalCfg.pcrAddress = tmp[1].trim();
		    GlobalCfg.prrAddress = GlobalCfg.pcrAddress;
		    System.out.println("ServerAddressForPathComputation=" + GlobalCfg.pcrAddress);
		} else if (tmp[0].trim().equals("ServerPortForPathComputation")) {
		    GlobalCfg.pcrPort = Integer.parseInt(tmp[1].trim());
		    System.out.println("ServerPortForPathComputation=" + GlobalCfg.pcrPort);
		} else if (tmp[0].trim().equals("ServerPortForResourceReserveAndRelease")) {
		    GlobalCfg.prrPort = Integer.parseInt(tmp[1].trim());
		    System.out.println("ServerPortForResourceReserveAndRelease=" + GlobalCfg.prrPort);
		} else if (tmp[0].trim().equals("PathCount")) {
		    GlobalCfg.pathCount = Integer.parseInt(tmp[1].trim());
		    System.out.println("PathCount=" + GlobalCfg.pathCount);
		} else if (tmp[0].trim().equals("Topology")) {
		    GlobalCfg.topology = tmp[1].trim();
		    System.out.println("Topology=" + GlobalCfg.topology);
		} else if (tmp[0].trim().equals("ErlangValue")) {
		    GlobalCfg.holdingTime = Integer.parseInt(tmp[1].trim());
		    System.out.println("ErlangValue=" + GlobalCfg.holdingTime);
		} else if (tmp[0].trim().equals("Bandwidth")) {
		    tmp1 = tmp[1].trim().split("-");
		    GlobalCfg.bwLow = Integer.parseInt(tmp1[0].trim());
		    GlobalCfg.bwHigh = Integer.parseInt(tmp1[1].trim());
		    System.out.println("Bandwidth=" + GlobalCfg.bwLow);
		} else if (tmp[0].trim().equals("CPU")) {
		    tmp1 = tmp[1].trim().split("-");
		    GlobalCfg.cpuLow = Integer.parseInt(tmp1[0].trim());
		    GlobalCfg.cpuHigh = Integer.parseInt(tmp1[1].trim());
		    System.out.println("CPU=" + GlobalCfg.cpuHigh);
		} else if (tmp[0].trim().equals("RAM")) {
		    tmp1 = tmp[1].trim().split("-");
		    GlobalCfg.ramLow = Integer.parseInt(tmp1[0].trim());
		    GlobalCfg.ramHigh = Integer.parseInt(tmp1[1].trim());
		    System.out.println("RAM=" + GlobalCfg.ramHigh);
		} else if (tmp[0].trim().equals("Storage")) {
		    tmp1 = tmp[1].trim().split("-");
		    GlobalCfg.storageLow = Integer.parseInt(tmp1[0].trim());
		    GlobalCfg.storageHigh = Integer.parseInt(tmp1[1].trim());
		    System.out.println("Storage=" + GlobalCfg.storageHigh);
		} else if (tmp[0].trim().equals("InterArrivalTime")) {
		    GlobalCfg.interArrivalTime = Integer.parseInt(tmp[1].trim());
		    System.out.println("IntegerArrivaltime=" + GlobalCfg.interArrivalTime);
		} else if (tmp[0].trim().equals("RequestQueueSize")){
		    GlobalCfg.queueSize = Integer.parseInt(tmp[1].trim());
		    System.out.println("RequestQueueSize=" + GlobalCfg.queueSize);
		}
	    }
	    reader.close();
	} catch (IOException e) {
	    e.printStackTrace();
	    System.out.println("ERROR in parsing user input! Default Configuration will be used!");
	}
    }
}

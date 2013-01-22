package com.pcee.architecture.computationmodule.ted;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Iterator;

import com.graph.elements.edge.EdgeElement;
import com.graph.elements.edge.params.EdgeParams;
import com.graph.elements.edge.params.impl.BasicEdgeParams;
import com.graph.elements.vertex.params.ITResourceVertexParams;
import com.graph.graphcontroller.Gcontroller;
import com.pcee.client.TEDecommissionClientLauncher;
import com.pcee.logger.Logger;

/**
 * Reserve and Release request processing class
 * 
 * @author Yuesheng Zhong
 * 
 */
public class SocketProcessing extends Thread {

	Socket socket;

	/**
	 * @param socket
	 */
	public SocketProcessing(Socket socket) {
		this.socket = socket;
	}

	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		// Do processing for Socket
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			String message;
			while (true) {
				message = in.readLine();
				if (message != null) {
					System.out.println("Processing: " + message);
					String[] arr = parseInformation(message);
					extractInformation(arr);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * @param message
	 * @return
	 */
	public static String[] parseInformation(String message) {
		String[] arr = message.split(":");

		return arr;
	}

	/**
	 * @param arr
	 */
	public static void extractInformation(String[] arr) {
		String situation = arr[0];
		System.out.println("Situation = " + situation);
		synchronized (TopologyInformation.getInstance().getGraph()) {
			if (situation.equals("RESERVE")) {
				reserveCapacity(arr[1], arr[2], arr[3]);
			} else if (situation.equals("RELEASE")) {
				releaseCapacity(arr[1], arr[2], arr[3]);
			} else if (situation.equals("GET")) {
				localLogger("Capacity of link from " + arr[1] + " to " + arr[2] + "=" + getCapacity(arr[1], arr[2]));
			} else if (situation.equals("CREATE")) {
				createLink(arr[1], arr[2], arr[3]);
			} else if (situation.equals("RESERVEVERTEX")) {

			} else if (situation.equals("RELEASEVERTEX")) {

			} else if (situation.equals("FINISHED")) {
				Logger.logQueuingAndComputationTime();
			} else {
				System.out.println("Wrong Input Parameter in ServerUpdateListener.extractInformation()");
				System.exit(0);
			}
		}
	}

	/**
	 * @param sourceVector
	 * @param destVector
	 * @param bw
	 */
	public static void reserveCapacity(String sourceVector, String destVector, String bw) {
		localLogger("Entering reserveCapacity(...)");
		localLogger(sourceVector + " " + destVector + " " + bw);
		if (TopologyInformation.getInstance().getGraph().aConnectingEdge(sourceVector, destVector)) {
			TopologyInformation.getInstance().getGraph().getConnectingEdge(sourceVector, destVector).getEdgeParams().reserveCapacity(Double.valueOf(bw));
			localDebugger("Updating Capacity of link");
			localLogger("Capacity of Link from " + sourceVector + " to " + destVector + " = " + TopologyInformation.getInstance().getGraph().getConnectingEdge(sourceVector, destVector).getEdgeParams().getAvailableCapacity());
		} else {
			localLogger("Link not found");
		}
	}

	/**
	 * @param vertexID
	 * @param cpu
	 * @param ram
	 * @param storage
	 */
	public static void reserveVertex(String vertexID, int cpu, int ram, int storage) {
		localLogger("reserveVertex(...)");
		localLogger(vertexID + " CPU-" + cpu + " , RAM-" + ram + " , STORAGE-" + storage);
		ITResourceVertexParams params = null;
		if (TopologyInformation.getInstance().getGraph().vertexExists(vertexID)) {
			params = ((ITResourceVertexParams) TopologyInformation.getInstance().getGraph().getVertex(vertexID).getVertexParams());

			// the actual reserve work
			params.reserveITResource(cpu, ram, storage);

			localLogger("Updating IT Resource of Vertex");
			localLogger("Available IT Resource in Vertex " + vertexID + " is: \n\t CPU-" + params.getAvailableCPU() + " \n\t RAM-" + params.getAvailableRAM() + " \n\t STORAGE-" + params.getAvailableSTORAGE());
			localLogger("Total IT Resource in Vertex " + vertexID + " is: \n\t CPU-" + params.getCpu() + " \n\t RAM-" + params.getRam() + " \n\t STORAGE-" + params.getStorage());

		} else {
			localLogger("Vertex not found in the Graph!");
		}
	}

	/**
	 * @param vertexID
	 * @param cpu
	 * @param ram
	 * @param storage
	 */
	public static void releaseVertex(String vertexID, int cpu, int ram, int storage) {
		localLogger("releaseVertex(...)");
		localLogger(vertexID + " CPU-" + cpu + " , RAM-" + ram + " , STORAGE-" + storage);
		ITResourceVertexParams params = null;
		if (TopologyInformation.getInstance().getGraph().vertexExists(vertexID)) {
			params = ((ITResourceVertexParams) TopologyInformation.getInstance().getGraph().getVertex(vertexID).getVertexParams());

			// the actual reserve work
			params.releaseITResource(cpu, ram, storage);

			localLogger("Updating IT Resource of Vertex");
			localLogger("Available IT Resource in Vertex " + vertexID + " is: \n\t CPU-" + params.getAvailableCPU() + " \n\t RAM-" + params.getAvailableRAM() + " \n\t STORAGE-" + params.getAvailableSTORAGE());
			localLogger("Total IT Resource in Vertex " + vertexID + " is: \n\t CPU-" + params.getCpu() + " \n\t RAM-" + params.getRam() + " \n\t STORAGE-" + params.getStorage());

		} else {
			localLogger("Vertex not found in the Graph!");
		}
	}

	/**
	 * @param sourceVector
	 * @param destVector
	 * @param bw
	 */
	public static void releaseCapacity(String sourceVector, String destVector, String bw) {
		localLogger("releaseCapacity(...)");
		localLogger(sourceVector + " " + destVector + " " + bw);
		if (TopologyInformation.getInstance().getGraph().aConnectingEdge(sourceVector, destVector)) {
			TopologyInformation.getInstance().getGraph().getConnectingEdge(sourceVector, destVector).getEdgeParams().releaseCapacity(Double.valueOf(bw));
			localLogger("Updating Capacity of link");
			localLogger("Available Capacity on Links is : " + TopologyInformation.getInstance().getGraph().getConnectingEdge(sourceVector, destVector).getEdgeParams().getAvailableCapacity());
			localLogger("Total Capacity on Links is : " + TopologyInformation.getInstance().getGraph().getConnectingEdge(sourceVector, destVector).getEdgeParams().getMaxCapacity());
		} else {
			localLogger("Link not found");
		}
		// checkPolicy();
	}

	/**
	 * @param sourceVector
	 * @param destVector
	 * @return
	 */
	public static double getCapacity(String sourceVector, String destVector) {
		localLogger("getCapacity(...)");
		localLogger(sourceVector + " " + destVector);
		if (TopologyInformation.getInstance().getGraph().aConnectingEdge(sourceVector, destVector)) {
			localDebugger("Updating Capacity of link");
			return TopologyInformation.getInstance().getGraph().getConnectingEdge(sourceVector, destVector).getEdgeParams().getAvailableCapacity();
		} else {
			localLogger("Link not found");
			return -1;
		}
	}

	/**
	 * @param sourceVector
	 * @param destVector
	 * @param bw
	 */
	public static void createLink(String sourceVector, String destVector, String bw) {
		localLogger("createLink(...)");
		localLogger(sourceVector + " " + destVector + " " + bw);
		Gcontroller graph = TopologyInformation.getInstance().getGraph();
		if (graph.aConnectingEdge(sourceVector, destVector) == true) {
			double maxCap = graph.getConnectingEdge(sourceVector, destVector).getEdgeParams().getMaxCapacity();
			double availableCap = graph.getConnectingEdge(sourceVector, destVector).getEdgeParams().getAvailableCapacity();
			graph.getConnectingEdge(sourceVector, destVector).getEdgeParams().setMaxCapacity(maxCap + Double.parseDouble(bw));
			graph.getConnectingEdge(sourceVector, destVector).getEdgeParams().setAvailableCapacity(availableCap + Double.parseDouble(bw));
		} else {
			EdgeElement edge = new EdgeElement(sourceVector + "-" + destVector, graph.getVertex(sourceVector), graph.getVertex(destVector), graph);
			EdgeParams params = new BasicEdgeParams(edge, 1, 1, Double.valueOf(bw));
			edge.setEdgeParams(params);
			TopologyInformation.getInstance().getGraph().addEdge(edge);
		}

		/*
		 * int p = ((BasicVertexParams) TopologyInformation.getInstance()
		 * .getGraph().getVertex(sourceVector).getVertexParams())
		 * .getFreePorts(); ((BasicVertexParams)
		 * TopologyInformation.getInstance().getGraph()
		 * .getVertex(sourceVector).getVertexParams()).setFreePorts(p - 1); p =
		 * ((BasicVertexParams) TopologyInformation.getInstance().getGraph()
		 * .getVertex(destVector).getVertexParams()).getFreePorts();
		 * ((BasicVertexParams) TopologyInformation.getInstance().getGraph()
		 * .getVertex(destVector).getVertexParams()).setFreePorts(p - 1);
		 */
		int p = ((ITResourceVertexParams) TopologyInformation.getInstance().getGraph().getVertex(sourceVector).getVertexParams()).getFreePorts();
		((ITResourceVertexParams) TopologyInformation.getInstance().getGraph().getVertex(sourceVector).getVertexParams()).setFreePorts(p - 1);

		p = ((ITResourceVertexParams) TopologyInformation.getInstance().getGraph().getVertex(destVector).getVertexParams()).getFreePorts();
		((ITResourceVertexParams) TopologyInformation.getInstance().getGraph().getVertex(destVector).getVertexParams()).setFreePorts(p - 1);
	}

	/**
	 * @param message
	 * @return
	 */
	public static String[] printMessage(String message) {
		String[] arr = message.split(":");

		return arr;
	}

	/**
	 * 
	 */
	public static void checkPolicy() {
		Gcontroller graph = TopologyInformation.getInstance().getGraph();
		Iterator<EdgeElement> iter = graph.getEdgeSet().iterator();
		while (iter.hasNext()) {
			EdgeElement temp = iter.next();
			if (temp.getEdgeParams().getMaxCapacity() > 10) {
				if (temp.getEdgeParams().getAvailableCapacity() >= 10) {
					new TEDecommissionClientLauncher(temp.getSourceVertex().getVertexID(), temp.getDestinationVertex().getVertexID());

				}
			}
		}
	}

	/**
	 * @param event
	 */
	private static void localLogger(String event) {
		Logger.logSystemEvents("[TopologyUpdateListener]     " + event);
	}

	/**
	 * Function to log debugging events
	 * 
	 * @param event
	 */
	private static void localDebugger(String event) {
		// Logger.debugger("[TopologyUpdateListener]     " + event);
	}

}

package com.pcee.architecture.computationmodule.ted.client;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.graph.elements.edge.EdgeElement;
import com.graph.elements.edge.params.EdgeParams;
import com.graph.elements.edge.params.impl.PathElementEdgeParams;
import com.graph.graphcontroller.Gcontroller;
import com.graph.graphcontroller.impl.GcontrollerImpl;
import com.graph.path.PathElement;
import com.graph.path.algorithms.PathComputationAlgorithm;
import com.graph.path.algorithms.constraints.Constraint;
import com.graph.path.algorithms.constraints.impl.SimplePathComputationConstraint;
import com.graph.path.algorithms.impl.MaxBandwidthShortestPathComputationAlgorithm;
import com.graph.topology.importers.ImportTopology;
import com.graph.topology.importers.impl.SNDLibImportTopology;

public class TopologyUpdateDomainClient {

	private static Gson gson = new Gson();
	

	public static void sendMessage(String ip, int port, String text){
		System.out.println("Attempting a connection to " + ip + ":" + port + " String = " + text);
		
		Socket socket = null;
		BufferedOutputStream out = null;
		try{
			//1. creating a socket to connect to the server
			socket = new Socket(ip, port);
			out = new BufferedOutputStream(socket.getOutputStream());
			//3: Communicating with the server
			System.out.println(new String(text.getBytes()));
			out.write(text.getBytes());
			out.flush();
		}
		catch(UnknownHostException e){
			System.err.println("You are trying to connect to an unknown host!");
		}
		catch(IOException e){
			e.printStackTrace();
		}
		finally{
			//4: Closing connection
			try{
				if (out!=null)
					out.close();
				if (socket!=null)
					socket.close();
			}
			catch(IOException e){
				e.printStackTrace();
			}
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void reserveCapacity(String ip, int port, double capacity, ArrayList<String> vertexSequence) {
		Map map = new HashMap();
		map.put("operation", "reserve");
		map.put("capacity", new Double(capacity));
		map.put("vertexSequence", vertexSequence);
		String json = gson.toJson(map);
		//Send message to the server
		sendMessage(ip, port, json);
	}

	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void releaseCapacity(String ip, int port, double capacity, ArrayList<String> vertexSequence) {
		Map map = new HashMap();
		map.put("operation", "release");
		map.put("capacity", new Double(capacity));
		map.put("vertexSequence", vertexSequence);
		String json = gson.toJson(map);
		//Send message to the server
		sendMessage(ip, port, json);
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void updateVirtualTopologyWithParent(String ip, int port) {
		Map map = new HashMap();
		map.put("operation", "updateVirtualTopologyBandwidth");
		String json = gson.toJson(map);
		//Send message to the server
		sendMessage(ip, port, json);
	}

	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void reomputeVirtuaTopology(String ip, int port) {
		Map map = new HashMap();
		map.put("operation", "recomputeVirtualTopology");
		String json = gson.toJson(map);
		//Send message to the server
		sendMessage(ip, port, json);
	}
	
	public static void main (String[] args) {
		String sourceID = "192.169.2.1";
		String destID = "192.169.2.7";
		
		String ip = "127.0.0.1";
		int port = 5190;
		reomputeVirtuaTopology(ip, port);
		
		ImportTopology importer = new SNDLibImportTopology();
		Gcontroller graph = new GcontrollerImpl();
		importer.importTopology(graph, "atlantaDomain1.txt");
		

		
		PathComputationAlgorithm algo = new MaxBandwidthShortestPathComputationAlgorithm();
		Constraint constr = new SimplePathComputationConstraint(graph.getVertex(sourceID), graph.getVertex(destID), 10);
		
		PathElement temp = algo.computePath(graph, constr);
		
		if (temp!=null) {
			EdgeElement edge = new EdgeElement (sourceID + "-" + destID, graph.getVertex(sourceID), graph.getVertex(destID), graph);
			EdgeParams params = new PathElementEdgeParams(edge, temp);
			reserveCapacity(ip, port, 10, params.getVertexSequence(sourceID, destID));
			releaseCapacity(ip, port, 10, params.getVertexSequence(sourceID, destID));
		}
		
		updateVirtualTopologyWithParent(ip, port);
		
		
	}

}

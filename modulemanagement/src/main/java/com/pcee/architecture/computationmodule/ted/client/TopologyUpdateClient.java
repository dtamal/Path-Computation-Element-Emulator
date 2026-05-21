package com.pcee.architecture.computationmodule.ted.client;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.graph.elements.vertex.VertexElement;
import com.graph.graphcontroller.Gcontroller;
import com.graph.graphcontroller.impl.GcontrollerImpl;
import com.graph.path.PathElement;
import com.graph.path.algorithms.PathComputationAlgorithm;
import com.graph.path.algorithms.constraints.Constraint;
import com.graph.path.algorithms.constraints.impl.SimplePathComputationConstraint;
import com.graph.path.algorithms.impl.MaxBandwidthShortestPathComputationAlgorithm;
import com.graph.topology.importers.ImportTopology;
import com.graph.topology.importers.impl.SNDLibImportTopology;

public class TopologyUpdateClient {

	private static Gson gson = new Gson();


	public static String sendMessage(String ip, int port, String text){
		System.out.println("Attempting a connection to " + ip + ":" + port + " String = " + text);

		String inText = "";
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
			out.write(new String("\n@\n").getBytes());
			out.flush();
		//	out.close();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String line = "";
			while ((line = bufferedReader.readLine()) != null) {
				inText = inText + line;
			}
			System.out.println(inText);
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
		return inText;
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
	public static void updateEdgeDefinition(String ip, int port, String sourceID, String destID, double weight, double capacity, double delay, double availableCapacity) {
		Map map = new HashMap();
		map.put("operation", "updateEdgeDefinition");
		map.put("capacity", new Double(capacity));
		map.put("avcapacity", new Double(availableCapacity));
		map.put("delay", new Double(delay));
		map.put("weight", new Double(weight));
		ArrayList<String> vertexSequence = new ArrayList<String>();
		vertexSequence.add(sourceID);
		vertexSequence.add(destID);
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




	public static void main (String[] args) {
		String sourceID = "192.169.2.1";
		String destID = "192.169.2.7";

		String ip = "127.0.0.1";
		int port = 5189;

		ImportTopology importer = new SNDLibImportTopology();
		Gcontroller graph = new GcontrollerImpl();
		importer.importTopology(graph, "atlanta.txt");

		PathComputationAlgorithm algo = new MaxBandwidthShortestPathComputationAlgorithm();
		Constraint constr = new SimplePathComputationConstraint(graph.getVertex(sourceID), graph.getVertex(destID), 10);

		PathElement temp = algo.computePath(graph, constr);

		if (temp!=null) {
//			EdgeElement edge = new EdgeElement (sourceID + "-" + destID, graph.getVertex(sourceID), graph.getVertex(destID), graph);
			ArrayList<String> vertexSequence = new ArrayList<String>();
			ArrayList<VertexElement> vertices = temp.getTraversedVertices();
			for (int i=0;i<vertices.size();i++)
				vertexSequence.add(vertices.get(i).getVertexID());
			
	//		vertexSequence.add("192.169.2.13");
			reserveCapacity(ip, port, 10, vertexSequence);
		//	releaseCapacity(ip, port, 10, vertexSequence);
		}


	}
}

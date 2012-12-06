package com.pcee.architecture.computationmodule.ted.client;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.google.gson.Gson;
import com.graph.elements.vertex.VertexElement;
import com.graph.path.PathElement;

public class TopologyUpdateParentClient {

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
	public static void updateEdge(String ip, int port, PathElement virtualLink) {
		Map map = new HashMap();
		map.put("operation", "updateEdgeDefinition");
		map.put("capacity", new Double(virtualLink.getPathParams().getAvailableCapacity()));
		map.put("delay", new Double(virtualLink.getPathParams().getPathDelay()));
		map.put("weight", new Double(virtualLink.getPathParams().getPathWeight()));
		
		ArrayList<String> vertexSequence = new ArrayList<String>();
		Iterator<VertexElement> iter = virtualLink.getTraversedVertices().iterator();
		while(iter.hasNext()) {
			vertexSequence.add(iter.next().getVertexID());
		}
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
}

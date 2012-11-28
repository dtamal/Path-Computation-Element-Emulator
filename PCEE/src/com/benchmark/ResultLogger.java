package com.benchmark;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import com.pcee.protocol.message.PCEPMessage;
import com.pcee.protocol.request.PCEPRequestFrame;
import com.pcee.protocol.request.PCEPRequestFrameFactory;
import com.pcee.protocol.response.PCEPResponseFrame;
import com.pcee.protocol.response.PCEPResponseFrameFactory;

/**
 * <pre>
 * Timestamps can be found here:
 * 
 * NetworkModuleImpl: line 420 Message read from Socket 
 * ComputationModuleMLImpl: line 165 Message arrives in Computation Layer
 * WorkerTask2: line 115 Begin of Computation
 * 
 * if(Single Layer Path Exists){
 * 	WorkerTask2: line 152 Single Layer Path Computed
 * }
 * else{
 * 	if(Free Ports are availible){
 * 		WorkerTask2: line 187 Starting ML Path Request
 * 		WorkerTask2: line 202 Received ML Path Response
 * 
 * 		if(No Path Object received){
 * 			WorkerTask2: line 223 Processing Information for NoPath Situation
 * 		{
 * 		else{
 * 			WorkerTask2: line 260 Processing Information for WSON Path 
 * 		}
 * 	}
 * 	else{
 * 		WorkerTask2: line 282 No Interfaces are free, generating NoPath Object 
 * 	}
 * }
 * 
 * NetworkModuleImpl: line 487 Before writing to the socket
 * NetworkModuleImpl: line 488 (flushing Operation)
 * </pre>
 */
public class ResultLogger {

    private static String FILENAME = "TEST_ML_PARAMS.txt";

    private static HashMap<String, LinkedList<Long>> results = new HashMap<String, LinkedList<Long>>();

    public static void main(String[] args) throws IOException {
	long l1 = 100;

	System.out.println(results.size());
	logResult("1", l1++);
	System.out.println(results.size());
	logResult("1", l1++);
	flush("1");

	System.out.println(results.size());
	logResult("2", l1++);
	System.out.println(results.size());
	logResult("2", l1++);
	System.out.println(results.size());
	logResult("3", l1++);
	System.out.println(results.size());
	logResult("3", l1++);
	System.out.println(results.size());

	flush("3");

    }

    public static synchronized void setFilename(String filename) {
	FILENAME = filename;
    }

    public static synchronized void logResult(String requestID, long timeVal) throws IOException {

	if (results.containsKey(requestID)) {
	    LinkedList<Long> timeValuesList = results.get(requestID);
	    timeValuesList.addLast(timeVal);
	} else {
	    LinkedList<Long> timeValuesList = new LinkedList<Long>();
	    timeValuesList.addLast(timeVal);
	    results.put(requestID, timeValuesList);
	}
    }

    public static synchronized void logResult(PCEPMessage message) throws IOException {

	int type = message.getMessageHeader().getTypeDecimalValue();

	if (type == 3 || type == 4) {

	    String requestID = "";
	    long timestamp = System.currentTimeMillis();

	    if (type == 3) {
		PCEPRequestFrame reqFrame = PCEPRequestFrameFactory.getPathComputationRequestFrame(message);
		requestID = String.valueOf(reqFrame.getRequestID());
	    }

	    if (type == 4) {
		PCEPResponseFrame respFrame = PCEPResponseFrameFactory.getPathComputationResponseFrame(message);
		requestID = String.valueOf(respFrame.getRequestID());
	    }

	    if (results.containsKey(requestID)) {
		LinkedList<Long> timeValuesList = results.get(requestID);
		timeValuesList.addLast(timestamp);
	    } else {
		LinkedList<Long> timeValuesList = new LinkedList<Long>();
		timeValuesList.addLast(timestamp);
		results.put(requestID, timeValuesList);
	    }
	}

    }

    public static synchronized void flush(String requestID) throws IOException {
	LinkedList<Long> timeValuesList = results.get(requestID);

	save(requestID, timeValuesList);
	results.remove(requestID);
    }

    public static synchronized void flush(PCEPMessage message) throws IOException {
	int type = message.getMessageHeader().getTypeDecimalValue();

	if (type == 4) {

	    PCEPResponseFrame respFrame = PCEPResponseFrameFactory.getPathComputationResponseFrame(message);
	    String requestID = String.valueOf(respFrame.getRequestID());
	    LinkedList<Long> timeValuesList = results.get(requestID);

	    save(requestID, timeValuesList);
	    results.remove(requestID);
	}

    }

    private static void save(String requestID, LinkedList<Long> timeValuesList) throws IOException {
	FileWriter fstream = new FileWriter(FILENAME, true);
	BufferedWriter out = new BufferedWriter(fstream);

	String s = requestID;
	for (int i = 0; i < timeValuesList.size(); i++) {
	    s += " " + timeValuesList.get(i);
	}
	s += "\n";
	out.write(s);
	out.close();
    }

}

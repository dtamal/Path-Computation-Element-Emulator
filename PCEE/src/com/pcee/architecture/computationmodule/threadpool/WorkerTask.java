/**
 *  This file is part of Path Computation Element Emulator (PCEE).
 *
 *  PCEE is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  PCEE is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with PCEE.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.pcee.architecture.computationmodule.threadpool;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;

import com.benchmark.ResultLogger;
import com.graph.elements.edge.EdgeElement;
import com.graph.elements.vertex.VertexElement;
import com.graph.elements.vertex.params.BasicVertexParams;
import com.graph.graphcontroller.Gcontroller;
import com.graph.path.PathElement;
import com.graph.path.algorithms.PathComputationAlgorithm;
import com.graph.path.algorithms.constraints.Constraint;
import com.pcee.architecture.ModuleEnum;
import com.pcee.architecture.ModuleManagement;
import com.pcee.architecture.computationmodule.ComputationModuleMLImpl;
import com.pcee.logger.Logger;
import com.pcee.protocol.message.PCEPMessage;
import com.pcee.protocol.message.PCEPMessageFactory;
import com.pcee.protocol.message.objectframe.PCEPObjectFrameFactory;
import com.pcee.protocol.message.objectframe.impl.PCEPEndPointsObject;
import com.pcee.protocol.message.objectframe.impl.PCEPExplicitRouteObject;
import com.pcee.protocol.message.objectframe.impl.PCEPGenericExplicitRouteObjectImpl;
import com.pcee.protocol.message.objectframe.impl.PCEPRequestParametersObject;
import com.pcee.protocol.message.objectframe.impl.erosubobjects.EROSubobjects;
import com.pcee.protocol.message.objectframe.impl.erosubobjects.MLDelimiter;
import com.pcee.protocol.message.objectframe.impl.erosubobjects.PCEPAddress;
import com.pcee.protocol.request.PCEPRequestFrame;
import com.pcee.protocol.request.PCEPRequestFrameFactory;
import com.pcee.protocol.response.PCEPResponseFrame;
import com.pcee.protocol.response.PCEPResponseFrameFactory;

/**
 * Runnable class used by the thread pool to process path computation requests
 * 
 * @author Mohit Chamania
 * @author Marek Drogon
 */
public class WorkerTask implements Runnable {
    // Request to be processed
    private Request request;
    // Graph used for computation of the request
    private Gcontroller graph;
    // Module management object to send the response to the session layer
    private ModuleManagement lm;

    /** Default Constructor */
    public WorkerTask(ModuleManagement layerManagement, Request request, Gcontroller graph) {
	lm = layerManagement;
	this.request = request;
	this.graph = graph;
    }

    /**
     * Function to process the path computation request and create a response object from the same
     * 
     * @return Response Object
     */
    public Response processRequest() {
	// Initialize response object and set parameters
	Response response = new Response();
	response.setRequestID(request.getRequestID());
	response.setAddress(request.getAddress());

	// Compute path
	PathComputationAlgorithm algo = request.getAlgo();
	Constraint c = request.getConstrains();

	PathElement element = algo.computePath(graph, c);

	if (element != null) {
	    response.setElement(element);
	    localLogger("computed path is " + element.getVertexSequence());
	    ArrayList<VertexElement> vertices = new ArrayList<VertexElement>();
	    Iterator<VertexElement> iter = vertices.iterator();
	    localLogger("Size of queue is " + vertices.size());

	    while (iter.hasNext())
		localLogger("hop is " + iter.next().getVertexID());

	    Iterator<EdgeElement> iter1 = element.getTraversedEdges().iterator();
	    localLogger("Size of edge array is " + element.getTraversedEdges().size());

	    while (iter1.hasNext()) {
		localLogger("Edge is " + iter1.next().getEdgeID());
	    }
	} else
	    localDebugger("Error in Computing Path from " + request.getConstrains().getSource().getVertexID() + " to " + request.getConstrains().getDestination().getVertexID());

	// return response
	return response;
    }

    /** Function to update the graph instance used for computation */
    public void updateGraph(Gcontroller newGraph) {
	this.graph = newGraph;
    }

    /** Function to implement the path computation operations */
    public void run() {

	localDebugger("Start processing of Request for path from " + request.getConstrains().getSource().getVertexID() + " to " + request.getConstrains().getDestination().getVertexID());
	localLogger("Processing of Request: " + request.getRequestID());

	localLogger("Capacity of link from 172.10.1.10 to 172.10.1.20 is : " + graph.getConnectingEdge("172.10.1.10", "172.10.1.20").getEdgeParams().getAvailableCapacity() + "\n\n\n");

	Constraint c = request.getConstrains();
	boolean isTestCase = (c.getSource().getVertexID().compareTo("172.10.1.10") == 0) && (c.getDestination().getVertexID().compareTo("172.10.1.40") == 0);

	if (isTestCase) {

	    PCEPAddress sourceAddress = new PCEPAddress(((BasicVertexParams) c.getSource().getVertexParams()).getSwitchID(), false);
	    PCEPAddress destinationAddress = new PCEPAddress(((BasicVertexParams) c.getDestination().getVertexParams()).getSwitchID(), false);
	    int reqID = Integer.parseInt(request.getRequestID());

	    PCEPRequestParametersObject RP = PCEPObjectFrameFactory.generatePCEPRequestParametersObject("1", "0", "1", "0", "0", "1", Integer.toString(reqID));
	    PCEPEndPointsObject endPoints = PCEPObjectFrameFactory.generatePCEPEndPointsObject("1", "0", sourceAddress, destinationAddress);

	    // Address destAddress = new Address(serverAddressTextField.getText());
	    PCEPAddress destAddress = new PCEPAddress("172.16.1.2", 4189);
	    lm.getClientModule().registerConnection(destAddress, false, true, false);

	    PCEPRequestFrame requestMessageFrame = PCEPRequestFrameFactory.generatePathComputationRequestFrame(RP, endPoints);
	    PCEPMessage reqMessage = PCEPMessageFactory.generateMessage(requestMessageFrame);

	    reqMessage.setAddress(destAddress);

	    ComputationModuleMLImpl compModImpl = (ComputationModuleMLImpl) lm.getComputationModule();

	    System.out.println("\n\n\n\n\n request ID -- " + reqID + "\n\n\n\n\n");
	    LinkedBlockingQueue<PCEPMessage> temp = compModImpl.getWorkerTaskQueue(reqID);

	    lm.getClientModule().sendMessage(reqMessage, ModuleEnum.CLIENT_MODULE);

	    System.out.println("Added message to sending Queue");
	    PCEPMessage respMessage = null;
	    try {
		respMessage = temp.take();
	    } catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    } // TODO implement try catch for interrupted exception

	    PCEPResponseFrame frame = PCEPResponseFrameFactory.getPathComputationResponseFrame(respMessage);

	    ArrayList<EROSubobjects> vertexList1 = ((PCEPGenericExplicitRouteObjectImpl) frame.extractExplicitRouteObjectList().get(0)).getTraversedVertexList();

	    // String requestID = request.getRequestID();

	    ArrayList<EROSubobjects> vertexList = new ArrayList<EROSubobjects>();
	    vertexList.add(new PCEPAddress(c.getSource().getVertexID(), false));
	    vertexList.add(new MLDelimiter());

	    for (int i = 0; i < vertexList1.size(); i++) {
		vertexList.add(vertexList1.get(i));
	    }

	    vertexList.add(new MLDelimiter());
	    vertexList.add(new PCEPAddress(c.getDestination().getVertexID(), false));

	    RP = PCEPObjectFrameFactory.generatePCEPRequestParametersObject("1", "0", "1", "0", "0", "1", Integer.toString(reqID));
	    PCEPExplicitRouteObject ERO = PCEPObjectFrameFactory.generatePCEPExplicitRouteObject("1", "0", vertexList);

	    PCEPResponseFrame responseFrame = PCEPResponseFrameFactory.generatePathComputationRequestFrame(RP);
	    responseFrame.insertExplicitRouteObject(ERO);

	    reqMessage = PCEPMessageFactory.generateMessage(responseFrame);
	    reqMessage.setAddress(request.getAddress());

	    System.out.println("\n\n\n\n Sending response message to the Client with the ML ERO \n\n\n\n");

	    // Send response message from the computation layer to the session layer
	    lm.getComputationModule().sendMessage(reqMessage, ModuleEnum.SESSION_MODULE);

	} else {
	    Response response = processRequest();
	    processResponseMessage(response);
	}

    }

    /** Function to create a PCEPResponse message from the response object */
    private void processResponseMessage(Response resp) {

	String requestID = resp.getRequestID();
	ArrayList<PCEPAddress> vertexList = getTraversedVertexes(resp);

	ArrayList<EROSubobjects> newVertexList = new ArrayList<EROSubobjects>();

	for (int i = 0; i < vertexList.size(); i++) {
	    System.out.println("**" + vertexList.get(i).getIPv4Address(false));
	    newVertexList.add(vertexList.get(i));
	}

	PCEPRequestParametersObject RP = PCEPObjectFrameFactory.generatePCEPRequestParametersObject("1", "0", "0", "0", "0", "1", requestID);
	PCEPExplicitRouteObject ERO = PCEPObjectFrameFactory.generatePCEPExplicitRouteObject("1", "0", newVertexList);

	PCEPResponseFrame responseFrame = PCEPResponseFrameFactory.generatePathComputationRequestFrame(RP);
	responseFrame.insertExplicitRouteObject(ERO);

	PCEPMessage message = PCEPMessageFactory.generateMessage(responseFrame);
	message.setAddress(resp.getAddress());

	try {
	    ResultLogger.logResult(message);// FIXME remove after test
	} catch (IOException e) {
	    e.printStackTrace();
	}

	// Send response message from the computation layer to the session layer
	lm.getComputationModule().sendMessage(message, ModuleEnum.SESSION_MODULE);
    }

    /**
     * Function to get the list of traversed vertices from the response object. Used to create ERO
     * 
     * @param resp
     * @return
     */
    private ArrayList<PCEPAddress> getTraversedVertexes(Response resp) {

	ArrayList<VertexElement> vertexArrayList = resp.getElement().getTraversedVertices();
	ArrayList<PCEPAddress> traversedVertexesList = new ArrayList<PCEPAddress>();

	for (int i = 0; i < vertexArrayList.size(); i++) {
	    VertexElement vertex = vertexArrayList.get(i);

	    PCEPAddress address = new PCEPAddress(vertex.getVertexID(), false); // FIXME
	    traversedVertexesList.add(address);
	}

	return traversedVertexesList;
    }

    /**
     * Function to log events inside the WorkerTask
     * 
     * @param event
     */
    private void localLogger(String event) {
	Logger.logSystemEvents("[WorkerTask]     " + event);
    }

    /**
     * Function to log debugging information inside the worker task
     * 
     * @param event
     */
    private void localDebugger(String event) {
	Logger.debugger("[WorkerTask]     " + event);
    }

}

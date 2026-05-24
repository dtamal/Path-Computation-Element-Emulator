/**
 * This file is part of Path Computation Element Emulator (PCEE).
 *
 * <p>PCEE is free software: you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * <p>PCEE is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * <p>You should have received a copy of the GNU General Public License along with PCEE. If not, see
 * http://www.gnu.org/licenses/.
 */
package com.pcee.architecture.computationmodule.threadpool;

import com.graph.elements.vertex.VertexElement;
import com.graph.graphcontroller.Gcontroller;
import com.graph.path.PathElement;
import com.graph.path.algorithms.PathComputationAlgorithm;
import com.graph.path.algorithms.constraints.Constraint;
import com.graph.path.algorithms.constraints.impl.SimplePathComputationConstraint;
import com.graph.path.algorithms.impl.MaxBandwidthShortestPathComputationAlgorithm;
import com.graph.path.algorithms.impl.SimplePathComputationAlgorithm;
import com.pcee.architecture.ModuleEnum;
import com.pcee.architecture.ModuleManagement;
import com.pcee.logger.PceeLoggerFactory;
import com.pcee.protocol.message.PceMessage;
import com.pcee.protocol.message.PceMessageFactory;
import com.pcee.protocol.message.objectframe.PceObjectFrameFactory;
import com.pcee.protocol.message.objectframe.impl.PceBandwidthObject;
import com.pcee.protocol.message.objectframe.impl.PceExplicitRouteObject;
import com.pcee.protocol.message.objectframe.impl.PceNoPathObject;
import com.pcee.protocol.message.objectframe.impl.PceRequestParametersObject;
import com.pcee.protocol.message.objectframe.impl.erosubobjects.EroSubobjects;
import com.pcee.protocol.message.objectframe.impl.erosubobjects.PceAddress;
import com.pcee.protocol.request.PceRequestFrame;
import com.pcee.protocol.request.PceRequestFrameFactory;
import com.pcee.protocol.response.PceResponseFrame;
import com.pcee.protocol.response.PceResponseFrameFactory;
import java.util.ArrayList;
import org.slf4j.Logger;

/**
 * Runnable class used by the thread pool to process path computation requests
 *
 * @author Mohit Chamania
 * @author Marek Drogon
 */
public class WorkerTask implements Runnable {

  private Logger logger = PceeLoggerFactory.getLogger("WorkerTask");
  // Request to be processed
  private PceMessage request;
  // Graph used for computation of the request
  private Gcontroller graph;
  // Module management object to send the response to the session layer
  private ModuleManagement lm;

  /** Default Constructor */
  public WorkerTask(ModuleManagement layerManagement, PceMessage request, Gcontroller graph) {
    lm = layerManagement;
    this.request = request;
    this.graph = graph;
  }

  /** Function to update the graph instance used for computation */
  public void updateGraph(Gcontroller newGraph) {
    this.graph = newGraph;
  }

  /** Function to implement the path computation operations */
  public void run() {
    PceRequestFrame requestFrame = PceRequestFrameFactory.getPathComputationRequestFrame(request);
    logger.info("Starting Processing of Request: " + requestFrame.getRequestID());
    processSingleDomainRequest(requestFrame);
    logger.info("Completed Processing of Request: " + requestFrame.getRequestID());
  }

  private void processSingleDomainRequest(PceRequestFrame requestFrame) {
    // Check if source and destination domain are available in the graph, if not send a no path
    // object
    String sourceID = requestFrame.getSourceAddress().getIPv4Address(false).trim();
    String destID = requestFrame.getDestinationAddress().getIPv4Address(false).trim();
    if (graph.vertexExists(sourceID) && graph.vertexExists(destID)) {
      // begin path computation
      // Check if bandwidth objecy exists in the request frame
      Constraint constr = null;
      PathComputationAlgorithm algo = null;
      if (requestFrame.containsBandwidthObject()) {
        logger.info("Request Contains bandwidth Object");
        constr =
            new SimplePathComputationConstraint(
                graph.getVertex(sourceID),
                graph.getVertex(destID),
                requestFrame.extractBandwidthObject().getBandwidthFloatValue());
        algo = new MaxBandwidthShortestPathComputationAlgorithm();
      } else {
        constr =
            new SimplePathComputationConstraint(graph.getVertex(sourceID), graph.getVertex(destID));
        algo = new SimplePathComputationAlgorithm();
      }
      // Start Path Computation
      PathElement element = algo.computePath(graph, constr);
      if (element != null) {
        logger.info("Computed path is " + element.getVertexSequence());
        // return response
        ArrayList<EroSubobjects> vertexList = getTraversedVertexes(element.getTraversedVertices());

        // Generate ERO Object
        PceExplicitRouteObject ERO =
            PceObjectFrameFactory.generatePCEPExplicitRouteObject("1", "0", vertexList);
        // atleast one path was computed
        PceRequestParametersObject RP =
            PceObjectFrameFactory.generatePCEPRequestParametersObject(
                "1", "0", "0", "0", "0", "1", Integer.toString(requestFrame.getRequestID()));

        PceResponseFrame respFrame =
            PceResponseFrameFactory.generatePathComputationResponseFrame(RP);

        respFrame.insertExplicitRouteObject(ERO);

        if (requestFrame.containsBandwidthObject()) {
          PceBandwidthObject bw =
              PceObjectFrameFactory.generatePCEPBandwidthObject(
                  "1", "0", (float) element.getPathParams().getAvailableCapacity());
          respFrame.insertBandwidthObject(bw);
        }

        PceMessage mesg = PceMessageFactory.generateMessage(respFrame);
        mesg.setAddress(request.getAddress());

        logger.info("Path found in the domain. Sending back to client");
        // Send response message from the computation layer to the session layer
        lm.getComputationModule().sendMessage(mesg, ModuleEnum.SESSION_MODULE);

      } else {
        // No path Found in the source domain return no path Object
        returnNoPathMessage(requestFrame.getRequestID());
      }

    } else {
      // Source and/or destination not present in the PCE
      if (graph.vertexExists(sourceID))
        logger.info(
            "Destination IP address "
                + destID
                + " not in the topology. Returning a no path object");
      else if (graph.vertexExists(destID))
        logger.info(
            "Source IP address " + sourceID + " not in the topology. Returning a no path object");
      else {
        logger.info(
            "Both source IP address "
                + sourceID
                + " and destination IP address "
                + destID
                + " not in the topology. Returning a no path object");
      }
      returnNoPathMessage(requestFrame.getRequestID());
    }
  }

  /** Function to return the no Path message to the Client */
  protected void returnNoPathMessage(int requestID) {
    // Generate a No path object
    PceRequestParametersObject RP =
        PceObjectFrameFactory.generatePCEPRequestParametersObject(
            "1", "0", "0", "0", "0", "1", Integer.toString(requestID));
    PceNoPathObject noPath = PceObjectFrameFactory.generatePCEPNoPathObject("1", "0", 1, "0");
    PceResponseFrame responseFrame =
        PceResponseFrameFactory.generatePathComputationResponseFrame(RP);
    responseFrame.insertNoPathObject(noPath);
    PceMessage mesg = PceMessageFactory.generateMessage(responseFrame);
    mesg.setAddress(request.getAddress());
    lm.getComputationModule().sendMessage(mesg, ModuleEnum.SESSION_MODULE);
  }

  /**
   * Function to get the list of traversed vertices as PCEP addresses from the List of vertices in
   * the graph. Used to create ERO
   *
   * @param resp
   * @return
   */
  protected ArrayList<EroSubobjects> getTraversedVertexes(
      ArrayList<VertexElement> vertexArrayList) {

    ArrayList<EroSubobjects> traversedVertexesList = new ArrayList<EroSubobjects>();

    for (int i = 0; i < vertexArrayList.size(); i++) {
      traversedVertexesList.add(new PceAddress(vertexArrayList.get(i).getVertexID(), false));
    }
    return traversedVertexesList;
  }
}

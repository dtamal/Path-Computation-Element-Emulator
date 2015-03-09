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

package com.pcee.architecture.computationmodule.threadpoolManager;

import com.pcee.architecture.ModuleEnum;
import com.pcee.architecture.ModuleManagement;
import com.pcee.protocol.message.PCEPMessage;
import com.pcee.protocol.message.PCEPMessageFactory;
import com.pcee.protocol.message.objectframe.PCEPObjectFrameFactory;
import com.pcee.protocol.message.objectframe.impl.PCEPBandwidthObject;
import com.pcee.protocol.message.objectframe.impl.PCEPExplicitRouteObject;
import com.pcee.protocol.message.objectframe.impl.PCEPNoPathObject;
import com.pcee.protocol.message.objectframe.impl.PCEPRequestParametersObject;
import com.pcee.protocol.message.objectframe.impl.erosubobjects.EROSubobjects;
import com.pcee.protocol.message.objectframe.impl.erosubobjects.PCEPAddress;
import com.pcee.protocol.request.PCEPRequestFrame;
import com.pcee.protocol.request.PCEPRequestFrameFactory;
import com.pcee.protocol.response.PCEPResponseFrame;
import com.pcee.protocol.response.PCEPResponseFrameFactory;
import com.topology.algorithm.constraint.PathConstraint;
import com.topology.algorithm.PathComputationAlgorithm;
import com.topology.primitives.Connection;
import com.topology.primitives.ConnectionPoint;
import com.topology.primitives.NetworkElement;
import com.topology.primitives.Path;
import com.topology.primitives.Port;
import com.topology.primitives.TopologyManager;
import com.topology.primitives.exception.TopologyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Runnable class used by the thread pool to process path computation requests
 *
 * @author Mohit Chamania
 * @author Marek Drogon
 */
public class WorkerTask implements Runnable {

    private Logger logger = LoggerFactory.getLogger("WorkerTask");
    // Request to be processed
    private PCEPMessage request;
    // Graph used for computation of the request
    private TopologyManager manager;
    // Module management object to send the response to the session layer
    private ModuleManagement lm;

    public WorkerTask(ModuleManagement layerManagement, PCEPMessage request, TopologyManager manager) {
        lm = layerManagement;
        this.request = request;
        this.manager = manager;
    }

    public void updateGraph(TopologyManager manager) {
        this.manager = manager;
    }

    /**
     * Function to implement the path computation operations
     */
    public void run() {
        PCEPRequestFrame requestFrame = PCEPRequestFrameFactory.getPathComputationRequestFrame(request);
        logger.info("Starting Processing of Request: " + requestFrame.getRequestID());
        processSingleDomainRequest(requestFrame);
        logger.info("Completed Processing of Request: " + requestFrame.getRequestID());
    }


    private void processSingleDomainRequest(PCEPRequestFrame requestFrame) {
        //Check if source and destination domain are available in the graph, if not send a no path object
        String sourceID = requestFrame.getSourceAddress().getIPv4Address(false).trim();
        String destID = requestFrame.getDestinationAddress().getIPv4Address(false).trim();
        boolean isSrc = false, isDst = false;
        for (NetworkElement ne : manager.getAllElements(NetworkElement.class)) {
            if (ne.getLabel().equals(sourceID))
                isSrc = true;
            else if (ne.getLabel().equals(destID))
                isDst = true;
        }
        if (isSrc && isDst) {

            PathConstraint constraint = null;
            PathComputationAlgorithm algorithm = null;

            if (requestFrame.containsBandwidthObject()) {
                /**
                 * to implement
                 */
            } else {
                constraint = new PathConstraint(false, false);
                algorithm = new PathComputationAlgorithm();
            }

            Path path = null;
            try {
                ConnectionPoint srcCP = manager.getSingleElementByLabel(sourceID, Port.class);
                ConnectionPoint dstCP = manager.getSingleElementByLabel(destID, Port.class);
                path = algorithm.computePath(manager, srcCP, dstCP, constraint);
            } catch (TopologyException e) {
                e.printStackTrace();
            }


            if (path != null) {
                String pathString = "";

                for(Connection con : path.getForwardConnectionSequence()){
                    pathString += con.getzEnd().getLabel()+"-";;
                }
                logger.info("Computed path is: " + pathString);

                // return response
                ArrayList<EROSubobjects> vertexList = getTraversedVertexes(path.getForwardConnectionSequence());

                //Generate ERO Object
                PCEPExplicitRouteObject ERO = PCEPObjectFrameFactory.generatePCEPExplicitRouteObject("1", "0", vertexList);
                //atleast one path was computed
                PCEPRequestParametersObject RP = PCEPObjectFrameFactory.generatePCEPRequestParametersObject("1", "0", "0", "0", "0", "1", Integer.toString(requestFrame.getRequestID()));

                PCEPResponseFrame respFrame = PCEPResponseFrameFactory.generatePathComputationResponseFrame(RP);

                respFrame.insertExplicitRouteObject(ERO);

                if (requestFrame.containsBandwidthObject()) {
//                    PCEPBandwidthObject bw = PCEPObjectFrameFactory.generatePCEPBandwidthObject("1", "0", (float) element.getPathParams().getAvailableCapacity());
//                    respFrame.insertBandwidthObject(bw);
                }

                PCEPMessage mesg = PCEPMessageFactory.generateMessage(respFrame);
                mesg.setAddress(request.getAddress());

                logger.info("Path found in the domain. Sending back to client");
                // Send response message from the computation layer to the session layer
                lm.getComputationModule().sendMessage(mesg, ModuleEnum.SESSION_MODULE);

            } else {
                //No path Found in the source domain return no path Object
                returnNoPathMessage(requestFrame.getRequestID());
            }


        } else {
            //Source and/or destination not present in the PCE
            if (isSrc)
                logger.info("Destination IP address " + destID + " not in the topology. Returning a no path object");
            else if (isDst)
                logger.info("Source IP address " + sourceID + " not in the topology. Returning a no path object");
            else {
                logger.info("Both source IP address " + sourceID + " and destination IP address " + destID + " not in the topology. Returning a no path object");
            }
            returnNoPathMessage(requestFrame.getRequestID());
        }
    }


    /**
     * Function to return the no Path message to the Client
     */
    protected void returnNoPathMessage(int requestID) {
        //Generate a No path object
        PCEPRequestParametersObject RP = PCEPObjectFrameFactory.generatePCEPRequestParametersObject("1", "0", "0", "0", "0", "1", Integer.toString(requestID));
        PCEPNoPathObject noPath = PCEPObjectFrameFactory.generatePCEPNoPathObject("1", "0", 1, "0");
        PCEPResponseFrame responseFrame = PCEPResponseFrameFactory.generatePathComputationResponseFrame(RP);
        responseFrame.insertNoPathObject(noPath);
        PCEPMessage mesg = PCEPMessageFactory.generateMessage(responseFrame);
        mesg.setAddress(request.getAddress());
        lm.getComputationModule().sendMessage(mesg, ModuleEnum.SESSION_MODULE);
    }

    /**
     * Function to get the list of traversed vertices as PCEP addresses from the List of vertices in the graph. Used to create ERO
     *
     * @return
     */
    protected ArrayList<EROSubobjects> getTraversedVertexes(List<Connection> vertexArrayList) {

        ArrayList<EROSubobjects> traversedVertexesList = new ArrayList<EROSubobjects>();

        for (int i = 0; i < vertexArrayList.size(); i++) {
            traversedVertexesList.add(new PCEPAddress(vertexArrayList.get(i).getzEnd().getLabel(), false));
        }
        return traversedVertexesList;
    }

}

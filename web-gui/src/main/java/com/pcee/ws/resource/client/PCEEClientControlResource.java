package com.pcee.ws.resource.client;

import com.pcee.architecture.ModuleEnum;
import com.pcee.architecture.ModuleManagement;
import com.pcee.protocol.message.PceMessage;
import com.pcee.protocol.message.PceMessageFactory;
import com.pcee.protocol.message.objectframe.PceObjectFrameFactory;
import com.pcee.protocol.message.objectframe.impl.PceEndPointsObject;
import com.pcee.protocol.message.objectframe.impl.PceRequestParametersObject;
import com.pcee.protocol.message.objectframe.impl.erosubobjects.PceAddress;
import com.pcee.protocol.request.PceRequestFrame;
import com.pcee.protocol.request.PceRequestFrameFactory;
import com.pcee.protocol.response.PceResponseFrame;
import com.pcee.protocol.response.PceResponseFrameFactory;
import com.pcee.ws.launcher.PCEEWebLauncher;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.concurrent.LinkedBlockingQueue;

@Path("/client")
public class PCEEClientControlResource {

  private ModuleManagement clientModuleManagement = PCEEWebLauncher.getClientModuleManagement();

  @GET
  @Path("/connect")
  @Produces({MediaType.APPLICATION_JSON})
  public Response setConnection() {
    if (clientModuleManagement == null) {

      PCEEWebLauncher.setClientModuleManagement(new ModuleManagement(false));
      PceAddress address = new PceAddress("127.0.0.1", Integer.parseInt("4189"));
      PCEEWebLauncher.getClientModuleManagement()
          .getClientModule()
          .registerConnection(address, false, true, true);

      return Response.ok().build();

    } else return Response.status(400).build();
  }

  @GET
  @Path("/disconnect")
  @Produces({MediaType.APPLICATION_JSON})
  public Response setDisconnection() {
    if (clientModuleManagement != null) {

      ////////////////////////////////////////////
      clientModuleManagement.stop(false);
      PCEEWebLauncher.setClientModuleManagement(null);
      // /////////////////////////////////////////
      return Response.ok().build();

    } else return Response.status(400).build();
  }

  @POST
  @Path("/request")
  @Consumes({MediaType.APPLICATION_JSON})
  @Produces({MediaType.APPLICATION_JSON})
  public Response sendRequest(String data) {
    if (clientModuleManagement != null) {

      String[] part = data.substring(1, data.length() - 1).split(" ");
      String serverAddr = "127.0.0.1";
      String serverPort = "4189";
      String srcAddr = part[0];
      String dstAddr = part[1];

      PceAddress sourceAddress = new PceAddress(srcAddr, false);
      PceAddress destinationAddress = new PceAddress(dstAddr, false);
      PceRequestParametersObject RP =
          PceObjectFrameFactory.generatePCEPRequestParametersObject(
              "1", "0", "1", "0", "0", "1", "432");
      PceEndPointsObject endPoints =
          PceObjectFrameFactory.generatePCEPEndPointsObject(
              "0", "0", sourceAddress, destinationAddress);
      PceAddress destAddress = new PceAddress(serverAddr, Integer.parseInt(serverPort));
      PceRequestFrame requestMessage =
          PceRequestFrameFactory.generatePathComputationRequestFrame(RP, endPoints);
      PceMessage message = PceMessageFactory.generateMessage(requestMessage);
      message.setAddress(destAddress);
      clientModuleManagement.getClientModule().sendMessage(message, ModuleEnum.SESSION_MODULE);

      LinkedBlockingQueue<PceMessage> receiveQueue =
          clientModuleManagement.getClientModule().getReceiveQueue();

      PceResponseFrame responseFrame;
      String traversedNodes = null;
      try {
        responseFrame =
            PceResponseFrameFactory.getPathComputationResponseFrame(receiveQueue.take());

        traversedNodes = responseFrame.getTraversedVertexes();

      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      String[] nodes =
          ((String) traversedNodes.subSequence(1, traversedNodes.length() - 1)).split("-");

      return Response.ok(nodes).build();

    } else return Response.status(400).build();
  }
}

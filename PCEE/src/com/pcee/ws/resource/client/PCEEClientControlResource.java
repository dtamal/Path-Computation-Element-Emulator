package com.pcee.ws.resource.client;

import java.util.concurrent.LinkedBlockingQueue;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.pcee.architecture.ModuleEnum;
import com.pcee.architecture.ModuleManagement;
import com.pcee.protocol.message.PCEPMessage;
import com.pcee.protocol.message.PCEPMessageFactory;
import com.pcee.protocol.message.objectframe.PCEPObjectFrameFactory;
import com.pcee.protocol.message.objectframe.impl.PCEPEndPointsObject;
import com.pcee.protocol.message.objectframe.impl.PCEPRequestParametersObject;
import com.pcee.protocol.message.objectframe.impl.erosubobjects.PCEPAddress;
import com.pcee.protocol.request.PCEPRequestFrame;
import com.pcee.protocol.request.PCEPRequestFrameFactory;
import com.pcee.protocol.response.PCEPResponseFrame;
import com.pcee.protocol.response.PCEPResponseFrameFactory;
import com.pcee.ws.launcher.PCEEWebLauncher;

@Path("/client")
public class PCEEClientControlResource {

	private ModuleManagement clientModuleManagement = PCEEWebLauncher
			.getClientModuleManagement();

	@GET
	@Path("/connect")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response setConnection() {
		if (clientModuleManagement == null) {

			PCEEWebLauncher.setClientModuleManagement(new ModuleManagement(
					false));
			PCEPAddress address = new PCEPAddress("127.0.0.1",
					Integer.parseInt("4189"));
			PCEEWebLauncher.getClientModuleManagement().getClientModule()
					.registerConnection(address, false, true, true);

			return Response.ok().build();

		} else
			return Response.status(400).build();
	}

	@GET
	@Path("/disconnect")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response setDisconnection() {
		if (clientModuleManagement != null) {

			////////////////////////////////////////////
			clientModuleManagement.stop(false);
			PCEEWebLauncher.setClientModuleManagement(null);
			// /////////////////////////////////////////
			return Response.ok().build();

		} else
			return Response.status(400).build();
	}

	@POST
	@Path("/request")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response sendRequest(String data) {
		if (clientModuleManagement != null) {

			String[] part = data.substring(1, data.length() - 1).split(" ");
			String serverAddr = "127.0.0.1";
			String serverPort = "4189";
			String srcAddr = part[0];
			String dstAddr = part[1];

			PCEPAddress sourceAddress = new PCEPAddress(srcAddr, false);
			PCEPAddress destinationAddress = new PCEPAddress(dstAddr, false);
			PCEPRequestParametersObject RP = PCEPObjectFrameFactory
					.generatePCEPRequestParametersObject("1", "0", "1", "0",
							"0", "1", "432");
			PCEPEndPointsObject endPoints = PCEPObjectFrameFactory
					.generatePCEPEndPointsObject("0", "0", sourceAddress,
							destinationAddress);
			PCEPAddress destAddress = new PCEPAddress(serverAddr,
					Integer.parseInt(serverPort));
			PCEPRequestFrame requestMessage = PCEPRequestFrameFactory
					.generatePathComputationRequestFrame(RP, endPoints);
			PCEPMessage message = PCEPMessageFactory
					.generateMessage(requestMessage);
			message.setAddress(destAddress);
			clientModuleManagement.getClientModule().sendMessage(message,
					ModuleEnum.SESSION_MODULE);

			LinkedBlockingQueue<PCEPMessage> receiveQueue = clientModuleManagement
					.getClientModule().getReceiveQueue();
			
			
			PCEPResponseFrame responseFrame;
			String traversedNodes = null;
			try {
				responseFrame = PCEPResponseFrameFactory
						.getPathComputationResponseFrame(receiveQueue.take());
				
				traversedNodes = responseFrame.getTraversedVertexes();
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String[] nodes = ((String) traversedNodes.subSequence(1, traversedNodes.length()-1)).split("-");

			return Response.ok(nodes).build();

		} else
			return Response.status(400).build();
	}

}

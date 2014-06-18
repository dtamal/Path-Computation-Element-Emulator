package com.pcee.ws.resource.server;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.graph.elements.edge.EdgeElement;
import com.graph.elements.vertex.VertexElement;
import com.pcee.architecture.ModuleManagement;
import com.pcee.architecture.computationmodule.ted.TopologyInformation;
import com.pcee.ws.launcher.PCEEWebServerLauncher;

@Path("/server")
public class PCEEServerControlResource {

	private ModuleManagement server = PCEEWebServerLauncher.getModuleManagement();
	
	@GET
	@Path("/status")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getStatus() {
		if (server == null)
			return Response.status(400)
					// 200
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Methods",
							"GET, POST, DELETE, PUT").build();
		else
			return Response.ok()
					// 200
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Methods",
							"GET, POST, DELETE, PUT").build();
	}

	@GET
	@Path("/start")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response startServer() {
		if (server == null) {
			PCEEWebServerLauncher.setModuleManagement(new ModuleManagement(true));
			return Response.ok()
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Methods",
							"GET, POST, DELETE, PUT").build();
		} else
			return Response.status(400)
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Methods",
							"GET, POST, DELETE, PUT").build();
	}

	@GET
	@Path("/nodes")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getNodes() {

		List<String> setOfNodes = new ArrayList<String>();

		for (VertexElement v : TopologyInformation.getInstance().getGraph()
				.createCopy().getVertexSet()) {
			setOfNodes.add(v.getVertexID() + " " + v.getXCoord() + " "
					+ v.getYCoord());
		}

		return Response
				.ok(setOfNodes)
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Methods",
						"GET, POST, DELETE, PUT").build();
	}

	@GET
	@Path("/links")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getLinks() {

		List<String> setOfLinks = new ArrayList<String>();

		for (EdgeElement e : TopologyInformation.getInstance().getGraph()
				.createCopy().getEdgeSet()) {
			setOfLinks.add(e.getSourceVertex().getVertexID() + " "
					+ e.getDestinationVertex().getVertexID());
		}

		return Response
				.ok(setOfLinks)
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Methods",
						"GET, POST, DELETE, PUT").build();
	}

}

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
import com.pcee.ws.launcher.PCEEWebLauncher;

@Path("/server")
public class PCEEServerControlResource {

	private ModuleManagement serverModuleManagement = PCEEWebLauncher
			.getServerModuleManagement();

	@GET
	@Path("/status")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getStatus() {
		if (serverModuleManagement == null)
			return Response
					.status(400)
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Methods",
							"GET, POST, DELETE, PUT").build();
		else
			return Response
					.ok()
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Methods",
							"GET, POST, DELETE, PUT").build();
	}

	@GET
	@Path("/start")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response startServer() {
		if (serverModuleManagement == null) {
			PCEEWebLauncher
					.setServerModuleManagement(new ModuleManagement(true));
			return Response
					.ok()
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Methods",
							"GET, POST, DELETE, PUT").build();
		} else
			return Response
					.status(400)
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Methods",
							"GET, POST, DELETE, PUT").build();
	}

	@GET
	@Path("/stop")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response stopServer() {
		if (serverModuleManagement != null) {

			// This is not the correct way to stop the server
			serverModuleManagement.stop(false);
			PCEEWebLauncher.setServerModuleManagement(null);
			// ///////////////////////////////////

			return Response
					.ok()
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Methods",
							"GET, POST, DELETE, PUT").build();
		} else
			return Response
					.status(400)
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Methods",
							"GET, POST, DELETE, PUT").build();
	}

	@GET
	@Path("/topology/nodes")
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
	@Path("/topology/links")
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

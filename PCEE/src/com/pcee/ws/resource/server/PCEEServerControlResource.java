package com.pcee.ws.resource.server;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

//import com.graph.elements.edge.EdgeElement;
//import com.graph.elements.vertex.VertexElement;
import com.pcee.architecture.ModuleManagement;
//import com.pcee.architecture.computationmodule.ted.TopologyInformation;
import com.pcee.architecture.computationmodule.ted.TopoManager;
import com.pcee.ws.launcher.PCEEWebLauncher;
import com.topology.primitives.Link;
import com.topology.primitives.NetworkElement;
import com.topology.primitives.exception.properties.PropertyException;
import com.topology.primitives.properties.keys.TEPropertyKey;

@Path("/server")
public class PCEEServerControlResource {

	private ModuleManagement serverModuleManagement = PCEEWebLauncher
			.getServerModuleManagement();
	private String topology = PCEEWebLauncher.getTopology();

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

	@POST
	@Path("/start")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response startServer(String topology) {

		topology = topology.substring(1, topology.length() - 1);

		PCEEWebLauncher.setTopology(topology);

		topology = ".//" + topology + ".txt";

		if (serverModuleManagement == null) {
			PCEEWebLauncher.setServerModuleManagement(new ModuleManagement(
					topology, true));
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

//	@GET
//	@Path("/topology/nodes")
//	@Produces({ MediaType.APPLICATION_JSON })
//	public Response getNodes() {
//
//		double scalingFactor = 1.0;
//		if (this.topology.equals("austria"))
//			scalingFactor = 1.7;
//
//		List<String> setOfNodes = new ArrayList<String>();
//
//		for (VertexElement v : TopologyInformation.getInstance().getGraph()
//				.createCopy().getVertexSet()) {
//			setOfNodes.add(v.getVertexID() + " " + v.getXCoord()
//					* scalingFactor + " " + v.getYCoord() * scalingFactor);
//		}
//
//		return Response
//				.ok(setOfNodes)
//				.header("Access-Control-Allow-Origin", "*")
//				.header("Access-Control-Allow-Methods",
//						"GET, POST, DELETE, PUT").build();
//	}
//
//	@GET
//	@Path("/topology/links")
//	@Produces({ MediaType.APPLICATION_JSON })
//	public Response getLinks() {
//
//		List<String> setOfLinks = new ArrayList<String>();
//
//		for (EdgeElement e : TopologyInformation.getInstance().getGraph()
//				.createCopy().getEdgeSet()) {
//			setOfLinks.add(e.getSourceVertex().getVertexID() + " "
//					+ e.getDestinationVertex().getVertexID());
//		}
//
//		return Response
//				.ok(setOfLinks)
//				.header("Access-Control-Allow-Origin", "*")
//				.header("Access-Control-Allow-Methods",
//						"GET, POST, DELETE, PUT").build();
//	}

    @GET
    @Path("/topology/nodes")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getNodes() {

        double scalingFactor = 1.0;
        if (this.topology.equals("austria"))
            scalingFactor = 1.7;

        List<String> setOfNodes = new ArrayList<String>();

        for (NetworkElement ne : TopoManager.get_instance().getManager().getAllElements(NetworkElement.class)) {
            try {
                setOfNodes.add(ne.getLabel() + " " + (double) ne.getProperty(TEPropertyKey.XCOORD) * scalingFactor + " " + (double) ne.getProperty(TEPropertyKey.YCOORD) * scalingFactor);
            } catch (PropertyException e) {
                e.printStackTrace();
            }
        }
//        for (VertexElement v : TopologyInformation.getInstance().getGraph()
//                .createCopy().getVertexSet()) {
//            setOfNodes.add(v.getVertexID() + " " + v.getXCoord()
//                    * scalingFactor + " " + v.getYCoord() * scalingFactor);
//        }

        return Response
                .ok(setOfNodes)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods",
                        "GET, POST, DELETE, PUT").build();
    }

    @GET
    @Path("/topology/links")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getLinks() {

        List<String> setOfLinks = new ArrayList<String>();

        for (Link l:TopoManager.get_instance().getManager().getAllElements(Link.class)){
            setOfLinks.add(l.getaEnd().getLabel()+ " "
                    + l.getzEnd().getLabel());
        }
//        for (EdgeElement e : TopologyInformation.getInstance().getGraph()
//                .createCopy().getEdgeSet()) {
//            setOfLinks.add(e.getSourceVertex().getVertexID() + " "
//                    + e.getDestinationVertex().getVertexID());
//        }

        return Response
                .ok(setOfLinks)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods",
                        "GET, POST, DELETE, PUT").build();
    }
}

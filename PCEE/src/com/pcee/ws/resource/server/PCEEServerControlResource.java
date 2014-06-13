package com.pcee.ws.resource.server;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.pcee.architecture.ModuleManagement;

@Path("/server")
public class PCEEServerControlResource {

	private ModuleManagement server;
	
	@GET
	@Path("/status")
	@Produces ({MediaType.APPLICATION_JSON})
	public Response getStatus() {
		if (server==null)
			return Response.status(400) //200
		            .header("Access-Control-Allow-Origin", "*")
		            .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
		            .build();
		else 
			return Response.ok() //200
		            .header("Access-Control-Allow-Origin", "*")
		            .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
		            .build();
	}
	
	
}

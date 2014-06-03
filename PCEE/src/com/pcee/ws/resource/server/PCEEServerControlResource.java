package com.pcee.ws.resource.server;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.pcee.architecture.ModuleManagement;
import com.pcee.ws.primitives.server.ServerStatus;

@Path("/server")
public class PCEEServerControlResource {

	private ModuleManagement server;
	
	@GET
	@Path("/status")
	@Produces ({MediaType.APPLICATION_JSON})
	public ServerStatus getStatus() {
		if (server==null)
			return ServerStatus._FALSE;
		else 
			return ServerStatus._TRUE;
	}
	
	
}

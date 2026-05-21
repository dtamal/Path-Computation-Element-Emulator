package com.pcee.ws.resource.logging;

import java.util.ArrayList;
import java.util.List;

import com.pcee.logger.PCEELoggerFactory;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pcee.ws.primitives.logging.WsLogMessage;

@Path("/logs")
public class LoggingResource {

	private static Logger logger = PCEELoggerFactory.getLogger(LoggingResource.class);
	
	private static List<WsLogMessage> logs = new ArrayList<WsLogMessage> ();

	public static void addLogMessage(WsLogMessage log) {
		synchronized(logs) {	
			logs.add(log);
		}
	}

	@GET
	@Produces ({MediaType.APPLICATION_JSON})
	public List<WsLogMessage> getLogs() {
//		logger.info("Generating logs for consumption via WS");
		synchronized (logs) {
			List<WsLogMessage> out = logs;
			logs = new ArrayList<WsLogMessage>();
			return out;
		}
	}


}

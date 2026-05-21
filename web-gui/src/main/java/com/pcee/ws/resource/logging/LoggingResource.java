package com.pcee.ws.resource.logging;

import com.pcee.logger.PceeLoggerFactory;
import com.pcee.ws.primitives.logging.WsLogMessage;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;

@Path("/logs")
public class LoggingResource {

  private static Logger logger = PceeLoggerFactory.getLogger(LoggingResource.class);

  private static List<WsLogMessage> logs = new ArrayList<WsLogMessage>();

  public static void addLogMessage(WsLogMessage log) {
    synchronized (logs) {
      logs.add(log);
    }
  }

  @GET
  @Produces({MediaType.APPLICATION_JSON})
  public List<WsLogMessage> getLogs() {
    //		logger.info("Generating logs for consumption via WS");
    synchronized (logs) {
      List<WsLogMessage> out = logs;
      logs = new ArrayList<WsLogMessage>();
      return out;
    }
  }
}

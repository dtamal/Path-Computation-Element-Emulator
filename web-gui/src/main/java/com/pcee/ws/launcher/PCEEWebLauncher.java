package com.pcee.ws.launcher;

import com.pcee.architecture.ModuleManagement;
import com.pcee.logger.PceeLoggerFactory;
import com.pcee.ws.primitives.logging.WsLogMessage;
import com.pcee.ws.resource.logging.LoggingResource;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import org.eclipse.jetty.ee10.servlet.DefaultServlet;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.resource.ResourceFactory;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.bridge.SLF4JBridgeHandler;

public class PCEEWebLauncher {

  private static Server server;

  private static Logger logger = PceeLoggerFactory.getLogger(PCEEWebLauncher.class);

  private static boolean serverRole;

  private static int clientWsPort = 8081;
  private static int serverWsPort = 8080;

  private static ModuleManagement serverModuleManagement;
  private static ModuleManagement clientModuleManagement;

  private static String topology;

  public static ModuleManagement getClientModuleManagement() {
    return clientModuleManagement;
  }

  public static void setClientModuleManagement(ModuleManagement clientModuleManagement) {
    PCEEWebLauncher.clientModuleManagement = clientModuleManagement;
  }

  public static ModuleManagement getServerModuleManagement() {
    return serverModuleManagement;
  }

  public static void setServerModuleManagement(ModuleManagement moduleManagement) {
    PCEEWebLauncher.serverModuleManagement = moduleManagement;
  }

  /**
   * Function to indicate if the WebServer is running as a client or a server
   *
   * @return true in case role is server, false otherwise
   */
  public static boolean isServerRole() {
    return serverRole;
  }

  /**
   * Function to parse the input parameters to identify if the instance should be a PCEE server or
   * client instance
   *
   * @param args
   * @return true in case role is server, false otherwise, default to true
   */
  private static boolean getRole(String[] args) {
    if (args != null) {
      if (args.length == 1) {
        if (args[0].equalsIgnoreCase("false")) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Function to generate the Jersey handler based on the configuration defined using the web.xml
   * file
   *
   * @param isServerRole boolean to indicate if the configuration should be instantiated for a PCEE
   *     server or a client
   * @return
   */
  private static ServletHolder getJerseyContext(boolean isServerRole) {
    // TODO cleanup how servlets are initialized
    // Code to add Jersey Servlet to the Jetty Handlers
    logger.info("Initializing Jersey Servlet configuration");
    ServletHolder h = new ServletHolder(new ServletContainer());
    // Updated Jersey 4.x configuration parameters
    h.setInitParameter("jersey.config.server.provider.packages", "com.pcee.ws.resource");
    h.setInitParameter("jersey.config.server.json.enableMoxyJson", "true"); // Enable JSON support
    h.setInitOrder(1);
    return h;
  }

  /**
   * Function to generate jetty Handlers based on how an instance is started in the code
   *
   * @return list of handlers for serving content via Jetty
   * @throws MalformedURLException
   * @throws IOException
   */
  private static ServletContextHandler getWsHandlers(
      boolean isServerRole, ResourceFactory resourceFactory)
      throws MalformedURLException, IOException, URISyntaxException {
    ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
    context.setContextPath("/");

    // Set base resource for static content
    URL staticResourcePath;
    if (isServerRole) {
      staticResourcePath = PCEEWebLauncher.class.getClassLoader().getResource("pcee-server");
    } else {
      staticResourcePath = PCEEWebLauncher.class.getClassLoader().getResource("pcee-client");
    }

    //    // Fix: Use Resource.newClassPathResource(String) for Jetty 12+
    //    Resource baseResource = Resource.newClassPathResource(staticResourcePath);
    //    if (baseResource == null || !baseResource.exists()) {
    //      logger.error("Static resource path not found for role: {}", staticResourcePath);
    //        throw new IOException("Static resource path not found: " + staticResourcePath);
    //    }
    context.setBaseResource(resourceFactory.newResource(staticResourcePath));

    // Add DefaultServlet for static content
    ServletHolder staticContentHolder = new ServletHolder("default", DefaultServlet.class);
    staticContentHolder.setInitParameter(
        "dirAllowed", "true"); // Allow directory listing (optional)
    context.addServlet(staticContentHolder, "/");

    // Add Jersey Servlet for REST API
    ServletHolder jerseyContext = getJerseyContext(isServerRole);
    context.addServlet(jerseyContext, "/ctrl/*");

    return context;
  }

  public static void main(String[] args) {
    try {
      // log messages to the Web UI
      PceeLoggerFactory.setOperation(
          (arg) -> {
            WsLogMessage msg = new WsLogMessage();
            msg.setMessage(arg);
            LoggingResource.addLogMessage(msg);
          });
      //			StaticLoggerBinder.getSingleton().logToWs();

      // Check if instance is initialized as a server or a client
      serverRole = getRole(args);
      if (serverRole) logger.info("Instance launched to control a PCEE Server Instance");
      else logger.info("Instance launched to control a PCEE Client Instance");

      // SLF4J Bridge to migrate Jersey logging to SLF4J
      SLF4JBridgeHandler.removeHandlersForRootLogger();
      SLF4JBridgeHandler.install();

      logger.info("Initializing Jetty Server");
      if (serverRole) {
        server = new Server(serverWsPort);
      } else {
        server = new Server(clientWsPort);
      }
      try (ResourceFactory.Closeable resourceFactory = ResourceFactory.closeable()) {

        server.setHandler(getWsHandlers(serverRole, resourceFactory));
        server.start();
        server.join();
        logger.info("Jetty Server Started Successfully");
      }
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
  }

  public static String getTopology() {
    return topology;
  }

  public static void setTopology(String topology) {
    PCEEWebLauncher.topology = topology;
  }
}

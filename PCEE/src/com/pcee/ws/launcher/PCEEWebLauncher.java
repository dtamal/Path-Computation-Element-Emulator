package com.pcee.ws.launcher;

import java.io.IOException;
import java.net.MalformedURLException;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.slf4j.impl.StaticLoggerBinder;

import com.pcee.architecture.ModuleManagement;
import com.sun.jersey.spi.container.servlet.ServletContainer;

public class PCEEWebLauncher {

	private static Server server;

	private static Logger logger = LoggerFactory.getLogger(PCEEWebLauncher.class);

	private static boolean serverRole;

	private static int clientWsPort = 8081;
	private static int serverWsPort = 8080;
	
	private static ModuleManagement serverModuleManagement;
	private static ModuleManagement clientModuleManagement;

	public static ModuleManagement getClientModuleManagement() {
		return clientModuleManagement;
	}

	public static void setClientModuleManagement(
			ModuleManagement clientModuleManagement) {
		PCEEWebLauncher.clientModuleManagement = clientModuleManagement;
	}

	public static ModuleManagement getServerModuleManagement() {
		return serverModuleManagement;
	}

	public static void setServerModuleManagement(ModuleManagement moduleManagement) {
		PCEEWebLauncher.serverModuleManagement = moduleManagement;
	}

	/**Function to indicate if the WebServer is running as a client or a server
	 * 
	 * @return true in case role is server, false otherwise
	 */
	public static boolean isServerRole() {
		return serverRole;
	}
	
	/**Function to parse the input parameters to identify if the instance should be a PCEE server or client instance
	 * 
	 * @param args
	 * @return true in case role is server, false otherwise, default to true
	 */
	private static boolean getRole(String[] args) {
		if (args!=null) {
			if (args.length==1) {
				if (args[0].equalsIgnoreCase("false")) {
					return false;
				}
			}
		}
		return true;
	}
	
	
	/** Function to generate the Jersey handler based on the configuration defined using the web.xml file
	 * 
	 * @param isServerRole boolean to indicate if the configuration should be instantiated for a PCEE server or a client
	 * @return
	 * @throws IOException 
	 * @throws MalformedURLException 
	 */
	private static ServletContextHandler getJerseyContext(boolean isServerRole) throws MalformedURLException, IOException {
		//Code to add Jersey Servlet to the Jetty Handlers 
		logger.info("Initializing Jersey Servlet configuration");
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);
        ServletHolder h = new ServletHolder(new ServletContainer());
        h.setInitParameter("com.sun.jersey.api.json.POJOMappingFeature", "true");
        h.setInitParameter("com.sun.jersey.config.property.packages", "com.pcee.ws.resource");
        h.setInitOrder(1);
        context.addServlet(h, "/ctrl/*");
        return context;
	}

	/** Function to generate the ResourceHandler to serve static HTML/CSS/JS content 
	 * 
	 * @param isServerRole boolean to indicate if the configuration should be instantiated for a PCEE server or a client
	 * @return
	 */
	private static ContextHandler getStaticContext(boolean isServerRole) throws MalformedURLException, IOException {
		//Code to add Jersey Servlet to the Jetty Handlers 
		logger.info("Initializing Static Resource Handler configuration");
		ResourceHandler resource_handler = new ResourceHandler();
		if (isServerRole)
			resource_handler.setBaseResource(Resource.newResource("PCEEServer_WEB"));
		else
			resource_handler.setBaseResource(Resource.newResource("PCEEClient_WEB"));			
		ContextHandler handler = new ContextHandler();
		handler.setHandler(resource_handler);
		handler.setContextPath("/");
		return handler;
	}

	
	/**Function to generate jetty Handlers based on how an instance is started in the code
	 * 
	 * @return list of handlers for serving content via Jetty
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	private static HandlerList getWsHandlers(boolean isServerRole) throws MalformedURLException, IOException {
		HandlerList handlers = new HandlerList();
		//			  Code to add Jersey Servlet to the Jetty Handlers 
		//Jetty Context 
		ServletContextHandler jerseyContext = getJerseyContext(isServerRole);
		//Static Content Context
		ContextHandler staticContentContext = getStaticContext(isServerRole);

		logger.info("Adding Handlers and starting Jetty Server");
		handlers.setHandlers(new Handler[] { jerseyContext, staticContentContext, new DefaultHandler() });
		
		return handlers;
	}
	
	
	public static void main (String[] args) {
		try {
			
			StaticLoggerBinder.getSingleton().logToWs();

			
			//Check if instance is initialized as a server or a client
			serverRole = getRole(args);
			if (serverRole)
				logger.info("Instance launched to control a PCEE Server Instance");
			else 
				logger.info("Instance launched to control a PCEE Client Instance");
				
			// SLF4J Bridge to migrate Jersey logging to SLF4J
			SLF4JBridgeHandler.removeHandlersForRootLogger();
			SLF4JBridgeHandler.install();

			logger.info("Initializing Jetty Server");
			if (serverRole) {
				server = new Server (serverWsPort);
			} else {
				server = new Server (clientWsPort);
			}
			
			server.setHandler(getWsHandlers(serverRole));
			server.start();
			server.join();
			logger.info("Jetty Server Started Successfylly");
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}


}

package com.pcee.ws.launcher;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

public class PCEEWebServerLauncher {

	private static Server server;

	private static Logger logger = LoggerFactory.getLogger(PCEEWebServerLauncher.class);
	
	public static void main (String[] args) {
		try {

			// SLF4J Bridge to migrate Jersey logging to SLF4J
			SLF4JBridgeHandler.removeHandlersForRootLogger();
			SLF4JBridgeHandler.install();

			logger.info("Initializing Jetty Server");

			server = new Server (8080);

			HandlerList handlers = new HandlerList();

			//			  Code to add Jersey Servlet to the Jetty Handlers 
			logger.info("Initializing Jersey Servlet configuration");
			WebAppContext webAppContext = new WebAppContext();
			webAppContext.setDescriptor(webAppContext + "/WEB-INF/web.xml");
			webAppContext.setResourceBase(".");
			webAppContext.setContextPath("/");


			// Code to add Static resource handler to the Jetty Handlers 
			logger.info("Initializing Static Resource Handler configuration");
			ResourceHandler resource_handler = new ResourceHandler();
			resource_handler.setDirectoriesListed(false);
			resource_handler.setWelcomeFiles(new String[]{ "./web/index.html" });
			resource_handler.setResourceBase("/");


			logger.info("Adding Handlers and starting Jetty Server");
			handlers.setHandlers(new Handler[] { webAppContext, resource_handler, new DefaultHandler() });
//			handlers.setHandlers(new Handler[] { webAppContext, new DefaultHandler() });
			server.setHandler(handlers);
			server.start();
			server.join();
			logger.info("Jetty Server Started Successfylly");
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}


}

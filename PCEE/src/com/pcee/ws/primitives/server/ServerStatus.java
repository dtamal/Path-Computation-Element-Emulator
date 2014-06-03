package com.pcee.ws.primitives.server;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ServerStatus {
	
	public static ServerStatus _FALSE = new ServerStatus(false);
	public static ServerStatus _TRUE = new ServerStatus(true);
	
	/**Default Constructor */
	public ServerStatus() {}

	/**Default Constructor */
	public ServerStatus(boolean running) {
		this.running = running;
	}

	private boolean running;

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	
}

package com.pcee.ws.primitives.logging;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class WsLogMessage {

	private String level;
	
	private long time;
	
	private String message;

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}

package com.pcee.slf4j.logger;

import com.pcee.ws.primitives.logging.WsLogMessage;
import com.pcee.ws.resource.logging.LoggingResource;

public class WsLogModule implements LogModule{

	@Override
	public void log(LogLevelEnum level, String arg) {
		WsLogMessage message = new WsLogMessage();
		message.setLevel(level.toString());
		message.setMessage(arg);
		message.setTime(System.currentTimeMillis()/1000);
		LoggingResource.addLogMessage(message);
	}

}

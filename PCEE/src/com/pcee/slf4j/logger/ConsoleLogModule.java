package com.pcee.slf4j.logger;

public class ConsoleLogModule implements LogModule{

	@Override
	public void log(LogLevelEnum level, String arg) {
		System.out.println(arg);
	}

	
}

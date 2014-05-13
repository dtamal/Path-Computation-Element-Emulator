package com.pcee.slf4j.logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

public class PceeLoggerFactory implements ILoggerFactory {

	public static boolean LOG_TO_GUI = false;
	
	private ConcurrentMap<String, Logger> map;

	List<LogModule> logModules;
	
	public PceeLoggerFactory() {
		map = new ConcurrentHashMap<String, Logger>();
		logModules = new ArrayList<LogModule>();
		logModules.add(new ConsoleLogModule());
	}
	
	public void logToGUI() {
		logModules.add(new GUILogModule());
	}
	
	@Override
	public Logger getLogger(String name) {
		if (map.containsKey(name)) {
			return map.get(name);
		} else  {
			//generate new Logger
			Logger logger = new PceeLogger(name, logModules);
			map.put(name, logger);
			return logger;
		}
	}
	
	void reset() {
		map.clear();
	}

}

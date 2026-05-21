package com.pcee.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class PCEELoggerFactory {

  @FunctionalInterface
  public interface LogOperation {
    void execute(String logString);
  }

  private static final Map<String, Logger> pceeLogMap = new ConcurrentHashMap<>();

  private static LogOperation customOperation = null;

  public static void setOperation(LogOperation op) {
    customOperation = op;
  }

  public static Logger getLogger(Class<?> clazz) {
    return getLogger(clazz.getName());
  }

  public static Logger getLogger(String name) {
    if (customOperation == null) {
      return LoggerFactory.getLogger(name);
    } else {
      return getPCEELogger(name);
    }
  }

  private static Logger getPCEELogger(String name) {
    if (pceeLogMap.containsKey(name)) {
      return pceeLogMap.get(name);
    } else  {
      //generate new Logger
      Logger logger = new PCEELogger(name, customOperation);
      pceeLogMap.put(name, logger);
      return logger;
    }
  }

}

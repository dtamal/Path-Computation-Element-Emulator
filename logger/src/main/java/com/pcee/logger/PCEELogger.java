package com.pcee.logger;

import org.slf4j.Marker;
import org.slf4j.event.Level;
import org.slf4j.helpers.AbstractLogger;
import org.slf4j.helpers.MessageFormatter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PCEELogger extends AbstractLogger {

  private final String callerName;

  private final PCEELoggerFactory.LogOperation op;



  public PCEELogger() {
    this.callerName = "";
    op = _ -> {}; // do nothing
  }

  public PCEELogger(String callerName, PCEELoggerFactory.LogOperation op) {
    this.callerName = callerName;
    this.op = op;

  }


  static boolean TRACE_ENABLED = false;
  static boolean DEBUG_ENABLED = false;
  static boolean INFO_ENABLED = true;
  static boolean WARN_ENABLED = true;
  static boolean ERROR_ENABLED = true;

  static String SEPARATOR = "-";

  @Override
  protected String getFullyQualifiedCallerName() {
    return callerName;
  }

  @Override
  protected void handleNormalizedLoggingCall(Level level, Marker marker, String messagePattern, Object[] arguments, Throwable throwable) {
    StringBuilder buf = new StringBuilder(32);

    // Append date-time if so configured
    buf.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")));
    buf.append(SEPARATOR);
    buf.append('[').append(level.toString()).append(']');
    buf.append(SEPARATOR);

    if (marker != null) {
      buf.append(SEPARATOR).append(marker.getName()).append(SEPARATOR);
    }

    buf.append(MessageFormatter.basicArrayFormat(messagePattern, arguments));

    if (throwable != null) {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      throwable.printStackTrace(pw);
      buf.append(sw);
    }

    op.execute(buf.toString());
  }

  @Override
  public boolean isTraceEnabled() {
    return TRACE_ENABLED;
  }

  @Override
  public boolean isTraceEnabled(Marker marker) {
    return TRACE_ENABLED;
  }

  @Override
  public boolean isDebugEnabled() {
    return DEBUG_ENABLED;
  }

  @Override
  public boolean isDebugEnabled(Marker marker) {
    return DEBUG_ENABLED;
  }

  @Override
  public boolean isInfoEnabled() {
    return INFO_ENABLED;
  }

  @Override
  public boolean isInfoEnabled(Marker marker) {
    return INFO_ENABLED;
  }

  @Override
  public boolean isWarnEnabled() {
    return WARN_ENABLED;
  }

  @Override
  public boolean isWarnEnabled(Marker marker) {
    return WARN_ENABLED;
  }

  @Override
  public boolean isErrorEnabled() {
    return ERROR_ENABLED;
  }

  @Override
  public boolean isErrorEnabled(Marker marker) {
    return ERROR_ENABLED;
  }
}

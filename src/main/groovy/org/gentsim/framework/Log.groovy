package org.gentsim.framework

import org.apache.log4j.*

// This code is based on the original version at http://groovy.dzone.com/tips/log4j-groovy-way?page=0,1.
class Log {

  public static String PATTERN = "%d{ABSOLUTE} %-5p [%c{1}] %m%n"
  public static Level LEVEL = Level.DEBUG
  //public static Level LEVEL = Level.INFO
  //public static Level LEVEL = Level.WARN

  private static boolean initialized = false

  private static Logger logger() {
    def caller = Thread.currentThread().stackTrace.getAt(42)
    if (!initialized) basic()
    return Logger.getInstance(caller.className)
  }

  static basic() {
    def simple = new PatternLayout(PATTERN)
    BasicConfigurator.configure(new ConsoleAppender(simple))
    LogManager.rootLogger.level = LEVEL
    initialized = true
  }

  static setLevel(level) {
    def Level l = null
    if (level instanceof Level) {
      l = level
    } else if (level instanceof String) {
      l = (Level."${level.toUpperCase()}")?: null
    }
    if (l) LogManager.rootLogger.level = l
  }

  static trace(Object... messages) { log("Trace", null, messages) }
  static trace(Throwable t, Object... messages) { log("Trace", t, messages) }
  
  static debug(Object... messages) { log("Debug", null, messages) }
  static debug(Throwable t, Object... messages) { log("Debug", t, messages) }

  static info(Object... messages) { log("Info", null, messages) }
  static info(Throwable t, Object... messages) { log("Info", t, messages) }

  static warn(Object... messages) { log("Warn", null, messages) }
  static warn(Throwable t, Object... messages) { log("Warn", t, messages) }

  static error(Object... messages) { log("Error", null, messages) }
  static error(Throwable t, Object... messages) { log("Error", t, messages) }

  static fatal(Object... messages) { log("Fatal", null, messages) }
  static fatal(Throwable t, Object... messages) { log("Fatal", t, messages) }

  private static log(String level, Throwable t, Object... messages) {
    if (messages) {
      def log = logger()
      if (level.equals("Warn") || level.equals("Error") || level.equals("Fatal") || log."is${level}Enabled" ()) {
        log."${level.toLowerCase()}" (messages.join(), t)
      }
    }
  }
}


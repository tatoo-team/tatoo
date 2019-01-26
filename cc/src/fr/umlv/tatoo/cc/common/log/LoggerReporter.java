package fr.umlv.tatoo.cc.common.log;

import java.util.EnumMap;
import java.util.logging.Handler;
import java.util.logging.Logger;

/** A reporter that use {@link java.util.logging.Logger} to report
 *  informations.
 *  
 * @author Remi
 */
public class LoggerReporter extends Reporter {
  /** Create a reporter with level {@link Level#INFO} as default log level.
   */
  public LoggerReporter() {
    changeLevel(fr.umlv.tatoo.cc.common.log.Level.INFO);
  }
  
  @Override
  public void setLogLevel(fr.umlv.tatoo.cc.common.log.Level level) {
    changeLevel(level);
  }
  
  private void changeLevel(fr.umlv.tatoo.cc.common.log.Level level) {
    this.level=level;
    
    java.util.logging.Level logLevel=levelMap.get(level);
    for(Logger logger=LOGGER;logger!=null;logger=logger.getParent())
      for(Handler h : logger.getHandlers())
        h.setLevel(logLevel);
  }
  
  @Override
  public Level getLogLevel() {
    return level;
  }
  
  @Override
  public void report(Info info) {
    String message=info.toString();
    Object file=info.file();
    fr.umlv.tatoo.cc.common.log.Level level=info.level();

    Throwable cause=info.cause();
    if (cause==null)
      logMessage(level,message,file);
    else
      logCause(level,message,cause);

    /* print stack trace
    if (level==fr.umlv.tatoo.cc.common.log.Level.ERROR && info.cause()!=null) {
      info.cause().printStackTrace();
    } */
    
    if (level==fr.umlv.tatoo.cc.common.log.Level.FATAL_ERROR) {
      if (cause==null)
        throw new AssertionError(message);
      throw (AssertionError)new AssertionError(message).initCause(cause);
    }
  }
  
  private Level level;
  
  private static void logMessage(fr.umlv.tatoo.cc.common.log.Level level, String message, Object source) {
    StackTraceElement element = new Throwable().getStackTrace()[3];
    
    java.util.logging.Level logLevel=levelMap.get(level);
    LOGGER.logp(logLevel,element.getClassName(),element.getMethodName(),message,new Object[] {source});
  }
  
  private static void logCause(fr.umlv.tatoo.cc.common.log.Level level, String message, Throwable cause) {
    StackTraceElement element = new Throwable().getStackTrace()[3];
    
    java.util.logging.Level logLevel=levelMap.get(level);
    LOGGER.logp(logLevel,element.getClassName(),element.getMethodName(),message,cause);
  }
  
  private final static Logger LOGGER = Logger.getLogger("fr.umlv.tatoo.cc");
  private final static EnumMap<fr.umlv.tatoo.cc.common.log.Level,java.util.logging.Level> levelMap;
  static {
    EnumMap<fr.umlv.tatoo.cc.common.log.Level,java.util.logging.Level> map=
      new EnumMap<fr.umlv.tatoo.cc.common.log.Level,java.util.logging.Level>(fr.umlv.tatoo.cc.common.log.Level.class);
    
    map.put(fr.umlv.tatoo.cc.common.log.Level.FATAL_ERROR,java.util.logging.Level.SEVERE);
    map.put(fr.umlv.tatoo.cc.common.log.Level.ERROR,java.util.logging.Level.SEVERE);
    map.put(fr.umlv.tatoo.cc.common.log.Level.WARNING,java.util.logging.Level.WARNING);
    map.put(fr.umlv.tatoo.cc.common.log.Level.INFO,java.util.logging.Level.INFO);
    map.put(fr.umlv.tatoo.cc.common.log.Level.FINE,java.util.logging.Level.FINE);
    
    levelMap=map;
  }
}

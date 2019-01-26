/*
 * Created on Jun 12, 2003
 *
 */
package fr.umlv.tatoo.runtime.log;


/** A logger specific for tatoo runtime that use
 *  java.util.logging for its implementation.
 * 
 * @author Julien
 */
public class TatooLogger {
  private TatooLogger (){/* nothing */}

  /** Set the logger level of all parent logger handlers.
   * @param newLevel the new level of the logger.
   */
  public static void setLevel(Level newLevel) {
    if (! (LOGGER instanceof LoggingLoggerFactory.LoggingLogger))
      LOGGER=LoggingLoggerFactory.getLogger();
    ((LoggingLoggerFactory.LoggingLogger)LOGGER).setLevel(newLevel);
  }	


  /** Add a new handler to this logger.
   * @param handler the new handler
   */
  public static void setHandler(Handler handler) {
    if (! (LOGGER instanceof LoggingLoggerFactory.LoggingLogger))
      LOGGER=LoggingLoggerFactory.getLogger();
    ((LoggingLoggerFactory.LoggingLogger)LOGGER).setHandler(handler);
  }

  /** Emits a warning with a message by the current logger.
   * @param message a message
   */
  public static void warning(String message) {
    LOGGER.warning(message);
  }

  /** Emits an information message by the current logger.
   * @param message a message
   */
  public static void info(String message) {
    LOGGER.info(message);
  }  

  /** Emits a message with g level SEVERE by the current logger.
   * @param message a message
   */
  public static void severe(String message) {
    LOGGER.severe(message);
  }

  /** Emits a message with g level FINE by the current logger.
   * @param message a message
   */
  public static void fine(String message) {
    LOGGER.fine(message);
  }

  /** Emits a message with g level FINEST by the current logger.
   * @param message a message
   */
  public static void finest(String message) {
    LOGGER.finest(message);
  }

  private static Logger LOGGER;
  static {
    init();
  }
  
  private static void init() {
    String levelName = System.getProperty("fr.umlv.tatoo.loggerLevel");
    if (levelName==null || levelName.equalsIgnoreCase(Level.NONE.name())) {
      LOGGER = new NoLogLogger();
      return;
    }
    Level level = Level.valueOf(levelName.toUpperCase());
    setLevel(level);
    String className=System.getProperty("fr.umlv.tatoo.loggerClass");
    if (className==null) {
      return;
    }
    try {
      Object handler = Class.forName(className).newInstance();
      if (!(handler instanceof Handler))
        throw new IllegalArgumentException("class "+className+" is not a logger Handler");
      setHandler((Handler)handler);
    } catch (InstantiationException e) {
      throw new IllegalArgumentException(e);
    } catch (IllegalAccessException e) {
      throw new IllegalArgumentException(e);
    } catch (ClassNotFoundException e) {
      throw new IllegalArgumentException(e);
    }
  }
}

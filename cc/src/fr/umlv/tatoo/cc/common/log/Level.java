package fr.umlv.tatoo.cc.common.log;

/** Levels used by the {@link Reporter reporter} and the
 *  {@link Info information}.
 * 
 * @author Remi
 */
public enum Level {
  FATAL_ERROR, ERROR, WARNING, INFO, FINE;
  
  /** Transform a string to a level, the possible values are
   *  "FATAL_ERROR", "ERROR", "WARNING", "INFO", "FINE" or "ALL".
   *  
   * @param level a string representing the level.
   * @return the corresponding level.
   */
  public static Level parse(String level) {
    level=level.toUpperCase();
    if ("ALL".equals(level)) {
      Level[] levels=Level.values();
      return levels[levels.length-1];
    }
    return Level.valueOf(level);
  }
}
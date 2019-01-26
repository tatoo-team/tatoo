package fr.umlv.tatoo.cc.common.log;

/** A reporter is able to report a specific {@link Info}
 *  by providing an implementation of the method
 *  {@link #report(Info)}.
 *  Futhermore the fact that the info is reported or not
 *  depends on the {@link #getLogLevel() reporter log level}
 *  and the {@link Info#level() info log level}.
 *  
 *  Reporter are associated to a thread using
 *  {@link ReporterFactory#installCurrentReporter(Reporter)}.
 * 
 * @author Remi
 */
public abstract class Reporter {
  /** Creates a reporter.
   */
  protected Reporter() {
    Info info=new Info();
    info.seal();
    this.defaultInfo=info;
  }
  
  /** Changes the log level of the current reporter.
   * @param level the new log level.
   */
  public abstract void setLogLevel(Level level);
  
  /** Returns the log level of the current reporter.
   * @return the log level of the current reporter.
   */
  public abstract Level getLogLevel();
  
  /** Reports informations.
   * @param info the infor
   * 
   * @see Info
   */
  public abstract void report(Info info);
  
  Info getDefaultInfo() {
    return defaultInfo;
  }
  
  void setDefaultInfo(Info defaultInfo) {
    this.defaultInfo=defaultInfo;
  }
  
  private Info defaultInfo;
}

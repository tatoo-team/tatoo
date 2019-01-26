package fr.umlv.tatoo.cc.common.log;

/** This class permits to
 *  {@link #installCurrentReporter(Reporter) install}/{@link #uninstallCurrentReporter() uninstall} a reporter
 *  on the current thread.
 *  
 * @author Remi
 *
 */
public class ReporterFactory {
  
  /** Changes the reporter of the current thread.
   * @param reporter the new reporter
   */
  public void installCurrentReporter(Reporter reporter) {
    localReporter.set(reporter);
  }
  
  /** Reverts the reporter of the current thread to the default reporter.
   * 
   *  @see #createDefaultReporter()
   */
  public void uninstallCurrentReporter() {
    localReporter.set(createDefaultReporter());
  }
  
  /** Returns the reporter attached to the current thread.
   * @return the thread local reporter.
   */
  public Reporter getCurrentReporter() {
    return localReporter.get();
  }
  
  /** Creates a default reporter used as thread local reporter.
   *  This method is called :
   *  <ol>
   *   <li>When a method lookup its reporter and none was defined.
   *   <li>When {@link #installCurrentReporter(Reporter)} is called.
   *  </ol>
   *  
   * @return a default reporter.
   */
  
  protected Reporter createDefaultReporter() {
    return new LoggerReporter();
  }
  
  private ThreadLocal<Reporter> localReporter=new ThreadLocal<Reporter>() {
    @Override
    protected Reporter initialValue() {
      return createDefaultReporter();
    }
  };
  
  /** Sets log level of the reporter attached to the current thread.
   * 
   * @param level the new log level.
   * 
   * @see #getFactory()
   * @see #getCurrentReporter()
   * @see Reporter#setLogLevel(Level)
   */
  public static void setLogLevel(Level level) {
    getFactory().getCurrentReporter().setLogLevel(level);
  }
  
  /** Returns the log level of the reporter attached to the current thread.
   * @return the log level of the thread local reporter.
   * 
   * @see #getFactory()
   * @see #getCurrentReporter()
   * @see Reporter#getLogLevel()
   */
  public static Level getLogLevel() {
    return getFactory().getCurrentReporter().getLogLevel();
  }
  
  /** Returns the Info object used to derive the info object 
   *  used to report an event or an error.
   *  The returned info is always {@link Info#isSealed() sealed}
   *  so any modification will not change the default info of
   *  the thread local reporter.
   * 
   * @return a sealed info
   * 
   * @see #setAndSealDefaultInfo(Info)
   * @see Info#derive(Level, String, Object[])
   * @see Info#isSealed()
   */
  public static Info getDefaultInfo() {
    return getFactory().getCurrentReporter().getDefaultInfo();
  }
  
  /** Changes the info object attached on the thread local reporter
   *  and used to derive the info object used to report an event or an error .
   *  If the info taken as parameter is not sealed, this method seal it.
   *  
   * @param defaultInfo the new default info of the thread local reporter.
   * 
   * @see Info#derive(Level, String, Object[])
   */
  public static void setAndSealDefaultInfo(Info defaultInfo) {
    defaultInfo.seal();
    getFactory().getCurrentReporter().setDefaultInfo(defaultInfo);
  }
  
  /** Returns the reporter factory currently used.
   * @return a reporter factory.
   */
  public static ReporterFactory getFactory() {
    return factory;
  }
  
  /** Changes the Reporter Factory used.
   *  This method will not change reporters already attached
   *  on a thread.
   *   
   * @param factory the new reporter factory.
   * 
   * @see #uninstallCurrentReporter()
   */
  public static void setFactory(ReporterFactory factory) {
    ReporterFactory.factory=factory;
  }
  
  private static ReporterFactory factory=new ReporterFactory();
}

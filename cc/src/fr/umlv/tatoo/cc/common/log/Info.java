package fr.umlv.tatoo.cc.common.log;

/** Info is an inmutable/mutable class that gathers different informations
 *  in order to report them using a {@link Reporter}.
 *  
 *  Objects of this class are by default mutable but they can be sealed using {@link #seal()}
 *  in order to make it not mutable.
 *  If an objet is sealed all mutable method will {@link #duplicate()} the object
 *  before change it.
 *  
 *  This class act as a builder so method can be chained.
 *  
 * @author Remi
 */
public final class Info {  
  public Info duplicate() {
    // sealed is not copied
    return new Info().message(message).cause(cause).data(data).file(file).line(line).column(column);
  }
  
  /** Returns the level of the current info.
   * @return the level of the current info.
   */
  public Level level() {
    return level;
  }
  
  /** Changes the level of the info.
   *  If the current info is {@link #isSealed() sealed}, a clone of the current object
   *  is returned.
   *  
   * @param level the new level.
   * @return an info with a new level.
   */
  public Info level(Level level) {
    Info builder=builder();
    builder.level=level;
    return builder;
  }
  
  /** Returns an array of datas used to format the {@link #message()} of the current info.
   * @return the data associated with the message of the current info.
   */
  public Object[] data() {
    return (data==null)?null:data.clone();
  }
  
  /** Replaces or set the datas used to format the {@link #message()}.
   *  If the current info is {@link #isSealed() sealed}, a clone of the current object
   *  is returned.
   *  
   * @param data the new data
   * @return an info with the new data.
   */
  public Info data(Object... data) {
    Info builder=builder();
    if (data==null)
      return builder;
    builder.data=data.clone();
    return builder;
  }
  
  /** Returns the message or the message of the cause of the current info.
   * @return a message or the message of the cause if the message is null.
   */
  public String message() {
    if (message==null && cause!=null) {
      String msg=cause.getMessage();
      if (msg==null)
        msg=cause.getClass().getName();
      return msg;
    }
      
    return message;
  }
  
  /** Changes the message of the current info, this message can contains '%'
   *  that are resolved using {@link String#format(String, Object[])}.
   *  If the current info is {@link #isSealed() sealed}, a clone of the current object
   *  is returned.
   *  
   * @param message the new message
   * @return an info with the new message.
   */
  public Info message(String message) {
    Info builder=builder();
    builder.message=message;
    return builder;
  }
  
  /** Returns the cause of the current info.
   * @return the ciause the current info or null if the cause is not defined.
   */
  public Throwable cause() {
    return cause;
  }
  
  /** Changes the cause of the current info.
   *  If the current info is {@link #isSealed() sealed}, a clone of the current object
   *  is returned.
   *  
   * @param cause the cause of the info.
   * @return an info with the cause defined.
   */
  public Info cause(Throwable cause) {
    Info builder=builder();
    builder.cause=cause;
    return builder;
  }
  
  /** Returns the file associated with the current info.
   * @return the file or null if the file is not defined.
   */
  public Object file() {
    return file;
  }
  
  /** Changes the file associated with the current info.
   *  If the current info is {@link #isSealed() sealed}, a clone of the current object
   *  is returned.
   * 
   * @param file the file.
   * @return an info with the associated file
   * 
   * @see #line()
   * @see #column()
   */
  public Info file(Object file) {
    Info builder=builder();
    builder.file=file;
    return builder;
  }
  
  /** Returns the line number of the {@link #file() file} associated
   *  with the current info.
   *  
   * @return the line number or -1 if the line number if not defined.
   * 
   * @see #file()
   */
  public int line() {
    return line;
  }
  
  /** Sets the line number of the {@link #file() file} associated
   *  with the current info.
   *  If the current info is {@link #isSealed() sealed}, a clone of the current object
   *  will be returned.
   *  
   * @param line the line number or -1.
   * @return an info with the new line number.
   */
  public Info line(int line) {
    Info builder=builder();
    builder.line=line;
    return builder;
  }
  
  /** Returns the column number of the {@link #file() file} associated
   *  with the current info.
   *  
   * @return the column number or -1 if the column number is not defined.
   */
  public int column() {
    return column;
  }
  
  /** Sets the column number of the {@link #file() file} associated
   *  with the current info.
   *  If the current info is {@link #isSealed() sealed}, a clone of the current object
   *  will be returned.
   *  
   * @param column the column number or -1.
   * @return an info with the column number.
   */
  public Info column(int column) {
    Info builder=builder();
    builder.column=column;
    return builder;
  }
  
  /** Reports the current info using the thread local reporter.
   *  
   *  @see ReporterFactory
   */
  public void report() {
    ReporterFactory.getFactory().getCurrentReporter().report(this);
  }
  
  /** Seals the current Info. After this call, the current object
   *  can't be mutated.
   */
  public void seal() {
    sealed=true;
  }
  
  /** Returns if the current object is sealed or not.
   *  If the current info is sealed any mutation will not change the current object
   *  but change a clone of it.
   * 
   * @return if the current object is sealed or not.
   * 
   * @see #seal()
   */
  public boolean isSealed() {
    return sealed;
  }
  
  private Info builder() {
    if (sealed)
      return duplicate();
    return this;
  }
  
  @Override
  public String toString() {
    StringBuilder builder=new StringBuilder();
    builder.append(" :");
    String message=message();
    if (message!=null)
      builder.append(String.format(message,data));
    else
      builder.append("no message");
    
    if (file!=null) {
      builder.append(" at ").append(file);
    }
    
    if (line!=-1 || column!=-1) {
      if (file==null)
        builder.append(" at ");
      builder.append(String.format(" (%d,%d)",line,column));
    }
    
    return builder.toString();
  }
  
  private Level level;
  private Object[] data;
  private String message;
  private Throwable cause;
  private Object file;
  private int line=-1;
  private int column=-1;
  
  private boolean sealed;
  
  /** Creates a new info derived from the default info of the
   *  thread local reporter.
   *  
   * @param level set the level of the information.
   * @param data the data associated with the info.
   * @return a new info.
   * 
   * @see #derive(Level, String, Object[])
   * @see ReporterFactory#getDefaultInfo()
   */
  public static Info derive(Level level,Object... data) {
    Info info=ReporterFactory.getFactory().getCurrentReporter().getDefaultInfo();
    return info.level(level).data(data);
  }
  
  /** Creates a new info derived from the default info of the
   *  thread local reporter.
   *  
   * @param level set the level of the information.
   * @param data the data associated with the info.
   * @param message the message of the info. It can contains '%' that are
   *        interpreted using {@link String#format(String, Object[])}.
   * @return a new info.
   * 
   * @see #fine(String, Object[])
   * @see #info(String, Object[])
   * @see #warning(String, Object[])
   * @see #error(String, Object[])
   * @see #fatalError(String, Object[])
   */
  public static Info derive(Level level,String message,Object... data) {
    return derive(level,data).message(message);
  }
  
  /** Creates a new info derived from the default info of the
   *  thread local reporter with the {@link #level()} {@link Level#INFO}.
   * 
   * @param data the data associated with the info.
   * @param message the message of the info. It can contains '%' that are
   *        interpreted using {@link String#format(String, Object[])}.
   * @return a new info.
   * 
   * @see #derive(Level, String, Object[])
   */
  public static Info fine(String message,Object... data) {
    return derive(Level.FINE,message,data);
  }
  
  /** Creates a new info derived from the default info of the
   *  thread local reporter with the {@link #level()} {@link Level#FINE}.
   * 
   * @param data the data associated with the info.
   * @param message the message of the info. It can contains '%' that are
   *        interpreted using {@link String#format(String, Object[])}.
   * @return a new info.
   * 
   * @see #derive(Level, String, Object[])
   */
  public static Info info(String message,Object... data) {
    return derive(Level.INFO,message,data);
  }
  
  /** Creates a new info derived from the default info of the
   *  thread local reporter with the {@link #level()} {@link Level#WARNING}.
   * 
   * @param data the data associated with the info.
   * @param message the message of the info. It can contains '%' that are
   *        interpreted using {@link String#format(String, Object[])}.
   * @return a new info.
   * 
   * @see #derive(Level, String, Object[])
   */
  public static Info warning(String message,Object... data) {
    return derive(Level.WARNING,message,data);
  }
  
  /** Creates a new info derived from the default info of the
   *  thread local reporter with the {@link #level()} {@link Level#ERROR}.
   * 
   * @param data the data associated with the info.
   * @param message the message of the info. It can contains '%' that are
   *        interpreted using {@link String#format(String, Object[])}.
   * @return a new info.
   * 
   * @see #derive(Level, String, Object[])
   */
  public static Info error(String message,Object... data) {
    return derive(Level.ERROR,message,data);
  }
  
  /** Creates a new info derived from the default info of the
   *  thread local reporter with the {@link #level()} {@link Level#FATAL_ERROR}.
   * 
   * @param data the data associated with the info.
   * @param message the message of the info. It can contains '%' that are
   *        interpreted using {@link String#format(String, Object[])}.
   * @return a new info.
   * 
   * @see #derive(Level, String, Object[])
   */
  public static Info fatalError(String message,Object... data) {
    return derive(Level.FATAL_ERROR,message,data);
  }
}

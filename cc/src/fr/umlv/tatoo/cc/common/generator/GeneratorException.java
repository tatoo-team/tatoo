/*
 * GeneratorException.java
 *
 * Created: 6 mars 2006
 */
package fr.umlv.tatoo.cc.common.generator;

/** An exception raised by the generator.
 *  In general this exception wrap its cause.
 *  
 * @author Remi
 */
public class GeneratorException extends Exception {
  /** Creates a generator exception.
   * @param message message of the exception
   */
  public GeneratorException(String message) {
    super(message);
  }

  /** Creates a generator exception.
   * @param message message of the exception
   * @param cause cause of the exception, may be null
   */
  public GeneratorException(String message, Throwable cause) {
    super(message, cause);
  }

  /** Creates a generator exception.
   * @param cause cause of the exception, may be null
   */
  public GeneratorException(Throwable cause) {
    super(cause);
  }
 
  private static final long serialVersionUID = -6335419533715970161L;
}

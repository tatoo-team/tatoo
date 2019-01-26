/*
 * Created on 8 juil. 2005
 *
 */
package fr.umlv.tatoo.cc.common.main;

/** Raised when arguments do not match description of the
 *  {@link fr.umlv.tatoo.cc.common.main.CommandLineParser command line}.
 * 
 * @author remi
 * @see fr.umlv.tatoo.cc.common.main.CommandLineParser
 */
public class IllegalCommandLineArgumentException extends Exception {
  public IllegalCommandLineArgumentException(String message) {
    super(message);
  }
  public IllegalCommandLineArgumentException(Throwable cause) {
    super(cause);
  }
  public IllegalCommandLineArgumentException(String message, Throwable cause) {
    super(message, cause);
  }
  private static final long serialVersionUID = -8124963187737902540L;
}

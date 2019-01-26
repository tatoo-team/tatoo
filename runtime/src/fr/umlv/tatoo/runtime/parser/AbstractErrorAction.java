package fr.umlv.tatoo.runtime.parser;

/**
 * Error action class.
 * 
 * @param <T> type of terminals.
 * @param <P> type of productions.
 * @param <V> type of versions.
 * 
 * @author Julien Cervelle
 */
abstract class AbstractErrorAction<T,P,V> extends Action<T,P,V> {

  /**
   * Constructs an error action with a particular error message.
   * @param message the message to display.
   */
  protected AbstractErrorAction(String message) {
    this.message = message;
  }
  
  @Override
  public boolean isError(V version) {
    return true;
  } 
  
  @Override
  public String toString() {
    return "Error : " + message;
  }
  
  /** Returns the error action message.
   * @return the error action message.
   */
  protected String getMessage() {
    return message;
  }
  
  private final String message;
}

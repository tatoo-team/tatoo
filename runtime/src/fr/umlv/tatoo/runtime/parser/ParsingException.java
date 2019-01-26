package fr.umlv.tatoo.runtime.parser;

/** This class is used to signal that an error occurs
 *  during the parsing process.
 *  
 *  A parsing exception contains :
 *  <ul>
 *   <li>a message
 *   <li>a cause, may be null
 *   <li>a terminal, may be null
 *   <li>a set of expected terminals, the lookahead, may be null.
 *   <li>a view of the parser stack, may be null
 *  </ul>
 *  
 * @author Julien Cervelle
 */
public class ParsingException extends RuntimeException {
  private static final long serialVersionUID = -2840177541602031572L;
  private Object detailMessage=this;
  
  public ParsingException() { // default constructor
  }

  public ParsingException(String message, Throwable cause) {
    super(message, cause);
  }

  public ParsingException(String message) {
    super(message);
  }

  public ParsingException(Throwable cause) {
    super(cause);
  }
  
  /** Allow to add a detail message to the exception message.
   *  The detail message is concatenated to the current message.
   *  This method can only be called once.
   *  
   * @param detailMessage a details message
   * 
   * @throws IllegalArgumentException if detailMessage is null
   * @throws IllegalStateException if detailMessage is already set
   * 
   * @see #getMessage()
   */
  public void setDetailMessage(String detailMessage) {
    if (detailMessage==null)
      throw new IllegalArgumentException("null message detail");
    if (this.detailMessage!=this)
      throw new IllegalStateException("detail message already set");
    this.detailMessage=detailMessage;
  }
  
  @Override
  public String getMessage() {
    String message=super.getMessage();
    return (detailMessage==this)?message:message+' '+detailMessage;
  }
}

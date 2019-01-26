package fr.umlv.tatoo.runtime.lexer;

public class LexingException extends RuntimeException {

  /** Creates a lexing exception with a message, a cause and a character.
   * @param message the message of the exception.
   * @param cause the cause of the exception
   */
  public LexingException(String message, Throwable cause) {
    super(message, cause);
  }

  /** Creates a lexing exception with a message.
   * @param message the message of the exception.
   */
  public LexingException(String message) {
    super(message);
  }

  /** Creates a lexing exception with a cause.
   * @param cause the cause of the exception
   */
  public LexingException(Throwable cause) {
    super(cause.getMessage(),cause);
  }

  private static final long serialVersionUID = 7453984342807245138L;
}

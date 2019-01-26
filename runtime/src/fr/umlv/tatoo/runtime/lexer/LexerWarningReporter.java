package fr.umlv.tatoo.runtime.lexer;

/** Error handler that handle warning during lexing process.
 *  
 * @author Julien
 *
 * @param <B> type of the buffer.
 */
public interface LexerWarningReporter<B> {
  /** Log error recovery message info to user
   * 
   * @param message the message to display
   */
  public void handleWarning(Lexer<? extends B> lexer, String message);
}

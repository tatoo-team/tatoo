package fr.umlv.tatoo.runtime.parser;

/** Error handler that handle warning during parsing process.
 *  
 * @author Remi
 *
 * @param <T> type of terminals.
 * @param <N> type of non-terminals.
 * @param <P> type of productions.
 * @param <V> type of versions.
 */
public interface ParserWarningReporter<T,N,P,V> {
  /** Log error recovery message info to user
   * 
   * @param message the message to display
   */
  public void handleWarning(Parser<T,N,P,V> parser, T terminal, String message);
}

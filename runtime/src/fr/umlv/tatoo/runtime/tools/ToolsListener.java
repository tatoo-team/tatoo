package fr.umlv.tatoo.runtime.tools;

/**
 * @author Remi Forax
 *
 * @param <R> type of rules.
 * @param <B> type of buffer.
 * @param <T> type of terminals.
 * @param <N> type of non terminals.
 * @param <P> type of productions.
 */
public interface ToolsListener<R,B,T,N,P> {
  /** Called when the lexer recognizes a rule tagged as comment.
   *  No terminal is associated to that rule.
   * 
   * @param rule the rule recognized by the lexer.
   * @param buffer the buffer currently used by the lexer.
   */
  public void comment(R rule, B buffer);
  
  /** Called when the analyzer recognized a terminal.
   *  i.e when the lexer recognized a rule and
   *  the parser shift the corresponding terminal.
   *   
   * @param terminal the terminal shifted by the parser.
   * @param rule the rule recognized by the lexer.
   * @param buffer the buffer currently used by the lexer.
   * 
   * @see fr.umlv.tatoo.runtime.lexer.LexerListener#ruleVerified(Object, int, Object)
   * @see fr.umlv.tatoo.runtime.parser.ParserListener#shift(Object)
   */
  public void shift(T terminal, R rule, B buffer);
  
  /**
   * Called when the parser reduce a production.
   * 
   * @param production production reduced by the parser.
   */
  public void reduce(P production);
  
  /**
   * Called when the parser accept a start non terminal.
   * 
   * @param nonTerminal non terminal accepted by the parser.
   * 
   * @see fr.umlv.tatoo.runtime.parser.ParserListener#accept(Object)
   */
  public void accept(N nonTerminal);
}

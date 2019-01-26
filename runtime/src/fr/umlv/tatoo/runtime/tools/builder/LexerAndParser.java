/**
 * 
 */
package fr.umlv.tatoo.runtime.tools.builder;

import fr.umlv.tatoo.runtime.buffer.LexerBuffer;
import fr.umlv.tatoo.runtime.lexer.Lexer;
import fr.umlv.tatoo.runtime.parser.Parser;

/** The couple lexer/parser.
 *  The parser is linked to the output of the lexer.
 *  
 * @author Remi Forax
 *
 * @param <B> type of the buffer
 * @param <T> type of terminals
 * @param <N> type of non terminals
 * @param <P> type of productions
 * @param <V> type of versions
 */
public interface LexerAndParser<B extends LexerBuffer,T,N,P,V> {
  /** Returns the lexer.
   * @return the lexer.
   */
  public Lexer<B> getLexer();
  
  /** Returns the parser.
   * @return the parser
   */
  public Parser<T,N,P,V> getParser();
}
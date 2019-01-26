/*
 * TokenBuffer.java
 *
 * Created: 13 nov. 2005
 */
package fr.umlv.tatoo.runtime.buffer;

/**
 * A <code>TokenBuffer</code> provides a method used by developer 
 * to access tokens processed by a
 * {@link fr.umlv.tatoo.runtime.lexer.Lexer lexer}.
 * 
 * This interface is parameterized by the type of a chunk of data
 * that is returned by the method {@link #view()}.
 * Because buffer of unicode chars is common,
 * this interface has a specific subtype {@link CharSequenceTokenBuffer}.
 * 
 * @param <D> type of the view's items.
 * 
 * @author Gilles
 * 
 * @see CharSequenceTokenBuffer
 * @see LexerBuffer
 * @see fr.umlv.tatoo.runtime.lexer.LexerListener
 */
public interface TokenBuffer<D> {
  /**
   * Discards all characters already recognized by the lexer.
   */
  public void discard();
  
  /**
   * Provides a view of all available tokens. Tokens are not copied 
   * from the buffer and thus may change during lexer process.
   * 
   * @return a view of all available tokens
   */
  public D view();
}

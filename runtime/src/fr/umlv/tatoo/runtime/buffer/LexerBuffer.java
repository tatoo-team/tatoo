/*
 * LexerBuffer.java
 *
 * Created: Sun Jul 13 19:35:35 2003
 *
 */
package fr.umlv.tatoo.runtime.buffer;

import java.io.IOException;

/**
 * A {@code LexerBuffer} represents a character stream on which
 * a {@link fr.umlv.tatoo.runtime.lexer.Lexer} can operate.
 * Methods of this interface are internally used by the lexer
 * 
 * @see fr.umlv.tatoo.runtime.lexer.Lexer
 * @author <a href="mailto:roussel@univ-mlv.fr">Gilles Roussel</a>
 */
public interface LexerBuffer {
  
  /** Returns a location provider.
   *  @return the location provider or null if the buffer
   *         has no location provider.
   *         
   *  @see fr.umlv.tatoo.runtime.buffer.impl.LocationTracker
   */
  public LocationProvider getLocationProvider();

  /**
   * Unwinds {@code count} characters from the buffer.
   * {@code count} must not be 0.
   * 
   * @param count the number of characters to unwind
   */
  public void unwind(int count);
  
  /**
   * Moves current position back to first non unwinded character.
   * This operation is optional and used by the lexer error recovery
   * mechanism or by the {@link fr.umlv.tatoo.runtime.lexer.Tokenizer tokenizer}.
   * 
   * @throws UnsupportedOperationException if this operation is not supported by
   *  the current buffer.
   */
  public void reset();
  
  
  /**
   * Moves current position back to first non discarded character.
   * This operation is optional and used when the parser needs to
   * restart the lexing process. See
   * {@link fr.umlv.tatoo.runtime.parser.SimpleParser#push(Object)}
   * for more details.
   * 
   * @throws UnsupportedOperationException if this operation is not supported by
   *  the current buffer.
   */
  public void restart();
  
  /**
   * Determines if the last unwinded character was an end of line.
   * 
   * @return <code>true</code> if the last unwinded character was an end of line; 
   * <code>false</code> otherwise
   */
  public boolean previousWasNewLine();
  
  
  /**
   * Returns last read char or -1 if not available. This method is used for logging and
   * error reporting. This operation is optional since this method can return -1 anytime
   * @return last read char or -1 if not available
   */
  public int lastChar();
  
  /**
   * Reads the next character from the buffer. Current position in 
   * the buffer is incremented.  
   * 
   * @return the next character in the buffer
   * @throws IllegalStateException when called while hasRemaining returns false
   */
  public int next();
  
  /**
   * Returns true if more characters are availables in the buffer.
   * If the end of file has been reached and backtracked by lexer
   * this method must return true.
   * 
   * @return true if more characters are availables in the buffer
   */
  public boolean hasRemaining();
  
  /**
   * Reads at some bytes from the stream. This operation is optionnal
   * and only used by {@link fr.umlv.tatoo.runtime.lexer.Lexer#run()}.
   * 
   * @return <code>false</code> at end of stream
   * @throws IOException if an i/o operation failed.
   * @throws UnsupportedOperationException if this operation is not supported by
   *  the current buffer.
   */
  public boolean read() throws IOException;
  
  /**
   * Discards all characters already recognized by the lexer.
   */
  public void discard();
}

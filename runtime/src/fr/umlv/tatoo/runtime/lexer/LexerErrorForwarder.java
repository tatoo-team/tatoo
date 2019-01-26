 /*
 * ErrorHandler.java
 *
 * Created: 13 nov. 2005
 */
package fr.umlv.tatoo.runtime.lexer;

import fr.umlv.tatoo.runtime.buffer.LexerBuffer;


/** Lexer error forwarding interface.
 * 
 * @author Julien
 */
public interface LexerErrorForwarder<B extends LexerBuffer> {
  /** Forwards unexpected character found during the lexing process.
   * 
   * @param buffer the buffer that push the character
   * @return false if lexer must discard input
   */
  public ForwardReturn forwardUnexpectedCharacter(Lexer<B> buffer);
  
  /** Forwards unexpected end of file during the lexing process.
   * If it returns, lexer considers that error is recovered and exits
   * without exception nor warning.
   * 
   * @param buffer the buffer
   */
  public void forwardUnexpectedEndOfFile(Lexer<B> buffer);
}

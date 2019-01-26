package fr.umlv.tatoo.runtime.lexer;

import fr.umlv.tatoo.runtime.buffer.LexerBuffer;


/** A lexer error forwarder that discard character if
 *  the lexer raises a lexing error.
 * If an error occurs during the lexing the offending character
 * is discarded and the lexing process continue with the following character. 
 *  
 * @author Julien Cervelle
 */
public class DiscardLexerErrorForwarder<B extends LexerBuffer> implements LexerErrorForwarder<B> {
  public ForwardReturn forwardUnexpectedCharacter(Lexer<B> lexer) {
    return ForwardReturn.DISCARD;
  }

  public void forwardUnexpectedEndOfFile(Lexer<B> lexer) {
    //nothing
  }
  
  @SuppressWarnings("unchecked")
  public static <B extends LexerBuffer> DiscardLexerErrorForwarder<B> discardForwarder() {
    return (DiscardLexerErrorForwarder<B>) discardForwarder;
  }

  private static final DiscardLexerErrorForwarder<LexerBuffer> discardForwarder =
    new DiscardLexerErrorForwarder<LexerBuffer>();
}

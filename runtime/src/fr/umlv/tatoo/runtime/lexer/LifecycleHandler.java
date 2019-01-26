package fr.umlv.tatoo.runtime.lexer;

import fr.umlv.tatoo.runtime.buffer.LexerBuffer;

/**
 * A lifecycleHandler will be called when the buffer
 * of the lexer changed;
 * see {@link Lexer#reset(fr.umlv.tatoo.runtime.buffer.LexerBuffer)};
 * or when the lexer is closed; see {@link Lexer#close()}.
 * 
 * @author Remi
 * 
 * @see fr.umlv.tatoo.runtime.tools.ParserForwarder
 */
public interface LifecycleHandler<B extends LexerBuffer> {
  /**
   * Called after the lexer is closed. 
   * @see Lexer#close()
   */
  public void lexerClosed(Lexer<B> lexer);
  
  /**
   * Called after the lexer is reset. 
   * @see Lexer#reset(fr.umlv.tatoo.runtime.buffer.LexerBuffer)
   */
  public void lexerReset(Lexer<B> lexer);
}

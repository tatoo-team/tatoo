package fr.umlv.tatoo.runtime.lexer.nano;

public interface LexerLifecycleHandler {
  /**
   * Called after the lexer is closed. 
   */
  public void lexerClosed();
  
  /**
   * Called after the lexer is reset. 
   */
  public void lexerReset();
}

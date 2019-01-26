package fr.umlv.tatoo.runtime.lexer;

import fr.umlv.tatoo.runtime.buffer.LexerBuffer;
import fr.umlv.tatoo.runtime.lexer.rules.ActionProcessor;
import fr.umlv.tatoo.runtime.lexer.rules.ProcessReturn;

public interface LexerErrorRecoveryPolicy<R,B extends LexerBuffer> {

  /**
   * Called when the impossible match first occurs. If necessary, {@link #errorRecoveryNeedsContinuation()}
   * returns true and {@link #continueRecoverOnError} is called
   * @param processor the action processor
   */
  public void recoverOnError(Lexer<B> lexer,ActionProcessor<R> processor);
  
  /**
   * Called when error recovery was initiated by recover on error, and method {@link #errorRecoveryNeedsContinuation()}
   * returns {@code true}
   * @param lexer the lexer
   * @param processor the action processor
   * @return {@link ProcessReturn#MORE MORE} if more characters are needed,
   * {@link ProcessReturn#ERROR ERROR}, if error recovery fails
   * and {@link ProcessReturn#TOKEN TOKEN} if a new token is spawned.
   */
  public ProcessReturn continueRecoverOnError(Lexer<B> lexer, ActionProcessor<R> processor);
  
  /**
   * Indicate if {@link #continueRecoverOnError} needs to be called by processor
   * @return true if {@link #continueRecoverOnError} needs to be called by processor
   */
  public boolean errorRecoveryNeedsContinuation();
  
  /**
   * Called when the impossible match first occurs at end of input.
   * If necessary, {@link #unexpectedEndOfFileRecoveryNeedsContinuation()}
   * returns true and {@link #continueRecoverOnUnexpectedEndOfFile} is called
   * @param processor the action processor
   */
  public void recoverOnUnexpectedEndOfFile(Lexer<B> lexer, ActionProcessor<R> processor);
  
  /**
   * Called when error recovery was initiated by recover on error, and method
   * {@link #unexpectedEndOfFileRecoveryNeedsContinuation()}
   * returns {@code true}
   * @param processor the action processor
   * @return {@link ProcessReturn#MORE MORE} if more characters are needed,
   * {@link ProcessReturn#ERROR ERROR}, if error recovery fails
   * and {@link ProcessReturn#TOKEN TOKEN} if a new token is spawned.
   */
  public ProcessReturn continueRecoverOnUnexpectedEndOfFile(Lexer<B> lexer, ActionProcessor<R> processor);
  
  /**
   * Indicate if {@link #continueRecoverOnUnexpectedEndOfFile} needs to be called by processor
   * @return true if {@link #continueRecoverOnUnexpectedEndOfFile} needs to be called by processor
   */
  public boolean unexpectedEndOfFileRecoveryNeedsContinuation();


}

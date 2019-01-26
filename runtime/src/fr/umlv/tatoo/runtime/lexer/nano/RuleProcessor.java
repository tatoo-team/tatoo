package fr.umlv.tatoo.runtime.lexer.nano;

import fr.umlv.tatoo.runtime.buffer.LexerBuffer;
import fr.umlv.tatoo.runtime.lexer.rules.ProcessReturn;

public interface RuleProcessor {

  public abstract boolean isProcessFinished();

  public abstract void initProcess();

  public void reset();
  
  /**
   * Processes available characters from the input stream.
   * @param buffer the lexer buffer.
   * @return {@link ProcessReturn#MORE MORE} if more characters are needed to perform the match,
   * {@link ProcessReturn#ERROR ERROR}, if an error occurred
   * and {@link ProcessReturn#TOKEN TOKEN} if a new token is spawned.
   */
  public abstract ProcessReturn step(LexerBuffer buffer);

  /**
   * This method is called after {@link #step} has returned
   * {@link ProcessReturn#MORE MORE} and end-of-file is reached
   * @return {@link ProcessReturn#NOTHING NOTHING} if no new token is available,
   * {@link ProcessReturn#ERROR ERROR}, if an error occurred
   * and {@link ProcessReturn#TOKEN TOKEN} if a new token is spawned. In this case;
   * if characters are back available in the buffer, {@link #step} must be
   * called again until it returns {@link ProcessReturn#MORE MORE}, and then
   * this method has to be called again
   */
  public abstract ProcessReturn stepClose();

}
/*
 * Created on 4 juil. 2005
 */
package fr.umlv.tatoo.runtime.lexer;

/**
 * Interface defining an observer registered on a lexer that
 * receive notifications of rule recognized.
 *
 * @param <R> type of rules.
 * @param <B> type of buffers.
 *
 * @author Remi Forax
 * 
 * @see Lexer#createLexer(LexerTable, fr.umlv.tatoo.runtime.buffer.LexerBuffer, LexerListener, RuleActivator, LifecycleHandler, LexerErrorRecoveryPolicy)
 */

public interface LexerListener<R,B> {
  /**
   * This method is called each time a token is recognized by the lexer.
   * 
   * The way to extract the token value depends of the kind of buffer
   * used by the lexer. If it's a 
   * the token buffer ({@link fr.umlv.tatoo.runtime.buffer.TokenBuffer})
   * the token can be get using method
   * {@link fr.umlv.tatoo.runtime.buffer.TokenBuffer#view()}.
   * Otherwise, the way to extract the token value depends on the
   * {@link fr.umlv.tatoo.runtime.buffer.LexerBuffer buffer}
   * implementation. 
   * 
   * If the token is accepted by this listener, it should be discarded
   * using the methods {@link fr.umlv.tatoo.runtime.buffer.TokenBuffer#discard()}
   * or {@link fr.umlv.tatoo.runtime.buffer.LexerBuffer#discard()}
   * otherwise the next token value will be concatenate with the current one. 
   * 
   * @param rule the rule verified
   * @param lastTokenLength the length of the token recognized by the rule
   * @param buffer the buffer containing the token to extract
   * @throws RuntimeException the implementor of this interface could throw a runtime exception
   *  to signal an error.
   */
  public void ruleVerified(R rule, int lastTokenLength, B buffer);
}

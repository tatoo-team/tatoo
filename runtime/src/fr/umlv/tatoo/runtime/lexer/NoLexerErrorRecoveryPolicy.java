package fr.umlv.tatoo.runtime.lexer;

import fr.umlv.tatoo.runtime.buffer.LexerBuffer;
import fr.umlv.tatoo.runtime.lexer.LexerErrorRecoveryPolicy;
import fr.umlv.tatoo.runtime.lexer.rules.ActionProcessor;
import fr.umlv.tatoo.runtime.lexer.rules.ProcessReturn;

public class NoLexerErrorRecoveryPolicy<R,B extends LexerBuffer> implements
    LexerErrorRecoveryPolicy<R,B> {

  public ProcessReturn continueRecoverOnError(Lexer<B> lexer,ActionProcessor<R> processor) {
    throw new AssertionError("no continuation needed");
  }

  public ProcessReturn continueRecoverOnUnexpectedEndOfFile(Lexer<B> lexer,ActionProcessor<R> processor) {
    throw new AssertionError("no continuation needed");
  }

  public boolean errorRecoveryNeedsContinuation() {
    return false;
  }

  public void recoverOnError(Lexer<B> lexer, ActionProcessor<R> processor) {
    throw new LexingException(DefaultLexerWarningReporter.formatMessage(lexer, "lexing error"));
  }

  public void recoverOnUnexpectedEndOfFile(Lexer<B> lexer, ActionProcessor<R> processor) {
    throw new LexingException(DefaultLexerWarningReporter.formatMessage(lexer, "lexing error at end of input"));
  }

  public boolean unexpectedEndOfFileRecoveryNeedsContinuation() {
    return false;
  }
}

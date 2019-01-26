package fr.umlv.tatoo.runtime.lexer;

import fr.umlv.tatoo.runtime.buffer.LexerBuffer;
import fr.umlv.tatoo.runtime.lexer.rules.ActionProcessor;
import fr.umlv.tatoo.runtime.lexer.rules.ProcessReturn;
import fr.umlv.tatoo.runtime.parser.ParsingException;

public class DefaultLexerErrorRecoveryPolicy<R,B extends LexerBuffer> implements
LexerErrorRecoveryPolicy<R,B> {

  /**
   * @param errorForwarder error forwarder used by example to forward the error to the parser.
   * @param warningReporter may be null.
   */
  public DefaultLexerErrorRecoveryPolicy(LexerErrorForwarder<B> errorForwarder, LexerWarningReporter<B> warningReporter) {
    this.errorForwarder = errorForwarder;
    this.warningReporter = warningReporter;
  }

  public void recoverOnError(Lexer<B> lexer, ActionProcessor<R> processor) {
    assert recovering==false : "continueRecoverOnError returned error";
    B buffer = lexer.getBuffer();
    ForwardReturn ret;
    try {
      ret= errorForwarder.forwardUnexpectedCharacter(lexer);
    } catch(ParsingException e) {
      throw new LexingException(DefaultLexerWarningReporter.formatMessage(lexer, "lexing error"),e);
    }
    if (ret==ForwardReturn.DISCARD) {
      buffer.unwind(1);
      if (warningReporter!=null)
        warningReporter.handleWarning(lexer,"discarding character for lexer error recovery");
    }
    else
      buffer.reset();
  }

  public ProcessReturn continueRecoverOnError(Lexer<B> lexer, ActionProcessor<R> processor) {
    throw new AssertionError("no continuation needed");
  }

  /*  public ProcessReturn continueRecoverOnError(ActionProcessor<R> processor, B buffer,
      Iterable<? extends R> rules, ErrorReporter<? super B> errorReporter) {
    while(true) {
      ProcessReturn ret = processor.step(buffer,rules);
      switch(ret) {
      case TOKEN:
        recovering=false; return ret;
      case MORE:
        return ret;
      case ERROR:
        buffer.unwind(1);
        errorReporter.handleWarning(buffer,"discarding character for lexer error recovery");
        if (!buffer.hasRemaining())
          return ProcessReturn.MORE;
        continue;
      case NOTHING:
        throw new AssertionError("ActionProcessor.step cannot return NOTHING");
      }
    }
  } */

  public boolean errorRecoveryNeedsContinuation() {
    return false;
  }

  public ProcessReturn continueRecoverOnUnexpectedEndOfFile(Lexer<B> lexer,ActionProcessor<R> processor) {
    throw new AssertionError("no continuation needed");
  }

  public void recoverOnUnexpectedEndOfFile(Lexer<B> lexer, ActionProcessor<R> processor) {
    lexer.getBuffer().reset();
    if (lexer.getBuffer().hasRemaining()) {
      recoverOnError(lexer, processor);
      if (!lexer.getBuffer().hasRemaining())
        errorForwarder.forwardUnexpectedEndOfFile(lexer);
    }
    else {
      errorForwarder.forwardUnexpectedEndOfFile(lexer);
    }
  }

  public boolean unexpectedEndOfFileRecoveryNeedsContinuation() {
    return false;
  }

  private boolean recovering;
  private final LexerErrorForwarder<B> errorForwarder;
  private final LexerWarningReporter<B> warningReporter; 
}

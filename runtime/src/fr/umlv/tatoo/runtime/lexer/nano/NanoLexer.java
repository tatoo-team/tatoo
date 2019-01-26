package fr.umlv.tatoo.runtime.lexer.nano;

import java.io.IOException;

import fr.umlv.tatoo.runtime.buffer.LexerBuffer;
import fr.umlv.tatoo.runtime.lexer.LexingException;
import fr.umlv.tatoo.runtime.lexer.SimpleLexer;
import fr.umlv.tatoo.runtime.lexer.rules.ProcessReturn;

public class NanoLexer<B extends LexerBuffer> implements SimpleLexer {
  private final LexerLifecycleHandler lifecycleHandler;
  B buffer;
  private final RuleProcessor ruleProcessor;
  private final RuleFeeder<B> ruleFeeder;

  public static <B extends LexerBuffer> NanoLexer<B> createNanoLexer(B buffer, 
      RuleFeeder<B> ruleFeeder, LexerLifecycleHandler lifecycleHandler) {
    return new NanoLexer<B>(buffer,ruleFeeder,lifecycleHandler);
  }

  NanoLexer(B buffer, RuleFeeder<B> ruleFeeder, LexerLifecycleHandler lifecycleHandler) {
    this.buffer = buffer;
    this.lifecycleHandler = lifecycleHandler;
    this.ruleFeeder = ruleFeeder;
    this.ruleProcessor = ruleFeeder.getRuleProcessor();
  }

  /**
   * {@inheritDoc}
   */
  public void step() {
    B buffer=this.buffer;
    RuleProcessor ruleProcessor=this.ruleProcessor;
    while(buffer.hasRemaining()) {
      if (ruleProcessor.isProcessFinished()) {
        ruleProcessor.initProcess();
        ruleFeeder.feedWithActiveRuleDatas(buffer.previousWasNewLine());
      }

      ProcessReturn ret = ruleProcessor.step(buffer);

      switch (ret) {
      case TOKEN:
        ruleFeeder.ruleVerified(buffer);
        break;
      case MORE:
        return;
      case ERROR:
        throw new LexingException("unrecognized pattern");
      case NOTHING:
        throw new AssertionError("ActionProcessor.step or LexerErrorRecoveryPolicy.continueRecoverOnError can not return NOTHING");
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  public void close() {
    RuleProcessor ruleProcessor=this.ruleProcessor;
    B buffer=this.buffer;
    while(true) {
      ProcessReturn ret = ruleProcessor.stepClose();
      switch(ret) {
      case NOTHING:
        if (lifecycleHandler!=null)
          lifecycleHandler.lexerClosed();
        return;
      case ERROR:
        throw new LexingException("unrecognized pattern");
      case TOKEN:
        ruleFeeder.ruleVerified(buffer);
        break;
      case MORE:
        throw new AssertionError("ActionProcessor.stepClose cannot return MORE");
      default:
        throw new AssertionError("Unknown stepClose return");
      }
      if (!buffer.hasRemaining()) {
        if (lifecycleHandler!=null)
          lifecycleHandler.lexerClosed();
        return;
      }
      step();
    }
  }

  /*
  private void ruleVerified(B buffer) {
    //int lastTokenLength=ruleProcessor.tokenLength();
    //RuleMatcher ruleMatcher = ruleFeeder.getBestRuleAction();
    //int lastTokenLength = ruleMatcher.lastMatch();
    //buffer.unwind(lastTokenLength);
    //ruleMatcher.ruleVerified(buffer, lastTokenLength);
    ruleFeeder.ruleVerified(buffer);
  }*/

  /**
   * {@inheritDoc}
   */
  public void run() {

    // buffer can already contains characters
    while(buffer.hasRemaining())
      step();

    try {
      while(buffer.read())
        step();
      close();
    } catch (IOException e) {
      throw new LexingException(e);
    }
  }

  public void reset(B buffer) {
    if (lifecycleHandler!=null)
      lifecycleHandler.lexerReset();
    this.buffer = buffer;
    ruleProcessor.reset();
  }

  /**
   * returns the Buffer used by this Lexer
   * @return the Buffer used by this Lexer
   */
  public B getBuffer() {
    return buffer;
  }

  /**
   * Returns the LifecycleHandler for this lexer
   * @return the LifecycleHandler for this lexer
   */
  public LexerLifecycleHandler getLifecycleHandler() {
    return lifecycleHandler;
  }
}

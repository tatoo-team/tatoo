/*
 * Created on 18 juin 2003
 */
package fr.umlv.tatoo.runtime.lexer;

import java.io.IOException;

import fr.umlv.tatoo.runtime.buffer.LexerBuffer;
import fr.umlv.tatoo.runtime.lexer.rules.ActionProcessor;
import fr.umlv.tatoo.runtime.lexer.rules.ProcessReturn;
import fr.umlv.tatoo.runtime.parser.ParsingException;

/**
 * A <code>Lexer</code> represents a lexer process.
 * 
 * @param <B> type of the buffer.
 * 
 * @author Julien Cervelle
 * 
 * @see fr.umlv.tatoo.runtime.tools.builder.Builder#lexer(LexerTable)
 */
public abstract class Lexer<B extends LexerBuffer> implements SimpleLexer {

  /**
   * Creates a new lexer process.
   * @param <R> type of rules.
   * @param <B> type of buffer.
   * 
   * @param lexerTable 
   *         table representing automata used by the lexer.
   * @param buffer
   *          character buffer the lexer has to process
   * @param listener
   *          the token listener called after each token recognition
   * @param activator
   *          the rule activator called before each token recognition start to
   *          determine the set of active rules        
   * @param lifecycleHandler 
   *          handler called when reset or close the lexer.
   * @return a new lexer.
   */
  public static <R,B extends LexerBuffer> Lexer<B> createLexer(LexerTable<R> lexerTable, B buffer, LexerListener<? super R,? super B> listener,
      RuleActivator<R> activator, LifecycleHandler<B> lifecycleHandler, 
      LexerErrorRecoveryPolicy<R, B> policy) {
    return new LexerImpl<R,B>(lexerTable,buffer,listener,activator,lifecycleHandler,policy);
  }

  public static class LexerImpl<R,B extends LexerBuffer> extends Lexer<B> {

    LexerImpl(LexerTable<R> lexerTable, B buffer, LexerListener<? super R,? super B> listener,
        RuleActivator<? extends R> activator, LifecycleHandler<B> lifecycleHandler,
        LexerErrorRecoveryPolicy<R,B> policy) {
      super(buffer,lifecycleHandler);
      
      if (listener==null)
        throw new IllegalArgumentException("null lexer listener");
      if (activator==null)
        throw new IllegalArgumentException("null lexer activator");
      if (policy==null)
        throw new IllegalArgumentException("null lexer policy");
      
      this.listener=listener;
      this.activator=activator;
      this.policy=policy;
      processor = new ActionProcessor<R>(lexerTable);
    }

    /**
     * {@inheritDoc}
     */
    public void step() {
      ActionProcessor<R> processor=this.processor;
      LexerErrorRecoveryPolicy<R, B> policy=this.policy;
      RuleActivator<? extends R> activator=this.activator;
      B buffer=this.buffer;
      while(buffer.hasRemaining()) {
        ProcessReturn ret;
        if (policy.errorRecoveryNeedsContinuation()) {
          ret= policy.continueRecoverOnError(this, processor);
        } else {
          R[] rules=activator.activeRules();
          //System.out.println("active rules "+Arrays.toString(rules));
          ret = processor.step(buffer,rules);
        }
        switch (ret) {
        case TOKEN:
          ruleVerified(buffer,processor);
          break;
        case MORE:
          return;
        case ERROR:
          policy.recoverOnError(this,processor);
          break;
        case NOTHING:
          throw new AssertionError("ActionProcessor.step or LexerErrorRecoveryPolicy.continueRecoverOnError can not return NOTHING");
        }
      }
    }

    /**
     * {@inheritDoc}
     */
    public void close() {
      ActionProcessor<R> processor=this.processor;
      LexerErrorRecoveryPolicy<R, B> policy=this.policy;
      LifecycleHandler<B> lifecycleHandler=this.lifecycleHandler;
      B buffer=this.buffer;
      while(true) {
        ProcessReturn ret;
        if (policy.unexpectedEndOfFileRecoveryNeedsContinuation())
          ret = policy.continueRecoverOnUnexpectedEndOfFile(this,processor);
        else
          ret = processor.stepClose();
        switch(ret) {
        case NOTHING:
          if (lifecycleHandler!=null)
            lifecycleHandler.lexerClosed(this);
          return;
        case ERROR:
          policy.recoverOnUnexpectedEndOfFile(this,processor);
          break;
        case TOKEN:
          ruleVerified(buffer,processor);
          break;
        case MORE:
          throw new AssertionError("ActionProcessor.stepClose cannot return MORE");
        default:
          throw new AssertionError("Unknown stepClose return");
        }
        if (!buffer.hasRemaining()) {
          if (lifecycleHandler!=null)
            lifecycleHandler.lexerClosed(this);
          return;
        }
        step();
      }
    }

    private void ruleVerified(B buffer,ActionProcessor<R> processor) {
      int lastTokenLength=processor.tokenLength();
      buffer.unwind(lastTokenLength);
      try {
        listener.ruleVerified(processor.winningRule(), lastTokenLength, buffer);
      } catch(ParsingException e) {
        throw new LexingException(DefaultLexerWarningReporter.formatMessage(this, "lexing error"),e);
      }
    }

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


    /**
     * returns the RuleActivator used by this Lexer
     * @return the RuleActivator used by this Lexer
     */
    @Override
    public RuleActivator<? extends R> getActivator() {
      return activator;
    }

    /**
     *  {@inheritDoc}
     */
    @Override
    public void reset(B buffer) {
      if (lifecycleHandler!=null)
        lifecycleHandler.lexerReset(this);
      this.buffer = buffer;
      processor.reset();
    }
    /**
     * Returns the LexerListener this lexer notifies events to
     * @return the LexerListener this lexer notifies events to
     */
    @Override
    public LexerListener<? super R, ? super B> getLexerListener() {
      return listener;
    }

    /**
     * Returns the LexerErrorRecoveryPolicy used by this lexer
     * @return the LexerErrorRecoveryPolicy used by this lexer
     */ 
    @Override
    public LexerErrorRecoveryPolicy<R, B> getLexerErrorRecoveryPolicy() {
      return policy;
    }


    private final LexerListener<? super R,? super B> listener;
    private final RuleActivator<? extends R> activator;
    private final ActionProcessor<R> processor;
    private final LexerErrorRecoveryPolicy<R, B> policy;
  }

  Lexer(B buffer,LifecycleHandler<B> lifecycleHandler) {
    this.buffer = buffer;
    this.lifecycleHandler = lifecycleHandler;
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
  public LifecycleHandler<B> getLifecycleHandler() {
    return lifecycleHandler;
  }

  /**
   * Reset the lexer to perform a new analysis. All current states are discarded.
   * The new buffer is the buffer supplied.
   * 
   * This implementation calls {@link LifecycleHandler#lexerReset(Lexer)}
   * after the reset of the lexer.
   */
  public abstract void reset(B buffer);  

  /**
   * returns the RuleActivator used by this Lexer
   * @return the RuleActivator used by this Lexer
   */
  public abstract RuleActivator<?> getActivator();

  /**
   * Returns the LexerListener this lexer notifies events to
   * @return the LexerListener this lexer notifies events to
   */
  public abstract LexerListener<?,?> getLexerListener();

  /**
   * Returns the LexerErrorRecoveryPolicy used by this lexer
   * @return the LexerErrorRecoveryPolicy used by this lexer
   */ 
  public abstract LexerErrorRecoveryPolicy<?,?> getLexerErrorRecoveryPolicy();

  final LifecycleHandler<B> lifecycleHandler;
  B buffer;
}

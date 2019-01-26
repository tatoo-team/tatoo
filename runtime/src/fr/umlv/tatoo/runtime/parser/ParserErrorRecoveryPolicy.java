package fr.umlv.tatoo.runtime.parser;

import java.util.Set;

import fr.umlv.tatoo.runtime.util.IntArrayList;

/** 
 * Error recovery policy of the parser.
 * @author Remi Forax
 *
 * @param <T> type of terminals.
 * @param <N> type of non-terminal.
 * @param <P> type of productions.
 * @param <V> type of versions.
 * 
 * @see Parser#createParser(ParserTable,ParserListener,ParserErrorRecoveryPolicy,Object,Object,LookaheadMap)
 */
public abstract class ParserErrorRecoveryPolicy<T,N,P,V> {
  
  /**
   * Indicate if {@link #continueRecoverOnError} needs to be called by processor
   * @return true if {@link #continueRecoverOnError} needs to be called by processor
   */
  public abstract boolean errorRecoveryNeedsContinuation();
  
  /** 
   * Tries to recover on an error. The method returns {@code true} 
   * if error recovery started and/or succeeded, {@code false}
   * if lexer must skip some input. This method may 
   * throw a ParseException to signify a fatal error.
   * 
   * @param parser the parser.
   * @param states the state stack of the parser.
   * @param terminal the terminal that caused the error or {@code null}
   * if no terminal is available.
   * 
   * @return {@code NEXT} if lexer must skip some input.
   * 
   */
  public abstract ActionReturn recoverOnError(Parser<T,N,P,V> parser,IntArrayList states,T terminal,String message);
  
  /**
   * Called when error recovery was initiated by recover on error, and method {@link #errorRecoveryNeedsContinuation()}
   * returns {@code true}
   *
   * @param parser the parser.
   * @param states the state stack of the parser.
   * @param terminal current terminal, argument of
   *  {@link Parser#step(Object) Parser.step(terminal)} or null.
   * @return <code>NEXT</code> if the terminal must be skipped by the parser,
   * <code>KEEP</code> otherwise.
   * 
   */
  public abstract ActionReturn continueRecoverOnError(Parser<T,N,P,V> parser,IntArrayList states,T terminal);
  
  /** 
   * Notifies that the parser is closing and an error was notified by {@link #continueRecoverOnError}
   * returning {@link ActionReturn#NEXT_ERROR}.
   * This method is called in {@link Parser#close() Parser.close()}.
   * 
   * @param parser the parser.
   * @return true if 
   * @see Parser#close()
   * 
   */
  public abstract boolean closeParser(Parser<T,N,P,V> parser);
  
  /**
   * Returns the lookahead set of terminals that the parser 
   * should accept as input of next {@link Parser#step(Object) Parser.step()}.
   * @param parser the parser.
   * @param proposedLookaheads the lookahead set of terminal
   * of current parser state when there is no error. 
   * @return the set of terminals or <code>null</code> 
   * for all terminals.
   */
  public abstract Set<? extends T> getLookahead(Parser<T,N,P,V> parser,Set<? extends T> proposedLookaheads);
  
  /**
   * Reset the error recovery policy.
   * If the policy maintains some states because it try to recover,
   *  they must be reseted.
   *  
   *  @see Parser#reset(Object)
   */
  public abstract void reset();
  
  /** 
   * Returns an error recover policy that doesn't recover errors.
   * 
   * @param warningReporter a reporter that will display a warning or null.
   * 
   * @param <T> type of terminals.
   * @param <N> type of non-terminals.
   * @param <P> type of productions.
   * @param <V> type of versions.
   * 
   * @return the singleton instance of a policy that 
   * doesn't recover errors.
   */
  public static <T,N,P,V> ParserErrorRecoveryPolicy<T,N,P,V> getNoErrorRecoveryPolicy(final ParserWarningReporter<T,N,P,V> warningReporter) {
    return new ParserErrorRecoveryPolicy<T,N,P,V>() {
      @Override
      public boolean closeParser(Parser<T,N,P,V> parser) {
        return true;
      }
      @Override
      public ActionReturn continueRecoverOnError(Parser<T,N,P,V> parser,IntArrayList states,T terminal) {
        throw new AssertionError("no continuation needed");
      }
      @Override public ActionReturn recoverOnError(Parser<T,N,P,V> parser,IntArrayList states,T terminal, String message) {
        if (warningReporter!=null)
          warningReporter.handleWarning(parser,terminal,message);
        return ActionReturn.NEXT_ERROR;
      }
      @Override
      public Set<? extends T> getLookahead(Parser<T,N,P,V> parser,Set<? extends T> proposedLookaheads) {
        return proposedLookaheads;
      }
      @Override
      public void reset() {
        //nothing: no state
      }
      @Override
      public boolean errorRecoveryNeedsContinuation() {
        return false;
      }
    };
  }
}

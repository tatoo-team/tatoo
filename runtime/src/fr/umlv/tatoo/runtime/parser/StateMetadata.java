/*
 * Created on 12 dec. 2005
 */
package fr.umlv.tatoo.runtime.parser;

import java.util.Set;

/**
 * Data associated to a state of the parsing table.
 * This class contains basically to kind of state :
 * <ol>
 *  <li>a set of compatible versions of the state,
 *      see {@link Parser#reset(Object)} or {@link Parser#setVersion(Object)}.
 *  <li>the terminal shifted by or the non-terminal reduced by the state,
 *      this information is used by the
 *      {@link DefaultParserErrorRecoveryPolicy default error recovery}
 *      mechanism if a state must be dropped to recover on error to signal to the
 *      {@link ParserErrorRecoveryListener} which terminal or non terminal value that must be pop
 *      from the stack.
 *  <li>a default reduce provider that is able to find a default reduce action
 *      for a version.
 * </ol>
 * 
 * @param <T> type of terminals.
 * @param <N> type of non-terminals.
 * @param <P> type of productions.
 * @param <V> type of versions.
 * 
 * @author Remi Forax
 
 */
public abstract class StateMetadata<T,N,P,V> {
  private final DefaultReduceProvider<T,P,V> defaultReduceProvider;
  
  StateMetadata(DefaultReduceProvider<T, P, V> defaultReduceProvider) {
    this.defaultReduceProvider = defaultReduceProvider;
  }

  /** Returns true if the state is compatible with the version taken as argument.
   *  This default implementation always return true.
   * @param version the version
   * @return true if the state is compatible with the version.
   */
  public boolean isCompatible(V version) {
    return true;
  }
  
  /** Called by the {@link DefaultParserErrorRecoveryPolicy default error recovery}
   *  mechanism to adjust the stack of value used by the evaluator.
   *  This implementation calls {@link ParserErrorRecoveryListener#popNonTerminalOnError(Object)}
   *  if the current state is a reduce with a non terminal or
   *  {@link ParserErrorRecoveryListener#popTerminalOnError(Object)} id the current state is
   *  a shift with a terminal.
   *  
   * @param listener the error recovery listener
   */
  public abstract void popVariableOnError(ParserErrorRecoveryListener<? super T,? super N> listener);
  
  /** Return the default reduce action.
   * @return the default reduce action.
   */
  public DefaultReduceProvider<T,P,V> getDefaultReduce() {
    return defaultReduceProvider;
  }
  
  /** Creates a state data compatible with all versions that shift a terminal.
   * 
   * @param <T> type of terminal
   * @param <N> type of non terminal
   * @param <V> type of version
   * 
   * @param terminal the shifted terminal
   * 
   * @return a new state data.
   */
  public static <T,N,P,V> StateMetadata<T,N,P,V> createAllVersionWithTerminal(final T terminal, DefaultReduceProvider<T,P,V> defaultReduceProvider) {
    return new TerminalStateMetadata<T,N,P,V>(terminal,defaultReduceProvider);
  }
  
  /** Creates a state data compatible with all versions that reduce a non terminal.
   * 
   * @param <T> type of terminal
   * @param <N> type of non terminal
   * @param <V> type of version
   * 
   * @param nonTerminal the reduced non terminal
   *
   * @return a new state data.
   */
  public static <T,N,P,V> StateMetadata<T,N,P,V> createAllVersionWithNonTerminal(final N nonTerminal,DefaultReduceProvider<T,P,V> defaultReduceProvider) {
    return new NonTerminalStateMetadata<T,N,P,V>(nonTerminal,defaultReduceProvider);
  }
  
  private static class TerminalStateMetadata<T,N,P,V> extends StateMetadata<T,N,P,V> {
    TerminalStateMetadata(T terminal,DefaultReduceProvider<T,P,V> defaultReduce) {
      super(defaultReduce);
      this.terminal=terminal;
    }
    @Override public void popVariableOnError(ParserErrorRecoveryListener<? super T,? super N> listener) {
      listener.popTerminalOnError(terminal);    
    }
    private final T terminal;
  }
  
  private static class NonTerminalStateMetadata<T,N,P,V> extends StateMetadata<T,N,P,V> {
    NonTerminalStateMetadata(N nonTerminal,DefaultReduceProvider<T,P,V> defaultReduce) {
      super(defaultReduce);
      this.nonTerminal=nonTerminal;
    }
    @Override public void popVariableOnError(ParserErrorRecoveryListener<? super T,? super N> listener) {
      listener.popNonTerminalOnError(nonTerminal);    
    }
    private final N nonTerminal;
  }
  
  /** Creates a state data compatible with a set of versions that shift a terminal.
   * 
   * @param <T> type of terminal
   * @param <N> type of non terminal
   * @param <V> type of version
   * 
   * @param compatible a set of compatible versions
   * @param terminal the shifted terminal
   * @return a new state data.
   */
  public static <T,N,P,V> StateMetadata<T,N,P,V> createWithTerminal(final Set<?> compatible,T terminal,DefaultReduceProvider<T,P,V> defaultReduceProvider) {
    return new TerminalStateMetadata<T,N,P,V>(terminal,defaultReduceProvider) {
      @Override public boolean isCompatible(V version) {
        return compatible.contains(version);
      }
    }; 
  }
  
  /** Creates a state data compatible with a set of versions that reduce a non terminal.
   * 
   * @param <T> type of terminal
   * @param <N> type of non terminal
   * @param <V> type of version
   * 
   * @param compatible a set of compatible versions
   * @param nonTerminal the reduced non terminal
   *
   * @return a new state data.
   */
  public static <T,N,P,V> StateMetadata<T,N,P,V> createWithNonTerminal(final Set<?> compatible,N nonTerminal,DefaultReduceProvider<T,P,V> defaultReduceProvider) {
    return new NonTerminalStateMetadata<T,N,P,V>(nonTerminal,defaultReduceProvider) {
      @Override public boolean isCompatible(V version) {
        return compatible.contains(version);
      }
    }; 
  }
}

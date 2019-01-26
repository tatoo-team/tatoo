package fr.umlv.tatoo.runtime.parser;


import java.util.Map;
import java.util.Set;

import fr.umlv.tatoo.runtime.log.TatooLogger;
import fr.umlv.tatoo.runtime.util.IntArrayList;
import fr.umlv.tatoo.runtime.util.ReadOnlyIntStack;

/**
 * Tatoo parser class.
 * @author Julien Cervelle
 *
 * @param <T> type of terminal.
 * @param <N> type of non-terminal.
 * @param <P> type of production.
 * @param <V> type of version.
 * 
 * @see fr.umlv.tatoo.runtime.tools.builder.Builder#parser(ParserTable)
 */
public class Parser<T,N,P,V> implements SimpleParser<T> {
  private Parser(
      ParserTable<T,N,P,V> table,
      ParserListener<? super T,? super N,? super P> listener,
      ParserErrorRecoveryPolicy<T,N,P,V> policy,N start,V version,
      LookaheadMap<? extends T,? super V> lookaheadMap) {
    TatooLogger.finest("parser created");
    
    if (table==null)
      throw new IllegalArgumentException("null parser table");
    if (listener==null)
      throw new IllegalArgumentException("null parser listener");
    if (policy==null)
      throw new IllegalArgumentException("null parser error recovery policy");
    if (start==null)
      throw new IllegalArgumentException("null start non terminal");
    if (version==null)
      throw new IllegalArgumentException("null version");
    
    this.table = table;
    this.stateStack = new IntArrayList();
    this.listener = listener;
    this.lookaheadMap=lookaheadMap;
    this.policy=policy;
    setStartNonTerminal(start); // must be private to avoid subtype upcall
    checkIfStateStackCompatibleWith(version);
    this.version=version;
  }
  
  /**
   * Creates a parser.
   * @param <T> type of terminal.
   * @param <N> type of non-terminal.
   * @param <P> type of production.
   * @param <V> type of version.
   * @param table the table produced by Tatoo.
   * @param lookaheadMap a map of valid terminals in a particular state.
   * @param listener the listener of parser actions.
   * @param policy the error recovery policy.
   * @param start the start non-terminal.
   * @param version the version of the grammar
   * @return the parser
   * @exception IllegalStateException if the initial state
   *  corresponding to the non-terminal is incompatible with the version.
   * 
   * @see #reset(Object)
   * @see #setVersion(Object)
   */
  public static <T,N,P,V> Parser<T,N,P,V> createParser(ParserTable<T,N,P,V> table,
      ParserListener<? super T,? super N,? super P> listener,
      ParserErrorRecoveryPolicy<T,N,P,V> policy,
      N start,
      V version,
      LookaheadMap<? extends T,? super V> lookaheadMap) {
    
    return new Parser<T,N,P,V>(table,listener,policy,start,version,lookaheadMap);
  }
  
  /**
   * Resets the parser and clears the state stack.
   * A call to {@link #reset()} is roughly equivalent
   * to a call to {@link #reset(Object)} with the current
   * start non-terminal.
   */
  public void reset() {
    reset(getStartNonTerminal());
  }
  
  /**
   * Resets the parser, clears the state stack
   * and changes a new start non-terminal and reset the error recovery policy.
   * @param start the new start non-terminal.
   * @exception IllegalStateException if the initial state
   *  corresponding to the non-terminal is incompatible with the current version.
   * 
   * @see #reset()
   * @see #push(Object,Object)
   * @see ParserErrorRecoveryPolicy#reset()
   */
  public void reset(N start) {
    stateStack.clear();
    setStartNonTerminal(start);
    checkIfStateStackCompatibleWith(version);
    policy.reset();
  }
  
  /**
   * Change the version. The parser stack must be
   * only contains compatible. 
   *  
   * @param version version of the grammar
   * @exception IllegalStateException if the state stack contains
   *  incompatible state with the version.
   * 
   * @see #reset(Object)
   */
  public void setVersion(V version) {
    checkIfStateStackCompatibleWith(version);
    this.version=version;
  }
  
  private void checkIfStateStackCompatibleWith(V version) {
    ReadOnlyIntStack states = stateStack;
    for(int i=states.size();--i>0;) {
      int state=states.get(i);
      if (state<0)
        break;
      if (!table.getMetadataForState(state).isCompatible(version)) {
        throw new IllegalStateException("Incompatible state "+state+" with version "+version);
      }
    }
  }
  
  /** 
   * Returns the start non-terminal
   * @return the start non terminal
   * 
   * @see #reset(Object)
   */
  public N getStartNonTerminal() {
    return startNonTerminal;
  }
  
  private void setStartNonTerminal(N start) {
    Integer startState = table.getStartMap().get(start);
    if (startState==null)
      throw new IllegalArgumentException("Non terminal "+start+" is not a start non terminal");
    this.startNonTerminal=start;
    stateStack.add(startState);
  }
  
  /** Push a new context into the state stack
   * @param start the new start non-terminal.
   * @param version new version
   * 
   * @see #close()
   * @see #reset(Object)
   */
  public void push(N start,V version) {
    if (!table.getStartMap().containsKey(start))
      throw new IllegalArgumentException("Non terminal "+start+" is not a start non terminal");
    
    //ugly! encode version and start non terminal into the stack
    int versionIndex=table.getVersions().indexOf(this.version);
    if (versionIndex==-1)
      throw new AssertionError("current version is not in the table version list");
    stateStack.add(-versionIndex-1);
    int nonTerminalIndex=table.getStartMap().get(startNonTerminal);
    stateStack.add(-nonTerminalIndex-1);
    
    setStartNonTerminal(start);
    setVersion(version);
    branchingLevel++;
  }
  
  // if the stack is a composite of more than one
  // stack frame, remove the last stack frame otherwise
  // reset the stack
  private void cleanStack() {
    // remove garbage from stack
    int state;
    do {
      if (stateStack.isEmpty()) {
        // reset the stack
        setStartNonTerminal(startNonTerminal);
        return;
      }
      state=stateStack.removeLast();
    } while(state>=0);
    
    // retrieve non terminal and version
    startNonTerminal=decodeNonTerminal(state);
    version=decodeVersion(stateStack.removeLast());
  }
  
  private N decodeNonTerminal(int state) {
    state=-state-1;
    for(Map.Entry<N,Integer> entry:table.getStartMap().entrySet())
      if (entry.getValue()==state)
        return entry.getKey();
    throw new AssertionError("bad state encoding");
  }
  
  private V decodeVersion(int state) {
    state=-state-1;
    return table.getVersions().get(state);
  }
  
  /** 
   * Returns the set of terminals which don't lead to an error for the current state
   * with the current version.
   * @return a set of terminals or {@code null} if no lookahead states was provided
   *  at construction of the parser.
   */
  public Set<? extends T> getLookahead() {
    if (lookaheadMap==null)
      return null;
    return policy.getLookahead(this,lookaheadMap.getLookahead(stateStack.last(),version));
  }
  
  /** 
   * @return the version used by the parser according
   *  to the grammar.
   */
  public V getVersion() {
    return version;
  }
  
  /** 
   * Returns the lookahead map.
   * @return the lookahead map or <code>null</code> if no lookahead
   * map was provided at construction of the parser or
   * by {@link #reset(Object)}.
   */
  public LookaheadMap<? extends T,? super V> getLookaheadMap() {
    return lookaheadMap;
  }
  
  /**
   * Returns the parser listener associated with the current parser.
   * @return the parser listener.
   */
  public ParserListener<? super T,? super N,? super P> getParserListener() {
    return listener;
  }
  
  /**
   * Returns the branching parser listener associated with the current parser.
   * @return the branching parser listener.
   */
  public BranchingParserListener<? super T> getBranchingParserListener() {
    return branchingParserListener;
  }
  
  /**
   * Set the branching parser listener associated with the current parser.
   * @param branchingParserListener the new sub-parser listener or null.
   */
  public void setBranchingParserListener(BranchingParserListener<? super T> branchingParserListener) {
    this.branchingParserListener=branchingParserListener;
  }
  
  /** {@inheritDoc}
   * 
   *  The parser is a branching parser if a branching parser listener is set.
   *  @see #setBranchingParserListener(BranchingParserListener)
   */
  public boolean isBranchingParser() {
    return branchingParserListener!=null;
  }
  
  /** Returns current the number of recursive call to this parser.
   *  If the parser is not a branching parser the branching level
   *  is always zero.
   *  If the parser is a branching parser the branching level is
   *  the number of recursive calls to
   *  {@link #push(Object, Object) push}.
   *  
   * @return the number of recursive call to this parser.
   */
  public int getBranchingLevel() {
    return branchingLevel;
  }
  
  /**
   * Default way to create a message from arguments
   * @param <T> terminals type
   * @param parser the parser
   * @param message the message to include in the output
   * @param terminal the terminal that caused this message
   * @return the full formatted message
   */
  public static <T> String formatMessage(SimpleParser<T> parser,String message,T terminal) {
    Set<? extends T> lookahead = parser.getLookahead();
    ReadOnlyIntStack stateStack = parser.getStateStack();
    StringBuilder builder = new StringBuilder();
    builder.append(message);
    if (terminal!=null)
      builder.append(" on terminal ").append(terminal);
    builder.append(" with stack ").append(stateStack);
    if (lookahead!=null)
      builder.append(" , expected ").append(lookahead);
    return builder.toString();
  }

  
  /* * 
   * Returns the error recovery policy used by the parser.
   * @return the error recovery policy.
   * @see ErrorRecoveryPolicy
   * /
  public ErrorRecoveryPolicy<T,N,P,V> getErrorRecoveryPolicy() {
    return policy;
  }*/
  
  /** 
   * Returns the table associated with the parser.
   * @return the parser table.
   */
  public ParserTable<T,N,P,V> getTable() {
    return table;
  }
  
  /** 
   * Returns a view on the current parser state stack.
   * The view is attached to the current parser states,
   * if the parser stack changes by a call to {@link #push(Object)}
   * or {@link #close()} then returned view is modified.
   * 
   * @return an unmodifiable list of states.
   * 
   * @see ReadOnlyIntStack#duplicate()
   */
  public ReadOnlyIntStack getStateStack() {
    return stateStack;
  }  

  
  /** 
   * Signals to the parser an external error, (by example a lexer error) has occurred. It either
   * returns in case of success of error recovery
   * @param terminal the terminal that causes the error or null if no terminal available.
   * 
   * @see ParserErrorRecoveryPolicy#recoverOnError(Parser, IntArrayList, Object, String)
   */
  private ActionReturn recoverOnError(T terminal, String message) {
    if (policy.errorRecoveryNeedsContinuation())
      return policy.continueRecoverOnError(this, stateStack, terminal);
    else
      return policy.recoverOnError(this,stateStack,terminal,message);
  }
  
  
  /** {@inheritDoc}
   */
  public ActionReturn branchOnError(T terminal, String message) {
    TatooLogger.finest("branch");
    if (policy.errorRecoveryNeedsContinuation()) {
      return policy.continueRecoverOnError(this, stateStack, terminal);
    }
    Action<T, P, V>[] branchArray = table.getBranchArray();
    ActionReturn ret;
    do {
      Action<T,P,V> act = branchArray[stateStack.last()];
      ret = act.doPerform(this,terminal);
    } while (ret==ActionReturn.KEEP);
    return ret;
  }
  
  /**
   * {@inheritDoc}
   */
  public void step(T terminal) {
    if (!push(terminal))
        throw new IllegalStateException("Relex not supported by caller");
  }
  
  /**
   * {@inheritDoc}
   */
  public boolean push(T next) {
    assert next!=table.getEof() : "step(eof) must be called with close()";
    ActionReturn result = doStep(next);
    assert result!=ActionReturn.KEEP;
    return result!=ActionReturn.RELEX;
  }
  
  private ActionReturn doStep(T next) {
    Action<T,P,V>[] actions = table.getActions(next);
    if (actions == null) {
      throw new IllegalArgumentException("unknown terminal "+next);
    }
    
    ActionReturn state;
    do {
      if (policy.errorRecoveryNeedsContinuation()) {
        state = policy.continueRecoverOnError(this,stateStack,next);
      }
      else {
        Action<T,P,V> act = actions[stateStack.last()];
        state = act.doPerform(this,next);
      }
    }
    while (state==ActionReturn.KEEP);
    return state;
  }  
  
  /**
   * Indicates to the parser that there is no more terminals.
   * This method clean the stack.
   */
  public void close() {
    try {
      if (doStep(table.getEof())==ActionReturn.NEXT_ERROR&&policy.closeParser(this)) {
        throw new ParsingException(Parser.formatMessage(this, "unrecoverable error", table.getEof()));
      }
    } finally {
      cleanStack();
      if (branchingLevel>0)
        branchingLevel--;
    }
  }
  
  // --------- table actions -------------------------- 
  
  ActionReturn performAccept() {
      if (DEBUG) {
        TatooLogger.finest("accept "+startNonTerminal);
      }
      listener.accept(startNonTerminal);
      return ActionReturn.NEXT;
  }
  
  ActionReturn performBranching(BranchAction<T,P,V> branch, T terminal) {
    if (DEBUG) {
      TatooLogger.finest("states stack is "+stateStack);
      TatooLogger.finest("branching error");
    }
    return branchOnError(terminal,branch.getMessage());
  }
  
  ActionReturn performEnter(EnterAction<T,P,V> enter) {
    //XXX Remi if branchingParserListener is null ??
    boolean result=branchingParserListener.enter(enter.terminal);
    if (result) {
      stateStack.add(enter.shift);
      return ActionReturn.RELEX;
    }
    return recoverOnError(null, "parse error");
  }
  
  ActionReturn performError(ErrorAction<T,P,V> error, T terminal) {
    if (DEBUG) {
      TatooLogger.finest("states stack is "+stateStack);
      TatooLogger.finest("error on "+terminal);
    }
    return recoverOnError(terminal, error.getMessage());
  }
  
  ActionReturn performExit(T terminal) {
    if (getBranchingLevel()==0) {
      return recoverOnError(terminal, "exit error");
    }
    try {
      branchingParserListener.exit();
    } finally {
      cleanStack();
    }
    throw new AssertionError("branching listener exit should always throws an exception");
  }
 
  ActionReturn performReduce(ReduceAction<T,P,V> reduce) {
    if (DEBUG) {
      TatooLogger.finest("states stack is "+stateStack);
      TatooLogger.finest("reduce by "+reduce.production);
    }
    stateStack.removeLast(reduce.rightSize);
    int currentState = stateStack.last();
    if (DEBUG) {
      TatooLogger.finest("goto "+reduce.gotos[currentState]);
    }
    stateStack.add(reduce.gotos[currentState]);
    listener.reduce(reduce.production);
    return ActionReturn.KEEP;
  }
  
  ActionReturn performShift(ShiftAction<T,P,V> shift,T terminal) {
    if (DEBUG) {
      TatooLogger.finest("states stack is "+stateStack);
      TatooLogger.finest("shift to "+shift.shift);
    }
    int state = shift.shift;
    stateStack.add(state);
    listener.shift(terminal);
    
    // fast reduce
    for(;;) {
      StateMetadata<T,N,P,V> metadata = table.getMetadataForState(state);
      DefaultReduceProvider<T,P,V> reduceAction = metadata.getDefaultReduce();
      if (reduceAction==null) {
        return ActionReturn.NEXT;
      }
      ReduceAction<T,P,V> reduce2Action=reduceAction.getReduceAction(version);
      if (reduce2Action==null) {
        return ActionReturn.NEXT;
      }
      performReduce(reduce2Action);
      state = stateStack.last();
    }
  }
  
  ActionReturn performVersioned(VersionedAction<T,P,V> versioned, T terminal) {
    return versioned.getAction(version).doPerform(this,terminal);
  }
  
  private N startNonTerminal;
  private V version;
  private int branchingLevel;
  private BranchingParserListener<? super T> branchingParserListener;
  
  private final IntArrayList stateStack;
  private final ParserTable<T,N,P,V> table;
  private final /*maybenull*/ LookaheadMap<? extends T,? super V> lookaheadMap;
  private final ParserListener<? super T,? super N,? super P> listener;
  private final ParserErrorRecoveryPolicy<T,N,P,V> policy;
  
  // true if parser needs debugging
  private static final boolean DEBUG=false;
}

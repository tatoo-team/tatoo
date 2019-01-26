/*
 * Created on 21 juil. 2003
 */
package fr.umlv.tatoo.runtime.parser;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Action table associated with a grammar. This table is created
 * using the generated informations by Tatoo parser.
 * 
 * @param <T> type of terminal.
 * @param <N> type of non-terminal.
 * @param <P> type of production.
 * @param <V> type of version.
 * 
 * @author Julien Cervelle
 * @version $Revision: 1935 $
 * $Date: 2009-03-18 16:28:04 +0100 (Wed, 18 Mar 2009) $
 */
public class ParserTable<T,N,P,V> {
  /** Creates a parser table.
   * @param table a table that map action or versioned action to a terminal
   *       for a specific state index.
   * @param branchArray an array that contains an action
   *       for a specific state index used by branching parser.
   * @param stateMetadata the metadata associated to the state,
   *        see {@link #getMetadataForState(int)}.
   * @param startMap a map that associates start non terminal to their state
   *        index in the table.
   * @param versions all declared versions of the grammar.
   * @param stateNb the number of states.
   * @param eof the terminal eof.
   * @param error the terminal error used by the
   *        {@link DefaultParserErrorRecoveryPolicy default error recovery} mecanism.
   */
  public ParserTable(Map<T,Action<T,P,V>[]> table,
        Action<T,P,V>[] branchArray,
        StateMetadata<T,N,P,V>[] stateMetadata,
        Map<N,Integer> startMap,V[] versions,
        int stateNb,T eof,T error) {
    this.table=table;
    this.branchArray=branchArray;
    this.stateNb=stateNb;
    this.eof=eof;
    this.error=error;
    this.stateMetadata=stateMetadata;
    this.startMap = startMap;
    this.versions=Arrays.asList(versions);
  }
  /**
   * Returns the actions (shift/reduce/accept/error) associated with
   * a particular terminal of the grammar.
   * @param terminal the terminal. 
   * @return the action array.
   */
  public Action<T,P,V>[] getActions(T terminal) {
    return table.get(terminal);
  }

  /**
   * Returns the number of states in the table.
   * @return the number of states.
   */
  public int getStateNb() {
    return stateNb;
  }
  /**
   * Returns the table filled by Tatoo that associates action (shift/reduce/accept/error)
   * with each terminal of the grammar.
   * 
   * WARNING, because array of parameterized type are not safe in Java, the returned value
   * must be used with caution. In peculiar, this array doesn't support write access.
   * 
   * @return the table 
   */
  public Map<T,Action<T,P,V>[]> getTable() {
    return table;
  }
  
  public Action<T,P,V>[] getBranchArray() {
    return branchArray;
  }
  
  /**
   * Returns the terminal indicating the end of file (<tt>eof</tt>).
   * @return the eof terminal.
   */
  public T getEof() {
    return eof;
  }
  /**
   * Returns the error terminal.
   * @return the error terminal or null.
   * 
   * @see DefaultParserErrorRecoveryPolicy
   */
  public T getErrorTerminal() {
    return error;
  }
  /**
   * Returns meta-data for error recovery associated with a particular 
   * state number.
   * @param state the state number.
   * @return meta-data associated with this state in the table.
   */
  public StateMetadata<T,N,P,V> getMetadataForState(int state) {
    return stateMetadata[state];
  }
  
  /**
   * Returns the table filled by Tatoo that associates the index of the
   * initial state with a peculiar start non-terminal.
   * @return the index of the state.
   */
  public Map<N,Integer> getStartMap() {
    return startMap;
  }
  
  /**
   * Returns the default start non terminal.
   * @return the non terminal corresponding to the
   *  default initial state.
   */
  public N getDefaultStart() {
    return startMap.keySet().iterator().next();
  }

  /** 
   * Returns the list containing all versions of the grammar.
   * @return a list of all versions.
   */
  public List<V> getVersions() {
    return versions;
  }
  
  /**
   * Returns the default version.
   * @return the default version.
   */
  public V getDefaultVersion() {
    return versions.get(0);
  }
  
  private final Map<T,Action<T,P,V>[]> table;
  private final Action<T,P,V>[] branchArray;
  private final List<V> versions;
  private final int stateNb;
  private final T eof,error;
  private final StateMetadata<T,N,P,V>[] stateMetadata;
  private final Map<N, Integer> startMap;
}

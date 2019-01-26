package fr.umlv.tatoo.runtime.parser;

/** An interface for action that can encode a default reduce action.
 * 
 * @param <T> type of terminals.
 * @param <P> type of productions
 * @param <V> type of versions.
 * 
 * @author Remi
 */
public interface DefaultReduceProvider<T,P,V> {
  /** Returns the current action as a reduce action for a specific version.
   * @param version a grammar version
   * @return return the reduce action for the specific version or null.
   */
  public ReduceAction<T,P,V> getReduceAction(V version);
}

package fr.umlv.tatoo.runtime.parser;

import java.util.Map;

/** Action that behaves differently depending of the
 *  version of the grammar. This class acts as a
 *  composite of versioned actions.
 * 
 * @author Remi Forax
 *
 * @param <T> type of terminal.
 * @param <P> type of productions.
 * @param <V> type of versions.
 */
public class VersionedAction<T,P,V> extends Action<T,P,V> implements DefaultReduceProvider<T,P,V> {
  /** Initializes the action with a map of versioned actions.
   * @param actionMap the map of versioned actions.
   */
  public VersionedAction(Map<V,? extends Action<T,P,V>> actionMap) {
    this.actionMap=actionMap;
  }
  
  @Override
  public <N> ActionReturn doPerform(Parser<T,N,P,V> parser, T terminal) {
    return parser.performVersioned(this,terminal);
  }
  
  @Override
  public boolean isError(V version) {
    return getAction(version).isError(version);
  }
  
  Action<T,P,V> getAction(V version) {
    return actionMap.get(version);
  }
  
  public ReduceAction<T,P,V> getReduceAction(V version) {
    return (ReduceAction<T,P,V>)getAction(version);
  }
  
  private final Map<V,? extends Action<T,P,V>> actionMap;
}

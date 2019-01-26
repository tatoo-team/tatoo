/*
 * Created on 27 nov. 2005
 */
package fr.umlv.tatoo.runtime.parser;

import java.util.Set;

/**
 * Map each parser state with the set of terminals the parser that will
 * not lead to an error from this state.
 * 
 * @param <T> type of terminals.
 * @param <V> type of versions.
 */
public abstract class LookaheadMap<T,V> {
  /**
   * Returns the current lookahead set of terminal
   * @param state current parser state.
   * @param version current parser version.
   * @return the current lookahead set of terminal.
   */
  public abstract Set<? extends T> getLookahead(int state,V version);
}

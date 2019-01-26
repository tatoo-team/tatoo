package fr.umlv.tatoo.runtime.parser;

/**
 * Parser actions (shift/reduce/accept/error) super-class.
 *   
 * @author Julien Cervelle
 * 
 * @param <T> type of terminals.
 * @param <P> type of productions.
 * @param <V> type of versions.
 * 
 * @see ShiftAction
 * @see ReduceAction
 * @see AcceptAction
 * @see ErrorAction
 * @see EnterAction
 * @see ExitAction
 */
public abstract class Action<T,P,V> {
  /**
   * Performs necessary computations required by the action.
   * @param parser the parser object.
   * @param terminal terminal on which the shift is performed.
   * @param <N> type of non-terminals.
   * 
   * @return NEXT if action consumed terminal, and KEEP else.

   * 
   */
  public abstract <N> ActionReturn doPerform(Parser<T,N,P,V> parser,T terminal);
  
  /**
   * Indicates if the action is an error.
   * @param version version of the action to consider, see {@link VersionedAction}.
   * 
   * @return <code>true</code> if the action is an errror.
   */
  public boolean isError(V version) {
    return false;
  }
  /**
   * Indicates if the action is a shift.
   * @param version version of the action to consider, see {@link VersionedAction}.
   * 
   * @return <code>true</code> if the action is an shift.
   */
  public boolean isShift(V version) {
    return false;
  }
}

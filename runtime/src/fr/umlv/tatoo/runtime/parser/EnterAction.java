package fr.umlv.tatoo.runtime.parser;

/**
 * Enter in a sub parser action class.
 * 
 * @param <T> type of terminals.
 * @param <P> type of productions.
 * @param <V> type of versions.
 * 
 * @author Remi
 * 
 * @see ExitAction
 */
public class EnterAction<T,P,V> extends Action<T,P,V> {
  /** Creates an enter action with an error action that will be used
   *  if enter doesn't succeed.
   *  
   * @param terminal magic terminal that can be replaced by a sub-grammar.
   */
  public EnterAction(T terminal,int shift) {
    this.terminal=terminal;
    this.shift=shift;
  }
  
  @Override
  public <N> ActionReturn doPerform(Parser<T,N,P,V> parser, T terminal) {
    return parser.performEnter(this);
  }
  
  @Override
  public String toString() {
    return "enter "+terminal+" shift "+shift;
  }
  
  final int shift;
  final T terminal;
}

package fr.umlv.tatoo.runtime.parser;

/**
 * Shift action class.
 * 
 * @author Julien Cervelle
 *
 * @param <T> type of terminals.
 * @param <P> type of productions.
 * @param <V> type of versions.
 */
public class ShiftAction<T,P,V> extends Action<T,P,V> {
  /**
   * Constructs a shift action.
   * @param shift state reached by this shift action.
   */
  public ShiftAction(int shift) {
    this.shift = shift;
  }  
  
  @Override
  public <N> ActionReturn doPerform(Parser<T,N,P,V> parser, T terminal) {
    return parser.performShift(this,terminal);
  }
  
  @Override
  public boolean isShift(V version) {
    return true;
  }

  @Override
  public String toString() {
    return "Shift to " + shift;
  }

  final int shift;
}

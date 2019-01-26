package fr.umlv.tatoo.runtime.parser;

/**
 * Exit out a sub parser action class.
 * 
 * @param <T> type of terminals.
 * @param <P> type of productions.
 * @param <V> type of versions.
 * 
 * @author Remi
 * 
 * @see EnterAction
 */
public class ExitAction<T,P,V> extends Action<T,P,V> {
  private ExitAction() {
    // enforce singleton
  }
  
  @Override
  public <N> ActionReturn doPerform(Parser<T,N,P,V> parser, T terminal) {
    return parser.performExit(terminal);
  }
  
  @Override
  public String toString() {
    return "exit";
  }
  
  /** Returns the singleton instance of accept action.
   * @param <T> type of terminals. 
   * @param <P> type of productions.
   * @param <V> type of versions.
   * @return the singleton instance of accept action.
   */
  @SuppressWarnings("unchecked")
  public static <T,P,V> ExitAction<T,P,V> getInstance() {
    return (ExitAction<T,P,V>)INSTANCE;
  }
  private static final ExitAction<?,?,?> INSTANCE=
    new ExitAction<Object,Object,Object>();
}

/*
 * Created on May 30, 2003
 *
 */
package fr.umlv.tatoo.runtime.parser;

/**
 * Accept action class.
 * 
 * @param <T> type of terminals.
 * @param <P> type of productions.
 * @param <V> type of versions.
 * 
 * @author Julien Cervelle
 */
public class AcceptAction<T,P,V> extends Action<T,P,V> {
  private AcceptAction() {
    // disable construction
  }
  
  @Override
  public <N> ActionReturn doPerform(Parser<T,N,P,V> parser, T terminal) {
    return parser.performAccept();
  }
  
  @Override
  public String toString() {
    return "accept";
  }
  
  /** Returns the singleton instance of accept action.
   * @param <T> type of terminals. 
   * @param <P> type of productions.
   * @param <V> type of versions.
   * @return the singleton instance of accept action.
   */
  @SuppressWarnings("unchecked")
  public static <T,P,V> AcceptAction<T,P,V> getInstance() {
    return (AcceptAction<T,P,V>)INSTANCE;
  }
  private static final AcceptAction<?,?,?> INSTANCE = new AcceptAction<Object,Object,Object>();
}

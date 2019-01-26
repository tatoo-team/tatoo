package fr.umlv.tatoo.runtime.parser;

/**
 * Reduce action class.
 * 
 * @author Julien Cervelle
 *
 * @param <T> type of terminals.
 * @param <P> type of productions.
 * @param <V> type of versions.
 */
public class ReduceAction<T,P,V> extends Action<T,P,V> implements DefaultReduceProvider<T,P,V> {
  /**
   * Constructs a reduce action.
   * @param production production associated with this reduce action.
   * @param rightSize size of the production right hand side.
   * @param gotos goto array used for goto actions after reduction.
   */
  public ReduceAction(P production,int rightSize,int[] gotos) {
    this.production = production;
    this.gotos = gotos;
    this.rightSize = rightSize;
  }
  /**
   * Returns the production on which the reduce is performed.
   * @return the production.
   */
  public P getProduction() {
    return production;
  }
  /**
   * Returns the size of the right hand size of the production associated
   * with this reduce action.
   * @return the right hand side size.
   */
  public int getRightSize() {
    return rightSize;
  }
  
  @Override
  public <N> ActionReturn doPerform(Parser<T,N,P,V> parser, T terminal) {
    return parser.performReduce(this);
  }
  
  public ReduceAction<T,P,V> getReduceAction(V version) {
    return this;
  }

  @Override
  public String toString() {
    return "Reduce by "+production;
  }
  
  final P production;
  final int[] gotos;
  final int rightSize;
}

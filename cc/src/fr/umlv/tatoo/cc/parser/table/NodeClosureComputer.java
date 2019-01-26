/*
 * Created on 20 febr. 2006
 */
package fr.umlv.tatoo.cc.parser.table;

public interface NodeClosureComputer<I extends NodeItem<I>> {
  
  /**
   * Gives the closure of the dotted non terminal of item
   * @param item the item containing the non terminal
   * @return the closure
   */
  public Closure<I> getClosure(I item);
}

/*
 * Created on Jun 2, 2003
 *
 */
package fr.umlv.tatoo.cc.parser.solver;

import java.util.Set;

/**
 * @author jcervell
 *
 */
public interface NodeContent<K,V> {
	
  /**
   * Notifies this node that some node has changed
   * @return true if this node has changed
   */
  public boolean hasChanged(K key,NodeContent<K,V> node,Solver<K,V> solver);
  
  /**
   * dependencies should not change during computation
   * @return the set of nodes which changes has to be notified to this node
   */
  public Set<K> dependencies();
  
  /**
   * retrieve the information computed so far
   * @return the information computed so far
   */
  public V getCurrentResult();
  
  /**
   * retrieve the information for this node when the computation
   * is finished
   * @return the final result
   */
  public V getResult();
}

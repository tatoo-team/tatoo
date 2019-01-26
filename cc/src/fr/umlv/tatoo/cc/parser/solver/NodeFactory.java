/*
 * Created on 2 juin 2003
 */
package fr.umlv.tatoo.cc.parser.solver;

/**
 * @author Julien
 *
 */
public interface NodeFactory<K,V> {
  /**
   * Build node information from key
   * @param key the key
   * @return the node
   */
   public NodeContent<K,V> getNode(K key);
}

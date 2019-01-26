package fr.umlv.tatoo.runtime.ast;

import java.util.List;

public interface Node {
  /** Return true if the current node is a token.
   * @return true if the current node is a token.
   * 
   * @see Token
   */
  public boolean isToken();
  
  /** Returns the kind of node.
   * @return the kind of node.
   */
  public Object getKind();
  
  /** Returns the parent of the current node.
   * @return the parent of the current node or null
   *  if the node has no parent.
   */
  public Node getParent();
  
  /** A read-only list of all sub nodes of the current node.
   *  This list provides access in constant time to each of its element.
   *  
   * @return a list of all sub nodes.
   */
  public List<? extends Node> nodeList();
}
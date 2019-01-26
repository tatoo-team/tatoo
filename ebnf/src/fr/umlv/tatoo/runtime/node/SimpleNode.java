package fr.umlv.tatoo.runtime.node;

import java.util.List;

public abstract class SimpleNode<V> extends AbstractNode  {
  private final V value;
  
  public SimpleNode(AST ast,V value,List<Node> allNodes) {
    super(ast,allNodes);
    
    this.value=value;
  }
  
  public V getValue() {
    return value;
  }
}

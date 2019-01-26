package fr.umlv.tatoo.runtime.node;

import java.util.Collections;
import java.util.List;

public abstract class Token<K,V> extends AbstractToken {
  private final K kind;
  private final V value;
  
  public Token(AST ast, K kind, V value) {
    super(ast);
    this.kind=kind;
    this.value=value;
  }
  
  public boolean isToken() {
    return true;
  }
  
  public K getKind() {
    return kind;
  }
  public V getValue() {
    return value;
  }
  
  @Override
  public List<Node> allNodeList() {
    return Collections.emptyList();
  }
  
  @Override
  public List<Node> nodeList() {
    return Collections.emptyList();
  }
  
  @Override
  public String toString() {
    return "("+getKind()+':'+getValue()+')';
  }
}

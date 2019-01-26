package fr.umlv.tatoo.runtime.node;

import java.util.HashMap;
import java.util.Map;

public class DefaultAttributeManager<A> extends AttributeManager<A> {
  private final Map<Node,A> attributeMap;
  
  public DefaultAttributeManager(Map<Node,A> attributeMap) {
    this.attributeMap=attributeMap;
  }
  
  public DefaultAttributeManager() {
    this(new HashMap<Node,A>());
  }

  @Override
  public A getAttribute(Node node) {
    if (node==null)
      throw new IllegalArgumentException("node is null");
    return attributeMap.get(node);
  }

  @Override
  public void putAttribute(Node node,A attribute) {
    if (node==null)
      throw new IllegalArgumentException("node is null");
    attributeMap.put(node,attribute);
  }
}

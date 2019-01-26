package fr.umlv.tatoo.runtime.node;

public abstract class AttributeManager<A> {
  protected abstract A getAttribute(Node node); 
  protected abstract void putAttribute(Node node, A attribute); 
}

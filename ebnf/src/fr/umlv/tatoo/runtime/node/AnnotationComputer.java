package fr.umlv.tatoo.runtime.node;


public interface AnnotationComputer {
  public void computeTokenAnnotation(Token<?,?> token);
  
  public void computeNodeAnnotation(Node node,Node first,Node last);
  
  public void computeEmptyNodeAnnotation(Node node);
}

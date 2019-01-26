package fr.umlv.tatoo.runtime.node;

import java.util.List;


public abstract class NodeBuilder<N extends Node> {
  private final AnnotationComputer annotationComputer;
  private final NodeListBuilder<Node> children=new NodeListBuilder<Node>();
  
  protected NodeBuilder(AnnotationComputer annotationComputer) {
    this.annotationComputer=annotationComputer;
  }
  
  public N create() {
    List<Node> list=children.createList();
    N node=createNode(list);
    
    if (children.isEmpty()) {
      annotationComputer.computeEmptyNodeAnnotation(node);
    } else {
      annotationComputer.computeNodeAnnotation(node,
          list.get(0),
          list.get(list.size()-1));  
    }
    
    return node;
  }
  
  protected abstract N createNode(List<Node> children);
  
  public NodeBuilder<N> add(Node tree) {
    children.add(tree);
    return this;
  }
  
  public NodeBuilder<N> addAll(Node... trees) {
    children.addAll(trees);
    return this;
  }
  
  public NodeBuilder<N> addAll(List<? extends Node> trees) {
    children.addAll(trees);
    return this;
  }
}
package fr.umlv.tatoo.runtime.node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class NodeListBuilder<T extends Node> {
  private final ArrayList<T> children=new ArrayList<T>();
  public boolean isEmpty() {
    return children.isEmpty();
  }
  public NodeListBuilder<T> add(T tree) {
    // remove empty node
    if (tree!=null)
      children.add(tree);
    return this;
  }
  
  public NodeListBuilder<T> addAll(T... trees) {
    for(T tree:trees)
      // remove empty node
      if (tree!=null)
        children.add(tree);
    return this;
  }
  
  public NodeListBuilder<T> addAll(List<? extends T> trees) {
    for(T tree:trees)
      // remove empty node
      if (tree!=null)
        children.add(tree);
    return this;
  }
  
  public List<T> createList() {
    return Collections.unmodifiableList(children);   
  }
}

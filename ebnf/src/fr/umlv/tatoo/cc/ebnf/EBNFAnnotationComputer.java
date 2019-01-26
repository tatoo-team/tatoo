package fr.umlv.tatoo.cc.ebnf;

import fr.umlv.tatoo.runtime.buffer.LocationProvider;
import fr.umlv.tatoo.runtime.node.AnnotationComputer;
import fr.umlv.tatoo.runtime.node.Node;
import fr.umlv.tatoo.runtime.node.Token;

public class EBNFAnnotationComputer implements AnnotationComputer {
  private final LocationProvider locationTracker;
  public EBNFAnnotationComputer(LocationProvider locationTracker) {
    this.locationTracker=locationTracker;
  }
  
  public void computeTokenAnnotation(Token<?,?> token) {
    token.setAttribute(LineColumnLocation.class,
      new LineColumnLocation(
        locationTracker.getLineNumber(),locationTracker.getColumnNumber()));
  }
  
  public void computeNodeAnnotation(Node node,Node first,Node last) {
    node.setAttribute(LineColumnLocation.class,
      first.getAttribute(LineColumnLocation.class));
  }
  
  public void computeEmptyNodeAnnotation(Node node) {
    node.setAttribute(LineColumnLocation.class,
      new LineColumnLocation(
        locationTracker.getLineNumber(),locationTracker.getColumnNumber()));
  }
}

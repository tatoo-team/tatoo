package fr.umlv.tatoo.samples.pseudo.comp.main;

import java.util.List;

import fr.umlv.tatoo.runtime.buffer.impl.LocationTracker;
import fr.umlv.tatoo.samples.pseudo.ast.Node;

public class ASTGrammarEvaluator extends fr.umlv.tatoo.samples.pseudo.ast.ASTGrammarEvaluator {
  private final LocationTracker locationTracker;
  private final LocationMap locationMap;
  
  public ASTGrammarEvaluator(LocationTracker locationTracker, LocationMap locationMap) {
    this.locationTracker=locationTracker;
    this.locationMap=locationMap;
  }
  
  @Override
  protected void computeAnnotation(Node node) {
    List<? extends Node> nodeList=node.nodeList();
    if (nodeList.isEmpty()) {
      locationMap.setLocation(node,
        new Location(
          locationTracker.getLineNumber(),
          locationTracker.getColumnNumber()));
    } else {
      locationMap.setLocation(node,
        locationMap.getLocation(nodeList.get(0)));
    }
  }
}

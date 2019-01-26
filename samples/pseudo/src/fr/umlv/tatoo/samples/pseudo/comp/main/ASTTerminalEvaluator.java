/**
 * 
 */
package fr.umlv.tatoo.samples.pseudo.comp.main;

import fr.umlv.tatoo.runtime.buffer.impl.LocationTracker;
import fr.umlv.tatoo.samples.pseudo.ast.Node;
import fr.umlv.tatoo.samples.pseudo.comp.ast.AbstractASTTerminalEvaluator;

public final class ASTTerminalEvaluator extends AbstractASTTerminalEvaluator {
  private final LocationTracker locationTracker;
  private final LocationMap locationMap;
  
  public ASTTerminalEvaluator(LocationTracker locationTracker, LocationMap locationMap) {
    this.locationTracker=locationTracker;
    this.locationMap=locationMap;
  }
  
  @Override
  protected void computeAnnotation(Node node) {
    locationMap.setLocation(node,
      new Location(
        locationTracker.getLineNumber(),
        locationTracker.getColumnNumber()));
  }
}
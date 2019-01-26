package fr.umlv.tatoo.samples.pseudo.comp.main;

import fr.umlv.tatoo.samples.pseudo.ast.Node;
import fr.umlv.tatoo.samples.pseudo.comp.analysis.ErrorReporter;

public class LocationErrorReporter implements ErrorReporter {
  private final LocationMap locationMap;
  private boolean onError;
  
  public LocationErrorReporter(LocationMap locationMap) {
    this.locationMap=locationMap;
  }
  
  public boolean isOnError() {
    return onError;
  }
  
  @Override
  public void error(Node node,String message) {
    onError=true;
    System.out.println("error: "+message+" at "+locationMap.getLocation(node));
  }
  
  @Override
  public void warning(Node node,String message) {
    System.out.println("warning: "+message+" at "+locationMap.getLocation(node));
  }
}

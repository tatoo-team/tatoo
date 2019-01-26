package fr.umlv.tatoo.samples.pseudo.comp.main;

import java.util.HashMap;

import fr.umlv.tatoo.samples.pseudo.ast.Node;

public class LocationMap {
  private final HashMap<Node,Location> locationMap=
    new HashMap<Node,Location>();
  
  public Location getLocation(Node node) {
    return locationMap.get(node);
  }
  
  public void setLocation(Node node, Location location) {
    locationMap.put(node,location);
  }
}

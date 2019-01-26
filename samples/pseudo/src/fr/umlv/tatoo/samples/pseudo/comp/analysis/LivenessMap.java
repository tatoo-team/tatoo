package fr.umlv.tatoo.samples.pseudo.comp.analysis;

import java.util.HashMap;

import fr.umlv.tatoo.samples.pseudo.ast.Node;

public class LivenessMap {
  private final HashMap<Node,Boolean> map=
    new HashMap<Node,Boolean>();
  
  public boolean isAlive(Node node) {
    Boolean alive=map.get(node);
    if (alive==null)
      throw new IllegalArgumentException("no liveness available for node "+node);
    return alive;
  }
  
  public void setAlive(Node node, boolean alive) {
    map.put(node,alive);
  }
}

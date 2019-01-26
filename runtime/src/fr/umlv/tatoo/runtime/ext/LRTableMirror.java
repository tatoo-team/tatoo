package fr.umlv.tatoo.runtime.ext;

import java.util.List;

public class LRTableMirror<N,P> {
  private final List<DottedProduction<P>> dottedProductions;
  
  public LRTableMirror(List<DottedProduction<P>> dottedProductions) {
    this.dottedProductions=dottedProductions;
  }
  
  public List<DottedProduction<P>> getDottedProductions() {
    return dottedProductions;
  }
  
  //TODO Remi add goto states for non-terminals here
}

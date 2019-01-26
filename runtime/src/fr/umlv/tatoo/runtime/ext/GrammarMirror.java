package fr.umlv.tatoo.runtime.ext;

import java.util.List;

public class GrammarMirror<N,P> {
  private final List<ProductionMirror<N,P>> productionList;
  private final List<N> startNonTerminalList;
  
  public GrammarMirror(List<ProductionMirror<N,P>> productionList, List<N> startNonTerminalList) {
    this.productionList = productionList;
    this.startNonTerminalList=startNonTerminalList;
  }
  
  public List<ProductionMirror<N,P>> getProductionList() {
    return productionList;
  }
  
  public List<N> getStartNonTerminalList() {
    return startNonTerminalList;
  }
}

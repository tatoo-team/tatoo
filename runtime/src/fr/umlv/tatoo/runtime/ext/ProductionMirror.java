package fr.umlv.tatoo.runtime.ext;

import java.util.List;

public class ProductionMirror<N,P> {
  private final N nonTerminal;
  private final List<Object> rightHandSize;
  private final P production;
  
  public ProductionMirror(N nonTerminal, List<Object> rightHandSize, P production) {
    this.nonTerminal = nonTerminal;
    this.rightHandSize = rightHandSize;
    this.production = production;
  }
  
  public N getNonTerminal() {
    return nonTerminal;
  }
  public List<Object> getRightHandSize() {
    return rightHandSize;
  }
  public P getProduction() {
    return production;
  }
  
  @Override
  public String toString() {
    return nonTerminal+" ::= "+rightHandSize+" {"+production+'}';
  }
}

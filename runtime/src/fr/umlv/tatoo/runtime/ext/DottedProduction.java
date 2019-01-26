package fr.umlv.tatoo.runtime.ext;

public class DottedProduction<P> {
  private final P production;
  private final int dot;

  public DottedProduction(P production,int dot) {
    this.production=production;
    this.dot=dot;
  }

  public P getProduction() {
    return production;
  }
  public int getDot() {
    return dot;
  }

  @Override
  public String toString() {
    return production+"("+dot+')';
  }
}
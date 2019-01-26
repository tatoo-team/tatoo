/**
 * 
 */
package fr.umlv.tatoo.cc.plugin;

import fr.umlv.tatoo.cc.parser.grammar.ProductionDecl;

public abstract class ContextDecl {
  public static class ShiftDecl extends ContextDecl {
    private final ProductionDecl production;
    private final int dot;

    public ShiftDecl(ProductionDecl production,int dot) {
      this.production=production;
      this.dot=dot;
    }
    
    public ProductionDecl getProduction() {
      return production;
    }
    public int getDot() {
      return dot;
    }
    
    @Override
    public String toString() {
      return "shift "+production+"("+dot+')';
    }
  }
  
  public static class ReduceDecl extends ContextDecl {
    private final ProductionDecl production;
    
    public ReduceDecl(ProductionDecl production) {
      this.production=production;
    }
    
    public ProductionDecl getProduction() {
      return production;
    }
    
    @Override
    public String toString() {
      return "reduce "+production;
    }
  }
}
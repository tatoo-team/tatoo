/*
 * Created on 6 juil. 2005
 */
package fr.umlv.tatoo.cc.parser.parser;

import fr.umlv.tatoo.cc.parser.grammar.NonTerminalDecl;
import fr.umlv.tatoo.cc.parser.grammar.ProductionDecl;

public class ReduceActionDecl extends AbstractActionDecl implements RegularTableActionDecl, BranchTableActionDecl {
  ReduceActionDecl(ProductionDecl production) {
    super("reduce"+production.getId());
    this.production = production;
  }
  
  @Override
  public boolean isReduce() {
    return true;
  }
  
  public NonTerminalDecl getGotos() {
    return production.getLeft();
  }
  
  public ProductionDecl getProduction() {
    return production;
  }
  
  public <R,P> R accept(ActionDeclVisitor<? extends R,? super P> visitor, P parameter) {
    return visitor.visit(this,parameter);
  }
  
  @Override
  public String toString() {
    return "reduce by " + production;
  }
  
  private final ProductionDecl production;
}

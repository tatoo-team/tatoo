/*
 * Created on 6 juil. 2005
 */
package fr.umlv.tatoo.cc.parser.parser;

import fr.umlv.tatoo.cc.parser.table.NodeDecl;

public class ShiftActionDecl extends AbstractActionDecl implements RegularTableActionDecl {
  ShiftActionDecl(NodeDecl<?> shift) {
    super("shift"+shift.getStateNo());
    this.shift = shift;
  }
  
  public NodeDecl<?> getState() {
    return shift;
  }
  
  public <R,P> R accept(ActionDeclVisitor<? extends R,? super P> visitor, P parameter) {
    return visitor.visit(this,parameter);
  }
  
  @Override
  public String toString() {
    return "shift to "+shift;
  }
  
  private final NodeDecl<?> shift;
}

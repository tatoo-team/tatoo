package fr.umlv.tatoo.cc.parser.parser;

import fr.umlv.tatoo.cc.parser.grammar.TerminalDecl;
import fr.umlv.tatoo.cc.parser.table.NodeDecl;

public class EnterActionDecl extends AbstractActionDecl implements BranchTableActionDecl {
  
  EnterActionDecl(TerminalDecl branchingTerminal,NodeDecl<?> shift) {
    super("enter_"+branchingTerminal.getId()+'_'+shift.getStateNo());
    this.branchingTerminal=branchingTerminal;
    this.shift=shift;
  }
  
  public NodeDecl<?> getState() {
    return shift;
  }
  public TerminalDecl getBranchingTerminal() {
    return branchingTerminal;
  }
  
  public <R,P> R accept(ActionDeclVisitor<? extends R,? super P> visitor, P parameter) {
    return visitor.visit(this,parameter);
  }
  
  @Override
  public String toString() {
    return "enter ("+branchingTerminal.getId()+','+shift+')';
  }
  
  private final NodeDecl<?> shift;
  private final TerminalDecl branchingTerminal;
}

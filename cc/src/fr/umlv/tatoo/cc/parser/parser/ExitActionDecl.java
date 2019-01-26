/*
 * Created on 6 juil. 2005
 */
package fr.umlv.tatoo.cc.parser.parser;

public class ExitActionDecl extends AbstractActionDecl implements BranchTableActionDecl {
  ExitActionDecl() {
    super("exit");
  }
  
  public <R,P> R accept(ActionDeclVisitor<? extends R,? super P> visitor, P parameter) {
    return visitor.visit(this,parameter);
  }
  
  @Override
  public String toString() {
    return "exit";
  }
}

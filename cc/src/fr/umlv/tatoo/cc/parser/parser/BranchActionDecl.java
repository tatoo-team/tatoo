/*
 * Created on 6 juil. 2005
 */
package fr.umlv.tatoo.cc.parser.parser;

public class BranchActionDecl extends AbstractActionDecl implements RegularTableActionDecl {
  
  BranchActionDecl(String message,int number) {
    super("branch"+number);
    this.message = message;
  }
  
  public String getMessage() {
    return message;
  }
  
  public <R,P> R accept(ActionDeclVisitor<? extends R,? super P> visitor, P parameter) {
    return visitor.visit(this,parameter);
  }
  
  @Override
  public String toString() {
    return "branch ("+message+')';
  }
  
  private final String message;

  @Override
  public boolean isError() {
    return true;
  }
}

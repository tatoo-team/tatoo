/*
 * Created on 6 juil. 2005
 */
package fr.umlv.tatoo.cc.parser.parser;


public class ErrorActionDecl extends AbstractActionDecl implements BranchTableActionDecl {
  ErrorActionDecl(String name,String message) {
    super(name);
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
    return "error ("+message+')';
  }
  
  private final String message;
}

/*
 * Created on 6 juil. 2005
 */
package fr.umlv.tatoo.cc.parser.parser;

public class AcceptActionDecl extends AbstractActionDecl implements RegularTableActionDecl {
  private AcceptActionDecl() {
    super("accept");
  }
  
  public <R,P> R accept(ActionDeclVisitor<? extends R,? super P> visitor, P parameter) {
    return visitor.visit(this,parameter);
  }
  
  @Override
  public String toString() {
    return "accept";
  }
  
  private static final AcceptActionDecl INSTANCE = new AcceptActionDecl();
  
  public static AcceptActionDecl getInstance() {
    return INSTANCE;
  }
}

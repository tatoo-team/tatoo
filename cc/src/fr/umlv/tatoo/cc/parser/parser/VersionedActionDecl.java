package fr.umlv.tatoo.cc.parser.parser;

import java.util.Map;

import fr.umlv.tatoo.cc.parser.grammar.VersionDecl;

/**
 * @author cervelle
 */
public class VersionedActionDecl extends AbstractActionDecl implements RegularTableActionDecl,BranchTableActionDecl {
  public VersionedActionDecl(int id,Map<VersionDecl,? extends ActionDecl> actionMap) {
    super("versionedAction"+id);
    this.actionMap=actionMap;
  }
  
  public Map<VersionDecl,? extends ActionDecl> getActionMap() {
    return actionMap;
  }
  
  private final Map<VersionDecl,? extends ActionDecl> actionMap;

  public <R, P> R accept(ActionDeclVisitor<? extends R, ? super P> visitor,
      P parameter) {
    return visitor.visit(this,parameter);
  }
}

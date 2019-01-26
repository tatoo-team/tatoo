package fr.umlv.tatoo.cc.parser.parser;

import fr.umlv.tatoo.cc.common.generator.ObjectId;

public interface ActionDecl extends ObjectId {
  public boolean isReduce();
 // public boolean isError();
  public <R,P> R accept(ActionDeclVisitor<? extends R,? super P> visitor, P parameter);
  public <R> R accept(SimpleActionDeclVisitor<? extends R> visitor);
}

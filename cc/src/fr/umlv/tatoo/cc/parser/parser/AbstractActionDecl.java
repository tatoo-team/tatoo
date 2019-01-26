/*
 * Created on 6 juil. 2005
 */
package fr.umlv.tatoo.cc.parser.parser;

import fr.umlv.tatoo.cc.common.generator.AbstractObjectId;

public abstract class AbstractActionDecl extends AbstractObjectId implements ActionDecl {
  protected AbstractActionDecl(String name) {
    super(name);
  }
  public boolean isReduce() {
    return false;
  }
  public boolean isError() {
    return false;
  }
  
  public <R> R accept(SimpleActionDeclVisitor<? extends R> visitor) {
    return accept(visitor,null);
  }
}

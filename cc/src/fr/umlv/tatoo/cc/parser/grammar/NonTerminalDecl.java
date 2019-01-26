package fr.umlv.tatoo.cc.parser.grammar;

import fr.umlv.tatoo.cc.common.generator.AbstractObjectId;

public class NonTerminalDecl extends AbstractObjectId implements VariableDecl {
  public NonTerminalDecl(String id) {
    super(id);
  }
  public boolean isTerminal() {
    return false;
  }
  @Override
  public String toString() {
    return getId();
  }
}

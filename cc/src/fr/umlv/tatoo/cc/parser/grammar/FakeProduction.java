package fr.umlv.tatoo.cc.parser.grammar;

import java.util.List;

public class FakeProduction extends ProductionDecl {

  public FakeProduction(String id, NonTerminalDecl left, List<? extends VariableDecl> right) {
    super(id, left, right, Priority.getNoPriority(), null);
  }

}

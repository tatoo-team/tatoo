package fr.umlv.tatoo.cc.ebnf.ast;

import fr.umlv.tatoo.cc.ebnf.ast.Bindings.NonTerminalBinding;
import fr.umlv.tatoo.runtime.node.BindingSite;
import fr.umlv.tatoo.runtime.node.Node;

public interface NonTerminalBinder extends Node, BindingSite {
  public NonTerminalBinding getBinding();
}

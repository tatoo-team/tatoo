package fr.umlv.tatoo.cc.ebnf.ast;

import fr.umlv.tatoo.cc.ebnf.ast.Bindings.VariableBinding;
import fr.umlv.tatoo.runtime.node.BindingSite;
import fr.umlv.tatoo.runtime.node.Node;

public interface VariableVarAST extends Node, BindingSite {
  public String getName();
  public VariableBinding<?> getBinding();
}

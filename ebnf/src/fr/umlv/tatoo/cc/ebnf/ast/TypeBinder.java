package fr.umlv.tatoo.cc.ebnf.ast;

import fr.umlv.tatoo.cc.ebnf.ast.Bindings.TypeBinding;
import fr.umlv.tatoo.runtime.node.BindingSite;
import fr.umlv.tatoo.runtime.node.Node;

public interface TypeBinder extends Node, BindingSite {
  public TypeBinding getBinding();
}

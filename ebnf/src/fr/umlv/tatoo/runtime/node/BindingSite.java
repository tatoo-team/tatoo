package fr.umlv.tatoo.runtime.node;

import fr.umlv.tatoo.cc.ebnf.ast.TokenAST;

public interface BindingSite extends Node {
  /** 
   * @return may be null.
   */
  public TokenAST<String> getNameToken();
}

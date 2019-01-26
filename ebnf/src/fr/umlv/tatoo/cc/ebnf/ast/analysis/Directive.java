package fr.umlv.tatoo.cc.ebnf.ast.analysis;

public enum Directive {
  AUTOTOKEN,  // token are created on demand
  AUTOALIAS,  // alias are created using automaton
  AUTORULE;   // token/rule are created 
  
  public static Directive parse(String directive) {
    return Directive.valueOf(directive.toUpperCase());
  }
}

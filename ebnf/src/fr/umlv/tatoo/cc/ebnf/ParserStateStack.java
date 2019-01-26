package fr.umlv.tatoo.cc.ebnf;

import fr.umlv.tatoo.runtime.util.ReadOnlyIntStack;

public class ParserStateStack {
  private final ReadOnlyIntStack stateStack;
  public ParserStateStack(ReadOnlyIntStack stateStack) {
    this.stateStack=stateStack;
  }
  
  public ReadOnlyIntStack getStateStack() {
    return stateStack;
  }
}

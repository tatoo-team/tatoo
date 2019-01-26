package fr.umlv.tatoo.cc.lexer.lexer;

import fr.umlv.tatoo.cc.lexer.regex.AutomatonDecl;

public class TableAutomata {
  public TableAutomata(AutomatonDecl main,AutomatonDecl follow) {
    this.main=main;
    this.follow=follow;
  }
  
  public AutomatonDecl getMainAutomaton() {
    return main;
  }
  
  public AutomatonDecl getFollowAutomaton() {
    return follow;
  }
  
  private final AutomatonDecl main;
  private final AutomatonDecl follow;
}

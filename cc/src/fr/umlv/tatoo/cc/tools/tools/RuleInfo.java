package fr.umlv.tatoo.cc.tools.tools;

import fr.umlv.tatoo.cc.parser.grammar.TerminalDecl;

public class RuleInfo {
  RuleInfo(TerminalDecl terminal, boolean discard, boolean alwaysActive, boolean spawn) {
    this.terminal = terminal;
    this.discard = discard;
    this.alwaysActive = alwaysActive;
    this.spawn = spawn;
  }
  
  public boolean isDiscardable() {
    return discard;
  }
  
  public boolean isAlwaysActive() {
    return alwaysActive;
  }
  
  public boolean isSpawnable() {
    return spawn;
  }
  
  /** 
   * @return may be <code>null</code> if this rule is not associated with a terminal 
   */
  public TerminalDecl getTerminal() {
    return terminal;
  }
  
  @Override
  public String toString() {
    return "terminal:"+((terminal==null)?null:terminal.getId())+" discardable:"+discard+" alwaysActive:"+alwaysActive+" spawnable:"+spawn;
  }
  
  private final TerminalDecl terminal;
  private final boolean discard;
  private final boolean alwaysActive;
  private final boolean spawn;
}

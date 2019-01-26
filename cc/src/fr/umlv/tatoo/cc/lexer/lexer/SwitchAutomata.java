package fr.umlv.tatoo.cc.lexer.lexer;

import fr.umlv.tatoo.cc.lexer.regex.RegexSwitch;

public class SwitchAutomata  {
  public SwitchAutomata(RegexSwitch mainRegex,RegexSwitch followRegex) {
    this.mainRegex=mainRegex;
    this.followRegex=followRegex;
  }
  
  public RegexSwitch getMainRegexSwitch() {
    return mainRegex;
  }
  
  public RegexSwitch getFollowRegexSwitch() {
    return followRegex;
  }
  
  private final RegexSwitch mainRegex;
  private final RegexSwitch followRegex;
}

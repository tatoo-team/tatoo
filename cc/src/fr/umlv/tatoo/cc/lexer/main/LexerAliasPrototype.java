package fr.umlv.tatoo.cc.lexer.main;

import fr.umlv.tatoo.cc.common.main.AliasPrototype;
import fr.umlv.tatoo.cc.common.main.Unit;

public enum LexerAliasPrototype implements AliasPrototype {
  rule("RuleEnum"),
  lexerDataTable("LexerDataTable"),
  lexerSwitch("LexerSwitch");
  
  LexerAliasPrototype(String defaultTypeName) {
    this.defaultTypeName=defaultTypeName;
  }
  
  public String getDefaultTypeName() {
    return defaultTypeName;
  }
  public Unit getUnit() {
    return Unit.lexer;
  }
  
  public static AliasPrototype[] lexers() {
    return new AliasPrototype[]{rule,lexerDataTable,lexerSwitch};
  }
  
  private final String defaultTypeName;
}

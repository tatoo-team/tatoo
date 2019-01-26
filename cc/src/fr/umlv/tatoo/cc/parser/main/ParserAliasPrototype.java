package fr.umlv.tatoo.cc.parser.main;

import fr.umlv.tatoo.cc.common.main.AliasPrototype;
import fr.umlv.tatoo.cc.common.main.Unit;

public enum ParserAliasPrototype implements AliasPrototype {
  production("ProductionEnum"),
  terminal("TerminalEnum"),
  nonTerminal("NonTerminalEnum"),
  parserDataTable("ParserDataTable"),
  version("VersionEnum");
  
  ParserAliasPrototype(String defaultTypeName) {
    this.defaultTypeName=defaultTypeName;
  }
  
  public String getDefaultTypeName() {
    return defaultTypeName;
  }
  public Unit getUnit() {
    return Unit.parser;
  }
  
  private final String defaultTypeName;
  
  public static AliasPrototype[] parsers() {
    return new AliasPrototype[]{production,terminal,nonTerminal,version,parserDataTable};
  }
}

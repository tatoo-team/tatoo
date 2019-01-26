package fr.umlv.tatoo.cc.tools.main;

import fr.umlv.tatoo.cc.common.main.AliasPrototype;
import fr.umlv.tatoo.cc.common.main.Unit;
import fr.umlv.tatoo.cc.lexer.main.LexerAliasPrototype;
import fr.umlv.tatoo.cc.parser.main.ParserAliasPrototype;

public enum ToolsAliasPrototype implements AliasPrototype {
  terminalEvaluator("TerminalEvaluator"),
  grammarEvaluator("GrammarEvaluator"),
  analyzerProcessor("AnalyzerProcessor"),
  analyzers("Analyzers"),
  toolsDataTable("ToolsDataTable");
  
  ToolsAliasPrototype(String defaultTypeName) {
    this.defaultTypeName=defaultTypeName;
  }
  
  public String getDefaultTypeName() {
    return defaultTypeName;
  }
  public Unit getUnit() {
    return Unit.tools;
  }
  
  private final String defaultTypeName;
  
  public static AliasPrototype[] tools() {
    return new AliasPrototype[]{
      LexerAliasPrototype.rule,LexerAliasPrototype.lexerDataTable,
      ParserAliasPrototype.production,ParserAliasPrototype.terminal,
      ParserAliasPrototype.nonTerminal,ParserAliasPrototype.parserDataTable,
      ParserAliasPrototype.version,
      terminalEvaluator,grammarEvaluator,analyzerProcessor,
      analyzers,toolsDataTable
      };
  }
}

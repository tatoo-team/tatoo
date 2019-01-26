package fr.umlv.tatoo.samples.justintime;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.umlv.tatoo.cc.lexer.charset.encoding.Encoding;
import fr.umlv.tatoo.cc.lexer.lexer.RuleDecl;
import fr.umlv.tatoo.cc.lexer.lexer.RuleFactory;
import fr.umlv.tatoo.cc.parser.grammar.GrammarFactory;
import fr.umlv.tatoo.cc.parser.grammar.NonTerminalDecl;
import fr.umlv.tatoo.cc.parser.grammar.ProductionDecl;
import fr.umlv.tatoo.cc.parser.grammar.TerminalDecl;
import fr.umlv.tatoo.cc.parser.grammar.VersionDecl;
import fr.umlv.tatoo.cc.tools.tools.RuleInfo;
import fr.umlv.tatoo.cc.tools.tools.ToolsFactory;
import fr.umlv.tatoo.runtime.buffer.LexerBuffer;
import fr.umlv.tatoo.runtime.lexer.LexerTable;
import fr.umlv.tatoo.runtime.parser.ParserTable;
import fr.umlv.tatoo.runtime.tools.ToolsListener;
import fr.umlv.tatoo.runtime.tools.ToolsTable;
import fr.umlv.tatoo.runtime.tools.builder.Builder;
import fr.umlv.tatoo.runtime.tools.builder.LexerAndParser;

public class RuntimeAnalyzerFactory {
  public static <B extends LexerBuffer> LexerAndParser<B, TerminalDecl,NonTerminalDecl,ProductionDecl,VersionDecl> createRuntimeAnalyzer(
      RuleFactory ruleFactory, GrammarFactory grammarFactory, ToolsFactory toolsFactory,
      Encoding encoding,B buffer,NonTerminalDecl start,VersionDecl version,
      ToolsListener<RuleDecl,B,TerminalDecl,NonTerminalDecl,ProductionDecl> toolsListener) {
  
    LexerTable<RuleDecl> lexerTable =
      RuntimeLexerFactory.createLexerTable(ruleFactory,encoding);
    ParserTable<TerminalDecl, NonTerminalDecl, ProductionDecl, VersionDecl> parserTable =
      RuntimeParserFactory.createParserTable(grammarFactory);
    ToolsTable<RuleDecl, TerminalDecl> toolsTable =
      createToolsTable(toolsFactory);
    
    return Builder.analyzer(lexerTable, parserTable, toolsTable).buffer(buffer).
      listener(toolsListener).start(start).version(version).createAnalyzer();
  }
  
  public static ToolsTable<RuleDecl,TerminalDecl> createToolsTable(ToolsFactory toolsFactory) {
    HashSet<RuleDecl> spawnSet=new HashSet<RuleDecl>();
    HashSet<RuleDecl> discardSet=new HashSet<RuleDecl>();
    HashMap<RuleDecl,TerminalDecl> ruleToTerminalMap=
      new HashMap<RuleDecl, TerminalDecl>();
    for(Map.Entry<RuleDecl,RuleInfo> entry:toolsFactory.getRuleInfoMap().entrySet()) {
      RuleDecl rule=entry.getKey();
      RuleInfo ruleInfo=entry.getValue();
      if (ruleInfo.isDiscardable())
        discardSet.add(rule);
      if (ruleInfo.isSpawnable())
        spawnSet.add(rule);
      TerminalDecl terminal=ruleInfo.getTerminal();
      if (terminal!=null)
        ruleToTerminalMap.put(rule, terminal);
    }
    
    Set<? extends RuleDecl> unconditionalRuleSet=toolsFactory.getUnconditionalRuleSet();
    
    return new ToolsTable<RuleDecl,TerminalDecl>(spawnSet,
      discardSet,unconditionalRuleSet,ruleToTerminalMap);
  }
}

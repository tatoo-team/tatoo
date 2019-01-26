package fr.umlv.tatoo.cc.lexer.regex.pattern;

import java.util.Map;

import fr.umlv.tatoo.cc.common.log.Info;
import fr.umlv.tatoo.cc.lexer.charset.encoding.Encoding;
import fr.umlv.tatoo.cc.lexer.lexer.RuleDecl;
import fr.umlv.tatoo.cc.lexer.lexer.RuleFactory;
import fr.umlv.tatoo.cc.lexer.regex.Regex;
import fr.umlv.tatoo.cc.lexer.regex.pattern.lexer.RuleEnum;
import fr.umlv.tatoo.cc.lexer.regex.pattern.parser.NonTerminalEnum;
import fr.umlv.tatoo.cc.lexer.regex.pattern.parser.ProductionEnum;
import fr.umlv.tatoo.cc.lexer.regex.pattern.parser.TerminalEnum;
import fr.umlv.tatoo.cc.lexer.regex.pattern.parser.VersionEnum;
import fr.umlv.tatoo.cc.lexer.regex.pattern.tools.Analyzers;
import fr.umlv.tatoo.cc.lexer.xml.PatternRuleCompiler;
import fr.umlv.tatoo.runtime.buffer.impl.CharSequenceWrapper;
import fr.umlv.tatoo.runtime.buffer.impl.LocationTracker;
import fr.umlv.tatoo.runtime.lexer.Lexer;
import fr.umlv.tatoo.runtime.parser.Parser;
import fr.umlv.tatoo.runtime.tools.SemanticStack;
import fr.umlv.tatoo.runtime.tools.builder.LexerAndParser;
import fr.umlv.tatoo.runtime.tools.builder.Builder.AnalyzerBuilder;

public class PatternRuleCompilerImpl implements PatternRuleCompiler {
  public PatternRuleCompilerImpl(Map<String,Regex> macroes,Encoding charset) {
    grammarEvaluator=new RegexGrammarEvaluator(macroes,charset);

    RegexTerminalAttributeEvaluator terminalEvaluator=new RegexTerminalAttributeEvaluator();
    AnalyzerBuilder<RuleEnum,CharSequenceWrapper,TerminalEnum,NonTerminalEnum,ProductionEnum,VersionEnum> builder=
      Analyzers.analyzerTokenBufferBuilder((CharSequenceWrapper)null,terminalEvaluator,grammarEvaluator,new SemanticStack());
    //builder.start(NonTerminalEnum.pattern);
    //builder.version(VersionEnum.DEFAULT);
    LexerAndParser<CharSequenceWrapper,TerminalEnum,NonTerminalEnum,ProductionEnum,VersionEnum> lexerAndParser=
      builder.createAnalyzer();
    parser=lexerAndParser.getParser();
    lexer=lexerAndParser.getLexer();
  }
  
  public Regex createMacro(String pattern) {
    Info.fine("Compiling macro: ",pattern).report();
    lexer.reset(new CharSequenceWrapper(pattern, new LocationTracker()));
    parser.reset(NonTerminalEnum.macro);
    lexer.run();
    return grammarEvaluator.getMacro();
  }
  
  public RuleDecl createRule(RuleFactory lexerFactory,String id,String pattern) {
    Info.fine("Compiling rule: ",pattern).report();
    lexer.reset(new CharSequenceWrapper(pattern, new LocationTracker()));
    parser.reset(NonTerminalEnum.pattern);
    lexer.run();
    return lexerFactory.createRule(id,
        grammarEvaluator.getMain(),
        grammarEvaluator.getFollow(),
        lexerFactory.getAllRules().size(),
        grammarEvaluator.isBeginningOfLineRequired());
  }
  
  private final RegexGrammarEvaluator grammarEvaluator;
  private final Parser<TerminalEnum,NonTerminalEnum,ProductionEnum,VersionEnum> parser;
  private final Lexer<CharSequenceWrapper> lexer;
}

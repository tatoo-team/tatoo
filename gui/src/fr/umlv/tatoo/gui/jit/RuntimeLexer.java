package fr.umlv.tatoo.gui.jit;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import fr.umlv.tatoo.cc.lexer.charset.encoding.Encoding;
import fr.umlv.tatoo.cc.lexer.charset.encoding.UTF16Encoding;
import fr.umlv.tatoo.cc.lexer.lexer.RuleDecl;
import fr.umlv.tatoo.cc.lexer.lexer.RuleFactory;
import fr.umlv.tatoo.cc.lexer.regex.AutomatonDecl;
import fr.umlv.tatoo.cc.lexer.regex.Regex;
import fr.umlv.tatoo.cc.lexer.regex.RegexFactory;
import fr.umlv.tatoo.cc.lexer.xml.LexerXMLDigester;
import fr.umlv.tatoo.runtime.buffer.LexerBuffer;
import fr.umlv.tatoo.runtime.buffer.TokenBuffer;
import fr.umlv.tatoo.runtime.lexer.Lexer;
import fr.umlv.tatoo.runtime.lexer.LexerListener;
import fr.umlv.tatoo.runtime.lexer.LexerTable;
import fr.umlv.tatoo.runtime.lexer.LifecycleHandler;
import fr.umlv.tatoo.runtime.lexer.RuleActivator;
import fr.umlv.tatoo.runtime.lexer.rules.RuleData;
import fr.umlv.tatoo.runtime.regex.CharRegexTable;
import fr.umlv.tatoo.runtime.tools.builder.Builder;

public class RuntimeLexer<B extends LexerBuffer & TokenBuffer<D>,D> {
  
  private final RuleFactory ruleFactory;
  private final LexerXMLDigester digester;
  private Lexer<B> lexer;
  
  public RuntimeLexer(File xmlFile) throws ParserConfigurationException, SAXException, IOException {
    ruleFactory = new RuleFactory();
    digester = new LexerXMLDigester(ruleFactory,UTF16Encoding.getInstance());
    digester.parse(xmlFile, true);
  }
  
  public RuleFactory getRuleFactory() {
    return ruleFactory;
  }
  
  public Lexer<B> getLexer() {
    return lexer;
  }
  
  public void createRuntimeLexer(B buffer, LexerListener<RuleDecl,? super B> listener,
      RuntimeParser parser,RuleActivator<RuleDecl> activator,LifecycleHandler<B> lifeCycleHandler) {
    
    Encoding encoding=UTF16Encoding.getInstance();
    HashMap<RuleDecl,RuleData> ruleMap=new HashMap<RuleDecl,RuleData>();
    for(RuleDecl rule:ruleFactory.getAllRules())
      ruleMap.put(rule,createRuleData(rule,encoding));
    
    LexerTable<RuleDecl> lexerTable=new LexerTable<RuleDecl>(ruleMap);
    
    lexer=Builder.lexer(lexerTable).buffer(buffer).listener(listener).activator(activator).expert().
      lifecycleHandler(lifeCycleHandler).parser(parser.getParser()).createAnalyzer().getLexer();
  }
  
  public void resetLexer(B buffer) {
    lexer.reset(buffer);
  }
  
  private static RuleData createRuleData(RuleDecl rule,Encoding encoding) {
    AutomatonDecl main=RegexFactory.table(rule.getMainRegex(),encoding).createAutomaton();
    CharRegexTable mainRegexTable=new CharRegexTable(main.getFirstState(),main.getTransitions(),main.getAccepts());
    
    Regex followRegex=rule.getFollowRegex();
    CharRegexTable followRegexTable;
    if (followRegex!=null) {
      AutomatonDecl follow=RegexFactory.table(followRegex,encoding).createAutomaton();
      followRegexTable=new CharRegexTable(follow.getFirstState(),follow.getTransitions(),follow.getAccepts());
    } else {
      followRegexTable=null;
    }
    return new RuleData(mainRegexTable,followRegexTable,rule.getPriority(),rule.isBeginningOfLineRequired());
  }
}

package fr.umlv.tatoo.samples.justintime;

import java.util.HashMap;

import fr.umlv.tatoo.cc.lexer.charset.encoding.Encoding;
import fr.umlv.tatoo.cc.lexer.lexer.RuleDecl;
import fr.umlv.tatoo.cc.lexer.lexer.RuleFactory;
import fr.umlv.tatoo.cc.lexer.regex.AutomatonDecl;
import fr.umlv.tatoo.cc.lexer.regex.Regex;
import fr.umlv.tatoo.cc.lexer.regex.RegexFactory;
import fr.umlv.tatoo.runtime.buffer.LexerBuffer;
import fr.umlv.tatoo.runtime.lexer.Lexer;
import fr.umlv.tatoo.runtime.lexer.LexerListener;
import fr.umlv.tatoo.runtime.lexer.LexerTable;
import fr.umlv.tatoo.runtime.lexer.rules.RuleData;
import fr.umlv.tatoo.runtime.regex.CharRegexTable;
import fr.umlv.tatoo.runtime.tools.builder.Builder;

public class RuntimeLexerFactory {
  public static <B extends LexerBuffer> Lexer<B> createRuntimeLexer(RuleFactory ruleFactory,Encoding encoding,B buffer,LexerListener<RuleDecl,? super B> listener) {
    LexerTable<RuleDecl> lexerTable = createLexerTable(ruleFactory,encoding);
    return Builder.lexer(lexerTable).buffer(buffer).listener(listener).createLexer();
  }
  
  public static LexerTable<RuleDecl> createLexerTable(RuleFactory ruleFactory,Encoding encoding) {
    HashMap<RuleDecl,RuleData> ruleMap=new HashMap<RuleDecl,RuleData>();
    for(RuleDecl rule:ruleFactory.getAllRules())
      ruleMap.put(rule,createRuleData(rule,encoding));
    
    return new LexerTable<RuleDecl>(ruleMap);
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

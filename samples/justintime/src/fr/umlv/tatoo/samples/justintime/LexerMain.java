package fr.umlv.tatoo.samples.justintime;

import java.io.InputStreamReader;
import java.util.Collections;

import fr.umlv.tatoo.cc.lexer.charset.encoding.Encoding;
import fr.umlv.tatoo.cc.lexer.lexer.RuleDecl;
import fr.umlv.tatoo.cc.lexer.lexer.RuleFactory;
import fr.umlv.tatoo.cc.lexer.main.LexerType;
import fr.umlv.tatoo.cc.lexer.regex.Regex;
import fr.umlv.tatoo.cc.lexer.regex.pattern.PatternRuleCompilerImpl;
import fr.umlv.tatoo.runtime.buffer.impl.LocationTracker;
import fr.umlv.tatoo.runtime.buffer.impl.ReaderWrapper;
import fr.umlv.tatoo.runtime.lexer.LexerListener;
import fr.umlv.tatoo.runtime.lexer.SimpleLexer;

public class LexerMain {
  public static void main(String[] args) {
    RuleFactory ruleFactory=new RuleFactory();
    
    Encoding charset = LexerType.unicode.getEncoding();
    PatternRuleCompilerImpl ruleCompiler=
      new PatternRuleCompilerImpl(Collections.<String,Regex>emptyMap(),charset);
    
    ruleCompiler.createRule(ruleFactory,"value","[0-9]+");
    ruleCompiler.createRule(ruleFactory,"space"," |\t");
    ruleCompiler.createRule(ruleFactory,"eoln","\n");
    
    ReaderWrapper buffer=new ReaderWrapper(new InputStreamReader(System.in),new LocationTracker());
    
    LexerListener<RuleDecl,ReaderWrapper> listener=
      new LexerListener<RuleDecl,ReaderWrapper>() {
        public void ruleVerified(RuleDecl rule, int lastTokenLength, ReaderWrapper buffer) throws RuntimeException {
          System.out.println(rule.getId()+" "+buffer.view());
          buffer.discard();
        }
    };
    
    SimpleLexer lexer=RuntimeLexerFactory.createRuntimeLexer(ruleFactory,charset,buffer,listener);
    lexer.run();
  } 
}

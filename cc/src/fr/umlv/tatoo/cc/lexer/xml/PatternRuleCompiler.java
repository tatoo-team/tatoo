package fr.umlv.tatoo.cc.lexer.xml;

import fr.umlv.tatoo.cc.lexer.lexer.RuleFactory;
import fr.umlv.tatoo.cc.lexer.lexer.RuleDecl;
import fr.umlv.tatoo.cc.lexer.regex.Regex;

public interface PatternRuleCompiler {
  public Regex createMacro(String pattern);
  
  public RuleDecl createRule(RuleFactory lexerFactory,String id,String pattern);
}

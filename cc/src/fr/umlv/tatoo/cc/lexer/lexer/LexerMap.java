package fr.umlv.tatoo.cc.lexer.lexer;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import fr.umlv.tatoo.cc.lexer.charset.encoding.Encoding;
import fr.umlv.tatoo.cc.lexer.regex.AutomatonDecl;
import fr.umlv.tatoo.cc.lexer.regex.Regex;
import fr.umlv.tatoo.cc.lexer.regex.RegexFactory;
import fr.umlv.tatoo.cc.lexer.regex.RegexSwitch;

public abstract class LexerMap<A> {
  private final LinkedHashMap<RuleDecl,A> automataMap;
  
  LexerMap(LinkedHashMap<RuleDecl,A> automataMap) {
    this.automataMap=automataMap;
  }
  
  public Map<RuleDecl,A> getAutomataMap() {
    return automataMap; 
  }
  
  public static class Table extends LexerMap<TableAutomata> {
    Table(LinkedHashMap<RuleDecl,TableAutomata> automataMap) {
      super(automataMap);
    }

    public static Table create(Collection<RuleDecl> rules,Encoding encoding) {
      LinkedHashMap<RuleDecl,TableAutomata> automataMap=
        new LinkedHashMap<RuleDecl,TableAutomata>();
      for(RuleDecl rule:rules) {
        AutomatonDecl main=RegexFactory.table(rule.getMainRegex(),encoding).createAutomaton();
        Regex followRegex=rule.getFollowRegex();
        AutomatonDecl follow=(followRegex==null)?null:
          RegexFactory.table(rule.getMainRegex(),encoding).createAutomaton();
        
        TableAutomata tableAutomata=new TableAutomata(main,follow);
        automataMap.put(rule,tableAutomata);
      }
      return new Table(automataMap);
    }
  }
  
  public static class Switch extends LexerMap<SwitchAutomata> {
    Switch(LinkedHashMap<RuleDecl,SwitchAutomata> automataMap) {
      super(automataMap);
    }

    public static Switch create(Collection<RuleDecl> rules,Encoding encoding) {
      LinkedHashMap<RuleDecl,SwitchAutomata> automataMap=
        new LinkedHashMap<RuleDecl,SwitchAutomata>();
      for(RuleDecl rule:rules) {
        RegexSwitch main=new RegexSwitch(
          RegexFactory.table(rule.getMainRegex(),encoding).createAutomaton());
        Regex followRegex=rule.getFollowRegex();
        RegexSwitch follow=(followRegex==null)?null:new RegexSwitch(
          RegexFactory.table(followRegex,encoding).createAutomaton());
        
        SwitchAutomata switchAutomata=new SwitchAutomata(main,follow);
        automataMap.put(rule,switchAutomata);
      }
      return new Switch(automataMap);
    }
  }
}

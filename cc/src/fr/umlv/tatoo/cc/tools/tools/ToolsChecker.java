package fr.umlv.tatoo.cc.tools.tools;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import fr.umlv.tatoo.cc.lexer.lexer.RuleDecl;
import fr.umlv.tatoo.cc.lexer.lexer.RuleFactory;
import fr.umlv.tatoo.cc.parser.grammar.GrammarFactory;
import fr.umlv.tatoo.cc.parser.grammar.TerminalDecl;

public class ToolsChecker {
  private ToolsChecker() {
    // avoid instantiation
  }
  
  public static Set<RuleDecl> checkUndefinedRules(RuleFactory ruleFactory,Map<RuleDecl,RuleInfo> ruleInfoMap) {
    HashSet<RuleDecl> rules=new HashSet<RuleDecl>(ruleFactory.getAllRules());
    rules.removeAll(ruleInfoMap.keySet());
    return rules;
  }
  
  public static Set<TerminalDecl> checkUnspawnTerminals(GrammarFactory grammarFactory,Map<RuleDecl,RuleInfo> ruleInfoMap) {
    HashSet<TerminalDecl> spawnedTerminals=new HashSet<TerminalDecl>(grammarFactory.getAllTerminals());
    spawnedTerminals.remove(grammarFactory.getEof());
    spawnedTerminals.remove(grammarFactory.getError());
    
    // remove branch terminals
    for(Iterator<TerminalDecl> it=spawnedTerminals.iterator();it.hasNext();) {
      TerminalDecl terminal=it.next();
      if (terminal.isBranching())
        it.remove();
    }
    
    for(RuleInfo info:ruleInfoMap.values()) {
      TerminalDecl terminal = info.getTerminal();
      if (terminal!=null) 
        spawnedTerminals.remove(terminal);
    }
    return spawnedTerminals;
  }
}

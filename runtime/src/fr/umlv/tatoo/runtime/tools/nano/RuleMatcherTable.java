package fr.umlv.tatoo.runtime.tools.nano;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;


import fr.umlv.tatoo.runtime.lexer.LexerTable;
import fr.umlv.tatoo.runtime.lexer.nano.BranchRegexRuleMatcher;
import fr.umlv.tatoo.runtime.lexer.nano.RuleMatcher;
import fr.umlv.tatoo.runtime.lexer.rules.RuleData;
import fr.umlv.tatoo.runtime.parser.Action;
import fr.umlv.tatoo.runtime.parser.ParserTable;
import fr.umlv.tatoo.runtime.tools.ToolsTable;

public class RuleMatcherTable<V> {
  /* FIXME Remi
  private final Map<V, BranchRegexRuleMatcher[][]> ruleMatcherMap;
  
  public <R, T> RuleMatcherTable(LexerTable<R> lexerTable, ParserTable<T, ?, ?, V> parserTable, ToolsTable<R, T> toolsTable, Map<V, BranchRegexRuleMatcher[]> ruleMatcherMap, V[] versions) {
    int stateNb=parserTable.getStateNb();
    
    @SuppressWarnings("unchecked")
    ArrayList<BranchRegexRuleMatcher>[] ruleMatcherListArray =
      (ArrayList<BranchRegexRuleMatcher>[])new ArrayList<?>[stateNb];
    for (int i = 0; i < stateNb; i++) {
      ruleMatcherListArray[i] = new ArrayList<BranchRegexRuleMatcher>();
    }
    
    for(Entry<R, ? extends T> entry:toolsTable.getRuleToTerminalMap().entrySet()) {
      R rule = entry.getKey();
      RuleData ruleData = lexerTable.getRuleDataMap().get(rule);
      
      T terminal= entry.getValue();
      Action<T,?,V>[] actions = parserTable.getActions(terminal);
      
      for (int i = 0; i < stateNb; i++) {
        Action<T,?,V> action = actions[i];
        if (!action.isError(version)) {
          
          BranchRegexRuleMatcher ruleMatcher = new BranchRegexRuleMatcher(ruleData);
          ruleMatcherListArray[i].add(ruleMatcher);
        }
      }
    }
    
    ruleMatcherMap.put(version, ruleMatcherListArray.toArray(new BranchRegexRuleMatcher[ruleMatcherListArray.size()]);
  }
  
  public BranchRegexRuleMatcher[] getRuleMatcher(int state, V version) {
    return ruleMatcherMap.get(version)[state];
  }*/
}

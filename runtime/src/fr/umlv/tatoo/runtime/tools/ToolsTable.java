package fr.umlv.tatoo.runtime.tools;

import java.util.Map;
import java.util.Set;

public class ToolsTable<R,T> {
  private final Set<? extends R> spawnSet;
  private final Set<? extends R> discardSet;
  private final Set<? extends R> unconditionalRuleSet;
  private final Map<R,? extends T> ruleToTerminalMap;
  
  public ToolsTable(Set<? extends R> spawnSet,
      Set<? extends R> discardSet,
      Set<? extends R> unconditionalRuleSet,
      Map<R,? extends T> ruleToTerminalMap) {
    this.spawnSet=spawnSet;
    this.discardSet=discardSet;
    this.unconditionalRuleSet=unconditionalRuleSet;
    this.ruleToTerminalMap=ruleToTerminalMap;
  }
  
  public Set<? extends R> getSpawnSet() {
    return spawnSet;
  }
  public Set<? extends R> getDiscardSet() {
    return discardSet;
  }
  public Set<? extends R> getUnconditionalRuleSet() {
    return unconditionalRuleSet;
  }
  public Map<R,? extends T> getRuleToTerminalMap() {
    return ruleToTerminalMap;
  }
}

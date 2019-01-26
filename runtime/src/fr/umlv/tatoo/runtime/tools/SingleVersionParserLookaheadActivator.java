/*
 * Created on 13 juil. 2005
 */
package fr.umlv.tatoo.runtime.tools;

import java.lang.reflect.Array;
import java.util.Map;
import java.util.Set;

import fr.umlv.tatoo.runtime.lexer.RuleActivator;
import fr.umlv.tatoo.runtime.parser.LookaheadMap;
import fr.umlv.tatoo.runtime.parser.Parser;
import fr.umlv.tatoo.runtime.parser.ParserTable;
import fr.umlv.tatoo.runtime.parser.SimpleParser;
import fr.umlv.tatoo.runtime.util.ReadOnlyIntStack;
import fr.umlv.tatoo.runtime.util.Utils;

/**
 * 
 * @see fr.umlv.tatoo.runtime.tools.ParserLookaheadActivator
 */
public class SingleVersionParserLookaheadActivator<R> implements RuleActivator<R> {
  private final ReadOnlyIntStack stateStack;
  private final R[][] activatorRulesArray;
   
  public SingleVersionParserLookaheadActivator(SimpleParser<?> parser,
      R[][] activatorRulesArray) {
    
    this.activatorRulesArray=activatorRulesArray;
    this.stateStack=parser.getStateStack();
  }

  public R[] activeRules() {
    return activatorRulesArray[stateStack.last()];
  }
  
  public static <R,T,V> SingleVersionParserLookaheadActivator<R> create(Parser<T,?,?,V> parser,ToolsTable<R,T> toolsTable,Class<?> ruleClass) {
    ParserTable<T, ?, ?, V> parserTable=parser.getTable();
    Class<?> terminalClass=parserTable.getEof().getClass();
    R[][] activatorRulesArray = SingleVersionParserLookaheadActivator.activatorRulesArray(parserTable.getStateNb(),
        parser.getVersion(),
        parser.getLookaheadMap(),
        ruleClass,
        terminalClass,
        toolsTable.getUnconditionalRuleSet(),
        toolsTable.getRuleToTerminalMap());
    return new SingleVersionParserLookaheadActivator<R>(parser, activatorRulesArray);
  }
  
  public static <R,T,V> R[][] activatorRulesArray(
      int stateNb,
      V version,
      LookaheadMap<? extends T,V> lookaheadMap,
      Class<?> ruleClass, 
      Class<?> terminalClass,
      Set<? extends R> unconditionalRules,
      Map<R,? extends T> ruleToTerminalMap) {
    
    if (lookaheadMap==null)
      throw new IllegalArgumentException("lookahead map must be non null");
    
    Map<T,? extends Set<? extends R>> terminalRulesMap=Utils.inverse(ruleToTerminalMap, ruleClass, terminalClass);
    
    @SuppressWarnings("unchecked") R[] emptyArray = (R[])Array.newInstance(ruleClass, 0);
    @SuppressWarnings("unchecked") R[][] rulesArray = (R[][])Array.newInstance(emptyArray.getClass(),stateNb);
    for(int state=0;state<stateNb;state++) {
      Set<R> rules=Utils.createSet(ruleClass);
      rules.addAll(unconditionalRules);

      for(T terminal:lookaheadMap.getLookahead(state,version)) {
        Set<? extends R> toAdd = terminalRulesMap.get(terminal);
        if (toAdd!=null)
          rules.addAll(toAdd);
      }

      rulesArray[state]=rules.toArray(emptyArray);
    }
    return rulesArray;
  }
}

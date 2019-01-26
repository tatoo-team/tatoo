/*
 * Created on 13 juil. 2005
 */
package fr.umlv.tatoo.runtime.tools;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import fr.umlv.tatoo.runtime.lexer.RuleActivator;
import fr.umlv.tatoo.runtime.parser.LookaheadMap;
import fr.umlv.tatoo.runtime.parser.Parser;
import fr.umlv.tatoo.runtime.parser.ParserTable;
import fr.umlv.tatoo.runtime.util.ReadOnlyIntStack;
import fr.umlv.tatoo.runtime.util.Utils;

/** A {@link RuleActivator rule activator} that use parser lookaheads. 
 *  For each parser state, the implementation used the set of possible terminal
 *  given by the parser to find the set corresponding rules to activate. 
 * 
 *  This implementation supposes that rules and terminals
 *  are specified using enums. In order to improve speed
 *  and memory usage, this activator internally
 *  uses {@link EnumSet} and {@link EnumMap}.
 *  
 *  This implementation pre-calculate all possibles rule set
 *  for all parser state.
 * 
 * @param <R> type of rule (must be an enum).
 * @param <T> type of terminal (must be an enum).
 * @param <V> type of version (must be an enum).
 * 
 * @author Remi Forax
 */
//TODO Remi try to see if some arrays can be shared
public class ParserLookaheadActivator<R,T,V> implements RuleActivator<R> {
  private final ReadOnlyIntStack stateStack;
  private final Map<? super V,R[][]> rulesArrayMap;
  private final Parser<T,?,?,V> parser;
  
  public ParserLookaheadActivator(Parser<T,?,?,V> parser, Map<? super V,R[][]> rulesArrayMap) {    
    this.parser = parser;
    this.stateStack = parser.getStateStack();
    this.rulesArrayMap = rulesArrayMap;
  }

  public R[] activeRules() {
    return rulesArrayMap.get(parser.getVersion())[stateStack.last()];
  }
  
  public static <R,T,V> ParserLookaheadActivator<R,T,V> create(Parser<T,?,?,V> parser,ToolsTable<R,T> toolsTable,Class<?> ruleClass) {
    Class<?> versionClass=parser.getVersion().getClass();
    ParserTable<T, ?, ?, V> parserTable=parser.getTable();
    Class<?> terminalClass=parserTable.getEof().getClass();
    Map<T,? extends Set<? extends R>> terminalRulesMap=Utils.inverse(toolsTable.getRuleToTerminalMap(), ruleClass, terminalClass);
    Map<? super V, R[][]> activatorRulesArray = ParserLookaheadActivator.activatorRulesArray(parserTable.getStateNb(),
        parserTable.getVersions(),
        parser.getLookaheadMap(),
        versionClass,
        ruleClass,
        toolsTable.getUnconditionalRuleSet(),
        terminalRulesMap);
    return new ParserLookaheadActivator<R,T,V>(parser, activatorRulesArray);
  }
  
  public static <R,T,V> Map<V, R[][]> activatorRulesArray(
      int stateNb,
      Collection<? extends V> versions,
      LookaheadMap<? extends T,V> lookaheadMap,
      Class<?> versionClass,
      Class<?> ruleClass, 
      Set<? extends R> unconditionalRules,
      Map<T,? extends Set<? extends R>> terminalRulesMap) {
    
    if (lookaheadMap==null)
      throw new IllegalArgumentException("lookahead map must be non null");
    
    Map<V,R[][]> container=Utils.createMap(versionClass);
    @SuppressWarnings("unchecked") R[] emptyArray=
      (R[])Array.newInstance(ruleClass,0);
    
    for(V version:versions) {
      @SuppressWarnings("unchecked") R[][] rulesArray = (R[][])Array.newInstance(emptyArray.getClass(), stateNb);
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
      container.put(version,rulesArray);
    }
    
    return container;
  }
}

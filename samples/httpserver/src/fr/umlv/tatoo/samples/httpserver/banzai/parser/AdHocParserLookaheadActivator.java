/*
 * Created on 13 juil. 2005
 */
package fr.umlv.tatoo.samples.httpserver.banzai.parser;

import java.lang.reflect.Array;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import fr.umlv.tatoo.runtime.lexer.RuleActivator;
import fr.umlv.tatoo.runtime.parser.LookaheadMap;
import fr.umlv.tatoo.runtime.parser.SimpleParser;
import fr.umlv.tatoo.runtime.util.ReadOnlyIntStack;

/**
 * 
 * @see fr.umlv.tatoo.runtime.tools.ParserLookaheadActivator
 */
public class AdHocParserLookaheadActivator<R extends Enum<R>> implements RuleActivator<R> {
  private final ReadOnlyIntStack stateStack;
  private final R[][] activatorRulesArray;
   
  public AdHocParserLookaheadActivator(SimpleParser<?> parser,
      R[][] activatorRulesArray) {
    
    this.activatorRulesArray=activatorRulesArray;
    this.stateStack=parser.getStateStack();
  }

  public R[] activeRules() {
    return activatorRulesArray[stateStack.last()];
  }
  
  public static <R extends Enum<R>,T extends Enum<T>,V extends Enum<V>> R[][] activatorRulesArray(
      int stateNb,
      V version,
      LookaheadMap<T,V> lookaheadMap,
      Class<R> ruleClass, 
      Set<? extends R> unconditionalRules,
      Map<? super T,? extends Set<? extends R>> terminalRulesMap) {
    @SuppressWarnings("unchecked") R[] emptyArray = (R[])Array.newInstance(ruleClass, 0);
    @SuppressWarnings("unchecked") R[][] rulesArray = (R[][])Array.newInstance(emptyArray.getClass(),stateNb);
    for(int state=0;state<stateNb;state++) {
      EnumSet<R> rules=EnumSet.noneOf(ruleClass);
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

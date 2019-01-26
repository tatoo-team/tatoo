/*
 * Created on 18 juin 2003
 */
package fr.umlv.tatoo.runtime.lexer.nano;

import java.util.ArrayList;

import fr.umlv.tatoo.runtime.buffer.LexerBuffer;
import fr.umlv.tatoo.runtime.lexer.rules.ProcessReturn;

/**
 * 
 * @author Julien
 */
public class MultipleRuleProcessor<B extends LexerBuffer> implements RuleProcessor {
  private boolean finished;
  
  //TODO use array instead of list
  // store one or more rule matcher,
  // if one:   single = rule matcher
  //           ruleMatcherList is empty
  // if more:  single = null
  //           ruleMatcherList is not empty 
  private RuleMatcher single;
  private final ArrayList<RuleMatcher> ruleMatcherList =
    new ArrayList<RuleMatcher>();
  
  // used to revert to original rule matcher list in #reset()
  private final ArrayList<RuleMatcher> copyOfRuleMatcherList =
    new ArrayList<RuleMatcher>();
  
  private int matchableActionSize;
  private int end;
  
  /**
   * Creates a new rule processor.
   * 
   */
  public MultipleRuleProcessor() {
    // do nothing
  }

  /** Reset the action processor.
   */
  public void reset() {
    finished = false;
    ruleMatcherList.clear();
    ruleMatcherList.addAll(copyOfRuleMatcherList);
  }

  /* (non-Javadoc)
   * @see fr.umlv.tatoo.runtime.lexer.nano.AbstractRuleProcessor#isProcessFinished()
   */
  public boolean isProcessFinished() {
    return finished;
  }
  
  /* (non-Javadoc)
   * @see fr.umlv.tatoo.runtime.lexer.nano.AbstractRuleProcessor#initProcess()
   */
  public void initProcess() {
    matchableActionSize = end = 0;
    single = null;
    ruleMatcherList.clear();
    copyOfRuleMatcherList.clear();
  }
  
  /*
  public void registerRuleDatas(boolean newLine, RuleDataProvider ruleDataProvider, RuleData... ruleDatas) {
    RuleAction[] actions = this.actions;
    int slot = matchableActionSize;
    int ruleSize = ruleDatas.length;
    for (int i=0; i<ruleSize; i++) {
      RuleData ruleData = ruleDatas[i];
      if (!newLine && ruleData.beginningOfLineRequired())
        continue;
      // System.out.println(r+" added");
      actions[slot++].reset(ruleData, i, ruleDataProvider);
    }
    matchableActionSize = slot;
  }*/
  
  public void registerRuleMatcher(RuleMatcher ruleMatcher) {
    if (ruleMatcher == null)
      return;
    finished = false;
    if (ruleMatcherList.isEmpty()) {
      if (single == null) {
        single = ruleMatcher;
        return;
      }
      ruleMatcherList.add(single);
      copyOfRuleMatcherList.add(single);
    }
    ruleMatcherList.add(ruleMatcher);
    copyOfRuleMatcherList.add(ruleMatcher);
    end = ++matchableActionSize;
  }
  
  /* (non-Javadoc)
   * @see fr.umlv.tatoo.runtime.lexer.nano.AbstractRuleProcessor#step(fr.umlv.tatoo.runtime.buffer.LexerBuffer)
   */
  public ProcessReturn step(LexerBuffer buffer) {
    if (single != null) {
      return singleStep(buffer, single);
    }
    
    int matchableSize = matchableActionSize;
    int end = this.end;
    ArrayList<RuleMatcher> ruleMatcherList = this.ruleMatcherList;
    while(buffer.hasRemaining()) {
      int step = buffer.next();
      
      for(int i=0; i<matchableSize; ) {
          RuleMatcher ruleMatcher = ruleMatcherList.get(i);
          switch(ruleMatcher.step(step)) {
          case ERROR: // substitute current with a valid matcher
            matchableSize--;
            if (matchableSize != 0 && i != matchableSize) {
              ruleMatcherList.set(i, ruleMatcherList.get(matchableSize));
            }
            continue;
          case TOKEN: // substitute current with a valid matcher
                      // and evacuate rule matcher at the end
            matchableSize--;
            if (matchableSize != 0 && i != matchableSize) {
              ruleMatcherList.set(i, ruleMatcherList.get(matchableSize));
            }
            ruleMatcherList.set(--end, ruleMatcher);
            continue;
          default:
          }
          i++;
      }
      
      this.matchableActionSize = matchableSize;
      this.end = end;
      
      if (matchableSize == 0) {
        finished = true;
        if (end == ruleMatcherList.size())
          return ProcessReturn.ERROR;
        return ProcessReturn.TOKEN;
      }
      
    }
    return ProcessReturn.MORE;
  }
  
  private ProcessReturn singleStep(LexerBuffer buffer, RuleMatcher single) {
    while(buffer.hasRemaining()) {
      int step = buffer.next();
      switch(single.step(step)) {
      case ERROR:
        finished = true;
        return ProcessReturn.ERROR;
      case TOKEN:
        finished = true;
        return ProcessReturn.TOKEN;
      default:
      }
    }
    return ProcessReturn.MORE;
  }
  
  
  
  /* (non-Javadoc)
   * @see fr.umlv.tatoo.runtime.lexer.nano.AbstractRuleProcessor#stepClose()
   */
  public ProcessReturn stepClose() {
    if (isProcessFinished())
      return ProcessReturn.NOTHING;
    
    int matchableSize = matchableActionSize;
    int end = this.end;
    ArrayList<RuleMatcher> ruleMatcherList = this.ruleMatcherList;
    for(int i=0; i<matchableSize; i++) {
      RuleMatcher ruleMatcher = ruleMatcherList.get(i);
      if (ruleMatcher.lastMatch() != -1) {
        end--;
        if (i != end) {
          ruleMatcherList.set(end, ruleMatcher); 
        }
      }
    }
    
    finished = true;
    matchableActionSize = 0; // unnecessary
    
    if (end == ruleMatcherList.size())
      return ProcessReturn.ERROR;
    return ProcessReturn.TOKEN;
  }
  
  
  
  /**
   * Returns the rule that matched, upon successful analyze. This method
   * must be called only after method {@link #step} or {@link #stepClose} has returned
   * {@link ProcessReturn#TOKEN}
   * @return the rule that matched
   */
  public RuleMatcher winningRuleAction(Policy policy) {
    int end = this.end;
    int size = ruleMatcherList.size();
    if (!isProcessFinished() || end == size)
      throw new IllegalStateException("This method must be called only after a succesfull match");
    
    // if there is only one matcher
    if (end == size - 1)
      return ruleMatcherList.get(end);
    
    return policy.findBestMatcher(ruleMatcherList.subList(end, size));
  }
}

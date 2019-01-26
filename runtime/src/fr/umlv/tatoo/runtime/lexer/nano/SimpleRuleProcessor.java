/*
 * Created on 18 juin 2003
 */
package fr.umlv.tatoo.runtime.lexer.nano;

import fr.umlv.tatoo.runtime.buffer.LexerBuffer;
import fr.umlv.tatoo.runtime.lexer.rules.ProcessReturn;
import fr.umlv.tatoo.runtime.lexer.rules.RuleData;

/**
 * 
 * @author Julien
 */
public class SimpleRuleProcessor<B extends LexerBuffer> implements RuleProcessor {
  private int maxVal;
  private int minPriority;
  private RegexRuleAction winningAction;
  private int matchableActionSize;
  private final RegexRuleAction[] actions;
  
  /**
   * Creates a new rule processor.
   * 
   */
  public SimpleRuleProcessor(int maxRuleActionSize) {
    RegexRuleAction[] actions = new RegexRuleAction[maxRuleActionSize];
    for(int i=0; i<maxRuleActionSize; i++) {
      actions[i] = new RegexRuleAction();
    }
    this.actions = actions;
    this.matchableActionSize = 0;
  }

  /** Reset the action processor.
   */
  public void reset() {
    // actions array cells are not nulled because
    // rules are accessible by lexerTable
    matchableActionSize = 0;
  }

  /* (non-Javadoc)
   * @see fr.umlv.tatoo.runtime.lexer.nano.AbstractRuleProcessor#isProcessFinished()
   */
  public boolean isProcessFinished() {
    return matchableActionSize == 0;
  }
  
  /* (non-Javadoc)
   * @see fr.umlv.tatoo.runtime.lexer.nano.AbstractRuleProcessor#initProcess()
   */
  public void initProcess() {
    maxVal = -1;
    minPriority = Integer.MAX_VALUE;
    //matchableActionSize = 0;
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
  
  public void registerRuleData(RuleData ruleData, Object attachment) {
    actions[matchableActionSize++].reuse(ruleData, attachment);
  }
  
  /* (non-Javadoc)
   * @see fr.umlv.tatoo.runtime.lexer.nano.AbstractRuleProcessor#step(fr.umlv.tatoo.runtime.buffer.LexerBuffer)
   */
  public ProcessReturn step(LexerBuffer buffer) {
    while(buffer.hasRemaining()) {
      int step = buffer.next();
      int size = stepActions(step);      
      
      if (size == 0) {
        if (maxVal == -1) {
          return ProcessReturn.ERROR;
        } else {
          return ProcessReturn.TOKEN;
        }
      }
    }
    return ProcessReturn.MORE;
  }
  
  private void updateWinner(RegexRuleAction action) {
    int lastMatch = action.lastMatch();
    if (lastMatch == -1)
      return;
    int priority = action.getPriority();
    if (lastMatch > maxVal || (lastMatch == maxVal && priority < minPriority)) {
      maxVal = lastMatch;
      minPriority = priority;
      winningAction = action;
    }
  }
  
  private int stepActions(int step) {
    int size = matchableActionSize;
    
    RegexRuleAction[] actions = this.actions;
    for (int i = 0; i < size;) {
      RegexRuleAction action = actions[i];
      if (action.step(step) != ProcessReturn.MORE) {
        updateWinner(action);
        int max = --size;
        if (i != max) {
          actions[i] = actions[max];
          actions[max] = action;
        }
      } else {
        i++;
      }
    }
    
    return matchableActionSize = size;
  }
  
  /* (non-Javadoc)
   * @see fr.umlv.tatoo.runtime.lexer.nano.AbstractRuleProcessor#stepClose()
   */
  public ProcessReturn stepClose() {
    if (isProcessFinished())
      return ProcessReturn.NOTHING;
    int size = matchableActionSize;
    RegexRuleAction[] actions = this.actions;
    for (int i = 0; i < size;i++) {
      updateWinner(actions[i]);
    }
    matchableActionSize = 0;
    if (maxVal == -1) {
        return ProcessReturn.ERROR;
    } else {
        return ProcessReturn.TOKEN;
    }
  }
  
  
  
  /**
   * Returns the rule that matched, upon successful analyze. This method
   * must be called only after method {@link #step} or {@link #stepClose} has returned
   * {@link ProcessReturn#TOKEN}
   * @return the rule that matched
   */
  public RegexRuleAction winningRuleAction() {
    if (!isProcessFinished() || maxVal == -1)
      throw new IllegalStateException("This method must be called only after a succesfull match");
    return winningAction;
  }

  /**
   * Returns the token length, upon successful analyze. This method
   * must be called only after method {@link #step} or {@link #stepClose} has returned
   * {@link ProcessReturn#TOKEN}
   * @return the token length
   */
  //public int tokenLength() {
  //  if (!isProcessFinished() || maxVal == -1)
  //    throw new IllegalStateException("This method must be called only after a succesfull match");
  //  return maxVal;
  //}
}

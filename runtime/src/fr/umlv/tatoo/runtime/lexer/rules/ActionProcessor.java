/*
 * Created on 18 juin 2003
 */
package fr.umlv.tatoo.runtime.lexer.rules;

import java.util.Map;

import fr.umlv.tatoo.runtime.buffer.LexerBuffer;
import fr.umlv.tatoo.runtime.lexer.LexerTable;

/**
 * @param <R> type of rules.
 * 
 * @author Julien
 */
public class ActionProcessor<R> {
  
  /**
   * Creates a new lexer process.
   * 
   * @param lexerTable the rule tables for this process
   */
  @SuppressWarnings("unchecked")
  public ActionProcessor(LexerTable<R> lexerTable) {
    int length=lexerTable.getRuleDataMap().size();
    Action<R>[] actions=(Action<R>[])new Action<?>[length];
    for(int i=0;i<length;i++)
      actions[i]=new Action<R>();
    this.actions=actions;
    this.matchableActionSize=0;
    this.lexerTable=lexerTable;
  }

  /** Reset the action processor.
   */
  public void reset() {
    // actions array cells are not nulled because
    // rules are accessible by lexerTable
    matchableActionSize=0;
  }

  private void initActions(boolean newLine, R[] rules) {
    maxVal = -1;
    minPriority = Integer.MAX_VALUE;
    Action<R>[] actions=this.actions;
    Map<R,RuleData> ruleDataMap = lexerTable.getRuleDataMap();
    int ruleSize=rules.length;
    int slot = 0;
    for (int i=0;i<ruleSize;i++) {
      R r = rules[i];
      RuleData ruleData = ruleDataMap.get(r);
      if (!newLine && ruleData.beginningOfLineRequired())
        continue;
      // System.out.println(r+" added");
      actions[slot++].reset(r,ruleData);
    }
    matchableActionSize = slot;
  }
  
  private void updateWinner(Action<R> action) {
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
    int size=matchableActionSize;
    
    Action<R>[] actions=this.actions;
    for (int i = 0; i < size;) {
      Action<R> action = actions[i];
      if (!action.step(step)) {
        updateWinner(action);
        int max=--size;
        if (i != max) {
          actions[i] = actions[max];
          actions[max] = action;
        }
      } else {
        i++;
      }
    }
    
    return matchableActionSize=size;
  }

  /**
   * Processes available characters from the input stream.
   * @param buffer the lexer buffer.
   * @return {@link ProcessReturn#MORE MORE} if more characters are needed to perform the match,
   * {@link ProcessReturn#ERROR ERROR}, if an error occurred
   * and {@link ProcessReturn#TOKEN TOKEN} if a new token is spawned.
   */
  public ProcessReturn step(LexerBuffer buffer,R... rules) {
    if (processFinished()) {
      initActions(buffer.previousWasNewLine(),rules);
    }
    while(buffer.hasRemaining()) {
      int step = buffer.next();
      int size=stepActions(step);      
      
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
  
  /**
   * This method is called after {@link #step} has returned
   * {@link ProcessReturn#MORE MORE} and end-of-file is reached
   * @return {@link ProcessReturn#NOTHING NOTHING} if no new token is available,
   * {@link ProcessReturn#ERROR ERROR}, if an error occurred
   * and {@link ProcessReturn#TOKEN TOKEN} if a new token is spawned. In this case;
   * if characters are back available in the buffer, {@link #step} must be
   * called again until it returns {@link ProcessReturn#MORE MORE}, and then
   * this method has to be called again
   */
  public ProcessReturn stepClose() {
    if (processFinished())
      return ProcessReturn.NOTHING;
    int size=matchableActionSize;
    Action<R>[] actions=this.actions;
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
  
  boolean processFinished() {
    return matchableActionSize == 0;
  }
  
  /**
   * Returns the rule that matched, upon successful analyze. This method
   * must be called only after method {@link #step} or {@link #stepClose} has returned
   * {@link ProcessReturn#TOKEN}
   * @return the rule that matched
   */
  public R winningRule() {
    if (!processFinished() || maxVal == -1)
      throw new IllegalStateException("This method must be called only after a succesfull match");
    return winningAction.getRule();
  }

  /**
   * Returns the token length, upon successful analyze. This method
   * must be called only after method {@link #step} or {@link #stepClose} has returned
   * {@link ProcessReturn#TOKEN}
   * @return the token length
   */
  public int tokenLength() {
    if (!processFinished() || maxVal == -1)
      throw new IllegalStateException("This method must be called only after a succesfull match");
    return maxVal;
  }
  
  /**
   * Returns the rule tables for this process
   * @return the rule tables for this process
   */
  public LexerTable<R> getLexerTable() {
    return lexerTable;
  }
  
  private int maxVal;
  private int minPriority;
  private Action<R> winningAction;
  private int matchableActionSize;
  private final Action<R>[] actions;
  
  private final LexerTable<R> lexerTable;
}

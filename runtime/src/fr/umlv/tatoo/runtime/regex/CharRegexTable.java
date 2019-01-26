/*
 * Created on 6 juil. 2005
 */
package fr.umlv.tatoo.runtime.regex;

import fr.umlv.tatoo.runtime.lexer.rules.RegexTable;

/** A {@link RegexTable} implementation for DFA which uses all unicode characters.
 * Transitions are represented coding characters intervals using arrays:
 * if the array is {a,b,c,d}, it means to from character a to c, DFA goes to state
 * b; from character c to '\uFFFF', DFA goes to d. Going to state -1 means no transition.
 * @author Julien
 */
public class CharRegexTable implements RegexTable {

  private final int[][] transitions;
  private final boolean[] accepts;
  private final int firstState;
  
  /**
   * Build a RegexTable with firstState as first state, transitions as transition table as
   * explained in {@link CharRegexTable}, and accepts which gives accepting states
   * @param firstState the first state
   * @param transitions transition table as explained in {@link CharRegexTable}
   * @param accepts accepting states
   */
  public CharRegexTable(int firstState, int[][] transitions, boolean[] accepts) {
    this.firstState = firstState;
    this.accepts = accepts;
    this.transitions = transitions;
  }
  
  public boolean accept(int i) {
    return accepts[i];
  }
  public int getStart() {
    return firstState;
  }

  public int getStateNumber() {
    return accepts.length;
  }

  public int getTransition(int state, int next) {
    int[] tr = transitions[state];
    int min = 0;
    int max = tr.length / 2;
    while (min < max - 1) {
      int mean = (min + max) / 2;
      if (tr[2 * mean] > next) {
        max = mean;
      } else {
        min = mean;
      }
    }
    return tr[2 * min + 1];
  }
  
  public boolean noOut(int currentState) {
    int[] stateTransition=transitions[currentState];
    return stateTransition.length == 2 && stateTransition[1]==-1;
  }
}

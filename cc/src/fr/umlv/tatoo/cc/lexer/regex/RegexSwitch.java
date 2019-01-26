/*
 *
 * Created: 3 mai 2006
 */
package fr.umlv.tatoo.cc.lexer.regex;
//FIXME Remi genilogize

import java.util.LinkedList;
import java.util.List;

import fr.umlv.tatoo.cc.lexer.generator.SwitchCase;

public class RegexSwitch {

  public RegexSwitch(AutomatonDecl regex) {
    this.transitions = constructTransitions(regex.getTransitions());
    this.accepts = regex.getAccepts();
    this.noOut = constructNoOut(regex.getTransitions());
    this.firstState = regex.getFirstState();
  }

  private List<Integer> constructNoOut(int[][] transitions) {
    List<Integer> result = new LinkedList<Integer>();
    for (int i=0;i<transitions.length;i++) {
      if (transitions[i].length == 2 && transitions[i][1]==-1) {
        result.add(i);
      }
    }
    return result;
  }
  @SuppressWarnings("unchecked")
  private List<SwitchCase>[] constructTransitions(int[][] trans) {
    List<SwitchCase>[] result = (List<SwitchCase>[]) new List<?>[trans.length];
    for (int state=0; state <trans.length;state++) {
      result[state] = simplifyTransitions(convertTransitions(trans[state],trans.length));
    }
    return result;
  }
  
  @SuppressWarnings("unchecked")
  private List<Integer>[] convertTransitions(int[] trans, int stateNb) {
    List<Integer>[] transList = (List<Integer>[]) new List<?>[stateNb+1];
    int j = 0;
    int to = -1;
    for (int c = -128;c<128;c++) {
      if (j < trans.length && c == trans[j]) {
        to = trans[j+1];
        j+=2;
      }
      if (transList[to+1] == null) {
        transList[to+1] = new LinkedList<Integer>();
      }
      transList[to+1].add(c);      
    }
    return transList;
  }
  
  private List<SwitchCase> simplifyTransitions(List<Integer>[] trans) {
    if (trans.length==0)
      throw new IllegalArgumentException("transitions array is empty");
    
    SwitchCase defaultDest = null;
    int maxLabels = 0;
    List<SwitchCase> result = new LinkedList<SwitchCase>();
    for (int i = 0;i<trans.length;i++) {
      if (trans[i]!=null) {
        List<Integer> labels = trans[i];
        SwitchCase dest = new SwitchCase(i-1,labels);
        result.add(dest);
        if (labels.size() > maxLabels) {
          maxLabels = labels.size();
          defaultDest = dest;
        }
      }
    }
    
    if (defaultDest==null)
      throw new AssertionError("all transitions are empty");
      
    defaultDest.setDefault();
    return result;
  }
  
  public int getStateNb() {
    return transitions.length;
  }
  
  public List<SwitchCase> getTransitions(int state) {
    return transitions[state];
  }

  public boolean[] getAccepts() {
    return accepts;
  }

  public List<Integer> getNoOut() {
    return noOut;
  }

  public int getFirstState() {
    return this.firstState;
  }

  private final List<SwitchCase>[] transitions;
  private final boolean[] accepts;
  private final List<Integer> noOut;
  private final int firstState;
}

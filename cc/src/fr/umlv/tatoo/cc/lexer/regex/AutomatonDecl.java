/*
 * Created on 5 juil. 2005
 */
package fr.umlv.tatoo.cc.lexer.regex;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.umlv.tatoo.cc.lexer.charset.CharacterInterval;
import fr.umlv.tatoo.cc.lexer.charset.CharacterSet;
import fr.umlv.tatoo.cc.lexer.charset.encoding.Encoding;

public class AutomatonDecl {
  private final int[][] transitions;
  private final boolean[] accepts;
  private final int firstState;
  
  public AutomatonDecl(int firstState, CharacterSet[][] trans, boolean[] accepts, Encoding encoding) {
    this.firstState = firstState;
    this.accepts = accepts.clone();
    transitions = new int[trans.length][];
    for (int i = 0; i < trans.length; i++)
      transitions[i] = getTransitions(trans[i],encoding);
  }

  public boolean acceptsEpsilon() {
    return accepts[firstState];
  }
  
  public boolean[] getAccepts() {
    return this.accepts.clone();
  }
  
  public int[][] getTransitions() {
    return this.transitions.clone();
  }
  
  public int getFirstState() {
    return this.firstState;
  }

  private static int[] getTransitions(CharacterSet[] set, Encoding encoding) {
    ArrayList<Transition> transitions = convertToTransitions(set);
    if (transitions.size() == 0) {
      return new int[] { encoding.getMinValue(), -1 };
    }
    ArrayList<Integer> rep = new ArrayList<Integer>();
    int empty = encoding.getMinValue();
    while (transitions.size() != 0) {
      Transition minTransition = getMinTransition(transitions);
      if (minTransition.getBegin() > empty) {
        //System.out.println("adding " + empty + " -1");
        rep.add(empty);
        rep.add(-1);
      }
      /*System.out.println(
       "adding " + tab[min].getBegin() + " " + tab[min].getTransition());*/
      rep.add(minTransition.getBegin());
      rep.add(minTransition.getDestination());
      empty = minTransition.getEnd() + 1;
      if (!minTransition.nextInterval()) {
        transitions.remove(minTransition);
      }
    }
    if (empty <= encoding.getMaxValue()) {
//    System.out.println("adding " + empty + " -1");
      rep.add(empty);
      rep.add(-1);
    }
    
    return toArray(rep);
  }
  
  private static Transition getMinTransition(ArrayList<Transition> transitions) {
    Iterator<Transition> it = transitions.iterator();
    Transition minTransition = it.next();
    Transition transition = null;
    while(it.hasNext()) {
      transition = it.next();
      if (transition.getBegin() < minTransition.getBegin()) {
        minTransition = transition;
      }
    }
    return minTransition;
  }
  
  private static ArrayList<Transition> convertToTransitions(CharacterSet[] set) {
    ArrayList<Transition> result = new ArrayList<Transition>();
    for (int i = 0; i < set.length; i++) {
      CharacterSet currentSet = set[i];
      if (currentSet == null)
        continue;
      result.add(new Transition(currentSet,i));
    }
    return result;
  }

  private static class Transition {
    private final int destination;
    private CharacterInterval currentInterval;
    private final Iterator<CharacterInterval> iterator;
    public Transition(CharacterSet set, int destination) {
      iterator = set.getList().iterator();
      currentInterval = iterator.next();
      this.destination = destination;
    }

    /**
     * @return <code>true</code> if interval available
     */
    public boolean nextInterval() {
      if (iterator.hasNext()) {
        currentInterval = iterator.next();
        return true;
      } else
        return false;
    }

    public int getDestination() {
      return destination;
    }

    public int getBegin() {
      return currentInterval.getBegin();
    }
    
    public int getEnd() {
      return currentInterval.getEnd();
    }
  }

  private static int[] toArray(List<Integer> list) {
    int[] rep = new int[list.size()];
    int i = 0;
    for (int num : list) {
      rep[i] = num;
      i++;
    }
    return rep;
  }
  
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("start:").append(firstState).append('\n');
    for(boolean b:accepts)
      builder.append(b).append(';');
    builder.append('\n');
    for(int[] tab:transitions) {
      for(int v:tab)
        builder.append(v).append(';');
      builder.append('\n');
    }
    builder.append("single word:");
    return builder.toString();
  }
}
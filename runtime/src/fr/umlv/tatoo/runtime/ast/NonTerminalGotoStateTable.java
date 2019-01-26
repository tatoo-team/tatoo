package fr.umlv.tatoo.runtime.ast;

import java.util.HashMap;

public class NonTerminalGotoStateTable<N> {
  private final State<N>[] stateArray;
  
  static class State<N> {
    private final HashMap<N,Integer> nonTerminalGotoStateMap=
      new HashMap<N, Integer>();

    int getGotoState(N nonTerminal) {
      return nonTerminalGotoStateMap.get(nonTerminal);
    }
    
    void setGotoState(N nonTerminal, int toState) {
      nonTerminalGotoStateMap.put(nonTerminal, toState);
    }
  }
  
  @SuppressWarnings("unchecked")
  public NonTerminalGotoStateTable(int stateNumber) {
    // this assignment is safe because stateArray never escape
    this.stateArray=new State[stateNumber];
    
    for(int i=0;i<stateNumber;i++) {
      stateArray[i]=new State<N>();
    }
  }
  
  public void setGotoState(int fromState, N nonTerminal, int toState) {
    stateArray[fromState].setGotoState(nonTerminal, toState);
  }
  
  public int getGotoState(int fromState, N nonTerminal) {
    return stateArray[fromState].getGotoState(nonTerminal);
  }
}

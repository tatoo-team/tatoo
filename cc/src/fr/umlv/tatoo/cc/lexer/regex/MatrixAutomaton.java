/*
 * Created on 5 juil. 2005
 */
package fr.umlv.tatoo.cc.lexer.regex;

import java.util.BitSet;

import fr.umlv.tatoo.cc.lexer.charset.CharacterSet;
import fr.umlv.tatoo.cc.lexer.charset.encoding.Encoding;

public class MatrixAutomaton {
  private final int firstState;
  private final CharacterSet[][] transitions;
  private final boolean[] accepts;
  private final Encoding encoding;
  
  public MatrixAutomaton(int firstState, CharacterSet[][] transitions, boolean[] accepts, Encoding encoding) {
    this.firstState = firstState;
    this.transitions = transitions;
    this.accepts = accepts;
    this.encoding = encoding;
  }
  
  public AutomatonDecl createAutomaton() {
    return new AutomatonDecl(firstState, transitions, accepts, encoding);
  }

  public String computeSingleWord(){
    int state = firstState;
    StringBuilder builder = new StringBuilder();
    BitSet seenStates = new BitSet();
    for(;;) {
      
      seenStates.set(state);
      CharacterSet singleCharSet = null;
      int toState=0;
      
      for(int i=0;i<transitions[state].length;i++) {
        CharacterSet currentSet = transitions[state][i];
        if(currentSet==null)
          continue;
        // more than one transition
        if (singleCharSet!=null) {
          //System.out.println("two+");
          return null;
        }
        singleCharSet = currentSet;
        toState=i;
      }
      //no transitions: ok if accepts
      if (singleCharSet==null) {
        if (accepts[state])
          return builder.toString();
        //System.out.println("zero (should not be reached)");
        return null;
      }
      // transition to an already seen state
      if (seenStates.get(toState)) {
        //System.out.println("loop");
        return null;
      }

      Integer integer = singleCharSet.getSingleCharacter();
      //one transition which is an interval
      if (integer==null){
        //System.out.println("one but [x-y] : "+singleCharSet);
        return null;
      }
      builder.append(encoding.decode(integer));
      state = toState;
    }
  }
}
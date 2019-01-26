/*
 * Created on 25 juin 2003
 */
package fr.umlv.tatoo.cc.lexer.regex;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.umlv.tatoo.cc.common.util.MultiMap;
import fr.umlv.tatoo.cc.lexer.charset.CharacterInterval;
import fr.umlv.tatoo.cc.lexer.charset.CharacterSet;
import fr.umlv.tatoo.cc.lexer.charset.Triplet;
import fr.umlv.tatoo.cc.lexer.charset.encoding.Encoding;

/**
 * @author Julien
 *
 */
public class RegexFactory {
  
  public static Regex createSet(boolean negate,List<CharacterInterval> intervals, Encoding encoding) {
    Regex regex = leaf(intervals,negate,encoding);
    return regex;
  }
  
  private static final Regex EPSILON = new EpsilonLeaf();
  
  /* r* */
  public static Regex star(Regex child) {
    return new Star(child);
  }
  
  /* r+ */
  public static Regex plus(Regex child) {
    return cat(child.cloneRegex(),star(child));
  }
  
  /* r? */
  public static Regex optional(Regex child) {
    return or(child,EPSILON);
  }
  
  /* r{from,to} */
  public static Regex range(int from,int to,Regex child) {
    Regex tmp=times(from,child);
    for(int i=from;i<to;i++) {
      tmp=cat(tmp,optional(child.cloneRegex()));
    }
    return tmp;
  }
  
  /* r{num} */
  public static Regex times(int num,Regex child) {
    Regex tmp;
    if (num==0) {
      tmp=EPSILON;
    }
    else {
      tmp=child;
      for(int i=1;i<num;i++) {
        tmp=cat(tmp,child.cloneRegex());
      }
    }
    return tmp;
  }
  
  /* run{from,} */
  public static Regex atLeast(int num,Regex child) {
    Regex tmp=star(child);
    for(int i=0;i<num;i++) {
      tmp=cat(tmp,child.cloneRegex());
    }
    return tmp;
    
  }
  
  /* rs */
  public static Regex cat(Regex left,Regex right) {
    return new Cat(left,right);
  }
  
  /* r|s */
  public static Regex or(Regex left,Regex right) {
    return new Or(left,right);
  } 
  
  public static Regex leaf(List<CharacterInterval> intervalList, boolean negate, Encoding encoding) {
    //System.out.println("asked for leaf with "+intervalList+" negate = "+negate);
    Leaf leaf = new Leaf(new CharacterSet(intervalList,negate,encoding));
    return leaf;
  }
  
  public static Regex letter(char c, Encoding encoding) {
    CharacterInterval interval = new CharacterInterval(c,c,encoding);
    CharacterSet set = new CharacterSet(Collections.singletonList(interval));
    Leaf leaf=new Leaf(set);
    return leaf;
  }
  
  public static Regex letterIgnoreCase(char letter,Encoding encoding) {
    char letterOtherCase;
    if (!Character.isLetter(letter))
      return letter(letter,encoding);
    if (Character.isLowerCase(letter)) {
      letterOtherCase = Character.toUpperCase(letter);
    } else {
      letterOtherCase = Character.toLowerCase(letter);
    }
    return new Or(letter(letter,encoding),letter(letterOtherCase,encoding));
  }
  
  public static Regex characterSequence(CharSequence s, boolean ignoreCase, Encoding encoding) {
    if (ignoreCase) {
      Regex r = letterIgnoreCase(s.charAt(0),encoding);
      for (int pos=1;pos<s.length();pos++){
        Regex leaf = letterIgnoreCase(s.charAt(pos),encoding);
        r = new Cat(r,leaf);
      }
      return r;
    }
    Regex r = letter(s.charAt(0),encoding);
    for (int pos=1;pos<s.length();pos++){
      Regex leaf = letter(s.charAt(pos),encoding);
      r = new Cat(r,leaf);
    }
    return r;
  }
  
  public static MatrixAutomaton table(Regex rootNode, Encoding encoding) {
    Leaf end = new Leaf();
    Regex root=cat(rootNode, end);
    MultiMap<Leaf,Leaf> transitions =
      new MultiMap<Leaf,Leaf>();
    root.computeFollowPos(transitions);
    return makeTable(root.firstPos(),transitions,end,encoding);
  }
  
  public static Regex clone(Regex root) {
    return root.cloneRegex();
  }
  
  private static MatrixAutomaton 
  minimize(HashMap<Set<Leaf>,HashMap<Set<Leaf>,CharacterSet>> dTrans,
      Leaf acceptPosition,Set<Leaf> firstState,Encoding encoding) {
    HashMap<Integer,HashMap<Integer,CharacterSet>> trans =
      new HashMap<Integer,HashMap<Integer,CharacterSet>>();
    HashMap<Set<Leaf>,Integer> map =
      new HashMap<Set<Leaf>,Integer>();
    HashMap<Set<Leaf>,Integer> newMap =
      new HashMap<Set<Leaf>,Integer>();
    MultiMap<HashMap<Integer,CharacterSet>,Set<Leaf>> multiMap =
      new MultiMap<HashMap<Integer,CharacterSet>,Set<Leaf>>();
    HashSet<Integer> finalStates = new HashSet<Integer>();
    
    @SuppressWarnings("unchecked")
    HashSet<Set<Leaf>>[] reverseMap =
      (HashSet<Set<Leaf>>[]) new HashSet<?>[2];
    for (int i = 0; i < reverseMap.length; i++)
      reverseMap[i] = new HashSet<Set<Leaf>>();
    
    int stateNb = 2;
    
    for (Set<Leaf> currentState :  dTrans.keySet()) {
      if (currentState.contains(acceptPosition)) {
        map.put(currentState, 0);
        reverseMap[0].add(currentState);
      } else {
        map.put(currentState, 1);
        reverseMap[1].add(currentState);
      }
    }
    
    while (true) {
      //System.out.println(map);
      int stateCount = 0;
      for (int i = 0; i < stateNb; i++) {
        multiMap.clear();
        for (Set<Leaf> state : reverseMap[i]) {
          HashMap<Set<Leaf>, CharacterSet> transition = dTrans.get(state);
          /* tmpMap associated the Integer i to the letters which leads to a state
           * whose number is i
           */
          HashMap<Integer,CharacterSet> tmpMap =
            new HashMap<Integer,CharacterSet>();
          for (Map.Entry<Set<Leaf>, CharacterSet> entry : transition.entrySet()) {
            Set<Leaf> targetState = entry.getKey();
            Integer targetStateNo = map.get(targetState);
            CharacterSet letters = entry.getValue();
            addLettersToState(tmpMap, letters, targetStateNo);
          }
          multiMap.add(tmpMap, state);
        }
        for (Set<Set<Leaf>> setOfStates: multiMap.values()) {
          for (Set<Leaf> state : setOfStates) {
            newMap.put(state, stateCount);
          }
          stateCount++;
        }
      }
      
      HashMap<Set<Leaf>,Integer> bufMap = map;
      map = newMap;
      newMap = bufMap;
      newMap.clear();
      
      @SuppressWarnings("unchecked")
      HashSet<Set<Leaf>>[] newReverseMap =
        (HashSet<Set<Leaf>>[]) new HashSet<?>[stateCount];
      for (int i = 0; i < newReverseMap.length; i++) {
        newReverseMap[i] = new HashSet<Set<Leaf>>();
      }
      reverseMap = newReverseMap;
      
      
      
      for (Map.Entry<Set<Leaf>, Integer> entry : map.entrySet()) {
        int stateNo = entry.getValue();
        Set<Leaf> state = entry.getKey();
        reverseMap[stateNo].add(state);
      }
      
      if (stateCount == stateNb)
        break;
      
      stateNb = stateCount;
    }
    
    //System.out.println(map);
    
    /*for (int i = 0; i < stateNb; i++) {
     System.out.println(i + " : " + reverseMap[i]);
     }*/
    
    for (int i = 0; i < stateNb; i++) {
      for (Set<Leaf> state : reverseMap[i]) {
        HashMap<Set<Leaf>, CharacterSet> transition = dTrans.get(state);
        HashMap<Integer,CharacterSet> tmpMap = new HashMap<Integer,CharacterSet>();
        for (Map.Entry<Set<Leaf>, CharacterSet> entry : transition.entrySet()) {
          Set<Leaf> targetState = entry.getKey();
          Integer targetStateNo = map.get(targetState);
          CharacterSet letters = entry.getValue();
          addLettersToState(tmpMap, letters, targetStateNo);
        }
        trans.put(i, tmpMap);
      }
    }
    
    //System.out.println("trans donne : " + trans);
    
    int start;
    boolean[] accepts;
    
    start = map.get(firstState);
    
    for (Map.Entry<Set<Leaf>,Integer> entry : map.entrySet()) {
      Set<Leaf> state = entry.getKey();
      if (state.contains(acceptPosition))
        finalStates.add(entry.getValue());
    }
    
    //System.out.println("start " + start + " final " + finalStates);
    
    for (Iterator<Map.Entry<Integer,HashMap<Integer,CharacterSet>>> i =
      trans.entrySet().iterator();i.hasNext();) {
      Map.Entry<Integer,HashMap<Integer,CharacterSet>> entry = i.next();
      if (finalStates.contains(entry.getKey()))
        continue;
      HashMap<Integer, CharacterSet> transition = entry.getValue();
      if (transition.size() == 1 && //transition.get(entry.getKey()) != null)
          transition.containsKey(entry.getKey()))
        i.remove();
    }
    HashSet<Integer> toKeep = new HashSet<Integer>();
    HashSet<Integer> layer = new HashSet<Integer>();
    layer.add(start);
    toKeep.add(start);
    HashSet<Integer> newLayer = new HashSet<Integer>();
    while (true) {
      //System.out.println("to keep : " + toKeep);
      //System.out.println("layer : " + layer);
      for (Integer currentState : layer) {
        HashMap<Integer, CharacterSet> transition = trans.get(currentState);
        for (Integer to : transition.keySet()) {
          if (toKeep.add(to))
            newLayer.add(to);
        }
      }
      if (newLayer.isEmpty())
        break;
      HashSet<Integer> tmpLayer = layer;
      layer = newLayer;
      newLayer = tmpLayer;
      newLayer.clear();
    }
    //System.out.println("to keep : " + toKeep);
    //System.out.println("layer : " + layer);
    
    //CharacterSet empty = new CharacterSet(Collections.EMPTY_LIST);
    int size = toKeep.size();
    CharacterSet[][] transitions = new CharacterSet[size][size];
    for (int i = 0; i < transitions.length; i++)
      for (int j = 0; j < transitions.length; j++)
        transitions[i][j] = null;
    
    HashMap<Integer,Integer> newNums = new HashMap<Integer,Integer>();
    int num = 0;
    for (Integer nb : toKeep) {
      newNums.put(nb, num++);
    }
    
    //System.out.println(newNums);
    
    for (Map.Entry<Integer,HashMap<Integer,CharacterSet>> entry : trans.entrySet()) {
      Integer oldFrom = entry.getKey();
      Integer newFrom = newNums.get(oldFrom);
      if (newFrom == null)
        continue;
      int from = newFrom;
      HashMap<Integer, CharacterSet> transition = entry.getValue();
      for (Map.Entry<Integer, CharacterSet> entry2 : transition.entrySet()) {
        Integer oldTo = entry2.getKey();
        Integer newTo = newNums.get(oldTo);
        if (newTo == null)
          continue;
        int to = newTo;
        transitions[from][to] = entry2.getValue();
      }
    }
    
    accepts = new boolean[toKeep.size()];
    Arrays.fill(accepts, false);
    
    for (Integer oldNum : finalStates) {
      Integer newNum = newNums.get(oldNum);
      if (newNum != null) {
        int no = newNum.intValue();
        accepts[no] = true;
      }
    }
    int newStart = newNums.get(start);
    /*for(T[] t : transitions)
     System.out.println(Arrays.asList(t));*/
    return new MatrixAutomaton(newStart, transitions, accepts,encoding);
  }
  
  public static MatrixAutomaton  
  makeTable(Set<Leaf> first,MultiMap<Leaf,Leaf> transitions,
      Leaf acceptPosition,Encoding encoding) {
    HashSet<Set<Leaf>> unmarked =
      new HashSet<Set<Leaf>>();
    HashSet<Set<Leaf>> buildingUnmarked =
      new HashSet<Set<Leaf>>();
    HashMap<CharacterSet,Set<Leaf>> toPut = new HashMap<CharacterSet,Set<Leaf>>();
    HashMap<CharacterSet,Set<Leaf>> map = new HashMap<CharacterSet,Set<Leaf>>();
    /* State = set of Leaf */
    /* dTrans : State -> Map : State -> CharacterSet */
    HashMap<Set<Leaf>,HashMap<Set<Leaf>,CharacterSet>> dTrans =
      new HashMap<Set<Leaf>,HashMap<Set<Leaf>,CharacterSet>>();
    
    unmarked.add(first);
    while (!unmarked.isEmpty()) {
      for (Set<Leaf> state: unmarked) {
        
        //System.out.println("state : " + state);
        
        map.clear();
        for (Leaf position :  state) {
          /* skip eof position */
          //System.out.println("Leaf : "+position);
          if (position==acceptPosition)
            continue;
          CharacterSet characterSet = position.getLetters();
          Set<Leaf> transition = transitions.get(position);
          //System.out.println("tr to : "+transition);
          toPut.clear();
          for (Iterator<Map.Entry<CharacterSet,Set<Leaf>>> k =
            map.entrySet().iterator();
          k.hasNext() && characterSet != null;) {
            Map.Entry<CharacterSet,Set<Leaf>> entry = k.next();
            CharacterSet targetSet = entry.getKey();
            Set<Leaf> targetState = entry.getValue();
            
            //System.out.println(
            //  "confronting :" + characterSet + " and " + targetSet);
            //System.out.println(characterSet);
            Triplet triplet =
              characterSet.split(targetSet);
            
            //System.out.println("result" + triplet);
            
            /* intersection is empty */
            if (triplet.getSecond() == null)
              continue;
            
            /* targetSet subset of characterSet */
            if (triplet.getThird() == null) {
            //  System.out.println("append in "+targetState+" : "+transition);
            //  System.out.println(" for charset "+targetSet);
              targetState.addAll(transition);
              characterSet = triplet.getFirst();
              continue;
            }
            
            k.remove();
            HashSet<Leaf> newState = new HashSet<Leaf>();
            newState.addAll(targetState);
            newState.addAll(transition);
            toPut.put(triplet.getSecond(), newState);
            toPut.put(triplet.getThird(), targetState);
            characterSet = triplet.getFirst();
          }
          //System.out.println("to add : " + toPut);
          for (Map.Entry<CharacterSet,Set<Leaf>> entry : toPut.entrySet()) {
            CharacterSet key = entry.getKey();
            Set<Leaf> s = map.get(key);
            if (s == null) {
              s = new HashSet<Leaf>();
              map.put(key, s);
            }
            s.addAll(entry.getValue());
          }
          if (characterSet != null) {
            Set<Leaf> s = map.get(characterSet);
            if (s == null) {
              s = new HashSet<Leaf>();
              map.put(characterSet, s);
            }
            s.addAll(transition);
            //System.out.println("add "+characterSet+" to "+s);
          }
          //System.out.println("transitions : "+map);
        }
        HashMap<Set<Leaf>,CharacterSet> trueMap = new HashMap<Set<Leaf>,CharacterSet>();
        dTrans.put(state, trueMap);
        for (Map.Entry<CharacterSet,Set<Leaf>> entry :  map.entrySet()) {
          CharacterSet currentLetters = entry.getKey();
          Set<Leaf> currentState = entry.getValue();
          addLettersToState(trueMap, currentLetters, currentState);
          if (dTrans.get(currentState) == null) {
            buildingUnmarked.add(currentState);
          }
        }
      }
      HashSet<Set<Leaf>>  tmp = unmarked;
      unmarked = buildingUnmarked;
      buildingUnmarked = tmp;
      buildingUnmarked.clear();
    }
    //System.out.println(dTrans);
    //System.out.println(acceptPosition);
    return minimize(dTrans, acceptPosition, first,encoding);
    //System.out.println("-----");
    //System.out.println(table);
    //System.out.println("-----");
  }
  
  private static <U> void 
  addLettersToState(Map<U,CharacterSet> map,
      CharacterSet letters, U state) {
    CharacterSet oldLetters = map.get(state);
    if (oldLetters == null)
      map.put(state, letters);
    else
      map.put(state, oldLetters.union(letters));
  }
  
  /**
   * Build the table which tests whenever end of line follows. It does not
   * recognize the whole end of line but only the beginning
   * @return the regex which tests whenever end of line follows
   */
  public static Regex createDollarRegex(Encoding encoding) {
    Regex regexr = letter('\r',encoding);
    Regex regexn = letter('\n',encoding);
    // newLine follows if next char is \r or \n
    return or(regexr, regexn);
  }

  public static Regex interval(char from, char to,Encoding encoding) {
    return new Leaf(new CharacterSet(Collections.singletonList(new CharacterInterval(from,to,encoding))));
  }
  
}

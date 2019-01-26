/*
 * Created on Jun 17, 2003
 *
 */
package fr.umlv.tatoo.runtime.lexer.nano;

import java.util.ArrayList;

import fr.umlv.tatoo.runtime.lexer.rules.DFA;
import fr.umlv.tatoo.runtime.lexer.rules.ProcessReturn;
import fr.umlv.tatoo.runtime.lexer.rules.RegexTable;
import fr.umlv.tatoo.runtime.lexer.rules.RuleData;
import fr.umlv.tatoo.runtime.util.IntArrayList;

/** This class contains the live data used to process
 *  one rule of the lexer.
 *  
 *  A rule data is break down in one regular expression
 *  and one optional regular expression represented by the
 *  class {@link RuleData}.
 *  
 *  The state of the automaton of a regular expression
 *  is represented by the class {@link DFA}.
 * 
 * @author Julien Cervelle
 *
 * @see RuleData
 * @see DFA
 */
public class BranchRegexRuleMatcher implements RuleMatcher {
  private final int priority;
  private final RegexTable followRegex;
  private final boolean hasFollowing;
  private int charNo;
  private int lastMatch;
  private boolean mainProcessFinished;
  private final DFA main;
  private ArrayList<DFA> subProcesses;
  private IntArrayList positions;
  
  private Object attachment;
  
  private static boolean containsEspilon(RegexTable table) {
    return table.accept(table.getStart());
  }
  
  /** Creates an action.
   */
  public BranchRegexRuleMatcher(RuleData ruleData) {
    this.priority=ruleData.getPriority();
    
    DFA main = new DFA();
    main.reset(ruleData.getMainRegex());
    this.main = main;
    
    RegexTable followRegex = ruleData.getFollowRegex();
    this.followRegex=followRegex;
    hasFollowing=(followRegex != null && !containsEspilon(followRegex));
  }
  
  /** Reuse the action with a new rule and its data.
   * 
   */
  public void reuse(Object attachment) {
    //System.out.println(hashCode()+" reset with rule "+rule);
    charNo = 0;
    lastMatch = -1;
    
    this.attachment = attachment;
    
    if (hasFollowing)
      resetProcesses();
    mainProcessFinished = false;
  }
  
  public Object getAttachment() {
    return attachment;
  }
  
  /** Returns the priority of the rule.
   * @return the priority of the rule.
   */
  public int getPriority() {
    return priority;
  }
 
  /** Steps by one letter on the internal automaton and
   *  returns false if the letter is rejected or
   *  accepted by the automaton.
   * 
   * @param a the letter
   * @return true is a further step is needed in order
   *  to parse a token.
   */
  public ProcessReturn step(int a) {
    //System.out.println(hashCode()+" "+rule+"#### "+a);
    //System.out.println(mainProcessFinished);
    charNo++;
    if (!hasFollowing) {
      DFA.ReturnCode result = main.step(a);
      switch (result) {
      case REJECT :
        return ProcessReturn.ERROR;
      case FINAL_ACCEPT :
        lastMatch = charNo;
        return ProcessReturn.TOKEN;
      case ACCEPT :
        lastMatch = charNo;
        return ProcessReturn.MORE;
      case CONTINUE :
        return ProcessReturn.MORE;
      default :
        throw new AssertionError("unknown process result");
      }
    } else {
      //FIXME Julien, stepFollowing should returns a ProcessReturn
      return (stepFollowing(a))?ProcessReturn.MORE:ProcessReturn.ERROR;
    }
  }
  
  @SuppressWarnings("fallthrough")
  private boolean stepFollowing(int a) {
    // lazy alloc
    if (subProcesses==null) {
      subProcesses = new ArrayList<DFA>();
      positions = new IntArrayList();
    }

    //System.out.println(rule+"###");
    for (int i=0;i<positions.size();i++) {
      DFA process = subProcesses.get(i);
      //System.out.println(rule + " DFA "+i+" stepping for " + a);
      DFA.ReturnCode result = process.step(a);
      //System.out.println(result);
      switch (result) {
        case REJECT :
          removeProcess(i);
          break;
        case FINAL_ACCEPT :
          //fallthrough
        case ACCEPT :
          lastMatch = Math.max(lastMatch, positions.get(i));
          removeProcess(i);
          break;
        case CONTINUE :
          break;
        default :
          throw new AssertionError("unknown process result");
      }
    }
    if (mainProcessFinished) {
      //System.out.println("fini");
      return !positions.isEmpty();
    } else {
      //System.out.println(rule + " DFA stepping for " + a);
      DFA.ReturnCode result = main.step(a);
      //System.out.println(result);
      switch (result) {
        case REJECT :
          //System.out.println("cici");
          mainProcessFinished = true;
          return !positions.isEmpty();
        case FINAL_ACCEPT :
          //System.out.println("cici2");
          mainProcessFinished = true;
          //fallthrough !
        case ACCEPT :
          addProcess();
          return true;
        case CONTINUE :
          return true;
        default :
          throw new AssertionError("unknown process result");
      }
    }
  }
  
  private void removeProcess(int i) {
    //System.out.println("rem "+i);
    //System.out.println(positions.size());
    int max = positions.size()-1;
    if (i!=max) {
      DFA tmp = subProcesses.get(i);
      subProcesses.set(i, subProcesses.get(max));
      subProcesses.set(max,tmp);
      positions.set(i,positions.get(max));
    }
    positions.removeLast(1);
    //System.out.println(positions.size());
  }
  
  private void addProcess() {
    int pos = positions.size();
    positions.add(charNo);
    if (pos < subProcesses.size()) {
      subProcesses.get(pos).reset();
    } else {
      subProcesses.add(new DFA(followRegex));
    }
  }
  
  private void resetProcesses() {
    if (positions!=null)
      positions.clear();
  }
  
  /** Returns the last buffer position that has matched the rule.
   * @return the last buffer position that has matched the rule.
   */
  public int lastMatch() {
    return lastMatch;
  }

  @Override
  public Object attachment() {
    throw new UnsupportedOperationException();
  }

  @Override
  public int depth() {
    throw new UnsupportedOperationException();
  }
}

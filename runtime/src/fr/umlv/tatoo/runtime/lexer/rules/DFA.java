/*
 * Created on 15 juin 2003
 */
package fr.umlv.tatoo.runtime.lexer.rules;


/** This class represents a cursor on an automaton defined
 *  by an object of the class {@link RegexTable}.
 *  
 * @author Julien
 *
 */
public class DFA {
  /** Creates an empty DFA that must be initialized thru a
   *  call to {@link #reset(RegexTable)}.
   */
  public DFA() {
    // nothing : must be initialized thru a reset(RegexTable)
  }
  
  /** Creates a DFA.
   * @param table the automaton table.
   */
  public DFA(RegexTable table) {
    reset(table);
  }
  
  /** Reuse the current DFA with a new automaton.
   * @param table the automaton table.
   */
  public void reset(RegexTable table) {
    this.table = table;
    currentState = table.getStart();
  }
  
  /** Reset the current DFA.
   */
  public void reset() {
    currentState = table.getStart();
  }
  
  
  /** Return code of the method {@link DFA#step(int)} of the DFA.
   */
  public enum ReturnCode {
    /** Accept the current state, but a longer match is possible.
     */
    ACCEPT,
    
    /** Reject he current state, parsing is over.
     */
    REJECT,
    
    /** Reject he current state but a longer match is possible.
     */
    CONTINUE,
    
    /** Accept the current state and no longer match is possible.
     */
    FINAL_ACCEPT
  }
  
  
  /** Follows the transition from the current state with the letter a.
   * @param a the letter
   * @return ACCEPT, REJECT, CONTINUE or FINAL_ACCEPT
   * 
   * @see RegexTable#getTransition(int, int)
   */
  public ReturnCode step(int a) {
    RegexTable table = this.table;
    int currentState = table.getTransition(this.currentState,a);
    this.currentState = currentState;
    if (currentState==-1)
      return ReturnCode.REJECT;
    if (table.accept(currentState))
      if (table.noOut(currentState))
        return ReturnCode.FINAL_ACCEPT;
      else
        return ReturnCode.ACCEPT;
    else if (table.noOut(currentState))
      return ReturnCode.REJECT;
    else
      return ReturnCode.CONTINUE;
  }
  
  
  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder();
    buf.append('<').append(table.getStart()).append(';');
    int length = table.getStateNumber();
    boolean acc = false;
    for (int i = 0; i < length; i++)
      if (table.accept(i)) {
        buf.append(i).append(',');
        acc = true;
      }
    
    if (acc) {
      int size = buf.length();
      buf.delete(size - 1, size);
    }
    buf.append(';').append(table).append('>');
    return buf.toString();
  }
  
  /** Returns the automaton table.
   * @return the automaton table.
   */
  public RegexTable getTable() {
    return table;
  }
  
  private RegexTable table;
  private int currentState;
}

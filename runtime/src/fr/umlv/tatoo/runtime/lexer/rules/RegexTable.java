/*
 * Created on 6 juil. 2005
 */
package fr.umlv.tatoo.runtime.lexer.rules;

/** High level interface of a lexer automaton.
 * 
 * @author Julien
 */
public interface RegexTable {
  /** Returns the index of the start state.
   * @return the index of the start state.
   */
  public int getStart();
  
  /** Returns the index of the following state
   *  from the state state with the letter next.
   *  
   * @param state previous state
   * @param next letter to read
   * @return next state or -1 if undefined
   */
  public int getTransition(int state,int next);
  
  /** Tests if the state is or not an accept state.
   * @param state the state
   * @return true if the state is an accept state
   */
  public boolean accept(int state);
  
  /** Returns the number of state of the whole automaton.
   * @return the number of state of the whole automaton.
   */
  public int getStateNumber();
  
  /** Returns if the state pass as argument is a sink state.
   * @param state the state
   * @return true if state is a sink state
   */
  public boolean noOut(int state);
}

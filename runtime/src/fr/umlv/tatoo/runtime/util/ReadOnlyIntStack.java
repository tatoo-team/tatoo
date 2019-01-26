package fr.umlv.tatoo.runtime.util;

/** This class is used to offer a read only view of the state stack.
 *  Each state number is encoded as an int.
 * 
 * @author Remi
 */
public interface ReadOnlyIntStack {

  /** Returns last inserted (i.e. the current) state.
   * @return the current state.
   */
  public int last();

  /** Returns the state number at the given position.
   * @param pos the position.
   * @return the state number.
   */
  public int get(int pos);

  /** Returns the number of states in the stack.
   * @return the number of states in the stack.
   */
  public int size();

  /** Returns true is the number of states in the stack is zero.
   * @return true if the stack is empty, false otherwise.
   */
  public boolean isEmpty();

  /** Duplicates the current stack.
   * @return a new stack with the same states that the current one.
   */
  public ReadOnlyIntStack duplicate();
}
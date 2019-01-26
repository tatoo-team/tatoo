package fr.umlv.tatoo.runtime.parser;

/**
 * Listeners notified by the parser when sub-parser events occur.
 * 
 * @param <T> type of terminals
 * 
 * @author Remi
 * 
 * @see ParserListener
 */
public interface BranchingParserListener<T> {
  /**
   * Notifies that an enter action is performed by the parser.
   * 
   * @param terminal the corresponding magic terminal.
   * @return true if a sub-parser was entered.
   * 
   * @see Parser#push(Object, Object)
   */
  public boolean enter(T terminal);
  
  /**
   * Notifies that an exit action is performed by the parser.
   * This method should never return.
   */
  public void exit();
}

/*
 * Created on 29 juin 2005
 */
package fr.umlv.tatoo.runtime.parser;

/**
 * Parser action listener.
 * 
 * @author Julien Cervelle
 *
 * @param <T> type of terminals.
 * @param <N> type of non terminals.
 * @param <P> type of productions.
 * 
 * @see ParserErrorRecoveryListener
 * @see BranchingParserListener
 */
public interface ParserListener<T,N,P> {
  /**
   * Notifies that a shift action is performed by the parser.
   * 
   * @param terminal shifted terminal.
   */
  public void shift(T terminal);
  
  /**
   * Notifies that a reduce action is performed by the parser.
   * 
   * @param production reduced production.
   */
  public void reduce(P production);
  
  /**
   * Notifies that the accept action is performed by the parser
   * on the start non terminaL.
   * 
   * @param nonTerminal accepted non terminal. 
   * 
   * @see Parser#getStartNonTerminal()
   */
  public void accept(N nonTerminal);
}

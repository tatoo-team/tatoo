/*
 * Created on 14 dec. 2005
 *
 */
package fr.umlv.tatoo.runtime.parser;

/**
 * Parser error recovery listener.
 * 
 * @author Remi Forax
 * @param <T> type of terminal.
 * @param <N> type of non-terminal.
 *
 */
public interface ParserErrorRecoveryListener<T,N> {
  /**
   * Notifies listener that error recovery has discarded a non-terminal
   * and that corresponding metadata should be discarded.
   * @param nonTerminal the non-terminal.
   */
  public void popNonTerminalOnError(N nonTerminal);
  
  /**
   * Notifies listener that error recovery has discarded a terminal
   * and that corresponding metadata should be discarded.
   * @param terminal the terminal.
   */
  public void popTerminalOnError(T terminal);
}

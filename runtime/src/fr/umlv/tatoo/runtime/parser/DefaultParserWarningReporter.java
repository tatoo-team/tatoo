/*
 * SimpleErrorHandler.java
 *
 * Created: 13 nov. 2005
 */
package fr.umlv.tatoo.runtime.parser;

import java.util.Set;

import fr.umlv.tatoo.runtime.util.ReadOnlyIntStack;

/**
 * @author Remi
 * 
 * @param <T> type of terminals.
 * @param <N> type of non-terminals.
 * @param <P> type of productions.
 * @param <V> type of versions.
 */
public class DefaultParserWarningReporter<T,N,P,V> implements ParserWarningReporter<T,N,P,V> {

  /**
   * Default way to create a message from arguments
   * @param <T> terminals type
   * @param parser the parser
   * @param message the message to include in the output
   * @param terminal the terminal that caused this message
   * @return the full formatted message
   */
  public static <T> String formatMessage(SimpleParser<T> parser,String message,T terminal) {
    Set<? extends T> lookahead = parser.getLookahead();
    ReadOnlyIntStack stateStack = parser.getStateStack();
    if (lookahead==null)
      return String.format("%s on terminal %s with stack %s",message,terminal,stateStack);
    return String.format("%s on terminal %s with stack %s, expected %s",message,terminal,stateStack,lookahead);
  }

  public void handleWarning(Parser<T,N,P,V> parser, T terminal, String message) {
    throw new ParsingException(formatMessage(parser,message,terminal));
  }
}

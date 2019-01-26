/*
 * SimpleErrorHandler.java
 *
 * Created: 13 nov. 2005
 */
package fr.umlv.tatoo.runtime.lexer;

import fr.umlv.tatoo.runtime.buffer.LexerBuffer;
import fr.umlv.tatoo.runtime.buffer.LocationProvider;

/**
 * 
 * @param <B> type of the buffer.  
 * 
 * @author <a href="mailto:roussel@univ-mlv.fr">Gilles Roussel</a>
 * @version $Revision$
 */
public class DefaultLexerWarningReporter<B extends LexerBuffer> implements LexerWarningReporter<B> {

  /** Format the error message of the lexing exception.
   * 
   * @param lexer the lexer
   * @param message the message indicating the cause of the lexing exception
   * @return an error message with all the informations.
   */
  public static String formatMessage(Lexer<?> lexer, String message) {
    LexerBuffer buffer = lexer.getBuffer();
    int character = buffer.lastChar();
    String firstPart;
    if (character == -1)
      firstPart = message;
    else
      firstPart = String.format("%s \"%c\" (%d)",message,(char)character,character);
    
    LocationProvider location = buffer.getLocationProvider();
    if (location!=null) {
      return String.format("%s at %s at %s",
          firstPart,
          interval("line",location.getUnwindedLineNumber(),location.getLineNumber()),
          interval("column",location.getUnwindedColumnNumber(),location.getColumnNumber()));
    }
    return firstPart;
  }
  
  private static String interval(String name,int from,int to) {
    if (from == to)
      return String.format("%s %d",name,from);
    else
      return String.format("%ss %d-%d",name, from, to);
  }

  public void handleWarning(Lexer<? extends B> lexer, String message) {
    System.err.println(formatMessage(lexer,message));
  }
}

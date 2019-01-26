package fr.umlv.tatoo.runtime.lexer.rules;

import fr.umlv.tatoo.runtime.lexer.LexerErrorRecoveryPolicy;


/** Result values of {@link ActionProcessor#step},
 *  {@link ActionProcessor#stepClose} or
 *  {@link LexerErrorRecoveryPolicy#continueRecoverOnError}
 *  and
 *  {@link LexerErrorRecoveryPolicy#continueRecoverOnUnexpectedEndOfFile}.
 * 
 * @author Julien
 *
 * @see ActionProcessor
 * @see LexerErrorRecoveryPolicy
 */
public enum ProcessReturn {
  /** Need more tokens to answer
   */
  MORE, 
  
  /** A rule matched
   */
  TOKEN, 
  
  /** An error occurred
   */
  ERROR, 
  
  /** No more input
   */
  NOTHING
}

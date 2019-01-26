package fr.umlv.tatoo.runtime.parser;

import java.util.Set;

import fr.umlv.tatoo.runtime.util.IntArrayList;

/** 
 * Error recover policy that try to recover ...
 * An instance of this policy must be associated to only one parser
 * because it owns an error state specific to a parser.
 * It use an error recovery listener to inform that during recover phase
 *  some terminal or non terminal must be discarded in order to recover.
 *  
 * @author Remi Forax
 * @author Julien Cervelle
 *
 * @param <T> type of terminals.
 * @param <N> type of non-terminals.
 * @param <P> type of productions.
 * @param <V> type of versions.
 */
public class DefaultParserErrorRecoveryPolicy<T,N,P,V> extends ParserErrorRecoveryPolicy<T,N,P,V> {
  /** 
   * Creates the recover policy with a listener to signal
   * when pop terminal and non terminal during a recover phase.
   * @param errorRecoveryListener the listener.
   */
  public DefaultParserErrorRecoveryPolicy(ParserErrorRecoveryListener<? super T,? super N> errorRecoveryListener) {
    this.errorRecoveryListener=errorRecoveryListener;
  }
  
  @Override 
  public ActionReturn recoverOnError(Parser<T,N,P,V> parser,IntArrayList states,T terminal, String message) {      
      // here error must be non null
      //new Exception().printStackTrace();
      T error = parser.getTable().getErrorTerminal();
      if (error == null) {
        throw new IllegalStateException("this error recovery policy needs a terminal error");      
      }
      
      ParserTable<T,N,P,V> table = parser.getTable();
      Action<T,P,V>[] errorActions = table.getActions(error);
      V version = parser.getVersion();
      int top=states.size()-1;
      for(;;) {
        int last = states.get(top);
        Action<T,P,V> action = errorActions[last];
        // if action is an error
        if (action.isError(version)) {
          // pop state if there are states left in the stack
          if (top==1) {
            throw new ParsingException(Parser.formatMessage(parser, "No error token to shift", terminal));
          }
          StateMetadata<T,N,P,V> metadata = table.getMetadataForState(last);
          metadata.popVariableOnError(errorRecoveryListener);
          top--;
        }
        else {
          // else trim invalid states and perform action
          states.trim(top+1);
          
          if (action.doPerform(parser, error)==ActionReturn.NEXT) {
            // if token error is shifted, pass to continue step
            errorState=true;
            return ActionReturn.KEEP;
          }
        }
      }
  } 
  
  @Override
  public ActionReturn continueRecoverOnError(Parser<T,N,P,V> parser,IntArrayList states,T terminal) {
    assert errorState:"no continuation needed";

    if (terminal==null || parser.getTable().getActions(terminal)[states.last()].isError(parser.getVersion()))
      return ActionReturn.NEXT_ERROR;

    errorState=false;
    return ActionReturn.KEEP;

  }
  
  @Override
  public boolean closeParser(Parser<T,N,P,V> parser) {
    return true;
  }
  

  @Override
  public Set<? extends T> getLookahead(Parser<T,N,P,V> parser,Set<? extends T> proposedLookaheads) {
    /*if (!errorState)
      return proposedLookaheads;
    return null;*/
    return proposedLookaheads;
  }
  
  @Override
  public void reset() {
    errorState=false;
  }

  @Override
  public boolean errorRecoveryNeedsContinuation() {
    return errorState;
  }
  
  private boolean errorState;
  private final ParserErrorRecoveryListener<? super T,? super N> errorRecoveryListener;
  
}
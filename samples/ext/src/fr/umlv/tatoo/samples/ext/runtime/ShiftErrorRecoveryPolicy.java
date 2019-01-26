/*
 * Created on 8 avr. 2006
 *
 */
package fr.umlv.tatoo.samples.ext.runtime;

import java.util.Set;

import fr.umlv.tatoo.runtime.parser.ActionReturn;
import fr.umlv.tatoo.runtime.parser.DefaultParserErrorRecoveryPolicy;
import fr.umlv.tatoo.runtime.parser.ParserErrorRecoveryListener;
import fr.umlv.tatoo.runtime.parser.Parser;
import fr.umlv.tatoo.runtime.util.IntArrayList;

public class ShiftErrorRecoveryPolicy<T,N,P,V> extends DefaultParserErrorRecoveryPolicy<T,N,P,V>{

  public ShiftErrorRecoveryPolicy(ShiftErrorRecoveryListener<T> shiftErrorRecoveryListener,ParserErrorRecoveryListener<? super T,? super N> errorRecoveryListener) {
    super(errorRecoveryListener);
    this.shiftErrorRecoveryListener=shiftErrorRecoveryListener;
  }
  
  @Override
  public ActionReturn recoverOnError(Parser<T,N,P,V> parser, IntArrayList states, T terminal, String message) {
    Set<? extends T> set=shiftErrorRecoveryListener.authorizedShiftTerminal(
        states.last(),parser.getLookahead());
      
      //System.out.println("available terminals "+set);
      
      if (set.size()==1) {
        T guessedTerminal=set.iterator().next();
        
        //System.out.println("shift by "+guessedTerminal);
        
        shiftErrorRecoveryListener.pushTerminalOnError(guessedTerminal);
        //FIXME
        parser.step(guessedTerminal);
        return ActionReturn.KEEP;
      }
      
      return super.recoverOnError(parser, states, terminal, message);
  }
  
  private final ShiftErrorRecoveryListener<T> shiftErrorRecoveryListener;
}

/*
 * Created on 8 avr. 2006
 *
 */
package fr.umlv.tatoo.samples.ext.runtime;

import java.util.Set;

public interface ShiftErrorRecoveryListener<T> {
  public Set<? extends T> authorizedShiftTerminal(int state,Set<? extends T> lookaheads);
  public void pushTerminalOnError(T terminal);
}

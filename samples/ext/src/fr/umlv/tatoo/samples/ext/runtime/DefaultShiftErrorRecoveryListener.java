/*
 * Created on 8 avr. 2006
 *
 */
package fr.umlv.tatoo.samples.ext.runtime;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;

public class DefaultShiftErrorRecoveryListener<T extends Enum<T>> implements ShiftErrorRecoveryListener<T> {
  public DefaultShiftErrorRecoveryListener(Class<T> terminalType,TypeInformations<T> typeInformations) {
    this.typeInformations=typeInformations;
    
    this.authorizedTerminals=EnumSet.noneOf(terminalType);
    EnumSet<T> set=EnumSet.allOf(terminalType);
    if (typeInformations!=null) {
      for(Iterator<T> it=set.iterator();it.hasNext();)
        if (typeInformations.getTerminalType(it.next())!=null)
          it.remove();
    }
    this.recoverTerminals=set;
  }
  
  public Set<? extends T> authorizedShiftTerminal(int state, Set<? extends T> lookaheads) {
    authorizedTerminals.clear();
    authorizedTerminals.addAll(recoverTerminals);
    authorizedTerminals.retainAll(lookaheads);
    return authorizedTerminals;
  }
  
  public void pushTerminalOnError(T terminal) {
    if (authorizedTerminals.contains(terminal))
      return;
    Class<?> type=typeInformations.getTerminalType(terminal);
    //XXX Remi
  }
  
  private final EnumSet<T> authorizedTerminals;
  private final EnumSet<T> recoverTerminals;
  private final TypeInformations<T> typeInformations;
}

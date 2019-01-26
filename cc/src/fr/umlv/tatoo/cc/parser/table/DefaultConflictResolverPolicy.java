/*
 * Created on 22 feb. 2006
 */
package fr.umlv.tatoo.cc.parser.table;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import fr.umlv.tatoo.cc.parser.grammar.Priority;
import fr.umlv.tatoo.cc.parser.grammar.TerminalDecl;
import fr.umlv.tatoo.cc.parser.parser.ActionDecl;
import fr.umlv.tatoo.cc.parser.parser.ActionDeclFactory;
import fr.umlv.tatoo.cc.parser.table.AbstractConflictDiagnosisReporter.ErrorKey;
import fr.umlv.tatoo.cc.parser.table.AbstractConflictDiagnosisReporter.InfoKey;
import fr.umlv.tatoo.cc.parser.table.AbstractConflictDiagnosisReporter.WarningKey;

public class DefaultConflictResolverPolicy extends ConflictResolverPolicy {

  // null means one has nopriority -> conflict between all
  private <A extends ActionDecl> HashSet<ActionEntry<A>> selectPrioritiers(Set<ActionEntry<A>> input) {
    HashSet<ActionEntry<A>> output = new HashSet<ActionEntry<A>>();
    Iterator<ActionEntry<A>> iterator = input.iterator();
    ActionEntry<A> first = iterator.next();
    Priority currentPriority = first.getPriority();
    output.add(first);
    if (iterator.hasNext() && currentPriority == Priority.getNoPriority())
      return null;
    while (iterator.hasNext()) {
      ActionEntry<A> entry = iterator.next();
      Priority newPriority = entry.getPriority();
      if (newPriority == Priority.getNoPriority())
        return null;
      int compare = currentPriority.comparePriorityValues(newPriority);
      if (compare == 0) {
        output.add(entry);
      } else if (compare < 0) {
        output.clear();
        output.add(entry);
        currentPriority = newPriority;
      }
    }
    return output;
  }



  @Override
  public <A extends ActionDecl> A priorityAction(Class<A> type, AbstractConflictDiagnosisReporter reporter,
      ActionDeclFactory actionFactory, Set<ActionEntry<A>> actions,
      NodeDecl<?> node, TerminalDecl terminal, TerminalDecl eof) {
    if (actions.isEmpty())
      return null;

    HashSet<ActionEntry<A>> newActions = selectPrioritiers(actions);
    if (newActions==null) {
      reporter.conflict(ErrorKey.no_priority,node,terminal,actions,eof);
      return null;
    }
    if (newActions.size() == 1)
      return newActions.iterator().next().getAction();
    // rules where it works:
    // two actions: use associativity
    // if more or associativity fails:
    // only one shift/enter/exit -> use it with warning
    // else conflict
    if (newActions.size() == 2) {
      Iterator<ActionEntry<A>> iterator = newActions.iterator();
      ActionEntry<A> one = iterator.next();
      ActionEntry<A> two = iterator.next();
      if ((one.getAction().isReduce() ^ two.getAction().isReduce())) {
        assert one.getPriority().getAssociativity() == two.getPriority()
        .getAssociativity() : "same priority implies same associativity";
        //TODO log for returns in the switch: chosen by associativity
        switch (one.getPriority().getAssociativity()) {
        case NONE:
          break;
        case LEFT:
          reporter.conflict(InfoKey.associativity_reduce, node,terminal,newActions, eof);
          if (one.getAction().isReduce()) {
            return one.getAction();
          }
          else {
            return two.getAction();
          }
        case RIGHT:
          reporter.conflict(InfoKey.associativity_shift, node,terminal,newActions, eof);
          if (two.getAction().isReduce())
            return one.getAction();
          else
            return two.getAction();
        case NON_ASSOCIATIVE:
          reporter.conflict(InfoKey.associativity_error, node,terminal,newActions, eof);
          return type.cast(actionFactory.getNonAssociativeError());
        default: throw new AssertionError("Unknown associativity");
        }
      }
    }
    A shift = null;
    for (ActionEntry<A> entry : newActions) {
      if (!entry.getAction().isReduce()) {
        if (shift == null) {
          shift = entry.getAction();
        }
        else {
          reporter.conflict(ErrorKey.shift_shift, node,terminal,newActions, eof);
          return null;
        }
      }
    }
    if (shift == null) {
      reporter.conflict(ErrorKey.reduce_reduce, node,terminal,newActions, eof);
      return null;
    }

    reporter.conflict(WarningKey.shift_by_default, node,terminal,newActions, eof);
    return shift;
  }
}

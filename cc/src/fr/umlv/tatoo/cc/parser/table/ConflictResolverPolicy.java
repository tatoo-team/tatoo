/*
 * Created on 22 febr. 2006
 */
package fr.umlv.tatoo.cc.parser.table;

import java.util.Set;

import fr.umlv.tatoo.cc.parser.grammar.Priority;
import fr.umlv.tatoo.cc.parser.grammar.TerminalDecl;
import fr.umlv.tatoo.cc.parser.parser.ActionDecl;
import fr.umlv.tatoo.cc.parser.parser.ActionDeclFactory;

public abstract class ConflictResolverPolicy {
  /**
   * Computes the priority action from a set
   * @param actions the set actions
   * @param actionFactory factory for actions
   * @param node the node corresponding to the actions, for logging purpose
   * @param terminal the terminal corresponding to the actions, for logging purpose.
   *        if the action set is a standard one, there is only one terminal in the set,
   *        if the action set is a branching action set, this set contains all
   *        conflicting branching terminals
   * @param eof the end-of-input terminal
   * @param <A> type of action from Regular/BranchTableActionDecl (cast can be safely performed to
   *            A from a type which implements both interfaces)
   *
   * @return the priority action or null in case of unresolved conflict.
   */
  public abstract <A extends ActionDecl> A priorityAction(Class<A> type, AbstractConflictDiagnosisReporter reporter,ActionDeclFactory actionFactory,Set<ActionEntry<A>> actions,NodeDecl<?> node,TerminalDecl terminal,TerminalDecl eof);
 
  public interface ActionEntry<A extends ActionDecl> {
    A getAction();
    Priority getPriority();
  }
}

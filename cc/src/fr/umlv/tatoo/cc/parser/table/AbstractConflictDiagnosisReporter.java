/*
 * Created on 22 feb. 2006
 */
package fr.umlv.tatoo.cc.parser.table;

import java.util.ResourceBundle;
import java.util.Set;

import fr.umlv.tatoo.cc.common.log.DiagnosisReporter;
import fr.umlv.tatoo.cc.common.log.UserDefinedLevelMap;
import fr.umlv.tatoo.cc.parser.grammar.TerminalDecl;
import fr.umlv.tatoo.cc.parser.table.ConflictResolverPolicy.ActionEntry;

public abstract class AbstractConflictDiagnosisReporter extends DiagnosisReporter { 
  public interface Key extends DiagnosisReporter.Key {
    public Key getAliasKey();
  }
  public enum ErrorKey implements Key {
    shift_shift,
    reduce_reduce_branch,
    reduce_reduce(reduce_reduce_branch),
    no_priority_branch,
    same_priority_branch,
    no_priority(no_priority_branch)
    ;

    ErrorKey() {
      this(null);
    }
    ErrorKey(ErrorKey branchKey) {
      this.aliasKey=branchKey;
    }
    public Level defaultLevel() {
      return Level.ERROR;
    }
    public Key getAliasKey() {
      return (aliasKey==null)?this:aliasKey;
    }
    private final ErrorKey aliasKey;
  }
  
  public enum WarningKey implements Key {
    shift_by_default,
    ;
    
    public Level defaultLevel() {
      return Level.WARNING;
    }
    public Key getAliasKey() {
      return this;
    }
  }
  
  public enum InfoKey implements Key {
    associativity_shift,
    associativity_reduce,
    associativity_error
    ;
    
    public Level defaultLevel() {
      return Level.INFO;
    }
    public Key getAliasKey() {
      return this;
    }
  }
  
  protected AbstractConflictDiagnosisReporter(UserDefinedLevelMap userDefinedLevelMap) {
    super(userDefinedLevelMap);
  }
  
  public void conflict(Key key, NodeDecl<?> node,
      TerminalDecl terminal, Set<? extends ActionEntry<?>> actions,
      TerminalDecl eof) {
    if (terminal==null) {
      branchConflict(key.getAliasKey(), node, actions, eof);
    } else {
      terminalConflict(key,node,terminal,actions,eof);
    }
  }
  

  protected abstract void terminalConflict(Key key, NodeDecl<?> node,
      TerminalDecl terminal,
      Set<? extends ActionEntry<?>> actions,
      TerminalDecl eof);

  protected abstract void branchConflict(Key key, NodeDecl<?> node,
      Set<? extends ActionEntry<?>> actions,
      TerminalDecl eof);
  
  @Override
  protected ResourceBundle getBundle() {
    return BUNDLE;
  }
  
  private static final ResourceBundle BUNDLE=
    ResourceBundle.getBundle("fr.umlv.tatoo.cc.parser.table.diagnosis");
}

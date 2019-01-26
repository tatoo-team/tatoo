/*
 * Created on 22 feb. 2006
 */
package fr.umlv.tatoo.cc.parser.table;

import java.util.Set;

import fr.umlv.tatoo.cc.common.log.Info;
import fr.umlv.tatoo.cc.common.log.UserDefinedLevelMap;
import fr.umlv.tatoo.cc.parser.grammar.TerminalDecl;

public class LogInfoConflictDiagnosisReporter extends AbstractConflictDiagnosisReporter { 
  public LogInfoConflictDiagnosisReporter(UserDefinedLevelMap userDefinedLevelMap) {
    super(userDefinedLevelMap);
  }
  
  @Override
  public void terminalConflict(Key key, NodeDecl<?> node,
      TerminalDecl terminal,
      Set<? extends ConflictResolverPolicy.ActionEntry<?>> actions,
      TerminalDecl eof) {
    reportConflict(key,node.getStateNo(),terminal.getId(),node.pathInfo());
  }

  @Override
  public void branchConflict(Key key, NodeDecl<?> node,
      Set<? extends ConflictResolverPolicy.ActionEntry<?>> actions,
      TerminalDecl eof) {
    reportConflict(key,node.getStateNo(),node.pathInfo());
  }
  
  private void reportConflict(Key key, Object... context) {
    String message=formatMessage(key,context);
    switch(getLevel(key)) {
      case ERROR:
        Info.error(message).report();
        return;
      case WARNING:
        Info.warning(message).report();
        return;
      default:
    }
    setErrorIfNedded(key);
  }
}

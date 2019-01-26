package fr.umlv.tatoo.cc.ebnf;

import fr.umlv.tatoo.cc.common.log.Info;
import fr.umlv.tatoo.cc.common.log.UserDefinedLevelMap;
import fr.umlv.tatoo.cc.ebnf.ast.analysis.AbstractASTDiagnosisReporter;
import fr.umlv.tatoo.runtime.node.Node;

public class LogInfoASTDiagnosisReporter extends AbstractASTDiagnosisReporter {
  public LogInfoASTDiagnosisReporter(UserDefinedLevelMap userDefinedLevelMap) {
    super(userDefinedLevelMap);
  }

  @Override
  protected void report(Key key,Node node,Object... data) {
    LineColumnLocation location=node.getAttribute(LineColumnLocation.class);
    
    String message;
    switch(getLevel(key)) {
      case ERROR:
        message=formatMessage(key,data);
        Info.error(message).line(location.getLineNumber()).column(location.getColumnNumber()).report();
        return;
      case WARNING:
        message=formatMessage(key,data);
        Info.warning(message).line(location.getLineNumber()).column(location.getColumnNumber()).report();
        return;
      default:
    }
  }
}

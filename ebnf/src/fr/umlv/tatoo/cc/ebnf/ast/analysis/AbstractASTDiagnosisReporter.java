package fr.umlv.tatoo.cc.ebnf.ast.analysis;

import java.util.ResourceBundle;

import fr.umlv.tatoo.cc.common.log.DiagnosisReporter;
import fr.umlv.tatoo.cc.common.log.UserDefinedLevelMap;
import fr.umlv.tatoo.runtime.node.Node;

public abstract class AbstractASTDiagnosisReporter extends DiagnosisReporter implements ASTDiagnosisReporter {
  
  protected AbstractASTDiagnosisReporter(UserDefinedLevelMap userDefinedLevelMap) {
    super(userDefinedLevelMap);
  }

  public void signal(Key key,Node node,Object... data) {
    report(key,node,data);
    setErrorIfNedded(key);
  }
  
  protected abstract void report(Key key,Node node,Object... data);
  
  @Override
  protected ResourceBundle getBundle() {
    return BUNDLE;
  }
  
  private static final ResourceBundle BUNDLE=
    ResourceBundle.getBundle("fr.umlv.tatoo.cc.ebnf.ast.analysis.diagnostic");
}

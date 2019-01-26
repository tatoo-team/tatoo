package fr.umlv.tatoo.samples.pseudo.comp.analysis;

import java.util.HashMap;

import fr.umlv.tatoo.samples.pseudo.ast.Node;
import fr.umlv.tatoo.samples.pseudo.comp.FunctionScope;
import fr.umlv.tatoo.samples.pseudo.comp.Scope;
import fr.umlv.tatoo.samples.pseudo.comp.Struct;
import fr.umlv.tatoo.samples.pseudo.comp.SymbolMap;
import fr.umlv.tatoo.samples.pseudo.comp.Type;
import fr.umlv.tatoo.samples.pseudo.comp.TypeScope;

public class ScriptEnv {
  private final Scope<Struct> structDefScope=
    new Scope<Struct>();
  private final TypeScope typeScope=
    new TypeScope();
  private final HashMap<Node,Type> exprTypeMap=
    new HashMap<Node,Type>();
  private final FunctionScope functionDefScope=
    new FunctionScope();
  private final SymbolMap symbolMap=
    new SymbolMap();
  private final ErrorReporter errorReporter;
  
  public ScriptEnv(ErrorReporter errorReporter) {
    this.errorReporter=errorReporter;
  }
  
  public Scope<Struct> getStructScope() {
    return structDefScope;
  }
  
  public TypeScope getTypeScope() {
    return typeScope;
  }
  
  public HashMap<Node,Type> getExprTypeMap() {
    return exprTypeMap;
  }
  
  public FunctionScope getFunctionScope() {
    return functionDefScope;
  }
  
  public SymbolMap getSymbolMap() {
    return symbolMap;
  }
  
  public void error(Node node, String message) {
    errorReporter.error(node,message);
  }
  
  public void warning(Node node, String message) {
    errorReporter.warning(node,message);
  }
}

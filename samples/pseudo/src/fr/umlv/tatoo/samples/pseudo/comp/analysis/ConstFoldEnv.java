package fr.umlv.tatoo.samples.pseudo.comp.analysis;

import fr.umlv.tatoo.samples.pseudo.ast.Node;
import fr.umlv.tatoo.samples.pseudo.comp.LocalVar;

public class ConstFoldEnv {
  private final ConstantMap<Node> constFoldMap=
    new ConstantMap<Node>();
  private final ConstantMap<LocalVar> constFoldVarMap=
    new ConstantMap<LocalVar>(); 
  
  public ConstantMap<Node> getConstFoldMap() {
    return constFoldMap;
  }
  
  public ConstantMap<LocalVar> getConstFoldVarMap() {
    return constFoldVarMap;
  }
}

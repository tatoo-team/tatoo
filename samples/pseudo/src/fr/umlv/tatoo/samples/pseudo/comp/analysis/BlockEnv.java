package fr.umlv.tatoo.samples.pseudo.comp.analysis;

import fr.umlv.tatoo.samples.pseudo.comp.LocalVar;
import fr.umlv.tatoo.samples.pseudo.comp.Scope;
import fr.umlv.tatoo.samples.pseudo.comp.Symbol;
import fr.umlv.tatoo.samples.pseudo.comp.Type;

public class BlockEnv implements Symbol {
  private final ScriptEnv scriptEnv;
  private final Scope<LocalVar> localVarScope;
  private final Type expectedReturnType;
  private final LoopStack<Boolean> loopStack;
  
  public BlockEnv(ScriptEnv scriptEnv, Type expectedReturnType) {
    this.scriptEnv=scriptEnv;
    this.localVarScope=new Scope<LocalVar>();
    this.expectedReturnType=expectedReturnType;
    this.loopStack=new LoopStack<Boolean>();
  }
  
  public BlockEnv(ScriptEnv scriptEnv, BlockEnv blockEnv) {
    this.scriptEnv=scriptEnv;
    this.localVarScope=new Scope<LocalVar>(blockEnv.localVarScope);
    this.expectedReturnType=blockEnv.expectedReturnType;
    this.loopStack=blockEnv.loopStack;
  }
  
  public ScriptEnv getScriptEnv() {
    return scriptEnv;
  }
  public Scope<LocalVar> getLocalVarScope() {
    return localVarScope;
  }
  public Type getExpectedReturnType() {
    return expectedReturnType;
  }
  public LoopStack<Boolean> getLoopStack() {
    return loopStack;
  }
}

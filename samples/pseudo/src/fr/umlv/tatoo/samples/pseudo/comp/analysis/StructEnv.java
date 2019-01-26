package fr.umlv.tatoo.samples.pseudo.comp.analysis;

import fr.umlv.tatoo.samples.pseudo.comp.Struct;

public class StructEnv {
  private final ScriptEnv scriptEnv;
  private final Struct struct;
  
  public StructEnv(ScriptEnv scriptEnv,Struct struct) {
    this.scriptEnv=scriptEnv;
    this.struct=struct;
  }
  
  public ScriptEnv getScriptEnv() {
    return scriptEnv;
  }
  
  public Struct getStruct() {
    return struct;
  }
}

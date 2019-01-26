package fr.umlv.tatoo.samples.pseudo.comp.analysis;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import fr.umlv.tatoo.samples.pseudo.ast.Funcall;
import fr.umlv.tatoo.samples.pseudo.ast.FunctionDecl;
import fr.umlv.tatoo.samples.pseudo.ast.Member;
import fr.umlv.tatoo.samples.pseudo.ast.MemberFunction;
import fr.umlv.tatoo.samples.pseudo.ast.Script;
import fr.umlv.tatoo.samples.pseudo.comp.Function;
import fr.umlv.tatoo.samples.pseudo.comp.SymbolMap;
import fr.umlv.tatoo.samples.pseudo.comp.UserFunction;
import fr.umlv.tatoo.samples.pseudo.comp.SymbolMap.Binding;

public class CoverageVisitor extends TraversalVisitor<CoverageVisitor.CoverageEnv> {
  private final ScriptEnv scriptEnv;
  
  static class CoverageEnv {
    private final HashMap<UserFunction,Boolean> accessibleMap=
      new LinkedHashMap<UserFunction,Boolean>();
    
    public void markUnvisited(UserFunction userFunction) {
      accessibleMap.put(userFunction,false);
    }
    public boolean isVisited(UserFunction userFunction) {
      return accessibleMap.get(userFunction);
    }
    public void markVisited(UserFunction userFunction) {
      accessibleMap.put(userFunction,true);
    }
    
    public void reportUnvisited(ScriptEnv scriptEnv) {
      SymbolMap symbolMap=scriptEnv.getSymbolMap();
      for(Map.Entry<UserFunction,Boolean> entry:accessibleMap.entrySet()) {
        // if not visited
        if (entry.getValue()==false) {
          UserFunction userFunction=entry.getKey();
          FunctionDecl functionDecl=(FunctionDecl)symbolMap.getBinding(userFunction).getDefinition();
          scriptEnv.warning(functionDecl.getId(),"function "+userFunction+" is not accessible from the script block");
        }
      }
    }
  }
  
  public CoverageVisitor(ScriptEnv scriptEnv) {
    this.scriptEnv=scriptEnv;
  }
  
  @Override
  public Void visit(Script script,CoverageEnv unused) {
    CoverageEnv coverageEnv=new CoverageEnv();
    
    SymbolMap symbolMap=scriptEnv.getSymbolMap();
    for(Member member:script.getMemberStar()) {
      if (member instanceof MemberFunction) {
        FunctionDecl functionDecl=((MemberFunction)member).getFunctionDecl();
        UserFunction userFunction=(UserFunction)symbolMap.getSymbol(functionDecl);
        coverageEnv.markUnvisited(userFunction);
      }
    }
    
    script.getBlock().accept(this,coverageEnv);
    
    coverageEnv.reportUnvisited(scriptEnv);
    return null;
  }
  
  @Override
  public Void visit(Funcall funcall,CoverageEnv coverageEnv) {
    SymbolMap symbolMap=scriptEnv.getSymbolMap();
    Function function=(Function)symbolMap.getSymbol(funcall);
    if (!(function instanceof UserFunction)) {
      return null;
    }
    UserFunction userFunction=(UserFunction)function;
    if (coverageEnv.isVisited(userFunction)) {
      return null;
    }
    coverageEnv.markVisited(userFunction);
    Binding binding=symbolMap.getBinding(userFunction);
    FunctionDecl functionDecl=(FunctionDecl)binding.getDefinition();
    functionDecl.accept(this,coverageEnv);
    return null;
  }
}

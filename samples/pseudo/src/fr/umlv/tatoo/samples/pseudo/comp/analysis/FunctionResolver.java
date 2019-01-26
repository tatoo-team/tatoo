package fr.umlv.tatoo.samples.pseudo.comp.analysis;

import java.util.List;
import java.util.Map;

import fr.umlv.tatoo.samples.pseudo.ast.Node;
import fr.umlv.tatoo.samples.pseudo.comp.Function;
import fr.umlv.tatoo.samples.pseudo.comp.FunctionScope;
import fr.umlv.tatoo.samples.pseudo.comp.Type;
import fr.umlv.tatoo.samples.pseudo.comp.TypeScope;

public class FunctionResolver {
  public static Function resolveFunction(ScriptEnv scriptEnv,Node node, String name, List<Type> parameterTypeList) {
    FunctionScope functionScope=scriptEnv.getFunctionScope();
    
    // first, get selector map
    Map<List<Type>,Function> selectorMap=functionScope.selectorMap(name);
    if (selectorMap==null) {
      scriptEnv.error(node,"no function named "+name+" found");
      // error recovery
      return null;
    }
    
    // second, compute applicable functions
    TypeScope typeScope=scriptEnv.getTypeScope();
    List<Function> applicableList=functionScope.applicableList(typeScope,selectorMap,parameterTypeList);
    if (applicableList.isEmpty()) {
      scriptEnv.error(node,"no function "+name+" "+parameterTypeList+" with matching parameter types found");
      // error recovery
      return null;
    }
    
    if (applicableList.size()==1)
      return applicableList.iterator().next();
    
    // third, find the most specific
    Function function=functionScope.mostSpecific(typeScope,applicableList);
    if (function==null) {
      scriptEnv.error(node,"no function is the most specific among "+applicableList);
      // error recovery
    }
    
    return function;
  }
  
}

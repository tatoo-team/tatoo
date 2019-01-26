package fr.umlv.tatoo.samples.pseudo.comp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FunctionScope {
  private final HashMap<String,Map<List<Type>,Function>> functionMap=
    new HashMap<String,Map<List<Type>,Function>>();
  
  public Map<List<Type>,Function> selectorMap(String name) {
    return functionMap.get(name); 
  }
  
  public List<Function> applicableList(TypeScope typeScope, Map<List<Type>,Function> selectorMap, List<Type> parameterTypeList) {
    //shortcut
    Function function=selectorMap.get(parameterTypeList);
    if (function!=null)
      return Collections.singletonList(function);
    
    ArrayList<Function> applicableList=new ArrayList<Function>();
    for(Function f:selectorMap.values()) {
      if (Types.isAssignable(typeScope,f.getParameterTypeList(),parameterTypeList))
        applicableList.add(f);
    }
    return applicableList;
  }
  
  // there is at least one function in the list
  public Function mostSpecific(TypeScope typeScope,List<Function> applicableList) {
    loop: for(Function mostSpecific:applicableList) {
      List<Type> mostSpecificTypeList=mostSpecific.getParameterTypeList();

      for(Function function:applicableList) {
        if (!Types.isAssignable(typeScope,function.getParameterTypeList(),mostSpecificTypeList))
          continue loop;
      }

      return mostSpecific;
    }
    return null;
  }
  
  // look up for an exact match
  public Function lookupFunction(String name, List<Type> parameterTypeList) {
    Map<List<Type>,Function> selectorMap=selectorMap(name);   
    if (selectorMap==null)
      return null;
    return selectorMap.get(parameterTypeList);
  }
  
  public void add(Function function) {
    String name=function.getName();
    Map<List<Type>,Function> selectorMap=selectorMap(name);
    if (selectorMap==null) {
      selectorMap=new LinkedHashMap<List<Type>,Function>();
      functionMap.put(name,selectorMap);
    }
    selectorMap.put(function.getParameterTypeList(),function);
  }
}

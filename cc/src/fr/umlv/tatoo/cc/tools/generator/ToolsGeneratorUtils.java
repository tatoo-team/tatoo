package fr.umlv.tatoo.cc.tools.generator;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import fr.umlv.tatoo.cc.common.generator.ObjectId;
import fr.umlv.tatoo.cc.common.generator.Type;
import fr.umlv.tatoo.cc.parser.grammar.EBNFSupport;
import fr.umlv.tatoo.cc.parser.grammar.VariableDecl;

public class ToolsGeneratorUtils {
  private ToolsGeneratorUtils()  {
    // prevent instantiation
  }
  
  public static String toUpperCase(ObjectId object) {
    return toUpperCase(object.getId());
  }
  
  public static String toUpperCase(String id) {
    StringBuilder builder=new StringBuilder();
    for(String s:id.split("_")) {
      capitalize(s,builder);
    }
    return builder.toString();
  }
  
  private static void capitalize(String name,StringBuilder builder) {
    builder.append(Character.toUpperCase(name.charAt(0))).append(name.substring(1));
  }
  
  public static abstract class ParamClosure<V> {
    private final Naming naming=new Naming();
    
    public String name(String id) {
      return naming.name(id);
    }
    
    /** Apply a typed variable with a name and a value.
     * @param type type of the variable
     * @param variable the variable
     * @param value the value of last call
     * @return the new value.
     */
    V apply(Type type, VariableDecl variable, V value) {
      return apply(type,variable,name(variable.getId()),value);
    }
    
    /** Apply a typed variable with a name and a value.
     * @param type type of the variable
     * @param variable the variable
     * @param name the name of the variable
     * @param value the value of last call
     * @return the new value.
     */
    public V apply(Type type, VariableDecl variable, String name, V value) {
      return apply(type,name,value);
    }
    
    /** Apply a typed variable with a name and a value.
     * @param type type of the variable
     * @param name the name of the variable
     * @param value the value of last call
     * @return the new value.
     */
    public V apply(Type type, String name, V value) {
      return value;
    }
  }
  
  public static abstract class VarParamClosure<V> extends ParamClosure<V> {
    private final EBNFSupport support;
    
    public VarParamClosure(EBNFSupport support) {
      this.support=support;
    }
    
    private static boolean isInteger(String suffix) {
      if (suffix.isEmpty())
        return true;
      return new Scanner(suffix).hasNextInt();
    }
    
    @Override
    V apply(Type type, VariableDecl variable, V value) {
      String name=variable.getId();
      if (!variable.isTerminal()) {
        if (support.getStarNonTerminals().contains(variable) ||
            support.getOptionalNonTerminals().contains(variable)) {
          int index=name.lastIndexOf('_');
          if (index!=-1 && isInteger(name.substring(index+1))) {
            name=name.substring(0,index); 
          }   
        }
      }
      name=name(name);
      
      return apply(type,variable,name,value);
    }
  }
  
  public static <V> V foreach(List<? extends VariableDecl> rhs,Map<VariableDecl, Type> variableTypeMap, ParamClosure<V> closure, V value) {
    for (VariableDecl v:rhs) {
      Type t = variableTypeMap.get(v);
      value=closure.apply(t,v,value);
    }
    return value;
  }
  
  public static <V> V foreachNonNull(List<? extends VariableDecl> rhs,Map<VariableDecl, Type> variableTypeMap, ParamClosure<V> closure, V value) {
    for (VariableDecl v:rhs) {
      Type t = variableTypeMap.get(v);
      if (t == null || t.isVoid()){
        continue;
      }
      value=closure.apply(t,v,value);
    }
    return value;
  }
  
  public static List<ParamDecl> getNonNullParameterList(List<? extends VariableDecl> rhs,Map<VariableDecl, Type> variableTypeMap) {
    return foreachNonNull(rhs,variableTypeMap,new ParamClosure<LinkedList<ParamDecl>>() {
      @Override
      public LinkedList<ParamDecl> apply(Type type,String name,LinkedList<ParamDecl> list) {
        list.addLast(new ParamDecl(type,name));
        return list;
      }
    },new LinkedList<ParamDecl>());
  }
  
  public static List<ParamDecl> getNonNullParameterReverseList(List<? extends VariableDecl> rhs,Map<VariableDecl, Type> variableTypeMap) {
    return foreachNonNull(rhs,variableTypeMap,new ParamClosure<LinkedList<ParamDecl>>() {
      @Override
      public LinkedList<ParamDecl> apply(Type type,String name,LinkedList<ParamDecl> list) {
        list.addFirst(new ParamDecl(type,name));
        return list;
      }
    },new LinkedList<ParamDecl>());
  }
  
  // don't want to use foreachNonNull, because i don't want to use exception to implement non local return
  public static boolean notAllNull(List<? extends VariableDecl> rhs,Map<VariableDecl, Type> variableTypeMap) {
    for (VariableDecl v:rhs) {
      Type t = variableTypeMap.get(v);
      if (t!=null)
        return true;
    }
    return false;
  }
}

package fr.umlv.tatoo.samples.pseudo.comp;

import java.util.ArrayList;
import java.util.List;

public class UserFunction extends Function {
  private final List<Parameter> parameterList;
  
  public static class Parameter extends AbstractLocalVar {
    public Parameter(String name,Type type) {
      super(name,type);
    }
 
    @Override
    public boolean isConstant() {
      return true;
    }
  }
  
  public UserFunction(String name, Type returnType,
      List<Parameter> parameterList) {
    super(name,returnType,parameterTypeList(parameterList));
    this.parameterList=parameterList;
  }
  
  private static ArrayList<Type> parameterTypeList(List<Parameter> parameterList) {
    ArrayList<Type> parameterTypeList=
      new ArrayList<Type>();
    for(Parameter parameter:parameterList)
      parameterTypeList.add(parameter.getType());
    return parameterTypeList;
  }
  
  @Override
  public boolean isBuiltin() {
    return false;
  }
  
  public List<Parameter> getParameterList() {
    return parameterList;
  }
  
  @Override
  public String toString() {
    if (parameterList.isEmpty())
      return super.toString();
    
    StringBuilder builder=new StringBuilder();
    for(Parameter parameter:parameterList) {
      builder.append(parameter).append(", ");
    }
    builder.setLength(builder.length()-2);
    return getReturnType()+" "+getName()+'('+builder+')';
  }
}

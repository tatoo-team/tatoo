package fr.umlv.tatoo.samples.pseudo.comp;

import java.util.List;

public abstract class Function extends Scope.AbstractItem implements Symbol {
  private final Type returnType;
  private final List<Type> parameterTypeList;
  
  public Function(String name,Type returnType,
      List<Type> parameterTypeList) {
    super(name);
    this.returnType=returnType;
    this.parameterTypeList=parameterTypeList;
  }
  
  public abstract boolean isBuiltin();
  
  public Type getReturnType() {
    return returnType;
  }
  public List<Type> getParameterTypeList() {
    return parameterTypeList;
  }
  
  @Override
  public String toString() {
    if (parameterTypeList.isEmpty())
      return getReturnType()+" "+getName()+"()";
    
    StringBuilder builder=new StringBuilder();
    for(Type type:parameterTypeList) {
      builder.append(type).append(", ");
    }
    builder.setLength(builder.length()-2);
    return getReturnType()+" "+getName()+'('+builder+')';
  }
}

package fr.umlv.tatoo.samples.pseudo.comp;

import java.lang.reflect.Method;
import java.util.List;

import fr.umlv.tatoo.samples.pseudo.comp.builtin.SideEffect;

public class BuiltInFunction extends Function {
  private final OperatorKind operatorKind;
  private final Method reflectMethod;

  public enum OperatorKind {
    eq("=="), neq("!="),
    lt("<"), le("<="),
    gt(">"), ge(">="),
    neg("!"),
    band("&&"), bor("||"),
    plus("+"), minus("-"),
    star("*"), slash("/"),
    mod("%"),
    print
    ;
    private OperatorKind(String operator) {
      this.operator=operator;
    }
    private OperatorKind() {
      this(null);
    }
    
    public String getOperator() {
      return (operator==null)?name():operator;
    }
    
    private final String operator;
  }
  
  public BuiltInFunction(String name,Type returnType,List<Type> parameterList,Method refectMethod) {
    super(name,returnType,parameterList);
    this.operatorKind=null;
    this.reflectMethod=refectMethod;
  }
  
  public BuiltInFunction(OperatorKind kind,Type returnType,List<Type> parameterList,Method refectMethod) {
    super(kind.getOperator(),returnType,parameterList);
    this.operatorKind=kind;
    this.reflectMethod=refectMethod;
  }
  
  @Override
  public boolean isBuiltin() {
    return true;
  }
  
  public boolean hasSideEffect() {
    return reflectMethod.isAnnotationPresent(SideEffect.class);
  }
  
  /**
   * @return may be null
   */
  public OperatorKind getOperatorKind() {
    return operatorKind;
  }
  
  public Method getReflectMethod() {
    return reflectMethod;
  }
}

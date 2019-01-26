package fr.umlv.tatoo.samples.pseudo.comp.builtin;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fr.umlv.tatoo.samples.pseudo.comp.BuiltInFunction;
import fr.umlv.tatoo.samples.pseudo.comp.Type;
import fr.umlv.tatoo.samples.pseudo.comp.TypeScope;
import fr.umlv.tatoo.samples.pseudo.comp.TypeScope.TypeKind;

public class Builtins {
  private Builtins() {
    // static helper
  }
  public static void appendBuiltinFunctions(List<BuiltInFunction> functionList,TypeScope typeScope,Class<?> builtinClass) {
    for(Method method:builtinClass.getMethods()) {
      if (!Modifier.isStatic(method.getModifiers())) {
        continue;
      }
      
      Type returnType=convertType(typeScope,method.getReturnType());
      
      ArrayList<Type> parameterTypeList=new ArrayList<Type>();
      for(Class<?> parameterClass:method.getParameterTypes()) {
        parameterTypeList.add(convertType(typeScope,parameterClass));
      }
      
      if (method.getReturnType()==void.class && !method.isAnnotationPresent(SideEffect.class)) {
        throw new AssertionError("method "+method+" that returns void must be annotated with SideEffect");
      }
      
      BuiltInFunction builtInFunction;
      BuiltinOperator operator=method.getAnnotation(BuiltinOperator.class);
      if (operator!=null) {
        builtInFunction=new BuiltInFunction(operator.value(),
            returnType,parameterTypeList,method);
      } else {
        builtInFunction=new BuiltInFunction(method.getName(),
            returnType,parameterTypeList,method);
      }
      functionList.add(builtInFunction);
    }
  }
  
  private static Type convertType(TypeScope typeScope,Class<?> clazz) {
    if (clazz.isArray()) {
      return convertType(typeScope,clazz.getComponentType()).getTypeArray();
    }
    TypeKind kind=CLASS_TO_TYPE_MAP.get(clazz);
    if (kind==null) {
      throw new IllegalStateException("no mapping for "+clazz); 
    }
    return typeScope.getType(kind);
  }
  
  private static final HashMap<Class<?>,TypeKind> CLASS_TO_TYPE_MAP;
  static {
    HashMap<Class<?>,TypeScope.TypeKind> map=
      new HashMap<Class<?>,TypeScope.TypeKind>();
    map.put(Boolean.class,TypeKind.BOOLEAN);
    map.put(Integer.class,TypeKind.INTEGER);
    map.put(Character.class,TypeKind.CHARACTER);
    map.put(Double.class,TypeKind.DOUBLE);
    map.put(String.class,TypeKind.STRING);
    map.put(void.class,TypeKind.VOID);
    map.put(Object.class,TypeKind.ANY);
    CLASS_TO_TYPE_MAP=map;
  } 
}

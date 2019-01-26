package fr.umlv.tatoo.samples.pseudo.comp;

import java.util.Iterator;
import java.util.List;

public class Types {
  private Types() {
    //static helper
  }
  
  static abstract class AbstractType implements Type {
    private Type typeArray;
    
    @Override
    public String toString() {
      return getName();
    }
    
    @Override
    public Type getTypeArray() {
      if (typeArray==null) {
        return typeArray=new ArrayType(this);
      }
      return typeArray;
    }
  }
  
  public static class BooleanType extends AbstractType {
    @Override
    public String getName() {
      return "boolean";
    }

    public <R,P,E extends Exception> R accept(TypeVisitor<R,P,E> visitor, P param) throws E {
      return visitor.visit(this,param);
    }
  }
  
  public static class CharacterType extends AbstractType {
    @Override
    public String getName() {
      return "char";
    }

    public <R,P,E extends Exception> R accept(TypeVisitor<R,P,E> visitor, P param) throws E {
      return visitor.visit(this,param);
    }
  }
  
  public static class IntegerType extends AbstractType {
    @Override
    public String getName() {
      return "int";
    }
    
    public <R,P,E extends Exception> R accept(TypeVisitor<R,P,E> visitor, P param) throws E {
      return visitor.visit(this,param);
    }
  }

  public static class DoubleType extends AbstractType {
    @Override
    public String getName() {
      return "double";
    }
    
    public <R,P,E extends Exception> R accept(TypeVisitor<R,P,E> visitor, P param) throws E {
      return visitor.visit(this,param);
    }
  }
  
  public static class StringType extends AbstractType {
    @Override
    public String getName() {
      return "string";
    }
    
    public <R,P,E extends Exception> R accept(TypeVisitor<R,P,E> visitor, P param) throws E {
      return visitor.visit(this,param);
    }
  }
  
  public static class VoidType extends AbstractType {
    @Override
    public String getName() {
      return "void";
    }
    
    public <R,P,E extends Exception> R accept(TypeVisitor<R,P,E> visitor, P param) throws E {
      return visitor.visit(this,param);
    }
  }
  
  public static class AnyType extends AbstractType {
    @Override
    public String getName() {
      return "any";
    }
    
    public <R,P,E extends Exception> R accept(TypeVisitor<R,P,E> visitor, P param) throws E {
      return visitor.visit(this,param);
    }
  }
  
  public static class NullType extends AbstractType {
    @Override
    public String getName() {
      return "null";
    }
    
    public <R,P,E extends Exception> R accept(TypeVisitor<R,P,E> visitor, P param) throws E {
      return visitor.visit(this,param);
    }
  }
  
  public static class StructType extends AbstractType {
    private final String name;
    public StructType(String name) {
      this.name=name;
    }
    
    public String getName() {
      return name;
    }
    
    public <R,P,E extends Exception> R accept(TypeVisitor<R,P,E> visitor, P param) throws E {
      return visitor.visit(this,param);
    }
  }
  
  public static class ArrayType extends AbstractType {
    private final Type componentType;
    public ArrayType(Type componentType) {
      this.componentType=componentType;
    }
    
    public String getName() {
      return componentType.getName()+"[]";
    }
    
    public Type getComponentType() {
      return componentType;
    }
    
    public <R,P,E extends Exception> R accept(TypeVisitor<R,P,E> visitor, P param) throws E {
      return visitor.visit(this,param);
    }
  }
  
  public static abstract class TypeVisitor<R,P,E extends Exception> {
    public R visit(BooleanType type, P param) throws E {
      return visit((Type)type,param);
    }
    public R visit(CharacterType type, P param) throws E {
      return visit((Type)type,param);
    }
    public R visit(IntegerType type, P param) throws E {
      return visit((Type)type,param);
    }
    public R visit(DoubleType type, P param) throws E {
      return visit((Type)type,param);
    }
    public R visit(StringType type, P param) throws E {
      return visit((Type)type,param);
    }
    public R visit(VoidType type, P param) throws E {
      return visit((Type)type,param);
    }
    public R visit(AnyType type, P param) throws E {
      return visit((Type)type,param);
    }
    public R visit(NullType type, P param) throws E {
      return visit((Type)type,param);
    }
    public R visit(StructType type, P param) throws E {
      return visit((Type)type,param);
    }
    public R visit(ArrayType type, P param) throws E {
      return visit((Type)type,param);
    }
    
    /** Default type visitor.
     *  The implementation always throws an
     *  {@link AssertionError}.
     * 
     * @param type the visited type.
     * @param param a parameter value.
     * @return a return value.
     * @throws E an exception (runtime or not).
     */
    protected R visit(Type type, P param) throws E {
      throw new AssertionError("no visit for type "+type+" with parameter "+param);
    }
  }
  
  public static Type arrayType(Type type,int dimension) {
    for(int i=0;i<dimension;i++)
      type=type.getTypeArray();
    return type;
  }
  
  public static boolean isAssignable(TypeScope typeScope, Type type1, Type type2) {
    if (type1==type2)
      return true;
    
    VoidType voidType=typeScope.voidType();
    if (type1==voidType || type2==voidType)
      return false;
    
    AnyType anyType=typeScope.anyType();
    if (type1==anyType)
      return true;
    
    if (type2==typeScope.nullType() &&
        (type1 instanceof AnyType ||
         type1 instanceof StructType ||
         type1 instanceof ArrayType))
      return true;
    
    if (type1 instanceof ArrayType &&
        type2 instanceof ArrayType) {
      return isAssignable(typeScope,
        ((ArrayType)type1).getComponentType(),
        ((ArrayType)type2).getComponentType());
    }
    
    return false;
  }
  
  public static boolean isAssignable(TypeScope typeScope, List<Type> typeList1, List<Type> typeList2) {
    if (typeList1.size()!=typeList2.size())
      return false;
    
    Iterator<Type> it1=typeList1.iterator();
    Iterator<Type> it2=typeList2.iterator();
    while(it1.hasNext()) {
      if (!isAssignable(typeScope,it1.next(),it2.next()))
        return false;
    }
    return true;
  }
}

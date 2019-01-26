/*
 * Created on 16 juil. 2005
 *
 */
package fr.umlv.tatoo.cc.common.generator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Represents a Java Type.
 * 
 * @author Remi Forax
 */
public class Type implements Comparable<Type> {
  // create an Object type
  private Type(String typeName,List<Type> typeArguments,Type componentType,Type wrapperType) {
    this.typeName=typeName;
    this.typeArguments=typeArguments;
    this.componentType=componentType;
    this.wrapperType=wrapperType;
  }
  
  // create a primitive type or void
  private Type(String typeName,Type wrapperType) {
    this(typeName,Collections.<Type>emptyList(),null,wrapperType);
  }
  
  @Override
  public int hashCode() {
    return typeName.hashCode();
  }
  
  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Type))
      return false;
    Type type=(Type)o;
    return eq(componentType,type.getComponentType()) &&
      typeName.equals(type.typeName) &&
      typeArguments.equals(type.typeArguments);
  }
  
  private static boolean eq(Type t1,Type t2) {
    if (t1==null)
      return t2==null;
    return t1.equals(t2);
  }
  
  /** {@inheritDoc}
   * 
   *  This implementation compares to type using their names.
   *  @see #getName() 
   */
  public int compareTo(Type type) {
    return getName().compareTo(type.getName());
  }
  
  @Override
  public String toString() {
    return getName();
  }
  
  /** Returns true is the current type is a primitive type.
   *  {@link #VOID} is not a primitive type.
   * @return true is the current type is a primitive type.
   * 
   * @see #isObjectType()
   * @see #isVoid()
   */
  public boolean isPrimitive() {
    return wrapperType!=null;
  }
  
  /** Returns true if the current type is an object type.
   *  {@link #VOID} is an object type.
   * @return true if the current type is an object type.
   * 
   * @see #isPrimitive()
   * @see #isVoid()
   */
  public boolean isObjectType() {
    return !(isPrimitive() || isVoid());
  }
  
  /** Returns true if the current type is qualified.
   * @return true if the current type is qualified.
   */
  public boolean isQualifiedType() {
    return typeName.indexOf('.')!=-1;
  }
  
  /** Returns true if the current type is an array.
   * @return true if the current type is an array.
   * 
   * @see #getComponentType()
   */
  public boolean isArray() {
    return componentType!=null;
  }
  
  /** Returns true if the current type is {@link #VOID}.
   * @return true if the current type is {@link #VOID}.
   * 
   * @see #isPrimitive()
   * @see #isObjectType()
   */
  public boolean isVoid() {
    return this==VOID;
  }

  /** Returns the fully qualified name of the current type.
   *  by example, {@code java.util.List&lt;E&gt;}.
   * 
   * @return the fully qualified name of the current type.
   * 
   * @see #getSimpleName()
   * @see #getSimpleRawName()
   * @see #getPackageName()
   */
  public String getName() {
    int array=0;
    Type type=this;
    for(;type.isArray();type=type.getComponentType()) {
      array++;
    }
    if (typeArguments.isEmpty())
      return typeName;
    
    StringBuilder builder=new StringBuilder();
    builder.append(typeName).append('<');
    for(Type t:typeArguments) {
      builder.append(t.getName()).append(',');
    }
    builder.setCharAt(builder.length()-1,'>');
    
    for(int i=0;i<array;i++)
      builder.append("[]");
    return builder.toString();
  }
  
  /** Returns the component type of the current type
   *  if the current type is an array.
   * @return the component type of the current type
   *  if the current type is an array or null otherwise.
   * 
   * @see #isArray()
   */
  public Type getComponentType() {
    return componentType;
  }
  
  /** Returns the wrapper type of the current type
   *  if the current type is a primitive type.
   * @return the wrapper type of the current type
   *  if the current type is a primitive type or null otherwise.
   *  
   *  @see #boxIfPrimitive()
   */
  public Type getWrapperType() {
    return wrapperType;
  }
  
  /** Returns the current type or its {@link #getWrapperType() wrapper type}
   *  if the the current type is a primitive.
   * @return the current type or its {@link #getWrapperType() wrapper type}
   *  if the the current type is a primitive.
   * 
   * @see #getWrapperType()
   */
  public Type boxIfPrimitive() {
    if (wrapperType!=null)
      return wrapperType;
    return this;
  }
  
  /** Returns the default value of the current type.
   *  By example, 0 for int or '\0' for char, etc.
   *  
   * @return the default value of the current type.
   */
  public String getDefaultValue() {
    if (!isPrimitive())
      return "null";
    if (this==INT)
      return "0";
    if (this==DOUBLE)
      return "0.0";
    if (this==BOOLEAN)
      return "false";
    if (this==CHARACTER)
      return "\\0";
    if (this==LONG)
      return "0L";
    if (this==FLOAT)
      return "0.0F";
    if (this==BYTE)
      return "(byte)0";
    if (this==SHORT)
      return "(short)0";
    throw new IllegalStateException("void as no default value");
  }
  
  /** Returns the qualified name of the current type
   *  without its type arguments.
   *  This name is the name used for imports.
   *  
   * @return the qualified name of the current type
   *  without its type arguments if it is not an array.
   * 
   * @throws IllegalStateException if the current type is an array.
   * 
   * @see #getRawType()
   * @see #getName()
   * @see #getSimpleRawName()
   */
  public String getRawName() {
    if (isArray())
      throw new IllegalStateException("type is array");
     return typeName;
  }
  
  
  /** Returns a qualified type corresponding to the current type
   *  without its type arguments.
   *  This type is for imports.
   *  
   * @return the qualified name of the current type
   *  without its type arguments if it is not an array.
   *  
   * @throws IllegalStateException if the current type is an array.
   * 
   * @see #addImportsTo(Set)
   * @see #getRawName()
   */
  public Type getRawType() {
    if (isArray())
      throw new IllegalStateException("type is array");
     return Type.createQualifiedType(typeName);
  }
  
  /** Returns the unqualified name of the current type
   *  without its type arguments.
   *  
   * @return the unqualified name of the current type
   *  without its type arguments if it is not an array.
   * 
   * @throws IllegalStateException if the current type is an array.
   * 
   * @see #getRawType()
   * @see #getName()
   * @see #getRawName()
   */
  public String getSimpleRawName() {
    if (isArray())
      throw new IllegalStateException("type is array");
    
    int index=typeName.lastIndexOf('.');
    if (index==-1)
      return typeName;
    return typeName.substring(index+1);
  }
  
  /** Returns the unqualified name of the current type.
   *  by example, {@code List&lt;E&gt;}.
   * 
   * @return the unqualified name of the current type.
   * 
   * @see #getName()
   * @see #getSimpleRawName()
   * @see #getPackageName()
   */
  public String getSimpleName() {
    int array=0;
    Type type=this;
    for(;type.isArray();type=type.getComponentType()) {
      array++;
    }
    
    StringBuilder builder=new StringBuilder();
    if (type.typeArguments.isEmpty()) {
      builder.append(type.getSimpleRawName());
    }
    else {
      builder.append(type.getSimpleRawName()).append('<');
      for(Type t:type.typeArguments) {
        builder.append(t.getSimpleName()).append(',');
      }
      builder.setCharAt(builder.length()-1,'>');
    }
    
    for(int i=0;i<array;i++)
      builder.append("[]");
    return builder.toString();
  }
  
  /** Return the qualified part of the current type.
   * @return the qualified part of the current type.
   * 
   * @throws IllegalStateException if the current type is an array
   *  or an unqualified type.
   *  
   * @see #getName()
   * @see #getSimpleName()
   */
  public String getPackageName() {
    if (isArray())
      throw new IllegalStateException("type is array");
    
    int index=typeName.lastIndexOf('.');
    if (index==-1)
      throw new IllegalArgumentException("typeName ("+typeName+") has no defined package");
    return typeName.substring(0,index);
  }
  
  /** Return the name used by the VM in case of erasure.
   *  Primitive type return their name and others return "Object".
   * @return its name if its a primitive type or "Object" otherwise.
   * 
   * @see #getRawName()
   * @see #isPrimitive()
   */
  public String getVMTypeName() {
    if (isPrimitive())
      return typeName;
    return "Object";
  }
  
  /** Returns true if the current type is a parameterized type
   *  with type arguments.
   * @return if the current type has type arguments.
   * 
   * @see #getTypeArguments()
   */
  public boolean hasTypeArguments() {
    return !typeArguments.isEmpty();
  }
  
  /** Returns a read/write list of type arguments of
   *  the current type or an empty list if the current type
   *  is not a parameterized type.
   *  
   * @return a list of type arguments.
   */
  public List<Type> getTypeArguments() {
    return typeArguments;
  }
  
//  /** Prints the type using getSimpleName() AND include the type
//   *  in the current reference context.
//   */
//  public void formatTo(Formatter formatter, int flags, int width, int precision) {
//    addImportsTo(ReferenceContext.getLocalContext().getImports());
//    try {
//      formatter.out().append(getSimpleName());
//    } catch (IOException e) {
//      throw new AssertionError(e);
//    }
//  }
  
  /** Add all types used by the current type
   *  to the import set.
   *  
   *  @see #getRawType()
   */
  public void addImportsTo(Set<Type> imports) {
    Type type=this;
    while(type.isArray()) {
      type=type.getComponentType();
    }
    
    if (type.isQualifiedType()) {
      imports.add(type.getRawType());
    }
    
    for(Type t:type.typeArguments) {
      t.addImportsTo(imports);
    }
  }
    
  private final String typeName;
  private final List<Type> typeArguments;
  private final Type componentType;
  private final Type wrapperType;
  
  /** Creates a Type from a qualified string.
   * @param qualifiedName the qualified name of the type
   * @return the corresponding type.
   * @throws IllegalArgumentException if the name taken as argument
   *  is not qualified. 
   *  
   * @see #createType(String, Map)
   */
  public static Type createQualifiedType(String qualifiedName) {
    if (qualifiedName.indexOf('.')==-1)
      throw new IllegalArgumentException("type "+qualifiedName+" must be qualified");
    return createType(qualifiedName,Collections.<String,Type>emptyMap());
  }
  
  private static Type getPrimitiveType(String typeName) {
    //warning: during primitive type init, VOID may be null
    if (VOID==null)
      return null;
      
    if (VOID.typeName.equals(typeName))
      return VOID;
    return primitiveTypes.get(typeName);
  }
  
  /** Creates a parameterized type.
   * @param type the generic type.
   * @param typeArguments the type arguments.
   * @return a parameterized type.
   * 
   * @see #createType(String, Map)
   * @see #createParametrizedType(Type, List)
   */
  public static Type createParametrizedType(Type type,Type... typeArguments) {
    return createParametrizedType(type,Arrays.asList(typeArguments));
  }
  
  /** Creates a parameterized type.
   * @param type the generic type.
   * @param typeArguments the type arguments.
   * @return a parameterized type.
   * 
   * @see #createType(String, Map)
   * @see #createParametrizedType(Type, Type...)
   */
  public static Type createParametrizedType(Type type,List<Type> typeArguments) {
    return new Type(type.typeName,typeArguments,null,null);
  }
  
  /** Creates an array type from a type.
   * @param type the component type.
   * @return an array type.
   * 
   * @see #getComponentType()
   */
  public static Type createArrayType(Type type) {
    return new Type(null,Collections.<Type>emptyList(),type,null);
  }
  
  /** Creates a type from a name with optional type arguments
   *  and a map of imports.
   *  
   * @param typeName a name with optional type arguments
   * @param imports a map of imports (unqualified name to qualified name)
   * @return a new type.
   * 
   * @see #createParametrizedType(Type, Type...)
   * @see #createQualifiedType(String)
   */
  public static Type createType(String typeName, Map<String,? extends Type> imports) {
    Type type=getPrimitiveType(typeName);
    if (type!=null)
      return type;
    
    List<Type> typeParameters;
    int index=typeName.indexOf('<');
    if (index!=-1) {
      int lastIndex=typeName.lastIndexOf('>');
      typeParameters=extractTypeParameters(typeName.substring(index+1,lastIndex),imports);
      typeName=typeName.substring(0,index);
    } else {
      typeParameters=Collections.<Type>emptyList();
    }
    
    if (typeName.indexOf('.')==-1) {
      typeName=resolveTypeName(typeName,imports);
    }
    
    return new Type(typeName,typeParameters,null,null);
  }
  
  private static String resolveTypeName(String typeName,Map<String,? extends Type> imports) {
    Type importType=imports.get(typeName);
    if (importType!=null)
      return importType.getRawName();
    return typeName;
  }
  
  private static List<Type> extractTypeParameters(String parameters,Map<String,? extends Type> imports) {
    int index=0;
    int lastIndex=0;
    ArrayList<Type> list=new ArrayList<Type>();
    while(index<parameters.length()) {
      char c=parameters.charAt(index);
      if (c==',') {
        list.add(createType(parameters.substring(lastIndex,index),imports));
        lastIndex=++index;
      } else {
        if (c=='<') {
          index=parameters.indexOf(">,",index+1);
          if (index==-1)
            index=parameters.length()-1;
          list.add(createType(parameters.substring(lastIndex,index+1),imports));
          lastIndex=index+=2;
        }
        else {
          index++;
        }
      }
    }
    if (lastIndex!=index) {
      list.add(createType(parameters.substring(lastIndex),imports));
    }
    return Collections.unmodifiableList(list);
  }

  /** The boolean Type. */
  public static final Type BOOLEAN=new Type("boolean",Type.createQualifiedType("java.lang.Boolean"));
  /** The byte Type. */
  public static final Type BYTE=new Type("byte",Type.createQualifiedType("java.lang.Byte"));
  /** The short Type. */
  public static final Type SHORT=new Type("short",Type.createQualifiedType("java.lang.Short"));
  /** The char Type. */
  public static final Type CHARACTER=new Type("char",Type.createQualifiedType("java.lang.Character"));
  /** The int Type. */
  public static final Type INT=new Type("int",Type.createQualifiedType("java.lang.Integer"));
  /** The long Type. */
  public static final Type LONG=new Type("long",Type.createQualifiedType("java.lang.Long"));
  /** The float Type. */
  public static final Type FLOAT=new Type("float",Type.createQualifiedType("java.lang.Float"));
  /** The double Type. */
  public static final Type DOUBLE=new Type("double",Type.createQualifiedType("java.lang.Double"));
  /** The void Type. */
  //warning: VOID must be the last static field, see getPrimitiveType()
  public static final Type VOID=new Type("void",Type.createQualifiedType("java.lang.Void"));
  
  private static final HashMap<String,Type> primitiveTypes;
  static {
    primitiveTypes=new HashMap<String,Type>();
    for(Type type:new Type[]{BOOLEAN,BYTE,SHORT,CHARACTER,INT,LONG,FLOAT,DOUBLE})
      primitiveTypes.put(type.typeName,type);
  }
}

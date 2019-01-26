package fr.umlv.tatoo.samples.pseudo.comp;

public class TypeScope extends Scope<Type> {
  private final Types.BooleanType booleanType=new Types.BooleanType();
  private final Types.CharacterType charType=new Types.CharacterType();
  private final Types.IntegerType integerType=new Types.IntegerType();
  private final Types.DoubleType doubleType=new Types.DoubleType();
  private final Types.StringType stringType=new Types.StringType();
  private final Types.VoidType voidType=new Types.VoidType();
  private final Types.AnyType anyType=new Types.AnyType();
  private final Types.NullType nullType=new Types.NullType();
  
  public enum TypeKind {
    BOOLEAN {
      @Override
      Type getType(TypeScope typeScope) {
        return typeScope.booleanType();
      }
    }, CHARACTER { 
      @Override
      Type getType(TypeScope typeScope) {
        return typeScope.charType();
      }
    },INTEGER {
      @Override
      Type getType(TypeScope typeScope) {
        return typeScope.integerType();
      }
    }, DOUBLE {
      @Override
      Type getType(TypeScope typeScope) {
        return typeScope.doubleType();
      }
    }, STRING {
      @Override
      Type getType(TypeScope typeScope) {
        return typeScope.stringType();
      }
    }, VOID {
      @Override
      Type getType(TypeScope typeScope) {
        return typeScope.voidType();
      }
    }, ANY {
      @Override
      Type getType(TypeScope typeScope) {
        return typeScope.anyType();
      }
    }, NULL {
      @Override
      Type getType(TypeScope typeScope) {
        return typeScope.nullType();
      }
    }
    ;
    
    abstract Type getType(TypeScope typeScope);
  }
  
  public TypeScope() {
    // register default types
    add(booleanType);
    add(charType);
    add(integerType);
    add(doubleType);
    add(stringType);
    add(anyType);
    add(nullType);
  }
  
  public Type getType(TypeKind kind) {
    return kind.getType(this);
  }
  
  public Types.BooleanType booleanType() {
    return booleanType;
  }
  public Types.CharacterType charType() {
    return charType;
  }
  public Types.IntegerType integerType() {
    return integerType;
  }
  public Types.DoubleType doubleType() {
    return doubleType;
  }
  public Types.StringType stringType() {
    return stringType;
  }
  public Types.VoidType voidType() {
    return voidType;
  }
  public Types.AnyType anyType() {
    return anyType;
  }
  public Types.NullType nullType() {
    return nullType;
  }
}

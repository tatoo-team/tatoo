package fr.umlv.tatoo.samples.pseudo.comp;

public class Field extends Scope.AbstractItem implements Var{
  private final Struct struct;
  private final Type type;
  
  public Field(Struct struct,String name, Type type) {
    super(name);
    this.struct=struct;
    this.type=type;
  }
  
  @Override
  public boolean isConstant() {
    return false;
  }
  
  public Struct getStruct() {
    return struct;
  }
  
  public Type getType() {
    return type;
  }
}

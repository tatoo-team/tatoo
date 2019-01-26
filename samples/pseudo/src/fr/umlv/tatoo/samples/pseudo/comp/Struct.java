package fr.umlv.tatoo.samples.pseudo.comp;

import fr.umlv.tatoo.samples.pseudo.comp.Types.StructType;

public class Struct extends Scope.AbstractItem implements Symbol {
  private final StructType type;
  private Scope<Field> fieldScope=
    new Scope<Field>();
  
  public Struct(String name, StructType type) {
    super(name);
    this.type=type;  
  }
  
  public StructType getType() {
    return type;
  }
  
  public Scope<Field> getFieldDefScope() {
    return fieldScope;
  }
}

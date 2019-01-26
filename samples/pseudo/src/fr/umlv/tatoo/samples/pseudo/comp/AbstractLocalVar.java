package fr.umlv.tatoo.samples.pseudo.comp;


public abstract class AbstractLocalVar extends Scope.AbstractItem implements LocalVar {
  private final Type type;
  
  public AbstractLocalVar(String name, Type type) {
    super(name);
    this.type=type;
  }
  
  public Type getType() {
    return type;
  }
  
  @Override
  public String toString() {
    return getType()+" "+getName();
  }
}

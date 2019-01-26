package fr.umlv.tatoo.samples.pseudo.comp;


public class BlockLocalVar extends AbstractLocalVar {
  private final boolean constant;
  private final Scope<LocalVar> scope;
  
  public BlockLocalVar(String name, Type type, boolean constant, Scope<LocalVar> scope) {
    super(name,type);
    this.constant=constant;
    this.scope=scope;
  }
  
  public boolean isConstant() {
    return constant;
  }
  
  public Scope<LocalVar> getScope() {
    return scope;
  }
}

package fr.umlv.tatoo.runtime.node;

abstract class AbstractToken implements Node {
  private final AST ast;
  private AbstractNode parent;
  
  protected AbstractToken(AST ast) {
    this.ast=ast;
  }
  
  public AST getAST() {
    return ast;
  }
  
  public AbstractNode getParent() {
    return parent;
  }
  protected void setParent(AbstractNode parent) {
    this.parent=parent;
  }
  
  /** This implementation always return null.
   */
  @Override
  public Binding getBinding() {
    return null;
  }
  
  public final <A> A getAttribute(Class<A> attributeType) {
    return ast.getAttribute(this,attributeType);
  }
  public final <A> void setAttribute(Class<A> attributeType,A attribute) {
    ast.setAttribute(this,attributeType,attribute);
  }
}
package fr.umlv.tatoo.runtime.node;

import fr.umlv.tatoo.cc.ebnf.ast.TreeASTVisitor;

public interface AST {
  public void setRoot(Node tree);
  //public TreeAST getRoot();
  
  public boolean isAttributeSupported(Class<?> attributeType);
  public <A> A getAttribute(Node node, Class<A> attributeType);
  public <A> void setAttribute(Node node, Class<A> attributeType, A attribute);
  
  public <R,P,E extends Exception> R visit(
      TreeASTVisitor<? extends R,? super P,? extends E> visitor,P parameter) throws E;
}
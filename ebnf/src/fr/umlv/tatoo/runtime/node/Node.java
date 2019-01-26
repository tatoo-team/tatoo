package fr.umlv.tatoo.runtime.node;

import java.util.List;

import fr.umlv.tatoo.cc.ebnf.ast.TreeASTVisitor;

public interface Node {
  public boolean isToken();
  
  public Object getKind();
  public Binding getBinding();
  
  public <A> A getAttribute(Class<A> attributeType);
  public <A> void setAttribute(Class<A> attributeType, A attribute);
  
  public Node getParent();
  public AST getAST();
  
  public List<Node> nodeList();
  public List<Node> allNodeList();
  
  public <R,P,E extends Exception> R accept(
      TreeASTVisitor<? extends R,? super P,? extends E> visitor,P parameter) throws E;
  
}
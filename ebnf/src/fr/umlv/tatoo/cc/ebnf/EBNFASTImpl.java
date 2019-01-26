package fr.umlv.tatoo.cc.ebnf;

import java.util.HashMap;

import fr.umlv.tatoo.cc.ebnf.ast.TreeASTVisitor;
import fr.umlv.tatoo.runtime.node.AST;
import fr.umlv.tatoo.runtime.node.Node;

public class EBNFASTImpl implements AST {
  private Node root;
  private final HashMap<Node,LineColumnLocation> locationMap=
    new HashMap<Node,LineColumnLocation>();

  public Node getRoot() {
    return root;
  }
  public void setRoot(Node root) {
    this.root=root;
  }
  
  public boolean isAttributeSupported(Class<?> attributeType) {
    return attributeType==LineColumnLocation.class;
  }

  public <A> void setAttribute(Node node,Class<A> attributeType,A attribute) {
    if (attributeType!=LineColumnLocation.class)
      throw new IllegalArgumentException("attribute can only be a LineColumnLocation "+attributeType);
    locationMap.put(node,(LineColumnLocation)attribute);
  }

  @SuppressWarnings("unchecked")
  public <A> A getAttribute(Node node,Class<A> attributeType) {
    if (attributeType!=LineColumnLocation.class)
      throw new IllegalArgumentException("attribute can only be LineColumnLocation "+attributeType);
    return (A)locationMap.get(node);
  }
  
  public <R,P,E extends Exception> R visit(
      TreeASTVisitor<? extends R,? super P,? extends E> visitor,P parameter)
      throws E {
    Node root=this.root;
    if (root==null)
      return null;
    return root.accept(visitor,parameter);
  }
}
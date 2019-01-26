package fr.umlv.tatoo.cc.ebnf.ast;

import java.util.List;

import fr.umlv.tatoo.runtime.node.AST;
import fr.umlv.tatoo.runtime.node.Node;
import fr.umlv.tatoo.runtime.node.SimpleNode;

public class SimpleNodeAST<V> extends SimpleNode<V>  {
  private final Kind kind;
  
  public SimpleNodeAST(AST ast,Kind kind,V value,List<Node> allNodes) {
    super(ast,value,allNodes);
    
    this.kind=kind;
  }
  
  @Override
  public Kind getKind() {
    return kind;
  }
  
  public <R,P,E extends Exception> R accept(
      TreeASTVisitor<? extends R,? super P,? extends E> visitor,P parameter) throws E {
    return visitor.visit(this,parameter);
  }
}

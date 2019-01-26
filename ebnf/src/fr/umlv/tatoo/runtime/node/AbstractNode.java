package fr.umlv.tatoo.runtime.node;

import java.util.List;

import fr.umlv.tatoo.cc.ebnf.ast.Kind;

public abstract class AbstractNode extends AbstractToken {
  private final List<Node> allNodeList;
  private List<Node> nodeList; // may be always null if nodeList() is overridden
  
  protected AbstractNode(AST ast,List<Node> allNodeList) {
    super(ast);
    
    // set parent
    for(Node tree:allNodeList)
      ((AbstractToken)tree).setParent(this);
    
    this.allNodeList=allNodeList;
  }
  
  @Override
  public abstract Kind getKind();
  
  public boolean isToken() {
    return false;
  }
  
  public List<Node> allNodeList() {
    return allNodeList;
  }
  
  @Override
  public String toString() {
    if (allNodeList.isEmpty())
      return "["+getKind()+']';
    StringBuilder builder=new StringBuilder();
    builder.append('[').append(getKind()).append(' ');
    for(Node node:allNodeList)
      builder.append(node).append(',');
    builder.setLength(builder.length()-1);
    return builder.append(']').toString();
  }

  public List<Node> nodeList() {
    if (nodeList!=null)
      return nodeList;
    NodeListBuilder<Node> nodeList=new NodeListBuilder<Node>();
    for(Node node:allNodeList()) {
      if (!node.isToken()) {
        nodeList.add(node);
      }
    }
    return this.nodeList=nodeList.createList();
  }
}

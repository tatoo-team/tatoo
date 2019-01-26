package fr.umlv.tatoo.cc.ebnf.ast.analysis;

import fr.umlv.tatoo.cc.ebnf.ast.BindingMap;
import fr.umlv.tatoo.cc.ebnf.ast.SimpleNodeAST;
import fr.umlv.tatoo.cc.ebnf.ast.TokenAST;
import fr.umlv.tatoo.cc.ebnf.ast.TreeASTVisitor;
import fr.umlv.tatoo.runtime.node.Node;

public class AbstractEnterPass extends TreeASTVisitor<Object,Object,RuntimeException> {
  private final BindingMap bindingMap;
  protected AbstractEnterPass(BindingMap bindingMap) {
    this.bindingMap=bindingMap;
  }
  protected AbstractEnterPass(AbstractEnterPass pass) {
    this.bindingMap=pass.bindingMap;
  }
  
  protected BindingMap getBindingMap() {
    return bindingMap;
  }
  
  /** Escape null.
   */
  protected Object processOneSubNode(Node subnode,Object parameter) {
    if (subnode==null)
      return null;
    return subnode.accept(this,parameter);
  }
  
  protected void processSubNodes(Node node,Object parameter) {
    for(Node n:node.nodeList()) {
      n.accept(this,parameter);
    }
  }
  
  @Override
  public Object visit(SimpleNodeAST<?> node,Object parameter) {
    processSubNodes(node,parameter);
    return null;
  }
  
  @Override
  protected Object visit(Node node,Object parameter) {
    processSubNodes(node,parameter);
    return null;
  }
  
  @Override
  public Object visit(TokenAST<?> token, Object parameter) {
    return token.getValue();
    //throw new AssertionError("only nodes can be visited");
  }
}

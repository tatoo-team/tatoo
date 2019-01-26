package fr.umlv.tatoo.cc.ebnf.ast;

import java.util.List;

import fr.umlv.tatoo.runtime.node.AST;
import fr.umlv.tatoo.runtime.node.AbstractNode;
import fr.umlv.tatoo.runtime.node.Node;

public class AttributeDefAST extends AbstractNode {
  private final TokenAST<String> id;
  private final TypeVarAST type;
  
  AttributeDefAST(AST ast,TokenAST<String> id,TypeVarAST type,List<Node> allNodes) {
    super(ast,allNodes);
    this.id=id;
    this.type=type;
  }
  
  public TokenAST<String> getId() {
    return id;
  }
  public TypeVarAST getType() {
    return type;
  }
  
  @Override
  public Kind getKind() {
    return Kind.VARIABLE_TYPE_DEF;
  }
  
  public <R,P,E extends Exception> R accept(
      TreeASTVisitor<? extends R,? super P,? extends E> visitor,P parameter) throws E {
    return visitor.visit(this,parameter);
  }
}

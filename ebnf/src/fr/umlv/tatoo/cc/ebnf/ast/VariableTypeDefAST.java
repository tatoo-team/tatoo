package fr.umlv.tatoo.cc.ebnf.ast;

import java.util.List;

import fr.umlv.tatoo.runtime.node.AST;
import fr.umlv.tatoo.runtime.node.AbstractNode;
import fr.umlv.tatoo.runtime.node.Node;

public class VariableTypeDefAST extends AbstractNode {
  private final VariableVarAST variable;
  private final TypeVarAST type;
  
  VariableTypeDefAST(AST ast,VariableVarAST variable,TypeVarAST type,List<Node> allNodes) {
    super(ast,allNodes);
    this.variable=variable;
    this.type=type;
  }
  
  public VariableVarAST getVariable() {
    return variable;
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

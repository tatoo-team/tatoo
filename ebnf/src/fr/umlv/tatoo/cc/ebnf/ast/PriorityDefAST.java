package fr.umlv.tatoo.cc.ebnf.ast;

import java.util.Collections;
import java.util.List;

import fr.umlv.tatoo.cc.ebnf.ast.Bindings.PriorityBinding;
import fr.umlv.tatoo.runtime.node.AST;
import fr.umlv.tatoo.runtime.node.AbstractNode;
import fr.umlv.tatoo.runtime.node.BindingSite;
import fr.umlv.tatoo.runtime.node.Node;

public class PriorityDefAST extends AbstractNode implements BindingSite {
  private final TokenAST<String> id;
  private final double number;
  private final String association;
  
  private PriorityBinding binding;
  PriorityDefAST(AST ast,TokenAST<String> id,double number,String association,List<Node> allNodes) {
    super(ast,allNodes);
    this.id=id;
    this.number=number;
    this.association=association;
  }
  @Override
  public Kind getKind() {
    return Kind.PRIORITY_DEF;
  }
  @Override
  public List<Node> nodeList() {
    return Collections.emptyList();
  }
  public TokenAST<String> getNameToken() {
    return id;
  }
  
  public String getName() {
    return id.getValue();
  }
  public double getNumber() {
    return number;
  }
  public String getAssociation() {
    return association;
  }
  
  @Override
  public PriorityBinding getBinding() {
    return binding;
  }
  public void setBinding(PriorityBinding binding) {
    if (this.binding!=null)
      throw new AssertionError("binding can only be initialized once");
    this.binding=binding;
  }
  
  public <R,P,E extends Exception> R accept(
      TreeASTVisitor<? extends R,? super P,? extends E> visitor,P parameter) throws E {
    return visitor.visit(this,parameter);
  }
}

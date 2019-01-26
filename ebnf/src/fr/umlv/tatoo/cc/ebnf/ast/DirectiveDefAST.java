package fr.umlv.tatoo.cc.ebnf.ast;

import java.util.Collections;
import java.util.List;

import fr.umlv.tatoo.cc.ebnf.ast.Bindings.DirectiveBinding;
import fr.umlv.tatoo.runtime.node.AST;
import fr.umlv.tatoo.runtime.node.AbstractNode;
import fr.umlv.tatoo.runtime.node.BindingSite;
import fr.umlv.tatoo.runtime.node.Node;

public class DirectiveDefAST extends AbstractNode implements BindingSite {
  private final TokenAST<String> name;
  
  private DirectiveBinding binding;
  DirectiveDefAST(AST ast,TokenAST<String> name,List<Node> allNodes) {
    super(ast,allNodes);
    this.name=name;
  }
  @Override
  public Kind getKind() {
    return Kind.DIRECTIVE_DEF;
  }
  @Override
  public List<Node> nodeList() {
    return Collections.emptyList();
  }
  public TokenAST<String> getNameToken() {
    return name;
  }
  
  public String getName() {
    return name.getValue();
  }
  
  @Override
  public DirectiveBinding getBinding() {
    return binding;
  }
  public void setBinding(DirectiveBinding binding) {
    if (this.binding!=null)
      throw new AssertionError("binding can only be initialized once");
    this.binding=binding;
  }
  
  public <R,P,E extends Exception> R accept(
      TreeASTVisitor<? extends R,? super P,? extends E> visitor,P parameter) throws E {
    return visitor.visit(this,parameter);
  }
}

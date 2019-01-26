package fr.umlv.tatoo.cc.ebnf.ast;

import java.util.Collections;
import java.util.List;

import fr.umlv.tatoo.cc.ebnf.ast.Bindings.TerminalBinding;
import fr.umlv.tatoo.runtime.node.AST;
import fr.umlv.tatoo.runtime.node.AbstractNode;
import fr.umlv.tatoo.runtime.node.Node;

public class QuotedIdVarAST extends AbstractNode implements VariableVarAST {
  private final TokenAST<String> id;
  
  private TerminalBinding binding;
  QuotedIdVarAST(AST ast, TokenAST<String> id, List<Node> allNodes) {
    super(ast,allNodes);
    this.id=id;
  }

  @Override
  public Kind getKind() {
    return Kind.QUOTED_ID_VAR;
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
  
  @Override
  public TerminalBinding getBinding() {
    return binding;
  }
  public void setBinding(TerminalBinding binding) {
    if (this.binding!=null)
      throw new AssertionError("binding can only be initialized once");
    this.binding=binding;
    binding.addReferee(this);
  }
  
  public <R,P,E extends Exception> R accept(
      TreeASTVisitor<? extends R,? super P,? extends E> visitor,P parameter) throws E {
    return visitor.visit(this,parameter);
  }
}

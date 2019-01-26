package fr.umlv.tatoo.cc.ebnf.ast;

import java.util.List;

import fr.umlv.tatoo.cc.ebnf.ast.Bindings.VariableBinding;
import fr.umlv.tatoo.runtime.node.AST;
import fr.umlv.tatoo.runtime.node.AbstractNode;
import fr.umlv.tatoo.runtime.node.Node;

public class UnquotedIdVarAST extends AbstractNode implements VariableVarAST /*, NonTerminalBinder*/ {
  private final TokenAST<String> id;
  
  private VariableBinding<?> binding;
  UnquotedIdVarAST(AST ast, TokenAST<String> id,List<Node> allNodes) {
    super(ast,allNodes);
    this.id=id;
  }

  @Override
  public Kind getKind() {
    return Kind.UNQUOTED_ID_VAR;
  }
  public TokenAST<String> getNameToken() {
    return id;
  }
  
  public String getName() {
    return id.getValue();
  }
  
  @Override
  public VariableBinding<?> getBinding() {
    return binding;
  }
  public void setBinding(VariableBinding<?> binding) {
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

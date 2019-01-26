package fr.umlv.tatoo.cc.ebnf.ast;

import java.util.Collections;
import java.util.List;

import fr.umlv.tatoo.cc.ebnf.ast.Bindings.VersionBinding;
import fr.umlv.tatoo.runtime.node.AST;
import fr.umlv.tatoo.runtime.node.AbstractNode;
import fr.umlv.tatoo.runtime.node.BindingSite;
import fr.umlv.tatoo.runtime.node.Node;

public class VersionVarAST extends AbstractNode implements BindingSite {
  private final TokenAST<String> name;
  
  private VersionBinding binding;
  VersionVarAST(AST ast,TokenAST<String> name,List<Node> allNodes) {
    super(ast,allNodes);
    this.name=name;
  }
  @Override
  public Kind getKind() {
    return Kind.VERSION_VAR;
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
  public VersionBinding getBinding() {
    return binding;
  }
  public void setBinding(VersionBinding binding) {
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

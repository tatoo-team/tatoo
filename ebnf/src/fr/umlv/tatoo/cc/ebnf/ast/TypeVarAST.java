package fr.umlv.tatoo.cc.ebnf.ast;

import java.util.Collections;
import java.util.List;

import fr.umlv.tatoo.cc.ebnf.ast.Bindings.TypeBinding;
import fr.umlv.tatoo.runtime.node.AST;
import fr.umlv.tatoo.runtime.node.AbstractNode;
import fr.umlv.tatoo.runtime.node.BindingSite;
import fr.umlv.tatoo.runtime.node.Node;

public class TypeVarAST extends AbstractNode implements TypeBinder, BindingSite {
  private final TokenAST<String> qualifiedid;
  
  private TypeBinding binding;
  TypeVarAST(AST ast,TokenAST<String> qualifiedid,List<Node> allNodes) {
    super(ast,allNodes);
    this.qualifiedid=qualifiedid;
  }
  @Override
  public Kind getKind() {
    return Kind.TYPE_VAR;
  }
  @Override
  public List<Node> nodeList() {
    return Collections.emptyList();
  }
  public TokenAST<String> getNameToken() {
    return qualifiedid;
  }
  
  public String getQualifiedId() {
    return qualifiedid.getValue();
  }
  
  @Override
  public TypeBinding getBinding() {
    return binding;
  }
  public void setBinding(TypeBinding binding,boolean addReferee) {
    if (this.binding!=null)
      throw new AssertionError("binding can only be initialized once");
    this.binding=binding;
    if (addReferee)
      binding.addReferee(this);
  }
  
  public <R,P,E extends Exception> R accept(
      TreeASTVisitor<? extends R,? super P,? extends E> visitor,P parameter) throws E {
    return visitor.visit(this,parameter);
  }
}

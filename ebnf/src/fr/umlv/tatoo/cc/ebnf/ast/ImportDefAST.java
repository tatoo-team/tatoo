package fr.umlv.tatoo.cc.ebnf.ast;

import java.util.Collections;
import java.util.List;

import fr.umlv.tatoo.cc.ebnf.ast.Bindings.TypeBinding;
import fr.umlv.tatoo.runtime.node.AST;
import fr.umlv.tatoo.runtime.node.AbstractNode;
import fr.umlv.tatoo.runtime.node.BindingSite;
import fr.umlv.tatoo.runtime.node.Node;

public class ImportDefAST extends AbstractNode implements TypeBinder, BindingSite {
  private final TokenAST<String> qualifiedId;
  
  private TypeBinding definition;
  ImportDefAST(AST ast,TokenAST<String> qualifiedId,List<Node> allNodes) {
    super(ast,allNodes);
    this.qualifiedId=qualifiedId;
  }
  @Override
  public Kind getKind() {
    return Kind.IMPORT_DEF;
  }
  @Override
  public List<Node> nodeList() {
    return Collections.emptyList();
  }
  public TokenAST<String> getNameToken() {
    return qualifiedId;
  }
  
  public String getQualifiedId() {
    return qualifiedId.getValue();
  }
  
  @Override
  public TypeBinding getBinding() {
    return definition;
  }
  public void setBinding(TypeBinding binding) {
    if (this.definition!=null)
      throw new AssertionError("binding can only be initialized once");
    this.definition=binding;
  }
  
  public <R,P,E extends Exception> R accept(
      TreeASTVisitor<? extends R,? super P,? extends E> visitor,P parameter) throws E {
    return visitor.visit(this,parameter);
  }
}

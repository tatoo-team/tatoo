package fr.umlv.tatoo.cc.ebnf.ast;

import java.util.List;

import fr.umlv.tatoo.cc.ebnf.ast.Bindings.NonTerminalBinding;
import fr.umlv.tatoo.runtime.node.AST;
import fr.umlv.tatoo.runtime.node.AbstractNode;
import fr.umlv.tatoo.runtime.node.BindingSite;
import fr.umlv.tatoo.runtime.node.Node;

public class NonTerminalDefAST extends AbstractNode implements NonTerminalBinder, BindingSite {
  private final TokenAST<String> id;
  private final TypeVarAST type; // may be null
  private final boolean appendMode;
  private final List<ProductionDefAST> productions;
  
  private NonTerminalBinding binding;
  
  NonTerminalDefAST(AST ast,TokenAST<String> id,final TypeVarAST type,boolean appendMode, final List<ProductionDefAST> productions,List<Node> allNodes) {
    super(ast,allNodes);
    this.id=id;
    this.type=type;
    this.appendMode=appendMode;
    this.productions=productions;
  }

  @Override
  public Kind getKind() {
    return Kind.NONTERMINAL_DEF;
  }
  @Override
  public TokenAST<String> getNameToken() {
    return id;
  }
  
  public String getName() {
    return id.getValue();
  }
  public TypeVarAST getType() {
    return type;
  }
  public boolean isAppendMode() {
    return appendMode;
  }
  public List<ProductionDefAST> getProductions() {
    return productions;
  }
  
  public void setBinding(NonTerminalBinding binding) {
    if (this.binding!=null)
      throw new AssertionError("binding can only be initialized once");
    this.binding=binding;
  }
  @Override
  public NonTerminalBinding getBinding() {
    return binding;
  }
  
  public <R,P,E extends Exception> R accept(
      TreeASTVisitor<? extends R,? super P,? extends E> visitor,P parameter) throws E {
    return visitor.visit(this,parameter);
  }
}

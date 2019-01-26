package fr.umlv.tatoo.cc.ebnf.ast;

import java.util.List;

import fr.umlv.tatoo.cc.ebnf.ast.Bindings.NonTerminalBinding;
import fr.umlv.tatoo.runtime.node.AST;
import fr.umlv.tatoo.runtime.node.AbstractNode;
import fr.umlv.tatoo.runtime.node.BindingSite;
import fr.umlv.tatoo.runtime.node.Node;

public class EnhancedDefAST extends AbstractNode implements VariableVarAST, NonTerminalBinder, BindingSite {
  private final Enhancement enhancement;
  private final VariableVarAST element;   // may be null
  private final VariableVarAST separator; // may be null
  private final List<Node> vargroup;   // may be null
  
  private String name; // name is set in Enter pass 2
  private NonTerminalBinding binding; // binding of the fake EBNF nonterminal
  EnhancedDefAST(AST ast,Enhancement enhancement,VariableVarAST element,VariableVarAST separator,List<Node> vargroup,List<Node> allNodes) {
    super(ast,allNodes);
    this.enhancement=enhancement;
    this.element=element;
    this.separator=separator;
    this.vargroup=vargroup;
  }
  
  public enum Enhancement {
    OPTIONAL(Kind.ENHANCED_OPTIONAL),
    STAR(Kind.ENHANCED_STAR),
    PLUS(Kind.ENHANCED_PLUS),
    GROUP(Kind.ENHANCED_GROUP);
    Enhancement(Kind kind) {
      this.kind=kind;
    }
    final Kind kind;
  }
  
  @Override
  public Kind getKind() {
    return enhancement.kind;
  }
  public TokenAST<String> getNameToken() {
    return null;
  }
  
  public Enhancement getEnhancement() {
    return enhancement;
  }
  
  public VariableVarAST getElement() {
    return element;
  }
  public VariableVarAST getSeparator() {
    return separator;
  }
  public List<Node> getVarGroup() {
    return vargroup;
  }
  
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name=name;
  }
  
  @Override
  public NonTerminalBinding getBinding() {
    return binding;
  }
  public void setBinding(NonTerminalBinding binding) {
    if (this.binding!=null)
      throw new AssertionError("binding can only be initialized once");
    this.binding=binding;
  }
  
  public <R,P,E extends Exception> R accept(
      TreeASTVisitor<? extends R,? super P,? extends E> visitor,P parameter) throws E {
    return visitor.visit(this,parameter);
  }
}

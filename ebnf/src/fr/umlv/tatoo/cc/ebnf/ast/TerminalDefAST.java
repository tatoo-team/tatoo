package fr.umlv.tatoo.cc.ebnf.ast;

import java.util.List;

import fr.umlv.tatoo.cc.ebnf.ast.Bindings.TerminalBinding;
import fr.umlv.tatoo.runtime.node.AST;
import fr.umlv.tatoo.runtime.node.AbstractNode;
import fr.umlv.tatoo.runtime.node.BindingSite;
import fr.umlv.tatoo.runtime.node.Node;

public class TerminalDefAST extends AbstractNode implements BindingSite {
  private final TokenAST<String> id;
  private final TerminalKind terminalKind;
  private final AliasDefAST alias; // may be null
  private final TypeVarAST type; // may be null
  private final RuleDefAST rule; // may be null
  private final PriorityVarAST priority; // may be null
  
  private TerminalBinding binding;
  TerminalDefAST(AST ast, TerminalKind terminalKind,AliasDefAST alias, TokenAST<String> id,TypeVarAST type,RuleDefAST rule,PriorityVarAST priority,List<Node> allNodes) {
    super(ast,allNodes);
    this.terminalKind=terminalKind;
    this.alias=alias;
    this.id=id;
    this.type=type;
    this.rule=rule;
    this.priority=priority;
  }
  
  public enum TerminalKind {
    BLANK(Kind.BLANK_DEF),
    COMMENT(Kind.COMMENT_DEF),
    BRANCH(Kind.BRANCH_DEF),
    ERROR(Kind.ERROR_DEF),
    EOF(Kind.EOF_DEF),
    TOKEN(Kind.TOKEN_DEF);
    TerminalKind(Kind kind) {
      this.kind=kind;
    }
    final Kind kind;
  }

  @Override
  public Kind getKind() {
    return terminalKind.kind;
  }
  public TokenAST<String> getNameToken() {
    return id;
  }
  
  public TerminalKind getTerminalKind() {
    return terminalKind;
  }
  public String getName() {
    return id.getValue();
  }
  public TypeVarAST getType() {
    return type;
  }
  public RuleDefAST getRule() {
    return rule;
  }
  public PriorityVarAST getPriority() {
    return priority;
  }
  public AliasDefAST getAlias() {
    return alias;
  }
  
  @Override
  public TerminalBinding getBinding() {
    return binding;
  }
  public void setBinding(TerminalBinding binding) {
    if (this.binding!=null)
      throw new AssertionError("binding can only be initialized once");
    this.binding=binding;
  }
  
  public <R,P,E extends Exception> R accept(
      TreeASTVisitor<? extends R,? super P,? extends E> visitor,P parameter) throws E {
    return visitor.visit(this,parameter);
  }
}

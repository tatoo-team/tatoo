package fr.umlv.tatoo.cc.ebnf.ast;

import fr.umlv.tatoo.runtime.node.Node;

public class TreeASTVisitor<R,P,E extends Exception> {
  public R visit(ProductionIdAndVersionDefAST node,P parameter) throws E {
    return visit((Node)node,parameter);
  }
  public R visit(EnhancedDefAST node,P parameter) throws E {
    return visit((VariableVarAST)node,parameter);
  }
  public R visit(AliasDefAST node,P parameter) throws E {
    return visit((Node)node,parameter);
  }
  public R visit(DirectiveDefAST node,P parameter) throws E {
    return visit((Node)node,parameter);
  }
  public R visit(ImportDefAST node,P parameter) throws E {
    return visit((Node)node,parameter);
  }
  public R visit(AttributeDefAST node,P parameter) throws E {
    return visit((Node)node,parameter);
  }
  public R visit(MacroDefAST node,P parameter) throws E {
    return visit((Node)node,parameter);
  }
  public R visit(NonTerminalDefAST node,P parameter) throws E {
    return visit((Node)node,parameter);
  }
  public R visit(UnquotedIdVarAST node,P parameter) throws E {
    return visit((VariableVarAST)node,parameter);
  }
  public R visit(PriorityDefAST node,P parameter) throws E {
    return visit((Node)node,parameter);
  }
  public R visit(PriorityVarAST node,P parameter) throws E {
    return visit((Node)node,parameter);
  }
  public R visit(ProductionDefAST node,P parameter) throws E {
    return visit((Node)node,parameter);
  }
  public R visit(RuleDefAST node,P parameter) throws E {
    return visit((Node)node,parameter);
  }
  public R visit(RootDefAST node,P parameter) throws E {
    return visit((Node)node,parameter);
  }
  public R visit(SimpleNodeAST<?> node,P parameter) throws E {
    return visit((Node)node,parameter);
  }
  public R visit(StartNonTerminalSetDefAST node,P parameter) throws E {
    return visit((Node)node,parameter);
  }
  public R visit(TerminalDefAST node,P parameter) throws E {
    return visit((Node)node,parameter);
  }
  public R visit(QuotedIdVarAST node,P parameter) throws E {
    return visit((VariableVarAST)node,parameter);
  }
  public R visit(TokenAST<?> node,P parameter) throws E {
    return visit((Node)node,parameter);
  }
  public R visit(TypeVarAST node,P parameter) throws E {
    return visit((Node)node,parameter);
  }
  public R visit(VariableTypeDefAST node,P parameter) throws E {
    return visit((Node)node,parameter);
  }
  public R visit(VersionDefAST node,P parameter) throws E {
    return visit((Node)node,parameter);
  }
  public R visit(VersionVarAST node,P parameter) throws E {
    return visit((Node)node,parameter);
  }
  protected R visit(VariableVarAST node,P parameter) throws E {
    return visit((Node)node,parameter);
  }
  
  /** visit an AST node.
   *  The implementation of this method always throws an
   *  {@link AssertionError}.
   * 
   * @param node the aST node.
   * @param parameter a parameter value
   * @return a return value
   * @throws E an exception (runtime or not)
   */
  protected R visit(Node node,P parameter) throws E {
    throw new AssertionError("no visit implemented for "+node+" "+parameter);
  }
}

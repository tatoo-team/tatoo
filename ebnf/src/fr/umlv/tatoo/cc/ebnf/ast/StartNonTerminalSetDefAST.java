package fr.umlv.tatoo.cc.ebnf.ast;

import java.util.List;

import fr.umlv.tatoo.runtime.node.AST;
import fr.umlv.tatoo.runtime.node.AbstractNode;
import fr.umlv.tatoo.runtime.node.Node;

public class StartNonTerminalSetDefAST extends AbstractNode {
  private final List<UnquotedIdVarAST> startNonTerminalList;
  
  StartNonTerminalSetDefAST(AST ast, List<UnquotedIdVarAST> startNonTerminalList,List<Node> allNodes) {
    super(ast,allNodes);
    this.startNonTerminalList=startNonTerminalList;
  }
      
  @Override
  public Kind getKind() {
    return Kind.START_NONTERMINAL_SET_DEF;
  }

  public List<UnquotedIdVarAST> getStartNonTerminalList() {
    return startNonTerminalList;
  }

  public <R,P,E extends Exception> R accept(
    TreeASTVisitor<? extends R,? super P,? extends E> visitor,P parameter) throws E {
    return visitor.visit(this,parameter);
  }
}

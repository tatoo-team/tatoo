package fr.umlv.tatoo.cc.ebnf.ast;

import java.util.List;

import fr.umlv.tatoo.runtime.node.AST;
import fr.umlv.tatoo.runtime.node.AbstractNode;
import fr.umlv.tatoo.runtime.node.Node;

public class RootDefAST extends AbstractNode {
  private final StartNonTerminalSetDefAST startNonTerminalDef;
  
  RootDefAST(AST ast,StartNonTerminalSetDefAST startNonTerminalDef,List<Node> trees) {
    super(ast,trees);
    this.startNonTerminalDef=startNonTerminalDef;
  }
  @Override
  public Kind getKind() {
    return Kind.ROOT_DEF;
  }
  public StartNonTerminalSetDefAST getStartNonTerminalSetDef() {
    return startNonTerminalDef;
  }
  
  public <R,P,E extends Exception> R accept(
      TreeASTVisitor<? extends R,? super P,? extends E> visitor,P parameter) throws E {
    return visitor.visit(this,parameter);
  }
}

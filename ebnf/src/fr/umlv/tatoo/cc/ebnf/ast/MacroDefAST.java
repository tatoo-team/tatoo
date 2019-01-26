package fr.umlv.tatoo.cc.ebnf.ast;

import java.util.Collections;
import java.util.List;

import fr.umlv.tatoo.runtime.node.AST;
import fr.umlv.tatoo.runtime.node.AbstractNode;
import fr.umlv.tatoo.runtime.node.BindingSite;
import fr.umlv.tatoo.runtime.node.Node;

public class MacroDefAST extends AbstractNode implements BindingSite {
  private final TokenAST<String> id;
  private final String regex;
  
  MacroDefAST(AST ast,TokenAST<String> id,String regex,List<Node> allNodes) {
    super(ast,allNodes);
    this.id=id;
    this.regex=regex;
  }
  
  @Override
  public Kind getKind() {
    return Kind.MACRO_DEF;
  }
  @Override
  public List<Node> nodeList() {
    return Collections.emptyList();
  }
  public TokenAST<String> getNameToken() {
    return id;
  }
  
  public String getName() {
    return id.getValue();
  }
  public String getRegex() {
    return regex;
  }
  
  public <R,P,E extends Exception> R accept(
      TreeASTVisitor<? extends R,? super P,? extends E> visitor,P parameter) throws E {
    return visitor.visit(this,parameter);
  }
}

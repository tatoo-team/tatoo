package fr.umlv.tatoo.cc.ebnf.ast;

import java.util.List;

import fr.umlv.tatoo.runtime.node.AST;
import fr.umlv.tatoo.runtime.node.AbstractNode;
import fr.umlv.tatoo.runtime.node.BindingSite;
import fr.umlv.tatoo.runtime.node.Node;

public class ProductionIdAndVersionDefAST extends AbstractNode implements BindingSite {
  private final TokenAST<String> name;
  private final VersionVarAST version;
  
  ProductionIdAndVersionDefAST(AST ast,TokenAST<String> name,VersionVarAST version,List<Node> allNodes) {
    super(ast,allNodes);
    this.name=name;
    this.version=version;
  }
  
  @Override
  public Kind getKind() {
    return Kind.PRODUCTION_ID_AND_VERSION;
  }
  
  public TokenAST<String> getNameToken() {
    return name;
  }
  public VersionVarAST getVersion() {
    return version;
  }
  
  public <R,P,E extends Exception> R accept(
      TreeASTVisitor<? extends R,? super P,? extends E> visitor,P parameter) throws E {
    return visitor.visit(this,parameter);
  }
}

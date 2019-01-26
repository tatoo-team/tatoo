package fr.umlv.tatoo.samples.pseudo.comp.analysis;

import fr.umlv.tatoo.samples.pseudo.ast.Node;
import fr.umlv.tatoo.samples.pseudo.ast.Visitor;

public class TraversalVisitor<P> extends Visitor<Void,P,RuntimeException> {
  @Override
  protected Void visit(Node node, P param) {
    for(Node n:node.nodeList()) {
      n.accept(this,param);
    }
    return null;
  }
}

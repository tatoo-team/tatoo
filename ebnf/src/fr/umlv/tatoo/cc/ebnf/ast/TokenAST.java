package fr.umlv.tatoo.cc.ebnf.ast;

import fr.umlv.tatoo.cc.lexer.ebnf.parser.TerminalEnum;
import fr.umlv.tatoo.runtime.node.AST;
import fr.umlv.tatoo.runtime.node.Token;

public class TokenAST<V> extends Token<TerminalEnum,V> {
  public TokenAST(AST ast, TerminalEnum kind, V value) {
    super(ast,kind,value);
  }
  
  public <R,P,E extends Exception> R accept(
      TreeASTVisitor<? extends R,? super P,? extends E> visitor,P parameter) throws E {
    return visitor.visit(this,parameter);
  }
}

package fr.umlv.tatoo.cc.ebnf.ast;

import fr.umlv.tatoo.cc.lexer.ebnf.tools.TerminalEvaluator;
import fr.umlv.tatoo.cc.lexer.ebnf.parser.TerminalEnum;

// FIXME Remi remove unuseful methods and add @Override to others
public class ASTTerminalEvaluator implements TerminalEvaluator<CharSequence> {
  private final TreeFactory factory;
  public ASTTerminalEvaluator(TreeFactory factory) {
    this.factory=factory;
  }
  
  @Override
  public void comment(CharSequence data) {
    //XXX Remi create a node and insert it where it should
  }
  
  public TokenAST<?> assign(CharSequence data) {
    return factory.createToken(TerminalEnum.assign,null);
  }

  public TokenAST<String> assoc(CharSequence data) {
    return factory.createToken(TerminalEnum.assoc,data.toString());
  }

  public TokenAST<?> blanksdecl(CharSequence data) {
    return factory.createToken(TerminalEnum.blanksdecl,null);
  }
  
  @Override
  public TokenAST<?> commentsdecl(CharSequence data) {
    return factory.createToken(TerminalEnum.commentsdecl,null);
  }
  
  public TokenAST<?> branchesdecl(CharSequence data) {
    return factory.createToken(TerminalEnum.branchesdecl,null);
  }

  public TokenAST<?> colon(CharSequence data) {
    return factory.createToken(TerminalEnum.colon,null);
  }
  
  public TokenAST<?> directivesdecl(CharSequence data) {
    return factory.createToken(TerminalEnum.directivesdecl,null);
  }

  public TokenAST<?> dollar(CharSequence data) {
    return factory.createToken(TerminalEnum.dollar,null);
  }

  public TokenAST<?> doublequote(CharSequence data) {
    return factory.createToken(TerminalEnum.doublequote,null);
  }

  public TokenAST<String> eof(CharSequence data) {
    return factory.createToken(TerminalEnum.eof,null);
  }

  public TokenAST<?> errordecl(CharSequence data) {
    return factory.createToken(TerminalEnum.errordecl,null);
  }

  public TokenAST<String> id(CharSequence data) {
    return factory.createToken(TerminalEnum.id,data.toString());
  }

  public TokenAST<?> importsdecl(CharSequence data) {
    return factory.createToken(TerminalEnum.importsdecl,null);
  }
  
  public TokenAST<?> lbracket(CharSequence data) {
    return factory.createToken(TerminalEnum.lbracket,null);
  }
  
  public TokenAST<?> lsqbracket(CharSequence data) {
    return factory.createToken(TerminalEnum.lsqbracket,null);
  }

  public TokenAST<?> lpar(CharSequence data) {
    return factory.createToken(TerminalEnum.lpar,null);
  }

  public TokenAST<Double> number(CharSequence data) {
    return factory.createToken(TerminalEnum.number,
      Double.parseDouble(data.toString()));
  }

  /* FIXME Remi seperator should be null or void
     currently not enforced by the AST generator.
  public TokenAST<?> pipe(CharSequence data) {
    return factory.createToken(TerminalEnum.pipe,null);
  }*/
  public void pipe(CharSequence data) {
    //return factory.createToken(TerminalEnum.pipe,null);
  }

  public TokenAST<?> plus(CharSequence data) {
    return factory.createToken(TerminalEnum.plus,null);
  }
  
  public TokenAST<?> prioritiesdecl(CharSequence data) {
    return factory.createToken(TerminalEnum.prioritiesdecl,null);
  }

  public TokenAST<?> productionsdecl(CharSequence data) {
    return factory.createToken(TerminalEnum.productionsdecl,null);
  }

  public TokenAST<?> qmark(CharSequence data) {
    return factory.createToken(TerminalEnum.qmark,null);
  }

  public TokenAST<String> qualifiedid(CharSequence data) {
    return factory.createToken(TerminalEnum.qualifiedid,data.toString());
  }

  public TokenAST<?> quote(CharSequence data) {
    return factory.createToken(TerminalEnum.quote,null);
  }

  public TokenAST<?> rbracket(CharSequence data) {
    return factory.createToken(TerminalEnum.rbracket,null);
  }
  
  public TokenAST<?> rsqbracket(CharSequence data) {
    return factory.createToken(TerminalEnum.rsqbracket,null);
  }

  public TokenAST<String> regexdoublequote(CharSequence data) {
    return factory.createToken(TerminalEnum.regexdoublequote,data.toString());
  }

  public TokenAST<String> regexquote(CharSequence data) {
    return factory.createToken(TerminalEnum.regexquote,data.toString());
  }

  public TokenAST<?> rpar(CharSequence data) {
    return factory.createToken(TerminalEnum.rpar,null);
  }

  public TokenAST<?> semicolon(CharSequence data) {
    return factory.createToken(TerminalEnum.semicolon,null);
  }

  public TokenAST<?> slash(CharSequence data) {
    return factory.createToken(TerminalEnum.slash,null);
  }

  public TokenAST<?> star(CharSequence data) {
    return factory.createToken(TerminalEnum.star,null);
  }

  public TokenAST<?> startsdecl(CharSequence data) {
    return factory.createToken(TerminalEnum.startsdecl,null);
  }

  public TokenAST<?> tokensdecl(CharSequence data) {
    return factory.createToken(TerminalEnum.tokensdecl,null);
  }
  
  public TokenAST<?> typesdecl(CharSequence data) {
    return factory.createToken(TerminalEnum.typesdecl,null);
  }
  
  public TokenAST<?> attributesdecl(CharSequence data) {
    return factory.createToken(TerminalEnum.attributesdecl,null);
  }
  
  public TokenAST<?> versionsdecl(CharSequence data) {
    return factory.createToken(TerminalEnum.versionsdecl,null);
  }
  
  private static String unescape(CharSequence data) {
    StringBuilder buffer = new StringBuilder(data.length());
    for(int i=1;i<data.length()-1;i++) {
      char c = data.charAt(i);
      if (c!='\\')
        buffer.append(c);
      else {
        i++;
        buffer.append(data.charAt(i));
      }
    }
    return buffer.toString();
  }

  public TokenAST<String> quoted_name(CharSequence data) {
    return factory.createToken(TerminalEnum.quoted_name,unescape(data));
  }  
}

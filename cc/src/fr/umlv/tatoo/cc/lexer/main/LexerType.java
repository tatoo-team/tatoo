package fr.umlv.tatoo.cc.lexer.main;

import fr.umlv.tatoo.cc.lexer.charset.encoding.ASCIIEncoding;
import fr.umlv.tatoo.cc.lexer.charset.encoding.Encoding;
import fr.umlv.tatoo.cc.lexer.charset.encoding.ISO8859_1Encoding;
import fr.umlv.tatoo.cc.lexer.charset.encoding.UTF16Encoding;
import fr.umlv.tatoo.cc.lexer.charset.encoding.UTF8Encoding;

public enum LexerType  {
  unicode(UTF16Encoding.getInstance()),
  ascii(ASCIIEncoding.getInstance()),
  latin1(ISO8859_1Encoding.getInstance()),
  utf8(UTF8Encoding.getInstance());
 
  
  private LexerType(Encoding charset) {
    this.charset = charset;
  }
  public Encoding getEncoding() {
    return charset;
  }
  
  private final Encoding charset;
}
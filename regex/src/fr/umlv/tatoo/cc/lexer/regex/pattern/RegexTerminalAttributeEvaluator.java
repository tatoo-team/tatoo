/*
 * Created on 10 janv. 2006
 */
package fr.umlv.tatoo.cc.lexer.regex.pattern;

import fr.umlv.tatoo.cc.lexer.regex.pattern.tools.TerminalEvaluator;

public class RegexTerminalAttributeEvaluator implements TerminalEvaluator<CharSequence> {
  public char normalChar(CharSequence seq) {
    return seq.charAt(0);
  }

  public char stringChar(CharSequence seq) {
    return seq.charAt(0);
  }

  public char intervalChar(CharSequence seq) {
    return seq.charAt(0);
  }

  public char escapedChar(CharSequence seq) {
    return seq.charAt(1);
  }  

  public char stringEscapedChar(CharSequence seq) {
    return seq.charAt(1);
  }
  
  public char intervalEscapedChar(CharSequence seq) {
    return seq.charAt(1);
  }
  
  public char unicodeChar(CharSequence seq) {
    return (char) parseInt(seq,16,2);    
  }

  public char eoln(CharSequence seq) {
    return '\n';
  }

  public char cr(CharSequence seq) {
    return '\r';
  }

  public char formfeed(CharSequence seq) {
    return '\f';
  }

  public char tab(CharSequence seq) {
    return '\t';
  }

  public char backspace(CharSequence seq) {
    return '\b';
  }
  
  public int integer(CharSequence seq) {
    return parseInt(seq,10,0);
  }
  
  public String macro(CharSequence seq) {
    return seq.subSequence(1, seq.length()-1).toString();
  }
  
  private static int parseInt(CharSequence seq,int radix,int offset) {
    int ans=0;
    for(int i=offset;i<seq.length();i++) {
      ans=radix*ans+(Character.digit(seq.charAt(i), radix));
    }
    return ans;    
  }
}

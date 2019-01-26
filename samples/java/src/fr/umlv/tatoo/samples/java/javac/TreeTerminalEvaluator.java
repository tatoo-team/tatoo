package fr.umlv.tatoo.samples.java.javac;

import com.sun.tools.javac.tree.JCTree;

import fr.umlv.tatoo.samples.java.tools.TerminalEvaluator;

public class TreeTerminalEvaluator implements TerminalEvaluator<CharSequence> {
  protected <T extends JCTree> T endPos(T tree) {
    return tree;
  }
  
  @Override
  public String identifier(CharSequence data) {
    return data.toString();
  }
  
  @Override
  public boolean booleanlit(CharSequence data) {
    return data.charAt(0)=='t';
  }
  @Override
  public char characterlit(CharSequence data) {
    return data.charAt(0);
  }
  @Override
  public String integerlit(CharSequence data) {
    return data.toString();
  }
  @Override
  public String floatingpointlit(CharSequence data) {
    return data.toString();
  }
  @Override
  public String stringlit(CharSequence data) {
    return data.subSequence(1,data.length()-1).toString();
  }
}

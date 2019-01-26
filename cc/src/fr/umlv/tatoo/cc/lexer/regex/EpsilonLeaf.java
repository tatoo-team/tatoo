/*
 * Created on Jun 13, 2003
 *
 */
package fr.umlv.tatoo.cc.lexer.regex;

import java.util.Collections;
import java.util.Set;

import fr.umlv.tatoo.cc.common.util.MultiMap;
/**
 * @author jcervell
 * The same instance of this class can be shared within one Regex
 */
public class EpsilonLeaf extends Regex {
  
  @Override
  public boolean nullable() {
    return true;
  }
  
  /* no need to clone epsilon */
  @Override
  public Regex cloneRegex() {
    return this;
  }
  
  @Override
  public <P> void accept(Visitor<? super P> visitor, P param) {
    visitor.visit(this,param);
  }
  
  @Override
  public Set<Leaf> firstPos() {
    return Collections.emptySet();
  }
  
  @Override
  public Set<Leaf> lastPos() {
    return Collections.emptySet();
  }
  
  @Override
  public String toString() {
    return "\u03B5";
  }
  
  @Override
  protected void computeFollowPos(MultiMap<Leaf,Leaf> followPos)
  {/*nothing to be done*/}
  
}

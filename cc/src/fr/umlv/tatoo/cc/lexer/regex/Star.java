/*
 * Created on Jun 13, 2003
 *
 */
package fr.umlv.tatoo.cc.lexer.regex;

import java.util.HashSet;
import java.util.Set;

import fr.umlv.tatoo.cc.common.util.MultiMap;

/**
 * @author jcervell
 *
 */
public class Star extends Node {
  
  private final Regex child;
  private final Set<Leaf> firstPos=new HashSet<Leaf>();
  private final Set<Leaf> lastPos=new HashSet<Leaf>();
  
  public Star(Regex child) {
    this.child=child;
    firstPos.addAll(child.firstPos());
    lastPos.addAll(child.lastPos());
  }
  
  @Override
  protected void computeFollowPos(MultiMap<Leaf,Leaf> followPos) {
    child.computeFollowPos(followPos);
    for (Leaf leaf : lastPos) {
      followPos.addSet(leaf,firstPos);
    }
  }
  
  @Override
  public boolean nullable() {
    return true;
  }
  
  @Override
  public Set<Leaf> firstPos() {
    return firstPos;
  }
  
  @Override
  public Set<Leaf> lastPos() {
    return lastPos;
  }
  
  @Override
  public String toString() {
    return "("+child+")*";
  }
  
  @Override
  public Regex cloneRegex() {
    return new Star(child.cloneRegex());
  }
  
  @Override
  public <P> void accept(Visitor<? super P> visitor, P param) {
    visitor.visit(this,param);
  }
}

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
public class Cat extends Node {
	
  private final Regex left,right;
  private final boolean nullable;
  private final Set<Leaf> firstPos=new HashSet<Leaf>();
  private final Set<Leaf> lastPos=new HashSet<Leaf>();
	
  public Cat(Regex left,Regex right) {
    this.left=left;
    this.right=right;
    nullable=left.nullable()&&right.nullable();
    firstPos.addAll(left.firstPos());
    if (left.nullable())
      firstPos.addAll(right.firstPos());
    lastPos.addAll(right.lastPos());
    if (right.nullable())
      lastPos.addAll(left.lastPos());
  }
  
  public Regex getLeft() {
    return left;
  }
  public Regex getRight() {
    return right;
  }
  
  @Override
  public Regex cloneRegex() {
    return new Cat(left.cloneRegex(),right.cloneRegex());
  }
  
  @Override
  public <P> void accept(Visitor<? super P> visitor, P param) {
    visitor.visit(this,param);
  }

  @Override
  protected void computeFollowPos(MultiMap<Leaf,Leaf> followPos) {
    for (Leaf leaf : left.lastPos()) {
      followPos.addSet(leaf,right.firstPos());
    }
    left.computeFollowPos(followPos);
    right.computeFollowPos(followPos);
  }

  @Override
  public boolean nullable() {
    return nullable;
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
    return "("+left+right+")";
  }

}

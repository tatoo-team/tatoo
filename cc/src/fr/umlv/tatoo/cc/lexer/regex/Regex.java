/*
 * Created on 8 juin 2003
 */
package fr.umlv.tatoo.cc.lexer.regex;

import java.util.*;


import fr.umlv.tatoo.cc.common.util.*;

/**
 * @author julien
 *
 */
public abstract class Regex {

  protected abstract void computeFollowPos(MultiMap<Leaf,Leaf> followPos);

  public abstract boolean nullable();
  public abstract Set<Leaf> firstPos();
    
  public abstract Set<Leaf> lastPos();
  public abstract Regex cloneRegex();
  
  public abstract <P> void accept(Visitor<? super P> visitor, P param);

  public static class Visitor<P> {
    public void visit(Cat cat, P param) {
      visit((Node)cat,param);   
    }
    public void visit(Or or, P param) {
      visit((Node)or,param);   
    }
    public void visit(Star star, P param) {
      visit((Node)star,param);   
    }
    
    protected void visit(Node node, P param) {
      visit((Regex)node,param);   
    }
    
    public void visit(Leaf leaf, P param) {
      visit((Regex)leaf,param);   
    }
    public void visit(EpsilonLeaf epsilonLeaf, P param) {
      visit((Regex)epsilonLeaf,param);   
    }
    
    protected void visit(Regex regex, P param) {
      throw new AssertionError("no visit for regex "+regex+" param "+param);
    }
  }
  
}

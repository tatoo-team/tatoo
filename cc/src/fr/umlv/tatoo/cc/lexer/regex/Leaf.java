/*
 * Created on 8 juin 2003
 */
package fr.umlv.tatoo.cc.lexer.regex;

import java.util.Collections;
import java.util.Set;

import fr.umlv.tatoo.cc.common.util.MultiMap;
import fr.umlv.tatoo.cc.lexer.charset.CharacterSet;

/**
 * @author julien
 * One instance of this class MUST NOT be shared within one Regex
 */
public class Leaf extends Regex {

  private final Set<Leaf> firstLast; 
  private final CharacterSet letters;
  
  public Leaf(CharacterSet intervalList) {
    this.firstLast=Collections.singleton(this);
    letters=intervalList;
  }
 
  /** this contructor must only be used for EOF leaf */
  Leaf() {
    this(null);
  }
  
  @Override
  public boolean nullable() {
    return false;
  }

  @Override
  public Set<Leaf> firstPos() {
    return firstLast;
  }

  @Override
  public Set<Leaf> lastPos() {
    return firstLast;
  }
  
  public CharacterSet getLetters() {
    return letters;
  }

  //static int X=0;
  //int x=X++;
  @Override
  public String toString() {
    if (letters==null)
      return "__eof__";
    return letters.toString();
  }

  @Override
  public Regex cloneRegex() {
    return new Leaf(letters);
  }
  
  @Override
  public <P> void accept(Visitor<? super P> visitor, P param) {
    visitor.visit(this,param);
  }

	@Override
  protected void computeFollowPos(MultiMap<Leaf,Leaf> followPos)
    {/* nothing to be done */}

}

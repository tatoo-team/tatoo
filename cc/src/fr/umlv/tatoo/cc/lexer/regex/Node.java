/*
 * Created on 8 juin 2003
 */
package fr.umlv.tatoo.cc.lexer.regex;


/**
 * @author julien
 *
 */
public abstract class Node extends Regex {
  
  public boolean isLeaf() {
    return false;
  }
}

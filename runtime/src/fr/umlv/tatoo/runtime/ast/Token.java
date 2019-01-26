package fr.umlv.tatoo.runtime.ast;

public interface Token extends Node {
  /** Return the value of the current token.
   * @return the value of the current token or null otherwise.
   */
  public Object getValue();
}

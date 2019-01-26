/*
 * Created on 20 juil. 2005
 */
package fr.umlv.tatoo.cc.common.generator;

/**
 * Interface for all object that could be referenced
 * with a name in the generated code.
 * 
 * @author Remi
 */
public interface ObjectId {
  /** Returns the identifier of the current object
   * @return the identifier of the object.
   */
  public String getId();
}

/**
 * 
 */
package fr.umlv.tatoo.cc.common.main;

import fr.umlv.tatoo.cc.common.generator.Type;

public interface Alias {
  public Type getType();
  
  /** Tells if the file must be generated.
   * @return true if the file must be generated, false otherwise.
   */
  public boolean generate();
}
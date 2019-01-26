/**
 * 
 */
package fr.umlv.tatoo.cc.tools.generator;

import fr.umlv.tatoo.cc.common.generator.Type;

/** This class is used in ToolsProcessor.mc
 * @author Remi
 * 
 * @see ToolsGeneratorUtils
 */
public class ParamDecl {
  ParamDecl(Type type, String name) {
    this.type = type;
    this.name = name;
  }
  public String getName() {
    return this.name;
  }
  public Type getType() {
    return this.type;
  }    
  private final Type type;
  private final String name;
}
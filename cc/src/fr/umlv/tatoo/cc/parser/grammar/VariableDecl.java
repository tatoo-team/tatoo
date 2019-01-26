package fr.umlv.tatoo.cc.parser.grammar;

import fr.umlv.tatoo.cc.common.generator.ObjectId;

/** Abstract class for terminals and non terminals
 */
public interface VariableDecl extends ObjectId {
  public boolean isTerminal();
  
//  public void reference(ReferenceContext context);
}

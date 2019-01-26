package fr.umlv.tatoo.cc.ebnf.ast.analysis;

import fr.umlv.tatoo.cc.common.generator.Type;

/** External type checker
 */
public interface TypeVerifier {
  public boolean typeExist(Type type);
}

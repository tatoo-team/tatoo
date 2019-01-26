package fr.umlv.tatoo.runtime.ast;

/** Super-type of grammar evaluators that accept some non terminals.  
 */
public interface RootNodeProvider {
  public <N> N getRootNode(Class<N> nonTerminalType);
}

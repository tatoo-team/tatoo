package fr.umlv.tatoo.cc.parser.grammar;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/** Interface allowing to get all items of a grammar specification.
 * 
 * @author Remi
 */
public interface GrammarRepository {

  public Collection<? extends TerminalDecl> getAllTerminals();
  
  public Collection<? extends NonTerminalDecl> getAllNonTerminals();
  
  public Collection<? extends ProductionDecl> getAllProductions();
  
  public Collection<? extends VersionDecl> getAllVersions();
  
  public Set<? extends NonTerminalDecl> getStartNonTerminalSet();
  
  public Map<NonTerminalDecl,? extends Collection<? extends ProductionDecl>> getProductionsByNonTerminal();
}

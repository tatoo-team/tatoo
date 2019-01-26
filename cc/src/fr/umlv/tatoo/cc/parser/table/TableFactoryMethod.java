/*
 * Created on 23 feb 2006
 */
package fr.umlv.tatoo.cc.parser.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import fr.umlv.tatoo.cc.parser.grammar.Grammar;
import fr.umlv.tatoo.cc.parser.grammar.GrammarSets;
import fr.umlv.tatoo.cc.parser.grammar.NonTerminalDecl;
import fr.umlv.tatoo.cc.parser.grammar.ProductionDecl;
import fr.umlv.tatoo.cc.parser.grammar.TerminalDecl;

public abstract class TableFactoryMethod<I extends NodeItem<I>> {
  /**
   * Returns the closure computer for this method
   * @param grammar the grammar
   * @param grammarSets the grammar sets
   * @param eof the end of file terminal
   * @return the closure computer for this method
   */
  public abstract NodeClosureComputer<I> getClosureComputer(Grammar grammar, GrammarSets grammarSets, TerminalDecl eof);
  
  /**
   * builds the augmented grammar
   * @param productions list of productions
   * @param starts list of starts
   * @param eof terminal eof
   * @return the grammar
   */
  public Grammar buildGrammar(Collection<? extends ProductionDecl> productions,
      Set<? extends NonTerminalDecl> starts,
      TerminalDecl eof) {
    
    int size=productions.size();
    ArrayList<ProductionDecl> productionList =
      new ArrayList<ProductionDecl>(size+starts.size());
    productionList.addAll(productions);
    
    HashSet<NonTerminalDecl> roots = new HashSet<NonTerminalDecl>(starts.size());
    for(NonTerminalDecl start : starts) {
      ProductionDecl production = getAugmentingProduction(start, eof);
      productionList.add(production);
      roots.add(production.getLeft());
    }
    
    return new Grammar(productionList, roots);
  }
  
  public abstract ProductionDecl 
  getAugmentingProduction(NonTerminalDecl start, TerminalDecl eof);
  
  private int count;
  
  public NonTerminalDecl getNewRoot() {
    return new NonTerminalDecl("-root-"+(count++));
  }
  
  /**
   * returns the set of lookahead corresponding to a reduce action
   * by item
   * @param g the grammar
   * @param grammarSets nullable, first and follow sets corresponding to g 
   * @param item the item to reduce by
   * @param node the node
   * @return the set of lookaheads
   */
  public abstract Set<TerminalDecl> getLookaheads(Grammar g,
      GrammarSets grammarSets, I item,NodeDecl<I> node);
    
  /**
   * Create the kernel item for the start states
   * @param production the starting production
   * @param eof the end of file terminal
   * @return the new item
   */
  public abstract I createStartItem(ProductionDecl production, TerminalDecl eof);
  
  /**
   * Initialize some computation (for LALR)
   * @param factory the factory used to build nodes
   * @param grammar the grammar
   * @param grammarSets grammarSets first and follow sets corresponding the grammar
   * @param eof the end of file terminal
   */
  public abstract void initializeComputation(NodeFactory<I> factory,
      Grammar grammar, GrammarSets grammarSets,TerminalDecl eof);
  
  /**
   * Computes whether grammar with specified start accepts the empty word
   * @param start axiom to consider
   * @param grammar the grammar
   * @param grammarSets grammarSets first and follow sets corresponding the grammar
   * @param eof the end of file terminal
   * @return true if grammar accepts the empty word
   */
  public abstract boolean acceptsEmptyWord(NonTerminalDecl start, Grammar grammar, GrammarSets grammarSets,TerminalDecl eof);
}

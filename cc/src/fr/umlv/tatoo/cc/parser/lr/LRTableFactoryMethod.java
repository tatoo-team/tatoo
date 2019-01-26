/*
 * Created on 23 fevr. 2006
 */
package fr.umlv.tatoo.cc.parser.lr;

import java.util.Collections;
import java.util.Set;

import fr.umlv.tatoo.cc.parser.grammar.FakeProduction;
import fr.umlv.tatoo.cc.parser.grammar.Grammar;
import fr.umlv.tatoo.cc.parser.grammar.GrammarSets;
import fr.umlv.tatoo.cc.parser.grammar.NonTerminalDecl;
import fr.umlv.tatoo.cc.parser.grammar.ProductionDecl;
import fr.umlv.tatoo.cc.parser.grammar.TerminalDecl;
import fr.umlv.tatoo.cc.parser.table.NodeClosureComputer;
import fr.umlv.tatoo.cc.parser.table.NodeDecl;
import fr.umlv.tatoo.cc.parser.table.NodeFactory;
import fr.umlv.tatoo.cc.parser.table.TableFactoryMethod;

public class LRTableFactoryMethod extends TableFactoryMethod<LR1Item> {

  @Override
  public NodeClosureComputer<LR1Item> getClosureComputer(Grammar grammar, GrammarSets grammarSets, TerminalDecl eof) {
    //return LR1ClosureFactory.getLR1ClosureFactory().createLR1ClosureComputer(grammar,grammarSets,eof);
    return new LR1ClosureComputer(grammar, grammarSets, eof);
  }

  @Override
  public Set<TerminalDecl> getLookaheads(Grammar g, GrammarSets sets, LR1Item item,NodeDecl<LR1Item> node) {
    return Collections.singleton(item.getLookahead());
  }

  @Override
  public LR1Item createStartItem(ProductionDecl production, TerminalDecl eof) {
    return new LR1Item(production,eof);
  }

  @Override
  public void initializeComputation(NodeFactory<LR1Item> factory,
      Grammar grammar, GrammarSets grammarSets,TerminalDecl eof) {
    // nothing for LR
  }

  @Override
  public boolean acceptsEmptyWord(NonTerminalDecl start, Grammar grammar,
      GrammarSets grammarSets, TerminalDecl eof) {
    return grammarSets.derivesToEpsilon(start);
  }

  @Override
  public ProductionDecl getAugmentingProduction(NonTerminalDecl start,
      TerminalDecl eof) {
    final NonTerminalDecl root = getNewRoot();
    
    return new FakeProduction(root.getId(),root,Collections.singletonList(start));
  }

}

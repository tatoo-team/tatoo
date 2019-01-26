/*
 * Created on 23 fb. 2006
 */
package fr.umlv.tatoo.cc.parser.slr;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import fr.umlv.tatoo.cc.parser.grammar.FakeProduction;
import fr.umlv.tatoo.cc.parser.grammar.Grammar;
import fr.umlv.tatoo.cc.parser.grammar.GrammarSets;
import fr.umlv.tatoo.cc.parser.grammar.NonTerminalDecl;
import fr.umlv.tatoo.cc.parser.grammar.ProductionDecl;
import fr.umlv.tatoo.cc.parser.grammar.TerminalDecl;
import fr.umlv.tatoo.cc.parser.grammar.VariableDecl;
import fr.umlv.tatoo.cc.parser.table.NodeDecl;
import fr.umlv.tatoo.cc.parser.table.NodeFactory;
import fr.umlv.tatoo.cc.parser.table.TableFactoryMethod;

public class SLRTableFactoryMethod extends TableFactoryMethod<LR0Item> {

  @Override
  public LR0ClosureComputer getClosureComputer(Grammar grammar, GrammarSets grammarSets, TerminalDecl eof) {
    return new LR0ClosureComputer(grammar);
  }

  @Override
  public Set<TerminalDecl> getLookaheads(Grammar g, GrammarSets sets, LR0Item item,NodeDecl<LR0Item> node) {
    // no reduce by the start
    if (g.getStarts().contains(item.getLeft()))
      return Collections.emptySet();
    return sets.follow(item.getLeft());
  }

  @Override
  public LR0Item createStartItem(ProductionDecl production, TerminalDecl eof) {
    return new LR0Item(production);
  }

  @Override
  public void initializeComputation(NodeFactory<LR0Item> factory,
      Grammar grammar, GrammarSets grammarSets,TerminalDecl eof) {
    // nothing for SLR
  }

  @Override
  public boolean acceptsEmptyWord(NonTerminalDecl start,Grammar grammar, GrammarSets grammarSets,
      TerminalDecl eof) {
    return grammarSets.first(start).contains(eof);
  }

  @Override
  public ProductionDecl getAugmentingProduction(NonTerminalDecl start,TerminalDecl eof) {
    final NonTerminalDecl root = getNewRoot();
    VariableDecl[] right = new VariableDecl[] {start,eof};

    return new FakeProduction(root.getId(),root,Arrays.asList(right));
  }

}

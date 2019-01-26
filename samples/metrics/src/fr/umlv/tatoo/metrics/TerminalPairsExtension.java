package fr.umlv.tatoo.metrics;

import java.util.HashSet;
import java.util.List;

import fr.umlv.tatoo.cc.common.extension.Extension;
import fr.umlv.tatoo.cc.common.extension.ExtensionBus;
import fr.umlv.tatoo.cc.common.extension.ExtensionBus.Context;
import fr.umlv.tatoo.cc.common.extension.ExtensionBus.Registry;
import fr.umlv.tatoo.cc.common.util.Pair;
import fr.umlv.tatoo.cc.parser.grammar.Grammar;
import fr.umlv.tatoo.cc.parser.grammar.GrammarSets;
import fr.umlv.tatoo.cc.parser.grammar.NonTerminalDecl;
import fr.umlv.tatoo.cc.parser.grammar.ProductionDecl;
import fr.umlv.tatoo.cc.parser.grammar.TerminalDecl;
import fr.umlv.tatoo.cc.parser.grammar.VariableDecl;
import fr.umlv.tatoo.cc.parser.main.ParserDataKeys;

public class TerminalPairsExtension implements Extension {

  @Override
  public void register(Registry registry) {
    registry.register(ParserDataKeys.grammarSets);
  }
  
  private final HashSet<Pair<TerminalDecl,TerminalDecl>> set = new HashSet<Pair<TerminalDecl,TerminalDecl>>(); 
  private GrammarSets grammarSets;
  
  private void addPair(TerminalDecl terminal, TerminalDecl terminal2) {
    set.add(Pair.pair(terminal, terminal2));
  }
  
  private void addPair(TerminalDecl terminal, NonTerminalDecl nonTerminal2) {
    for(TerminalDecl terminal2:grammarSets.first(nonTerminal2))
      addPair(terminal,terminal2);
  }
  
  private void addPair(NonTerminalDecl nonTerminal, TerminalDecl terminal2) {
    for(TerminalDecl terminal:grammarSets.last(nonTerminal))
      addPair(terminal,terminal2);
  }
  
  private void addPair(NonTerminalDecl nonTerminal, NonTerminalDecl nonTerminal2) {
    for(TerminalDecl terminal:grammarSets.last(nonTerminal))
      addPair(terminal,nonTerminal2);
  }
  
  private void addPair(VariableDecl variable, TerminalDecl terminal2) {
    if (variable.isTerminal())
      addPair((TerminalDecl)variable,terminal2);
    else
      addPair((NonTerminalDecl)variable,terminal2);
  }
    
  private void addPair(VariableDecl variable, NonTerminalDecl nonTerminal2) {
    if (variable.isTerminal())
      addPair((TerminalDecl)variable,nonTerminal2);
    else
      addPair((NonTerminalDecl)variable,nonTerminal2);
  }
  
  @Override
  public void execute(ExtensionBus bus, Context context) {
    grammarSets = context.getData(ParserDataKeys.grammarSets);
    Grammar grammar = grammarSets.getGrammar();
    for(List<? extends ProductionDecl> productionList:grammar.getProductions().values()) {
      for(ProductionDecl production:productionList) {
        List<? extends VariableDecl> right = production.getRight();
        if (right.size()<2)
          continue;
        for(int i=0;i<right.size()-1;i++) {
          VariableDecl variable = right.get(i);
          for(int j=i+1;j<right.size();j++) {
            VariableDecl variable2 = right.get(j);
            if (variable2.isTerminal()) {
              addPair(variable,(TerminalDecl)variable2);
              break;
            }
            NonTerminalDecl nonTerminal2 = (NonTerminalDecl)variable2;
            addPair(variable,nonTerminal2);
            if (!grammarSets.derivesToEpsilon(nonTerminal2))
              break;
          }
        }
      }
    }
    System.out.println("Terminal pairs "+set);
    System.out.println("Cardinal "+set.size());
  }

}

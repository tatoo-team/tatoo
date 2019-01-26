/*
 * Created on May 30, 2003
 */
package fr.umlv.tatoo.cc.parser.lr;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.umlv.tatoo.cc.parser.grammar.Grammar;
import fr.umlv.tatoo.cc.parser.grammar.GrammarSets;
import fr.umlv.tatoo.cc.parser.grammar.NonTerminalDecl;
import fr.umlv.tatoo.cc.parser.grammar.Priority;
import fr.umlv.tatoo.cc.parser.grammar.ProductionDecl;
import fr.umlv.tatoo.cc.parser.grammar.TerminalDecl;
import fr.umlv.tatoo.cc.parser.grammar.VariableDecl;
import fr.umlv.tatoo.cc.parser.solver.NodeContent;
import fr.umlv.tatoo.cc.parser.solver.NodeFactory;
import fr.umlv.tatoo.cc.parser.solver.Solver;
import fr.umlv.tatoo.cc.parser.table.Closure;
import fr.umlv.tatoo.cc.parser.table.NodeClosureComputer;

/**
 * @author jcervell
 *
 */
public class LR1ClosureComputer implements NodeClosureComputer<LR1Item>{
  final Grammar grammar;
  final GrammarSets grammarSets;
  final TerminalDecl eof;
  
  private final Solver<NonTerminalDecl,HashSet<LR1Item>> closure = 
    new Solver<NonTerminalDecl,HashSet<LR1Item>>(new ClosureNodeFactory());
  
  final TerminalDecl natural = new TerminalDecl("+\u266E+",Priority.getNoPriority(),false);
  
//  private final class ClosureInput {
//    /* this item must have a NonTerminal after the dot */
//    ClosureInput(LR1Item p) {
//      lookaheads = computeLookaheads(p);
//      nonTerminal = (NonTerminalDecl) p.getDottedVariable();
//    }
//    
//    private final Set<TerminalDecl> lookaheads;
//    private final NonTerminalDecl nonTerminal;
//    
//    @Override
//    public boolean equals(Object o) {
//      if (!(o instanceof ClosureInput))
//        return false;
//      ClosureInput i = (ClosureInput)o;
//      return lookaheads.equals(i.getLookaheads())
//      && nonTerminal.equals(i.getNonTerminal());
//    }
//    
//    @Override
//    public int hashCode() {
//      return nonTerminal.hashCode()^lookaheads.hashCode();
//    }
//    
//    @Override
//    public String toString() {
//      return nonTerminal+" | "+lookaheads;
//    }
//    public Set<TerminalDecl> getLookaheads() {
//      return lookaheads;
//    }
//    
//    public NonTerminalDecl getNonTerminal() {
//      return nonTerminal;
//    }
//    
//  }

  Set<TerminalDecl> computeLookaheads(LR1Item p) {
    int dotPlace = p.getDotPlace();
    List<? extends VariableDecl> right = p.getRight();
    int size = right.size();
    VariableDecl[] tab = new VariableDecl[size - dotPlace];
    for (int i = dotPlace + 1; i < size; i++)
      tab[i - dotPlace - 1] = right.get(i);
    tab[size - dotPlace - 1] = p.getLookahead();
    
    return grammarSets.first(tab);
  }
  
  public LR1ClosureComputer(Grammar grammar,GrammarSets grammarSets, TerminalDecl eof) {
    this.grammar = grammar;
    this.grammarSets = grammarSets;
    this.eof = eof;
  }
  
  private class ClosureNodeFactory
  implements NodeFactory<NonTerminalDecl,HashSet<LR1Item>> {
    
    ClosureNodeFactory() { /* nothing */ }
    
    public NodeContent<NonTerminalDecl,HashSet<LR1Item>> getNode(NonTerminalDecl key) {
      return new ClosureNode(key);
    }
  }
  
  private class ClosureNode implements NodeContent<NonTerminalDecl,HashSet<LR1Item>> {
    private final HashMap<NonTerminalDecl,HashSet<TerminalDecl>> depends =
      new HashMap<NonTerminalDecl,HashSet<TerminalDecl>>();
    private final HashSet<LR1Item> itemSet;
    private final HashSet<TerminalDecl> generatedLookahead;
    
    public ClosureNode(NonTerminalDecl input) {
      Map<? extends NonTerminalDecl,? extends List<? extends ProductionDecl>> prodmap = 
        grammar.getProductions();

      List<? extends ProductionDecl> prods = prodmap.get(input);
      itemSet = new HashSet<LR1Item>();
      generatedLookahead = new HashSet<TerminalDecl>();
      generatedLookahead.add(natural);
      for (ProductionDecl prod : prods) {
        //Set<TerminalDecl> lookaheads = input.getLookaheads();
        
        LR1Item item = new LR1Item(prod, natural);
        
        List<? extends VariableDecl> right = prod.getRight();
        
        if (right.isEmpty()) {
          continue;
        }       

        VariableDecl v = right.get(0);

        if (!v.isTerminal()) {
          if (v==input) {
            generatedLookahead.addAll(computeLookaheads(item)); 
          }
          else {
            HashSet<TerminalDecl> set = depends.get(v);
            if (set==null) {
              set = new HashSet<TerminalDecl>();
              depends.put((NonTerminalDecl)v, set);
            }
            set.addAll(computeLookaheads(item));
          }
        }
      }
      for (ProductionDecl prod : prods)
        for(TerminalDecl terminal:generatedLookahead)
          itemSet.add(new LR1Item(prod,terminal));
    }
    
    public boolean hasChanged(NonTerminalDecl key,
        NodeContent<NonTerminalDecl,HashSet<LR1Item>> node,
        Solver<NonTerminalDecl,HashSet<LR1Item>> solver) {
      HashSet<LR1Item> tmpC = node.getCurrentResult();
      tmpC=replaceNatural(tmpC,depends.get(key));
      if (!(generatedLookahead.size()==1))
        tmpC=replaceNatural(tmpC,generatedLookahead);
      return itemSet.addAll(tmpC);
    }
    
    public Set<NonTerminalDecl> dependencies() {
      return depends.keySet();
    }
    
    
    public HashSet<LR1Item> getCurrentResult() {
      return itemSet;
    }
    
    public HashSet<LR1Item> getResult() {
      return itemSet;
    }
  }
  
  private HashSet<LR1Item> getClosure(NonTerminalDecl e) {
    return closure.solve(e);
  }
  
  public Closure<LR1Item> getClosure(LR1Item p) {
    HashSet<LR1Item> closure = getClosure((NonTerminalDecl) p.getDottedVariable());
    return new Closure<LR1Item>(replaceNatural(closure,computeLookaheads(p)));
  }

  HashSet<LR1Item> replaceNatural(HashSet<LR1Item> initSet,
      Set<TerminalDecl> lookaheads) {
    HashSet<LR1Item> set = new HashSet<LR1Item>();
    for(LR1Item item:initSet) {
      if (item.getLookahead()==natural) {
        for(TerminalDecl terminal:lookaheads)
          set.add(new LR1Item(item.getProduction(),item.getDotPlace(),terminal));
      } else {
        set.add(item);
      }
    }
    return set;
  }
  
}

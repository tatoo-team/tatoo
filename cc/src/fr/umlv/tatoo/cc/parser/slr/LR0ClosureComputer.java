package fr.umlv.tatoo.cc.parser.slr;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.umlv.tatoo.cc.parser.grammar.Grammar;
import fr.umlv.tatoo.cc.parser.grammar.NonTerminalDecl;
import fr.umlv.tatoo.cc.parser.grammar.ProductionDecl;
import fr.umlv.tatoo.cc.parser.grammar.VariableDecl;
import fr.umlv.tatoo.cc.parser.solver.NodeContent;
import fr.umlv.tatoo.cc.parser.solver.NodeFactory;
import fr.umlv.tatoo.cc.parser.solver.Solver;
import fr.umlv.tatoo.cc.parser.table.Closure;
import fr.umlv.tatoo.cc.parser.table.NodeClosureComputer;

public class LR0ClosureComputer implements NodeClosureComputer<LR0Item>{
  final Grammar grammar;
  final Solver<NonTerminalDecl,Closure<LR0Item>> closure = 
    new Solver<NonTerminalDecl,Closure<LR0Item>>(new ClosureNodeFactory());
  
  public LR0ClosureComputer(Grammar grammar) {
    this.grammar= grammar;
  }
  
  
  
  private final class ClosureNodeFactory 
  implements NodeFactory<NonTerminalDecl,Closure<LR0Item>> {
  
    ClosureNodeFactory() { /* nothing */ }
  
    public NodeContent<NonTerminalDecl,Closure<LR0Item>> getNode(NonTerminalDecl key) {
      return new ClosureNode(key);
    }
    
  }
  
  private final class ClosureNode 
  implements NodeContent<NonTerminalDecl,Closure<LR0Item>> {
    
    private final HashSet<NonTerminalDecl> depends =
      new HashSet<NonTerminalDecl>();
    private final Closure<LR0Item> closure;
    
    public ClosureNode(NonTerminalDecl nonTerminal) {
      
      Map<? extends NonTerminalDecl, ? extends List<? extends ProductionDecl>> prodmap 
          = grammar.getProductions();
      
      List<? extends ProductionDecl> prods= prodmap.get(nonTerminal);
      
      HashSet<LR0Item> items = new HashSet<LR0Item>();
      
      for (ProductionDecl prod : prods) {
        
        LR0Item item=new LR0Item(prod);
        items.add(item);
        
        List<? extends VariableDecl> right = prod.getRight();
        
        if (right.isEmpty())
          continue;
        
        VariableDecl v= right.get(0);
        
        if (!v.isTerminal()&& ! v.equals(nonTerminal)) {
          depends.add((NonTerminalDecl)v);
        }
      }
      
      
      closure = new Closure<LR0Item>(items);
      
    }
    
    public boolean hasChanged(NonTerminalDecl key,
        NodeContent<NonTerminalDecl,Closure<LR0Item>> node,
        Solver<NonTerminalDecl,Closure<LR0Item>> solver) {
      Closure<LR0Item> tmpC = node.getCurrentResult();
      return closure.update(tmpC);
    }
    
    public Set<NonTerminalDecl> dependencies() {
      return depends;
    }
    
    public Closure<LR0Item> getCurrentResult() {
      return closure;
    }
    
    public Closure<LR0Item> getResult() {
      return closure;
    }
  }
  
  public Closure<LR0Item> getClosure(LR0Item t) {
    return closure.solve((NonTerminalDecl)t.getDottedVariable());	
  }
}

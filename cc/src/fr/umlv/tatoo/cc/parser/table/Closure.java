package fr.umlv.tatoo.cc.parser.table;

import java.util.HashSet;
import java.util.List;

import fr.umlv.tatoo.cc.common.util.MultiMap;
import fr.umlv.tatoo.cc.parser.grammar.NonTerminalDecl;
import fr.umlv.tatoo.cc.parser.grammar.ProductionDecl;
import fr.umlv.tatoo.cc.parser.grammar.TerminalDecl;
import fr.umlv.tatoo.cc.parser.grammar.VariableDecl;

public final  class Closure<I extends NodeItem<I>> {
  private final HashSet<I> items;
  private final MultiMap<NonTerminalDecl,I> gotos=
    new MultiMap<NonTerminalDecl,I>();
  private final MultiMap<TerminalDecl,I> shifts=
    new MultiMap<TerminalDecl,I>();
  private final HashSet<I> reduces= new HashSet<I>();
  
  
  public boolean same(Closure<I> closure) {
    return closure.items.equals(items);
  }
  
  // TODO replace by Set<I>
  public Closure(HashSet<I> items) {
    this.items = items;
    for(I item : items) {
      
      ProductionDecl prod = item.getProduction();
      List<? extends VariableDecl> right = prod.getRight();
      if (right.isEmpty()) {
        reduces.add(item);
        continue;
      }
    
      VariableDecl v = right.get(0);
    
      if (v.isTerminal())
        shifts.add((TerminalDecl)v, item);
      else 
        gotos.add((NonTerminalDecl)v,item);
    }
  }
  
  @Override
  public String toString() {
    return "Items : "
    + items
    + "\n Gotos : "
    + gotos
    + "\n Shifts : "
    + shifts
    + "\n Reduce : " + reduces;
  }
  public MultiMap<NonTerminalDecl,I> getGotos() {
    return gotos;
  }
  
  public HashSet<I> getItems() {
    return items;
  }
  
  public HashSet<I> getReduces() {
    return reduces;
  }
  
  public MultiMap<TerminalDecl,I> getShifts() {
    return shifts;
  }
    
  public boolean update(Closure<I> closure) {
    boolean g=gotos.addAll(closure.getGotos());
    boolean i=items.addAll(closure.getItems());
    boolean r=reduces.addAll(closure.getReduces());
    boolean s=shifts.addAll(closure.getShifts());
    return g||i||r||s;
  } 
}

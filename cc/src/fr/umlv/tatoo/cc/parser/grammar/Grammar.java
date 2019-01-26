package fr.umlv.tatoo.cc.parser.grammar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.umlv.tatoo.cc.common.log.Info;

public class Grammar {

  private final HashSet<TerminalDecl> alphabet;
  private final HashSet<NonTerminalDecl> nonTerminals;
  
  final HashSet<NonTerminalDecl> starts;
  final HashMap<NonTerminalDecl,ArrayList<ProductionDecl>> productions;
  final HashMap<NonTerminalDecl,ArrayList<MarkedProduction>> prodRight;
  
  public final Map<NonTerminalDecl,? extends List<ProductionDecl>> getProductions() {
    return productions;
  }

  public final Set<? extends NonTerminalDecl> getStarts() {
    return starts;
  }
  
  public final Set<? extends NonTerminalDecl> getNonTerminals() {
    return nonTerminals;
  }

  public final Set<? extends TerminalDecl> getAlphabet() {
    return alphabet;
  }
  
  public Map<NonTerminalDecl, ? extends List<MarkedProduction>> getRightProductionMap() {
    return prodRight;
  }
  
  private void updateMaps(List<? extends ProductionDecl> prod) {
    
    productions.clear();
    prodRight.clear();
    alphabet.clear();
    nonTerminals.clear();
    
    for (ProductionDecl production:prod) {
      NonTerminalDecl n = production.getLeft();
      nonTerminals.add(n);
      ArrayList<ProductionDecl> list = productions.get(n);
      if (list == null) {
        list = new ArrayList<ProductionDecl>();
        productions.put(n, list);
      }
      list.add(production);

      List<? extends VariableDecl> t = production.getRight();

      for (int j = 0; j < t.size(); j++) {
        VariableDecl variable = t.get(j);

        if (variable.isTerminal()) {
          alphabet.add((TerminalDecl)variable);
        } else {
          NonTerminalDecl nonTerminal = (NonTerminalDecl)variable;
          nonTerminals.add(nonTerminal);
          ArrayList<MarkedProduction> rightList = prodRight.get(nonTerminal);
          if (rightList == null) {
            rightList = new ArrayList<MarkedProduction>();
            prodRight.put(nonTerminal, rightList);
          }
          rightList.add(new MarkedProduction(production, j));
        }
      }
    }
  }

  private ArrayList<ProductionDecl> getUsefullProduction(Set<NonTerminalDecl> usefull,List<? extends ProductionDecl> productions) {
    ArrayList<ProductionDecl> usefullProd = new ArrayList<ProductionDecl>();
    production: for(ProductionDecl production:productions) {
      if (!usefull.contains(production.getLeft())) {
        Info.warning("Unusefull production %s",production).report();
        continue production;
      }
      List<? extends VariableDecl> rights = production.getRight();
      for(VariableDecl var : rights)
        if (!var.isTerminal() && !usefull.contains(var)){
          Info.warning("Unusefull production %s",production,var).report();
          continue production;
        }
      usefullProd.add(production);
    }
    return usefullProd;
  }
  
  public Grammar(List<? extends ProductionDecl> prod, Set<? extends NonTerminalDecl> starts) {
    productions = new HashMap<NonTerminalDecl,ArrayList<ProductionDecl>>();
    alphabet = new HashSet<TerminalDecl>();
    prodRight = new HashMap<NonTerminalDecl,ArrayList<MarkedProduction>>();
    this.starts=new HashSet<NonTerminalDecl>(starts);
    this.nonTerminals=new HashSet<NonTerminalDecl>();
    
    
    updateMaps(prod);
    starts.retainAll(nonTerminals);
    if (starts.isEmpty())
      throw new IllegalArgumentException("start non terminal not in non terminal");
    
    //System.out.println("Grammar.<init> [before] start non terminal "+starts);

    GrammarChecker checker=new GrammarChecker();
    Set<NonTerminalDecl> usefull = checker.getProducing(this.nonTerminals, productions);
    prod = getUsefullProduction(usefull, prod);
    
    updateStart(usefull);
    updateMaps(prod);
    

    usefull = checker.getAccessible(productions, this.starts);
    //this.nonTerminals=usefull;

    
    prod = getUsefullProduction(usefull, prod);
    updateMaps(prod);
    updateStart(usefull);
    
    //DEBUG
    //System.out.println("Grammar.<init> [after] start non terminal "+starts);
    //System.out.println("Grammar.<init> [after] productions "+productions);
    //System.out.println("Grammar.<init> [after] nonTerminals "+nonTerminals);
    //System.out.println("Grammar.<init> [after] prodRight "+prodRight);
    
  }
  
  private void updateStart(Set<NonTerminalDecl> usefull) {
    for(Iterator<? extends NonTerminalDecl> i = starts.iterator();i.hasNext();) {
      NonTerminalDecl start = i.next();
      if (!usefull.contains(start)) {
        Info.warning("Unusefull start ",start).report();

        i.remove();    
      }
    }
    if (starts.isEmpty())
      throw new IllegalArgumentException("No usefull start");
  }

  @Override
  public String toString() {
    StringBuilder builder=new StringBuilder().
      append("alphabet : ").append(alphabet).  
      append("}\nnon terminals : ").append(nonTerminals).
      append("}\nstarts : ").append(starts).
      append("\nproductions : \n");
    for (ArrayList<ProductionDecl> prd : productions.values()) {
      for (ProductionDecl p : prd)
        builder.append(p).append('\n');
    }
    return builder.toString();
  }
  
  public static final class MarkedProduction {
    private final ProductionDecl production;
    private final int position;

    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder();
      buf.append(production.getLeft()).append(" ->");
      List<? extends VariableDecl> right = production.getRight();
      for (int i = 0; i < right.size(); i++) {
        if (position == i) {
          buf.append(" .");
        }
        buf.append(" ").append(right.get(i));
      }
      return buf.toString();
    }

    public MarkedProduction(ProductionDecl production, int position) {
      this.production = production;
      this.position = position;
    }

    public int getPosition() {
      return position;
    }

    public ProductionDecl getProduction() {
      return production;
    }
  }
  
  public NonTerminalDecl realStart(NonTerminalDecl start) {
    return (NonTerminalDecl)productions.get(start).iterator().next().getRight().iterator().next();
  }

}

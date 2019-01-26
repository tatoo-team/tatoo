package fr.umlv.tatoo.cc.parser.grammar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import fr.umlv.tatoo.cc.common.util.MultiMap;
import fr.umlv.tatoo.cc.common.util.Pair;
import fr.umlv.tatoo.cc.parser.grammar.Grammar.MarkedProduction;
import fr.umlv.tatoo.cc.parser.solver.NodeContent;
import fr.umlv.tatoo.cc.parser.solver.NodeFactory;
import fr.umlv.tatoo.cc.parser.solver.Solver;

public class GrammarSets {
  final Grammar grammar;

  private final Solver<NonTerminalDecl,HashSet<TerminalDecl>> first;
  private final Solver<NonTerminalDecl,HashSet<TerminalDecl>> last;
  private final Solver<NonTerminalDecl,HashSet<TerminalDecl>> follow;
  private final Solver<NonTerminalDecl,Boolean> epsilon;
  private final Solver<NonTerminalDecl,List<? extends TerminalDecl>> shortestWord;
  private final Solver<NonTerminalDecl,List<? extends VariableDecl>> shortestLeadingWord;

  public GrammarSets(Grammar grammar) {
    this.grammar=grammar;
    epsilon = new Solver<NonTerminalDecl,Boolean>(new EpsilonNodeFactory());
    first = new Solver<NonTerminalDecl,HashSet<TerminalDecl>>(new FirstLastNodeFactory(true));
    last = new Solver<NonTerminalDecl,HashSet<TerminalDecl>>(new FirstLastNodeFactory(false));
    follow = new Solver<NonTerminalDecl,HashSet<TerminalDecl>>(new FollowNodeFactory());
    shortestWord = new Solver<NonTerminalDecl, List<? extends TerminalDecl>>(new ShortestWordFactory());
    shortestLeadingWord = new Solver<NonTerminalDecl, List<? extends VariableDecl>>(new ShortestLeadingWordFactory());
  }
  
  public Grammar getGrammar() {
    return grammar;
  }

  class ShortestLeadingWordFactory implements
  NodeFactory<NonTerminalDecl, List<? extends VariableDecl>> {

    @Override
    public NodeContent<NonTerminalDecl, List<? extends VariableDecl>> getNode(NonTerminalDecl key) {
      return new ShortestLeadingWordNode(key);
    }  
  }

  private final class ShortestLeadingWordNode implements NodeContent<NonTerminalDecl, List<? extends VariableDecl>> {
    private List<? extends VariableDecl> result ;
    private final MultiMap<NonTerminalDecl,ProductionDecl> dependenciesMap = new MultiMap<NonTerminalDecl,ProductionDecl>();
    private final NonTerminalDecl key;

    ShortestLeadingWordNode(NonTerminalDecl key){
      this.key=key;
      if (grammar.getStarts().contains(key)) {
        ArrayList<VariableDecl> tmpResult = new ArrayList<VariableDecl>(2);
        tmpResult.add(grammar.realStart(key));
        tmpResult.add(key);
        result=tmpResult;
      } else {
        List<MarkedProduction> list = grammar.getRightProductionMap().get(key);
        if (list==null)
          throw new IllFormedGrammarException();
        for(MarkedProduction production:list) {
          dependenciesMap.add(production.getProduction().getLeft(),production.getProduction());
        }
      }
    }

    @Override
    public Set<NonTerminalDecl> dependencies() {
      return dependenciesMap.keySet();
    }

    @Override
    public List<? extends VariableDecl> getCurrentResult() {
      return result;
    }

    @Override
    public List<? extends VariableDecl> getResult() {
      return result;
    }

    @Override
    public boolean hasChanged(NonTerminalDecl key,
        NodeContent<NonTerminalDecl, List<? extends VariableDecl>> node,
        Solver<NonTerminalDecl, List<? extends VariableDecl>> solver) {
      boolean changed = false;
      Set<ProductionDecl> productions = dependenciesMap.get(key);
      List<? extends VariableDecl> toKey = node.getCurrentResult();
      NonTerminalDecl nt = this.key;
      if (toKey==null)
        return false;
      for(ProductionDecl production:productions) {
        LinkedList<VariableDecl> list = new LinkedList<VariableDecl>();
        Iterator<? extends VariableDecl> iterator = toKey.iterator();
        list.add(iterator.next());
        // add terminals to key
        for(;;) {
          VariableDecl x = iterator.next();
          if (x.isTerminal())
            list.add(x);
          else break;
        }
        // replace key using production
        boolean first=true;
        for(VariableDecl v2:production.getRight()) {
          if (v2.isTerminal())
            list.add(v2);
          else if (first && v2==nt) {
            list.add(nt);
            first=false;
          }
          else {
            list.addAll(shortestWord((NonTerminalDecl)v2));
          }
        }
        // add remaining terminals
        while(iterator.hasNext()) {
          list.add(iterator.next());
        }
        if (result!=null && result.size()<=list.size())
          continue;
        result=list;
        changed=true;
      }
      return changed;
    }
  }

  class ShortestWordFactory implements
  NodeFactory<NonTerminalDecl, List<? extends TerminalDecl>> {

    @Override
    public NodeContent<NonTerminalDecl, List<? extends TerminalDecl>> getNode(NonTerminalDecl key) {
      return new ShortestWordNode(key);
    }  
  }

  private class ShortestWordNode implements NodeContent<NonTerminalDecl, List<? extends TerminalDecl>> {
    private List<? extends TerminalDecl> result ;
    private final HashSet<NonTerminalDecl> dependencies = new HashSet<NonTerminalDecl>();
    private final NonTerminalDecl key;

    ShortestWordNode(NonTerminalDecl key){
      this.key=key;
      List<ProductionDecl> productions = grammar.getProductions().get(key);
      if (productions==null)
        throw new IllFormedGrammarException();
      production: for(ProductionDecl production:productions) {
        List<? extends VariableDecl> rights = production.getRight();
        for(VariableDecl v:rights) {
          if (!v.isTerminal())
            continue production;
        }
        if (result==null || result.size()>rights.size()) {
          ArrayList<TerminalDecl> result = new ArrayList<TerminalDecl>();
          for(VariableDecl v:rights)
            result.add((TerminalDecl)v);
          this.result=result;
        }
      }
      for(ProductionDecl production:productions) {
        for(VariableDecl v: production.getRight()) {
          if (!v.isTerminal())
            dependencies.add((NonTerminalDecl)v);
        }
      }
    }

    @Override
    public Set<NonTerminalDecl> dependencies() {
      return dependencies;
    }

    @Override
    public List<? extends TerminalDecl> getCurrentResult() {
      return result;
    }

    @Override
    public List<? extends TerminalDecl> getResult() {
      return result;
    }

    @Override
    public boolean hasChanged(NonTerminalDecl key,
        NodeContent<NonTerminalDecl, List<? extends TerminalDecl>> node,
        Solver<NonTerminalDecl, List<? extends TerminalDecl>> solver) {
      boolean ans = false;
      List<ProductionDecl> productions = grammar.getProductions().get(this.key);
      if (productions==null)
        throw new IllFormedGrammarException();
      production: for(ProductionDecl production:productions) {
        ArrayList<TerminalDecl> rights = new ArrayList<TerminalDecl>();
        for(VariableDecl v:production.getRight()) {
          if (!v.isTerminal()) {
            List<? extends TerminalDecl> current = solver.getCurrent((NonTerminalDecl) v);
            if (current==null)
              continue production;
            rights.addAll(current);
          }
          else
            rights.add((TerminalDecl)v);
        }
        if (result==null || result.size()>rights.size()) {
          result = rights;
          ans=true;
        }
      }
      return ans;
    }

  }

  private final class EpsilonNodeFactory implements
  NodeFactory<NonTerminalDecl,Boolean> {

    EpsilonNodeFactory() { /* nothing */ }

    public NodeContent<NonTerminalDecl,Boolean> getNode(NonTerminalDecl key) {
      return new EpsilonNode(key);
    }
  }

  private final class EpsilonNode implements NodeContent<NonTerminalDecl,Boolean> {

    private Boolean epsilon; // null means not yet computed

    /* maps a non terminal to the set of non terminals which it belongs
     * such that if all are epsilon, the node's answer is true. It is
     * inited with all right members of production whose right
     * part is only made of nonterminals, and left part is the non
     * terminal this node is computing epsilon for*/
    private final MultiMap<NonTerminalDecl,HashSet<NonTerminalDecl>> currentDepends
    = new MultiMap<NonTerminalDecl,HashSet<NonTerminalDecl>>();
    private final Set<NonTerminalDecl> depends;


    public EpsilonNode(NonTerminalDecl nonTerminal) {
      List<ProductionDecl> list = grammar.getProductions().get(nonTerminal);
      if (list==null)
        throw new IllFormedGrammarException();

      production : for (ProductionDecl production : list) {
        List<? extends VariableDecl> var = production.getRight();

        /* if right member is epsilon, the result is true */
        if (var.size() == 0) {
          epsilon = true;
          depends = Collections.emptySet();
          return;
        }
        /* if there is a terminal in this production,
         * it can't reduce to epsilon; if it is recursive, it can't help
         */
        for (VariableDecl v: var) {
          if (v.isTerminal() || v.equals(nonTerminal)) {
            continue production;
          }
        }

        /* if the production is only made of NonTerminal, a
         * dependency is added to the first one.
         */
        HashSet<NonTerminalDecl> rights = new HashSet<NonTerminalDecl>();

        for(VariableDecl v:var) {
          NonTerminalDecl n = (NonTerminalDecl) v;
          rights.add(n);
          currentDepends.add(n, rights);
        }
      }

      if (currentDepends.keySet().isEmpty()) {
        epsilon = Boolean.FALSE;
        depends = Collections.emptySet();
      }
      else
        depends = new HashSet<NonTerminalDecl>(currentDepends.keySet());
    }

    public Boolean getCurrentResult() {
      return epsilon;
    }

    public Boolean getResult() {
      if (epsilon==null) {
        //System.out.println("null");
        epsilon=false;
      }
      return epsilon;
    }

    public boolean hasChanged(NonTerminalDecl key, NodeContent<NonTerminalDecl,Boolean> node,
        Solver<NonTerminalDecl,Boolean> solver) {
      if (epsilon!=null)
        return false;

      Boolean objNewVal = node.getCurrentResult();
      //System.out.println(key+" "+objNewVal);
      if (objNewVal == null)
        return false;

      if (!currentDepends.containsKey(key))
        return false;

      boolean newVal = objNewVal;

      if (newVal) {
        for (HashSet<NonTerminalDecl> set :  currentDepends.remove(key)) {
          set.remove(key);
          if (set.isEmpty()) {
            epsilon=true;
            currentDepends.clear();
            //System.out.println("true "+depends);
            return true;
          }
        }
      } else {
        //System.err.println(key+" "+currentDepends.get(key));
        for (HashSet<NonTerminalDecl> set :  currentDepends.remove(key)) {
          for(NonTerminalDecl n : set) {
            if (n==key)
              continue;
            Set<HashSet<NonTerminalDecl>> set2 = currentDepends.get(n);
            set2.remove(set);
            if (set2.isEmpty())
              currentDepends.remove(n);
          }
        }
      }
      if (currentDepends.keySet().isEmpty()) {
        epsilon = Boolean.FALSE;
        //System.out.println("false "+depends);
        return true;
      }
      return false;
    }

    public Set<NonTerminalDecl> dependencies() {
      return depends;
    }
  }

  private final class FirstLastNodeFactory
  implements NodeFactory<NonTerminalDecl,HashSet<TerminalDecl>> {
    private final boolean first;

    FirstLastNodeFactory(boolean first) {
      this.first = first;
    }

    public NodeContent<NonTerminalDecl,HashSet<TerminalDecl>> getNode(NonTerminalDecl key) {
      return new FirstLastNode(first,key);
    }
  }

  private static class FirstLastDependencies {
    final MultiMap<TerminalDecl,ProductionDecl> terminals;
    final MultiMap<NonTerminalDecl,ProductionDecl> firstsLasts;
    FirstLastDependencies(MultiMap<NonTerminalDecl,ProductionDecl> firsts,
        MultiMap<TerminalDecl,ProductionDecl> terminals) {
      this.firstsLasts = firsts;
      this.terminals = terminals;
    }
  }

  private final HashMap<NonTerminalDecl,FirstLastDependencies> lastDependencies =
    new HashMap<NonTerminalDecl,FirstLastDependencies>();
  
  private final HashMap<NonTerminalDecl,FirstLastDependencies> firstDependencies =
    new HashMap<NonTerminalDecl,FirstLastDependencies>();

  FirstLastDependencies firstDependencies(NonTerminalDecl nonTerminal) {
    return firstLastDependencies(nonTerminal,firstDependencies,true);
  }

  FirstLastDependencies lastDependencies(NonTerminalDecl nonTerminal) {
    return firstLastDependencies(nonTerminal,lastDependencies,false);
  }
  
  FirstLastDependencies firstLastDependencies(NonTerminalDecl nonTerminal,
      HashMap<NonTerminalDecl,FirstLastDependencies> map,
      boolean forward) {
    FirstLastDependencies dependencies = map.get(nonTerminal);
    if (dependencies!=null)
      return dependencies;
    MultiMap<NonTerminalDecl,ProductionDecl> depends = new MultiMap<NonTerminalDecl,ProductionDecl>();
    MultiMap<TerminalDecl,ProductionDecl> first = new MultiMap<TerminalDecl,ProductionDecl>();
    List<ProductionDecl> list = grammar.getProductions().get(nonTerminal);
    if (list==null)
      throw new IllFormedGrammarException();
    for (ProductionDecl production : list) {
      List<? extends VariableDecl> var = production.getRight();
      ListIterator<? extends VariableDecl> iterator = var.listIterator(forward?0:var.size());
      while (forward?iterator.hasNext():iterator.hasPrevious()) {
        VariableDecl v = forward?iterator.next():iterator.previous();
        if (v.isTerminal()) {
          first.add((TerminalDecl)v,production);
          break;
        }
        if (!v.equals(nonTerminal))
          depends.add((NonTerminalDecl)v,production);
        //System.out.println(v);
        if (!derivesToEpsilon((NonTerminalDecl) v))
          break;
      }
    }
    dependencies=new FirstLastDependencies(depends,first);
    map.put(nonTerminal,dependencies);
    return dependencies;
  }

  public MultiMap<NonTerminalDecl,ProductionDecl> firstFirstDependencies(NonTerminalDecl nonTerminal) {
    return firstDependencies(nonTerminal).firstsLasts;
  }

  public MultiMap<TerminalDecl,ProductionDecl> firstTerminalDependencies(NonTerminalDecl nonTerminal) {
    return firstDependencies(nonTerminal).terminals;
  }

  public MultiMap<NonTerminalDecl,ProductionDecl> lastLastDependencies(NonTerminalDecl nonTerminal) {
    return lastDependencies(nonTerminal).firstsLasts;
  }

  public MultiMap<TerminalDecl,ProductionDecl> lastTerminalDependencies(NonTerminalDecl nonTerminal) {
    return lastDependencies(nonTerminal).terminals;
  }

  private final class FirstLastNode implements NodeContent<NonTerminalDecl,HashSet<TerminalDecl>> {

    private final Set<NonTerminalDecl> depends;
    private final HashSet<TerminalDecl> first;

    public FirstLastNode(boolean first,
        NonTerminalDecl nonTerminal) {
      FirstLastDependencies dependencies = first?firstDependencies(nonTerminal):lastDependencies(nonTerminal);
      depends = dependencies.firstsLasts.keySet();
      this.first = new HashSet<TerminalDecl>(dependencies.terminals.keySet());
    }

    public boolean hasChanged(NonTerminalDecl key,
        NodeContent<NonTerminalDecl,HashSet<TerminalDecl>> node,
        Solver<NonTerminalDecl,HashSet<TerminalDecl>> solver) {
      /*System.out.println(
        "updating "
          + nonTerminal
          + " from "
          + key
          + " : "
          + ((FirstNode)node).getFirst());*/
      return first.addAll(node.getCurrentResult());
    }

    public Set<NonTerminalDecl> dependencies() {
      return depends;
    }

    public HashSet<TerminalDecl> getCurrentResult() {
      return first;
    }

    public HashSet<TerminalDecl> getResult() {
      return first;
    }
  }

  private final class FollowNodeFactory
  implements NodeFactory<NonTerminalDecl,HashSet<TerminalDecl>> {

    FollowNodeFactory() { /* nothing */ }

    public NodeContent<NonTerminalDecl,HashSet<TerminalDecl>> getNode(NonTerminalDecl key) {
      return new FollowNode(key);
    }
  }

  private static class FollowDependencies {
    final MultiMap<TerminalDecl,ProductionDecl> terminals;
    final MultiMap<NonTerminalDecl,ProductionDecl> firsts;
    final MultiMap<NonTerminalDecl,ProductionDecl> follows;
    FollowDependencies(MultiMap<NonTerminalDecl,ProductionDecl> follows,
        MultiMap<NonTerminalDecl,ProductionDecl> firsts, MultiMap<TerminalDecl,ProductionDecl> terminals) {
      this.follows = follows;
      this.firsts = firsts;
      this.terminals = terminals;
    }
  }

  private final HashMap<NonTerminalDecl,FollowDependencies> followDependencies = 
    new HashMap<NonTerminalDecl, FollowDependencies>();

  FollowDependencies followDependencies(NonTerminalDecl nonTerminal) {
    FollowDependencies dependencies = followDependencies.get(nonTerminal);
    if (dependencies != null)
      return dependencies;
    MultiMap<NonTerminalDecl,ProductionDecl> firsts = new MultiMap<NonTerminalDecl,ProductionDecl>();
    MultiMap<NonTerminalDecl,ProductionDecl> follows = new MultiMap<NonTerminalDecl,ProductionDecl>();
    MultiMap<TerminalDecl,ProductionDecl> terminals = new MultiMap<TerminalDecl,ProductionDecl>();
    List<MarkedProduction> list = grammar.getRightProductionMap().get(nonTerminal);
    if (list==null)
      throw new IllFormedGrammarException();
    //System.out.println(nonTerminal+" appears in "+list);
    production : for (MarkedProduction markedProduction : list) {
      ProductionDecl production = markedProduction.getProduction();
      List<? extends VariableDecl> right = production.getRight();
      int max = right.size();
      for (int i=markedProduction.getPosition()+1; i<max; i++) {
        VariableDecl var = right.get(i);
        if (var.isTerminal()) {
          terminals.add((TerminalDecl)var,production);
          continue production;
        }
        NonTerminalDecl tmpNT = (NonTerminalDecl) var;
        firsts.add(tmpNT,production);
        if (!derivesToEpsilon(tmpNT))
          continue production;
      }
      /* if control arrive to end of right part of production, need
       * to add follow of left part.
       */
      if (!production.getLeft().equals(nonTerminal)
          && !grammar.getStarts().contains(production.getLeft())) {
        follows.add(production.getLeft(),production);
      }
    }
    dependencies = new FollowDependencies(follows,firsts,terminals);
    followDependencies.put(nonTerminal, dependencies);
    return dependencies;  
  }

  public MultiMap<NonTerminalDecl,ProductionDecl> followFirstDependencies(NonTerminalDecl nonTerminal) {
    return followDependencies(nonTerminal).firsts;
  }

  public MultiMap<NonTerminalDecl,ProductionDecl> followFollowDependencies(NonTerminalDecl nonTerminal) {
    return followDependencies(nonTerminal).follows;
  }

  public MultiMap<TerminalDecl,ProductionDecl> followTerminalDependencies(NonTerminalDecl nonTerminal) {
    return followDependencies(nonTerminal).terminals;
  }

  private final class FollowNode
  implements NodeContent<NonTerminalDecl,HashSet<TerminalDecl>> {

    private final Set<NonTerminalDecl> depends;
    private final HashSet<TerminalDecl> follow;

    public FollowNode(NonTerminalDecl nonTerminal) {
      FollowDependencies dependencies = followDependencies(nonTerminal);
      depends = dependencies.follows.keySet();
      follow = new HashSet<TerminalDecl>(dependencies.terminals.keySet());
      for(NonTerminalDecl followNonTerminal: dependencies.firsts.keySet())
        follow.addAll(first(followNonTerminal));
    }

    public boolean hasChanged(NonTerminalDecl key, 
        NodeContent<NonTerminalDecl,HashSet<TerminalDecl>> node,
        Solver<NonTerminalDecl,HashSet<TerminalDecl>> solver) {
      return follow.addAll(node.getCurrentResult());
    }

    public Set<NonTerminalDecl> dependencies() {
      return depends;
    }

    public HashSet<TerminalDecl> getCurrentResult() {
      return follow;
    }

    public HashSet<TerminalDecl> getResult() {
      return follow;
    }
  }

  public boolean derivesToEpsilon(NonTerminalDecl t) {
    //System.out.println(t);
    return epsilon.solve(t);
  }

  public Set<TerminalDecl> first(NonTerminalDecl t) {
    return first.solve(t);
  }

  public Set<TerminalDecl> last(NonTerminalDecl t) {
    return last.solve(t);
  }

  
  /* vars must not be empty */
  public Set<TerminalDecl> first(VariableDecl[] vars) {
    HashSet<TerminalDecl> rep = new HashSet<TerminalDecl>();
    for (int i = 0; i < vars.length; i++) {
      if (vars[i].isTerminal()) {
        rep.add((TerminalDecl)vars[i]);
        break;
      }
      NonTerminalDecl n = (NonTerminalDecl) vars[i];
      rep.addAll(first(n));

      if (!derivesToEpsilon(n))
        break;
    }
    return rep;
  }

  public Set<TerminalDecl> follow(NonTerminalDecl t) {
    if (grammar.getStarts().contains(t))
      return null;
    return follow.solve(t);
  }

  public List<? extends TerminalDecl> shortestWord(NonTerminalDecl t) {
    //System.err.println("shortest word "+t);
    return shortestWord.solve(t);
  }

  /**
   * 
   * @param t
   * @return first is start
   */
  public List<? extends VariableDecl> shortestLeadingWord(NonTerminalDecl t) {
    return shortestLeadingWord.solve(t);
  }
  
  public Pair<NonTerminalDecl,List<? extends TerminalDecl>> wordUsingProduction(ProductionDecl prod) {
    LinkedList<TerminalDecl> list = new LinkedList<TerminalDecl>();
    Iterator<? extends VariableDecl> to = shortestLeadingWord(prod.getLeft()).iterator();
    NonTerminalDecl start = (NonTerminalDecl) to.next();
    for(;;) {
      VariableDecl v = to.next();
      if (v.isTerminal())
        list.add((TerminalDecl)v);
      else
        break;
    }
    for(VariableDecl v:prod.getRight()) {
      if (v.isTerminal())
        list.add((TerminalDecl)v);
      else
        list.addAll(shortestWord((NonTerminalDecl)v));
    }
    while(to.hasNext())
      list.add((TerminalDecl)to.next());
    list.removeLast();
    return new Pair<NonTerminalDecl, List<? extends TerminalDecl>>(start, list);
  }
  
  public static class IllFormedGrammarException extends IllegalArgumentException {

    private static final long serialVersionUID = -8894932502315323452L;

    IllFormedGrammarException() { //empty
    }

    IllFormedGrammarException(String message, Throwable cause) {
      super(message, cause);
    }

    IllFormedGrammarException(String s) {
      super(s);
    }

    IllFormedGrammarException(Throwable cause) {
      super(cause);
    }
    
  }
  
  

}

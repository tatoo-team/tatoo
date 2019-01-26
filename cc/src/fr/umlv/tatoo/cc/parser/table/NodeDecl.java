/*
 * Created on May 30, 2003
 */
package fr.umlv.tatoo.cc.parser.table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.umlv.tatoo.cc.common.util.MultiMap;
import fr.umlv.tatoo.cc.parser.grammar.GrammarSets;
import fr.umlv.tatoo.cc.parser.grammar.NonTerminalDecl;
import fr.umlv.tatoo.cc.parser.grammar.TerminalDecl;
import fr.umlv.tatoo.cc.parser.grammar.VariableDecl;
import fr.umlv.tatoo.cc.parser.grammar.VersionDecl;

/**
 * @author jcervell
 * @param <I> type of the item.
 *
 */
public class NodeDecl<I extends NodeItem<I>> {

  private final Set<I> kernelItems;
  private final HashMap<NonTerminalDecl,NodeDecl<I>> gotos;
  private final HashMap<TerminalDecl,NodeDecl<I>> shifts;
  private final HashSet<I> reduces;
  private final int stateNo;
  private final VariableDecl associated;
  private final Set<VersionDecl> compatibleVersion;
  private final GrammarSets grammarSets;
  private ArrayList<TerminalDecl> path;
  private int startPathState=-1;
  
  void setStartPathState(int startPathState) {
    this.startPathState = startPathState;
  }
  
  public int getStartPathState() {
    return startPathState;
  }
  
  ArrayList<TerminalDecl> getPath() {
    return path;
  }
  
  public List<TerminalDecl> getShortestPath() {
    return path;
  }
  
  void setPath(ArrayList<TerminalDecl> path) {
    this.path = path;
  }
  
  void addLocalPath(ArrayList<TerminalDecl> path) {
    if (associated.isTerminal()) {
      path.add((TerminalDecl)associated);
    } else {
      path.addAll(grammarSets.shortestWord((NonTerminalDecl)associated));
    }
  }
  
  int localCost() {
    if (associated.isTerminal()) {
      return 1;
    } else {
      return grammarSets.shortestWord((NonTerminalDecl)associated).size();
    }
  }
  
  public HashMap<NonTerminalDecl, NodeDecl<I>> getGotos() {
    return gotos;
  }

  public HashSet<I> getReduces() {
    return reduces;
  }

  public HashMap<TerminalDecl, NodeDecl<I>> getShifts() {
    return shifts;
  }

  public int getStateNo() {
    return stateNo;
  }

  private Set<I> shiftOrGoto(Set<I> s) {
    HashSet<I> rep = new HashSet<I>();
    for (I it : s) {
      rep.add(it.advance());
    }
    return rep;
  }
  
  /**
   * Constructor for start states
   */
  public NodeDecl(GrammarSets grammarSets,I firstKernelItem,
      Set<VersionDecl> compatibleVersion,
      int stateNo,
      NodeFactory<I> factory,
      NodeClosureComputer<I> closureComputer) {
    this(grammarSets,Collections.singleton(firstKernelItem),
        firstKernelItem,compatibleVersion,
        stateNo,factory,closureComputer,null);
    startPathState=stateNo;
    path=new ArrayList<TerminalDecl>();
  }
  
  /**
   * Constructor for other states
   */
  public NodeDecl(GrammarSets grammarSets,
      Set<I> kernelItems,
      Set<VersionDecl> compatibleVersion,
      int stateNo,
      NodeFactory<I> factory,
      NodeClosureComputer<I> closureComputer,
      VariableDecl associated) {
    this(grammarSets,kernelItems,null,compatibleVersion,
        stateNo,factory,closureComputer,associated);
  }
  
  private NodeDecl(
    GrammarSets grammarSets,
    Set<I> kI,
    I firstKernelItem,
    Set<VersionDecl> compatibleVersion,
    int stateNo,
    NodeFactory<I> factory,
    NodeClosureComputer<I> closureComputer,
    VariableDecl associated) {
    this.grammarSets = grammarSets;
    MultiMap<TerminalDecl,I> tShifts = new MultiMap<TerminalDecl,I>();
    MultiMap<NonTerminalDecl,I> tGotos = new MultiMap<NonTerminalDecl,I>();
    HashSet<I> closures = new HashSet<I>();
    gotos = new HashMap<NonTerminalDecl,NodeDecl<I>>();
    shifts = new HashMap<TerminalDecl,NodeDecl<I>>();
    kernelItems = kI;
    this.firstKernelItem = firstKernelItem;
    this.compatibleVersion = compatibleVersion;
    factory.register(kI, this);
    
    this.stateNo = stateNo;
    this.associated = associated;
    reduces = new HashSet<I>();

    for (I it : kI) {
      if (it.getRight().size() == it.getDotPlace()) {
        reduces.add(it);
        continue;
      }

      VariableDecl v = it.getDottedVariable();
      if (v.isTerminal())
        tShifts.add((TerminalDecl)v, it);
      else {
        tGotos.add((NonTerminalDecl)v, it);

        if (closures.add(it)) {
          Closure<I> c = closureComputer.getClosure(it);

          tShifts.addAll(c.getShifts());
          tGotos.addAll(c.getGotos());
          reduces.addAll(c.getReduces());
        }
      }
    }

    makeNewStates(tShifts,shifts,factory);
    makeNewStates(tGotos,gotos,factory);
  }
  
  private <T extends VariableDecl> void makeNewStates(MultiMap<T,I> toAdd,
      Map<T,NodeDecl<I>> target,NodeFactory<I> factory) {
    for (Map.Entry<T,Set<I>> entry :  toAdd.entrySet()) {
      T e = entry.getKey();
      Set<I> s = entry.getValue();
      s = shiftOrGoto(s);
      NodeDecl<I> kern= factory.buildNode(grammarSets,s,e);
      target.put(e, kern);
    }
  }

  private int toStateNo(VariableDecl v) {
    NodeDecl<I> no;
    if (v.isTerminal())
      no = shifts.get(v);
    else
      no = gotos.get(v);
    return no.getStateNo();
  }
  
  @Override
  public String toString() {
   return "state"+stateNo;
  }
  
  public String pathInfo() {
    StringBuilder builder=new StringBuilder("state ");
    builder.append(startPathState).append(":");
    for(TerminalDecl terminal:path) {
      builder.append(" ").append(terminal.getId());
    }
    return builder.toString();
  }

  public String description() {
    StringBuilder rep = new StringBuilder();
    rep.append("State #").append(stateNo).append("\nKernel items : \n");
    for (I i : kernelItems) {
      rep.append(i).append("\n");
    }
    rep.append("gotos :\n");
    for (NonTerminalDecl e : gotos.keySet()) {
      rep.append(e).append(" to ").append(toStateNo(e)).append("\n");
    }
    rep.append("shifts :\n");
    for (TerminalDecl t :  shifts.keySet()) {
      rep.append(t).append(" to ").append(toStateNo(t)).append("\n");
    }
    if (!reduces.isEmpty())
      rep.append("reduces :\n").append(reduces);
    return rep.toString();
  }

  public Set<I> getKernelItems() {
    return kernelItems;
  }
  
  private final I firstKernelItem;
  
  /**
   * Returns the kernel item of this start state, or null
   * if this is a secondary state
   * @return the kernel item of this start state, or null if this is a secondary state
   */
  public I getFirstKernelItem() {
    return firstKernelItem;
  }
  
  public VariableDecl getAssociated() {
    return associated;
  }
  
  public Set<VersionDecl> getCompatibleVersion() {
    return compatibleVersion;
  }
}

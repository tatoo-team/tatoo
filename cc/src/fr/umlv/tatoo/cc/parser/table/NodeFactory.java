/*
 * Created on 20 feb. 2006
 */
package fr.umlv.tatoo.cc.parser.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import fr.umlv.tatoo.cc.parser.grammar.Grammar;
import fr.umlv.tatoo.cc.parser.grammar.GrammarSets;
import fr.umlv.tatoo.cc.parser.grammar.NonTerminalDecl;
import fr.umlv.tatoo.cc.parser.grammar.ProductionDecl;
import fr.umlv.tatoo.cc.parser.grammar.TerminalDecl;
import fr.umlv.tatoo.cc.parser.grammar.VariableDecl;
import fr.umlv.tatoo.cc.parser.grammar.VersionDecl;
import fr.umlv.tatoo.cc.parser.grammar.VersionManager;

public class NodeFactory<I extends NodeItem<I>> {
  private final GrammarSets grammarSets;
  private final NodeClosureComputer<I> closureComputer;
  private final VersionManager versionManager;
  
  private final LinkedHashMap<Set<I>,NodeDecl<I>> nodes =
    new LinkedHashMap<Set<I>,NodeDecl<I>>();
  private final HashSet<NodeDecl<I>> startNodes =
    new HashSet<NodeDecl<I>>();
  private final LinkedHashMap<NonTerminalDecl,NodeDecl<I>> startStateMap =
    new LinkedHashMap<NonTerminalDecl, NodeDecl<I>>();
  
  
  public void register(Set<I> kernelItems, NodeDecl<I> node) {
    nodes.put(kernelItems, node);
  }
  
  public Collection<NodeDecl<I>> getNodes() {
    return nodes.values();
  }
  
  public void computeShortestPath() {
    PriorityQueue<NodeDecl<I>> priority = new PriorityQueue<NodeDecl<I>>(nodes.size(),
        new Comparator<NodeDecl<I>>() {
          @Override
          public int compare(NodeDecl<I> o1, NodeDecl<I> o2) {
            return o1.getPath().size()-o2.getPath().size();
          }
        });
    priority.addAll(startNodes);
    while (!priority.isEmpty()) {
      NodeDecl<I> node = priority.remove();
      for(Map.Entry<TerminalDecl,NodeDecl<I>> entry:node.getShifts().entrySet()) {
        if (updateMin(entry.getValue(),node))
          priority.add(entry.getValue());
      }
      for(Map.Entry<NonTerminalDecl,NodeDecl<I>> entry:node.getGotos().entrySet()) {
        if (updateMin(entry.getValue(),node))
          priority.add(entry.getValue());
      }
    }
  }
  
  private static <I extends NodeItem<I>> boolean updateMin(NodeDecl<I> toUpdate, NodeDecl<I> via) {
    int newValue = via.getPath().size()+toUpdate.localCost();
    if (toUpdate.getPath()==null || newValue<toUpdate.getPath().size()) {
      ArrayList<TerminalDecl> list = new ArrayList<TerminalDecl>(via.getPath());
      toUpdate.addLocalPath(list);
      toUpdate.setPath(list);
      toUpdate.setStartPathState(via.getStartPathState());
      return true;
    }
    return false;
  }
  
  public NodeDecl<I> buildNode(GrammarSets grammarSets,
      Set<I> kI,
      VariableDecl associated) {
    NodeDecl<I> node = nodes.get(kI);
    if (node == null) {
      node = new NodeDecl<I>(grammarSets, kI, compatibleVersion(kI), nodes.size(),
          this, closureComputer,associated);
    }
    return node;
  }
  
  public NodeFactory(Grammar grammar,GrammarSets grammarSets, TerminalDecl eof,
      VersionManager versionManager,
     TableFactoryMethod<I> method) {
    this.grammarSets=grammarSets;
    this.versionManager = versionManager;
    this.closureComputer = method.getClosureComputer(grammar,grammarSets,eof);
    for(NonTerminalDecl start : grammar.getStarts()) {
      ProductionDecl production = grammar.getProductions().get(start).get(0);
      I item = method.createStartItem(production,eof);
      NodeDecl<I> newNode = new NodeDecl<I>(grammarSets,item,
          compatibleVersion(grammar,(NonTerminalDecl)production.getRight().get(0)),
          nodes.size(),this,closureComputer);
      startStateMap.put((NonTerminalDecl)production.getRight().get(0),newNode);
      startNodes.add(newNode);
    }  
  }
  
  private HashSet<VersionDecl> compatibleVersion(Set<I> kI) {
    HashSet<VersionDecl> compatibleVersion = new HashSet<VersionDecl>();
    for(I i:kI)
      compatibleVersion.addAll(versionManager.getCompatibleVersion(i.getProduction().getVersion()));
    return compatibleVersion;
  }
  
  private HashSet<VersionDecl> compatibleVersion(Grammar grammar,NonTerminalDecl nt) {
    HashSet<VersionDecl> compatibleVersion = new HashSet<VersionDecl>();
    for(ProductionDecl production : grammar.getProductions().get(nt)) {
      compatibleVersion.addAll(versionManager.getCompatibleVersion(production.getVersion()));
    }
    return compatibleVersion;
  }
  
  public Map<NonTerminalDecl, NodeDecl<I>> getStartStateMap() {
    return startStateMap;
  }
  
  public HashSet<NodeDecl<I>> getStartNodes() {
    return startNodes;
  }
  
  public GrammarSets getGrammarSets() {
    return grammarSets;
  }
}

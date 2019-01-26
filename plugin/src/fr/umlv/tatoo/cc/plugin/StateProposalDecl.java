package fr.umlv.tatoo.cc.plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import fr.umlv.tatoo.cc.parser.grammar.TerminalDecl;

public class StateProposalDecl {
  private final TreeSet<String> wordProposalSet=
    new TreeSet<String>();
  private final HashMap<TerminalDecl,List<ContextDecl>> variantContextMap=
    new HashMap<TerminalDecl,List<ContextDecl>>();
  
  public Set<String> getWordProposalSet() {
    return wordProposalSet;
  }
  public Map<TerminalDecl,List<ContextDecl>> getVariantContextMap() {
    return variantContextMap;
  }
  
  @Override
  public String toString() {
    return wordProposalSet+" "+variantContextMap;
  }
  
  public void addWordProposal(Collection<? extends String> wordProposals) {
    wordProposalSet.addAll(wordProposals);
  }
  
  public List<ContextDecl> getVariantContextList(TerminalDecl terminal) {
    List<ContextDecl> contextList=variantContextMap.get(terminal);
    if (contextList==null) {
      contextList=new ArrayList<ContextDecl>();
      variantContextMap.put(terminal,contextList);
    }
    return contextList;
  }
}

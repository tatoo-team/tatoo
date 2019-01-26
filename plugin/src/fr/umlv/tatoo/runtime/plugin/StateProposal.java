package fr.umlv.tatoo.runtime.plugin;

import java.util.List;
import java.util.Map;

public class StateProposal<T,P> {
  private final List<String> wordProposalList;
  private final Map<T,List<Context<P>>> variantContextMap;
  
  public StateProposal(List<String> wordProposalList,
      Map<T,List<Context<P>>> variantContextMap) {
    this.wordProposalList=wordProposalList;
    this.variantContextMap=variantContextMap;
  }
  
  public List<String> getWordProposalList() {
    return wordProposalList;
  }
  public Map<T,List<Context<P>>> getVariantContextMap() {
    return variantContextMap;
  }
  
  @Override
  public String toString() {
    return getWordProposalList()+" "+getVariantContextMap();
  }
}

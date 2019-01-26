package fr.umlv.tatoo.cc.plugin;

import java.util.List;

public class RuleProposal {
  private final boolean variant;
  private final List<String> wordProposalList;
  
  public RuleProposal(boolean variant,List<String> wordProposalList) {
    this.variant=variant;
    this.wordProposalList=wordProposalList;
  }
  
  public boolean isVariant() {
    return variant;
  }
  public List<String> getWordProposalList() {
    return wordProposalList;
  }
  
  @Override
  public String toString() {
    return "isVariant "+variant+" word proposals "+wordProposalList;
  }
}

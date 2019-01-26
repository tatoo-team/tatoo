package fr.umlv.tatoo.runtime.tools.nano;

import java.util.ArrayList;
import java.util.BitSet;

import fr.umlv.tatoo.runtime.buffer.LexerBuffer;
import fr.umlv.tatoo.runtime.lexer.nano.MultipleRuleProcessor;
import fr.umlv.tatoo.runtime.lexer.nano.Policy;
import fr.umlv.tatoo.runtime.lexer.nano.RuleFeeder;
import fr.umlv.tatoo.runtime.lexer.nano.RuleMatcher;
import fr.umlv.tatoo.runtime.lexer.nano.RuleProcessor;

public class God<B extends LexerBuffer> extends RuleFeeder<B> {
  
  private static class ObjectBranchableRunPair<B extends LexerBuffer> {
    final Branchable<?,B>.BranchableRun run;
    final Object bnt;
    ObjectBranchableRunPair(Branchable<?,B>.BranchableRun run, Object bnt) {
      this.run = run;
      this.bnt = bnt;
    }
  }
  
  private static class BranchLink {
    final Object bnt;
    final BranchLink next;
    BranchLink(Object bnt, BranchLink next) {
      this.bnt = bnt;
      this.next = next;
    }
  }
  
  private final GrammarRegistry<?,B> registry;
  private Branchable<?,B>.BranchableRun branchableRun;
  private final MultipleRuleProcessor<B> ruleProcessor;
  private final BitSet set = new BitSet();
  private final ArrayList<ObjectBranchableRunPair<B>> stack = new ArrayList<ObjectBranchableRunPair<B>>();
  private Policy policy;
  
  public God(GrammarRegistry<?,B> registry, Branchable<?,B> branchable, Policy policy) {
    this.branchableRun = branchable.newRun();
    this.registry = registry;
    ruleProcessor = new MultipleRuleProcessor<B>();
    this.policy = policy;
  }
  
  @Override
  public void feedWithActiveRuleDatas(boolean previousWasNewLine) {
    boolean isBranched = stack.size() != 0;
    branchableRun.branchable().reset();
    branchableRun.fill(isBranched,previousWasNewLine,null,0);
    
    
    if (isBranched && branchableRun.exits()) {
      int stackSize = stack.size();
      for(int i = stack.size()-1;i>=0;i--) {
        ObjectBranchableRunPair<B> top = stack.get(i);
        top.run.fillUnbranch(top.bnt,previousWasNewLine,null,i-stackSize);
        ruleProcessor.registerRuleMatcher(top.run.branchable().matcher());
        if (!top.run.exits())
          break;
      }
    }
    
    Object[] branches = branchableRun.branches();
    if (branches==null) {
      ruleProcessor.registerRuleMatcher(branchableRun.branchable().matcher());
      return;
    }
    
    set.clear();
    if (branchableRun.initial())
      set.set(branchableRun.branchable().identifier());

    register(branches,previousWasNewLine,null,1);

    ruleProcessor.registerRuleMatcher(branchableRun.branchable().matcher());
    
  }
  private void register(Object[] bnts, boolean previousWasNewLine, BranchLink link, int depth) {
    for(int i=0;i<bnts.length;i++) {
      Object bnt = bnts[i];
      Branchable<?,B> branchable = registry.associatedGrammar(bnt);
      if (set.get(branchable.identifier())) {
        // FIXME add a warning about infinite branch tree
        continue;
      }
      set.set(branchable.identifier());

      
      Object[] childBnt = branchable.initialBranches();
      if (depth==1) {
        if (childBnt != null) {
          register(childBnt,previousWasNewLine, new BranchLink(bnt,null),depth+1);
        }
        branchable.initialRules(true, previousWasNewLine, bnt, depth);
      }
      else {
        BranchLink newLink = new BranchLink(bnt, link);
        if (childBnt != null) {
          register(childBnt,previousWasNewLine, newLink,depth+1);
        }
        branchable.initialRules(true, previousWasNewLine, newLink, depth);
      }
      set.clear(branchable.identifier());
      ruleProcessor.registerRuleMatcher(branchable.matcher());
    }
  }
  
  @Override
  public RuleProcessor getRuleProcessor() {
    return ruleProcessor;
  }
  @Override
  public void ruleVerified(B buffer) {
    RuleMatcher action = ruleProcessor.winningRuleAction(policy);
    buffer.unwind(action.lastMatch());
    
    int d = action.depth();
    switch (d) {
    case 0: break; //call regular listener
    case 1: 
      simpleBranch(action); break;
    default:
      if (d<0)
        unbranch(d);
      else
        multipleBranch(action);
    } 
  }
  
  private void unbranch(int d) {
    int stackSize = stack.size();
    for(int i=0;i<d;i++) {
      ObjectBranchableRunPair<B> pair = stack.remove(stackSize-i+1);
      // unbranch bnt in pair.bnt, run in pair.run.
    }
  }

  private void multipleBranch(RuleMatcher action) {
    BranchLink branchLink = (BranchLink) action.attachment();
    multipleBranch(branchLink);
    branchableRun = registry.associatedGrammar(branchLink.bnt).newRun();
  }
  
  private void multipleBranch(BranchLink branchLink) {
    if (branchLink.next == null)
      stack.add(new ObjectBranchableRunPair<B>(branchableRun, branchLink.bnt));
    else {
      multipleBranch(branchLink.next);
      Branchable<?,B>.BranchableRun run = registry.associatedGrammar(branchLink.next.bnt).newRun();
      stack.add(new ObjectBranchableRunPair<B>(run, branchLink.bnt));
    }
  }

  private void simpleBranch(RuleMatcher action) {
    Object bnt = action.attachment();
    Branchable<?, B> branchable = registry.associatedGrammar(bnt);
    stack.add(new ObjectBranchableRunPair<B>(branchableRun, bnt));
    branchableRun = branchable.newRun();
  }


}

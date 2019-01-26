package fr.umlv.tatoo.cc.plugin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import fr.umlv.tatoo.cc.lexer.charset.CharacterInterval;
import fr.umlv.tatoo.cc.lexer.charset.CharacterSet;
import fr.umlv.tatoo.cc.lexer.charset.encoding.Encoding;
import fr.umlv.tatoo.cc.lexer.lexer.RuleDecl;
import fr.umlv.tatoo.cc.lexer.regex.Cat;
import fr.umlv.tatoo.cc.lexer.regex.EpsilonLeaf;
import fr.umlv.tatoo.cc.lexer.regex.Leaf;
import fr.umlv.tatoo.cc.lexer.regex.Or;
import fr.umlv.tatoo.cc.lexer.regex.Regex;
import fr.umlv.tatoo.cc.lexer.regex.Star;
import fr.umlv.tatoo.cc.parser.grammar.TerminalDecl;
import fr.umlv.tatoo.cc.parser.grammar.VersionDecl;
import fr.umlv.tatoo.cc.parser.parser.ActionDecl;
import fr.umlv.tatoo.cc.parser.parser.BranchActionDecl;
import fr.umlv.tatoo.cc.parser.parser.ReduceActionDecl;
import fr.umlv.tatoo.cc.parser.parser.RegularTableActionDecl;
import fr.umlv.tatoo.cc.parser.parser.ShiftActionDecl;
import fr.umlv.tatoo.cc.parser.parser.VersionedActionDecl;
import fr.umlv.tatoo.cc.parser.table.NodeDecl;
import fr.umlv.tatoo.cc.parser.table.NodeItem;
import fr.umlv.tatoo.cc.parser.table.ParserTableDecl;
import fr.umlv.tatoo.cc.tools.tools.ToolsFactory;

public class StateProposalDeclFactory {
  @SuppressWarnings("serial")
  static class Abort extends RuntimeException {
    static final Abort abort=new Abort();
  }
  
  
  private static ActionDecl escapeVersion(ActionDecl actionDecl, VersionDecl version) {
    if (actionDecl instanceof VersionedActionDecl) {
      return ((VersionedActionDecl)actionDecl).getActionMap().get(version);
    }
    return actionDecl;
  }
  
  
  public static StateProposalDecl[] getProposals(ParserTableDecl parserTable,VersionDecl version,
    ToolsFactory toolsFactory,Encoding encoding) {
    
    StateProposalDecl[] proposals=new StateProposalDecl[parserTable.getStateCount()];
    for(int i=0;i<parserTable.getStateCount();i++) {
      proposals[i]=new StateProposalDecl();
    }
    
    //StateMetadataDecl[] metadata=parserTable.getMetadata();
    
    for(Entry<TerminalDecl,RegularTableActionDecl[]> entry:parserTable.getActionMap().entrySet()) {
      TerminalDecl terminal=entry.getKey();
      RegularTableActionDecl[] actionDecls=entry.getValue();
      
      boolean variant=false;
      HashSet<String> wordProposal=new HashSet<String>();
      Set<? extends RuleDecl> ruleSet=toolsFactory.getTerminalRulesMap().get(terminal);
      if (ruleSet!=null) {
        for(RuleDecl rule:ruleSet) {
          RuleProposal ruleProposal=findProposals(rule.getMainRegex(),encoding);
          wordProposal.addAll(ruleProposal.getWordProposalList());
          variant|=ruleProposal.isVariant();
        }
      }
      
      for(int i=0;i<parserTable.getStateCount();i++) {
        ActionDecl actionDecl=escapeVersion(actionDecls[i],version);
        if (actionDecl instanceof BranchActionDecl)
          continue;
        
        proposals[i].getWordProposalSet().addAll(wordProposal);
        
        if (!variant)
          continue;
        
        List<ContextDecl> contextList=proposals[i].getVariantContextList(terminal);
        
        if (actionDecl instanceof ShiftActionDecl) {
          ShiftActionDecl shiftAction=(ShiftActionDecl)actionDecl;
          NodeDecl<?> state=shiftAction.getState();
          for(NodeItem<?> item:state.getKernelItems()) {
            contextList.add(
              new ContextDecl.ShiftDecl(item.getProduction(),item.getDotPlace()));
          }
          
          /*
          int shiftState=state.getStateNo();
          ActionDecl defaultReduce=metadata[shiftState].getDefaultReduce();
          if (defaultReduce!=null) {
            defaultReduce=escapeVersion(defaultReduce,version);
            action.getReduceProductionList().add(((ReduceActionDecl)defaultReduce).getProduction());
          }*/
        }
        
        if (actionDecl instanceof ReduceActionDecl) {
          ReduceActionDecl reduceAction=(ReduceActionDecl)actionDecl;
          contextList.add(new ContextDecl.ReduceDecl(reduceAction.getProduction()));
        }
      }
    }
    
    return proposals;
  }
  
  public static RuleProposal findProposals(Regex regex,final Encoding encoding) {
    final ArrayList<String> proposalList=
      new ArrayList<String>();
    final boolean[] variant=new boolean[1];
    
    StringBuilder proposal=new StringBuilder();
    try {
      regex.accept(new Regex.Visitor<StringBuilder>() {
        @Override
        public void visit(EpsilonLeaf epsilonLeaf,StringBuilder param) {
          // do nothing
        }
        @Override
        public void visit(Leaf leaf,StringBuilder builder) {
          CharacterSet letters=leaf.getLetters();
          if (letters==null) //eof
            return;
          List<CharacterInterval> intervalList=letters.getList();
          if (intervalList.size()!=1) {
            throw Abort.abort;
          }
          CharacterInterval charInterval=intervalList.get(0);
          if (charInterval.getBegin()!=charInterval.getEnd()) {
            throw Abort.abort;
          }
          builder.append(
            encoding.decode(charInterval.getBegin()));
        }
        @Override
        public void visit(Cat cat,StringBuilder param) {
          cat.getLeft().accept(this,param);
          cat.getRight().accept(this,param);
        }
        @Override
        public void visit(Or or,StringBuilder param) {
          StringBuilder proposal=new StringBuilder(param);
          or.getLeft().accept(this,param);
          try {
            or.getRight().accept(this,proposal);
            proposalList.add(proposal.toString());
          } catch(Abort abort) {
            variant[0]=true;
          }
        }
        @Override
        public void visit(Star star,StringBuilder param) {
          throw Abort.abort;
        }
      },proposal);
      proposalList.add(proposal.toString());
    } catch(Abort abort) {
      variant[0]=true;
    }
    return new RuleProposal(variant[0],proposalList);
  }
  
  
}

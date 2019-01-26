/*
 * Created on 6 juil. 2005
 */
package fr.umlv.tatoo.cc.parser.table;

import java.util.Map;

import fr.umlv.tatoo.cc.parser.grammar.NonTerminalDecl;
import fr.umlv.tatoo.cc.parser.grammar.TerminalDecl;
import fr.umlv.tatoo.cc.parser.parser.ActionDeclFactory;
import fr.umlv.tatoo.cc.parser.parser.BranchTableActionDecl;
import fr.umlv.tatoo.cc.parser.parser.RegularTableActionDecl;

public class ParserTableDecl {
  public ParserTableDecl(Map<TerminalDecl, RegularTableActionDecl[]> map,
      BranchTableActionDecl[] branchArray,
      Map<NonTerminalDecl,int[]> gotoes,int count,
      Map<NonTerminalDecl,Integer> startStateMap,
      ActionDeclFactory actionFactory,
      NodeFactory<?> nodeFactory,
      StateMetadataDecl[] metadata,
      TerminalDecl eof,TerminalDecl error) {
    actionMap = map;
    stateCount = count;
    this.gotoes = gotoes;
    this.actionFactory = actionFactory;
    this.nodeFactory = nodeFactory;
    this.eof = eof;
    this.error = error;
    this.metadata = metadata;
    this.startStateMap = startStateMap;
    this.branchArray = branchArray;
  }

  public int getStateCount() {
    return stateCount;
  }
  
  public Map<NonTerminalDecl, int[]> getGotoes() {
    return gotoes;
  }
  
  public Map<TerminalDecl, RegularTableActionDecl[]> getActionMap() {
    return actionMap;
  }
  
  public BranchTableActionDecl[] getBranchArray() {
    return branchArray;
  }
  
  public Map<NonTerminalDecl, Integer> getStartStateMap() {
    return startStateMap;
  }
  
  public ActionDeclFactory getActionFactory() {
    return actionFactory;
  }

  public NodeFactory<?> getNodeFactory() {
    return nodeFactory;
  }
  
  public TerminalDecl getEof() {
    return eof;
  }

  public TerminalDecl getError() {
    return error;
  }

  public StateMetadataDecl[] getMetadata() {
    return metadata;
  }
  
  private final int stateCount;
  private final Map<TerminalDecl, RegularTableActionDecl[]> actionMap;
  private final Map<NonTerminalDecl,int[]> gotoes;
  private final ActionDeclFactory actionFactory;
  private final NodeFactory<?> nodeFactory;
  private final TerminalDecl eof,error;
  private final StateMetadataDecl[] metadata;
  private final Map<NonTerminalDecl, Integer> startStateMap;
  private final BranchTableActionDecl[] branchArray;
}
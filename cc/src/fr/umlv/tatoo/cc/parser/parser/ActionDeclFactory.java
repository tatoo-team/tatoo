/*
 * Created on 20 juil. 2005
 */
package fr.umlv.tatoo.cc.parser.parser;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import fr.umlv.tatoo.cc.parser.grammar.ProductionDecl;
import fr.umlv.tatoo.cc.parser.grammar.TerminalDecl;
import fr.umlv.tatoo.cc.parser.grammar.VersionDecl;
import fr.umlv.tatoo.cc.parser.table.NodeDecl;

public class ActionDeclFactory {
  public BranchActionDecl getDefaultBranchAction() {
    return defaultAction;
  }
  
  public ErrorActionDecl getDefaultErrorAction() {
    return defaultError;
  }
  
  public NonAssociativeErrorActionDecl getNonAssociativeError() {
    if (nonAssociativeError!=null)
      return nonAssociativeError;
    nonAssociativeError = new NonAssociativeErrorActionDecl("errornonassoc","non associative operator");
    errors.put(null, nonAssociativeError);
    return nonAssociativeError;
  }
  
  public AcceptActionDecl getAccept() {
    return AcceptActionDecl.getInstance();
  }
  
  public ErrorActionDecl getError(String message) {
    ErrorActionDecl action = errors.get(message);
    if (action == null) {
      action = new ErrorActionDecl("error"+errors.size(),message);
      errors.put(message,action);
    }
    return action;
  }
  
  public BranchActionDecl getBranch(String message) {
    BranchActionDecl action = branchs.get(message);
    if (action == null) {
      action = new BranchActionDecl(message,branchs.size());
      branchs.put(message,action);
    }
    return action;
  }

  
  public ShiftActionDecl getShift(NodeDecl<?> state) {
    ShiftActionDecl action = shifts.get(state);
    if (action == null) {
      action = new ShiftActionDecl(state);
      shifts.put(state,action);
    }
    return action;
  }
  
  public ReduceActionDecl getReduce(ProductionDecl production) {
    ReduceActionDecl action = reduces.get(production);
    if (action == null) {
      action = new ReduceActionDecl(production);
      reduces.put(production,action);
    }
    return action;
  }
  
  public EnterActionDecl getEnter(TerminalDecl branchingTerminal, NodeDecl<?> state) {
    EnterKey key=new EnterKey(branchingTerminal,state.getStateNo());
    EnterActionDecl action = enters.get(key);
    if (action == null) {
      action = new EnterActionDecl(branchingTerminal,state);
      enters.put(key,action);
    }
    return action;
  }
  
  private final static class EnterKey {
    public EnterKey(TerminalDecl branchingTerminal,int stateNo) {
      this.stateNo=stateNo;
      this.branchingTerminal=branchingTerminal;
    }
    
    @Override
    public int hashCode() {
      return branchingTerminal.getId().hashCode() ^ Integer.rotateLeft(stateNo,16);
    }
    @Override
    public boolean equals(Object obj) {
      if (!(obj instanceof EnterKey))
        return false;
      EnterKey key=(EnterKey)obj;
      return stateNo==key.stateNo &&
             branchingTerminal.equals(key.branchingTerminal);
    }
    
    private final int stateNo;
    private final TerminalDecl branchingTerminal;
  }
  
  public ExitActionDecl getExit() {
    return defaultExitAction;
  }
  
  public VersionedActionDecl getVersionedAction(Map<VersionDecl,? extends ActionDecl> actionMap) {
    VersionedActionDecl action = versionedActions.get(actionMap);
    if (action == null) {
      action = new VersionedActionDecl(versionedActions.size()+1,actionMap);
      versionedActions.put(actionMap,action);
    }
    return action;
  }

  public Collection<BranchActionDecl> getBranchs() {
    return branchs.values();
  }
  
  public Collection<ErrorActionDecl> getErrors() {
    return errors.values();
  }
  
  public Collection<ShiftActionDecl> getShifts() {
    return shifts.values();
  }
  
  public Collection<ReduceActionDecl> getReduces() {
    return reduces.values();
  }
  
  public Collection<EnterActionDecl> getEnters() {
    return enters.values();
  }
  
  
  public Collection<VersionedActionDecl> getVersioneds() {
    return versionedActions.values();
  } 
  
  private final HashMap<String,ErrorActionDecl> errors =
    new HashMap<String,ErrorActionDecl>();
  private final HashMap<String,BranchActionDecl> branchs =
    new HashMap<String,BranchActionDecl>();
  private final HashMap<NodeDecl<?>,ShiftActionDecl> shifts =
    new HashMap<NodeDecl<?>,ShiftActionDecl>();
  private final HashMap<ProductionDecl,ReduceActionDecl> reduces =
    new HashMap<ProductionDecl,ReduceActionDecl>();
  private final HashMap<EnterKey,EnterActionDecl> enters =
    new HashMap<EnterKey,EnterActionDecl>();
  private final HashMap<Map<VersionDecl,? extends ActionDecl>,VersionedActionDecl> versionedActions=
    new HashMap<Map<VersionDecl,? extends ActionDecl>,VersionedActionDecl>();
  private NonAssociativeErrorActionDecl nonAssociativeError;
  private final BranchActionDecl defaultAction=getBranch("parse error");
  private final ErrorActionDecl defaultError=getError("parse error");
  private final ExitActionDecl defaultExitAction=new ExitActionDecl();
}

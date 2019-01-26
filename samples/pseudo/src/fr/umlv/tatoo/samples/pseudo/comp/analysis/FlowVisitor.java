package fr.umlv.tatoo.samples.pseudo.comp.analysis;

import static fr.umlv.tatoo.samples.pseudo.comp.analysis.ConstantMap.NO_CONST;

import java.util.HashSet;

import fr.umlv.tatoo.samples.pseudo.ast.Block;
import fr.umlv.tatoo.samples.pseudo.ast.BlockInstr;
import fr.umlv.tatoo.samples.pseudo.ast.ConditionalIf;
import fr.umlv.tatoo.samples.pseudo.ast.ConditionalIfElse;
import fr.umlv.tatoo.samples.pseudo.ast.DeclVarType;
import fr.umlv.tatoo.samples.pseudo.ast.Expr;
import fr.umlv.tatoo.samples.pseudo.ast.ExprVar;
import fr.umlv.tatoo.samples.pseudo.ast.ForLoopIncr;
import fr.umlv.tatoo.samples.pseudo.ast.ForLoopInit;
import fr.umlv.tatoo.samples.pseudo.ast.IdToken;
import fr.umlv.tatoo.samples.pseudo.ast.Instr;
import fr.umlv.tatoo.samples.pseudo.ast.InstrBreak;
import fr.umlv.tatoo.samples.pseudo.ast.InstrContinue;
import fr.umlv.tatoo.samples.pseudo.ast.InstrLoop;
import fr.umlv.tatoo.samples.pseudo.ast.InstrReturn;
import fr.umlv.tatoo.samples.pseudo.ast.InstrThrow;
import fr.umlv.tatoo.samples.pseudo.ast.LoopFor;
import fr.umlv.tatoo.samples.pseudo.ast.LoopLabel;
import fr.umlv.tatoo.samples.pseudo.ast.LoopWhile;
import fr.umlv.tatoo.samples.pseudo.ast.LvalueId;
import fr.umlv.tatoo.samples.pseudo.ast.Node;
import fr.umlv.tatoo.samples.pseudo.ast.Script;
import fr.umlv.tatoo.samples.pseudo.ast.VarInit;
import fr.umlv.tatoo.samples.pseudo.comp.BlockLocalVar;
import fr.umlv.tatoo.samples.pseudo.comp.LocalVar;
import fr.umlv.tatoo.samples.pseudo.comp.SymbolMap;
import fr.umlv.tatoo.samples.pseudo.comp.SymbolMap.Binding;
import fr.umlv.tatoo.samples.pseudo.comp.analysis.ConstantMap.Constant;
import fr.umlv.tatoo.samples.pseudo.comp.analysis.FlowEnv.LoopDA;

public class FlowVisitor extends TraversalVisitor<FlowEnv> {
  private final ScriptEnv scriptEnv;
  private final ConstFoldEnv constFoldEnv;
  private final LivenessMap livenessMap;
  private final HashSet<LocalVar> knownUnusedSet=
    new HashSet<LocalVar>();
  
  public FlowVisitor(ScriptEnv scriptEnv,ConstFoldEnv constFoldEnv,LivenessMap livenessMap) {
    this.scriptEnv=scriptEnv;
    this.constFoldEnv=constFoldEnv;
    this.livenessMap=livenessMap;
  }
  
  private void checkInitialized(FlowEnv flowEnv,LocalVar localVar,Node node) {
    if (!localVar.isConstant() && !flowEnv.getInitSet().isMarked(localVar)) {
      scriptEnv.error(node,"access to a potentially non initialized variable "+localVar.getName());
      // error recovery
    }
  }
  
  private void markInitialized(FlowEnv flowEnv,LocalVar localVar) {
    if (!localVar.isConstant()) {
      flowEnv.getInitSet().mark(localVar);
    }
  }
  
  private void checkRead(FlowEnv flowEnv,LocalVar localVar) {
    if (!flowEnv.getReadSet().isMarked(localVar) && !knownUnusedSet.contains(localVar)) {
      SymbolMap symbolMap=scriptEnv.getSymbolMap();
      Binding binding=symbolMap.getBinding(localVar);
      scriptEnv.warning(binding.getDefinition(),"unused variable "+localVar.getName());
    }
  }
  
  private void markRead(FlowEnv flowEnv,LocalVar localVar) {
    flowEnv.getReadSet().mark(localVar);
  }
  
  /** unused
  private void markUnread(FlowEnv flowEnv,LocalVar localVar) {
    flowEnv.getReadSet().unmark(localVar);
  }*/
  
  private void checkUnusedVariables(BlockEnv blockEnv,FlowEnv flowEnv) {
    for(LocalVar localVar:blockEnv.getLocalVarScope().getItems()) {
      checkRead(flowEnv,localVar);
    }
  }
  
  public void flow(Script script) {
    // ARGS is a known unused variable
    BlockEnv blockEnv=(BlockEnv)scriptEnv.getSymbolMap().getSymbol(script.getBlock());
    LocalVar args=blockEnv.getLocalVarScope().lookupItem("ARGS");
    knownUnusedSet.add(args);
    
    script.accept(this,null);
  }
  
  @Override
  public Void visit(Block block,FlowEnv flowEnv) {
    boolean primary;
    if (flowEnv==null) { // primary block   
      flowEnv=new FlowEnv();
      primary=true;
    } else {
      primary=false;
    }
    
    for(BlockInstr blockInstr:block.getBlockInstrStar()) {
      if (!flowEnv.isAlive()) {
        scriptEnv.error(blockInstr,"instruction after a break or a return");
        //error recovery, don't output error for following instructions
        flowEnv.setAlive(true);
      }
      blockInstr.accept(this,flowEnv);
    }
    
    // detect unused variables
    BlockEnv blockEnv=(BlockEnv)scriptEnv.getSymbolMap().getSymbol(block);
    checkUnusedVariables(blockEnv,flowEnv);
    
    boolean blockAlive=flowEnv.isAlive();
    if (primary) {
      if (blockAlive && blockEnv.getExpectedReturnType()!=scriptEnv.getTypeScope().voidType()) {
        scriptEnv.error(block,"return missing");
      }
      livenessMap.setAlive(block,blockAlive);
    }
    return null;
  }
  
  @Override
  public Void visit(ConditionalIf conditional_if,FlowEnv flowEnv)  {
    Expr b_expr=conditional_if.getExpr();
    b_expr.accept(this,flowEnv);
    
    FlowEnv ifFlowEnv=new FlowEnv(flowEnv);
    conditional_if.getInstr().accept(this,ifFlowEnv);
    
    flowEnv.getReadSet().or(ifFlowEnv.getReadSet());
    
    Constant constant=constFoldEnv.getConstFoldMap().getConstant(b_expr); //TRUE, FALSE or null
    if (constant!=NO_CONST && ((Boolean)constant.getConstant())==true) {
      flowEnv.getInitSet().reset(ifFlowEnv.getInitSet());
      flowEnv.setAlive(ifFlowEnv.isAlive());
      return null;
    }
    // if false or if condition, do nothing
    return null;
  }
  
  @Override
  public Void visit(ConditionalIfElse conditional_if_else,FlowEnv flowEnv) {
    Expr b_expr=conditional_if_else.getExpr();
    b_expr.accept(this,flowEnv);
    
    FlowEnv trueFlowEnv=new FlowEnv(flowEnv);
    Instr instr=conditional_if_else.getInstr();
    instr.accept(this,trueFlowEnv);
    livenessMap.setAlive(instr,trueFlowEnv.isAlive());
    
    FlowEnv falseFlowEnv=new FlowEnv(flowEnv);
    Instr instr2=conditional_if_else.getInstr2();
    instr2.accept(this,falseFlowEnv);
    
    DASet readSet=flowEnv.getReadSet();
    readSet.or(trueFlowEnv.getReadSet());
    readSet.or(falseFlowEnv.getReadSet());
    flowEnv.getReadSet().reset(readSet);
    
    Constant constant=constFoldEnv.getConstFoldMap().getConstant(b_expr);
    if (constant!=NO_CONST) {
      boolean value=(Boolean)constant.getConstant();
      if (value==true) {
        // condition always true
        flowEnv.getInitSet().reset(trueFlowEnv.getInitSet());
        flowEnv.setAlive(trueFlowEnv.isAlive());
        return null; 
      } else {
        // condition always false
        flowEnv.getInitSet().reset(falseFlowEnv.getInitSet());
        flowEnv.setAlive(falseFlowEnv.isAlive());
        return null;
      }
    }
    // if condition
    DASet initSet=flowEnv.getInitSet();
    initSet.and(falseFlowEnv.getInitSet());
    flowEnv.getInitSet().reset(initSet);
    
    boolean conditionalAlive=trueFlowEnv.isAlive() || falseFlowEnv.isAlive();
    flowEnv.setAlive(conditionalAlive);
    return null;
  }
  
  private void flowLoop(Node loopNode,Expr expr,InstrLoop parent,Instr instr,ForLoopIncr incr,FlowEnv flowEnv) {
    if (expr!=null) {
      expr.accept(this,flowEnv);
    }
    
    LoopLabel loopLabel=parent.getLoopLabelOptional();
    String label=(loopLabel==null)?null:loopLabel.getId().getValue();
    
    LoopDA loopDA=new LoopDA();
    LoopStack<LoopDA> loopStack=flowEnv.getLoopStack();
    loopStack.push(label,loopDA);
    FlowEnv loopFlowEnv=new FlowEnv(flowEnv);
    instr.accept(this,loopFlowEnv);
    loopStack.pop();
    
    if (incr!=null) {
      incr.accept(this,loopFlowEnv);
    }
    
    livenessMap.setAlive(instr,loopFlowEnv.isAlive());
    flowEnv.getReadSet().or(loopFlowEnv.getReadSet());
    
    Constant constant;
    if (expr==null ||
        ((constant=constFoldEnv.getConstFoldMap().getConstant(expr))!=NO_CONST &&
          ((Boolean)constant.getConstant())==true)) {
      // while true, for ever
      DASet breakInitSet=loopDA.getBreakInitSet();
      if (breakInitSet!=null) {
        flowEnv.getInitSet().reset(breakInitSet);
        livenessMap.setAlive(loopNode,true);
        return;
      } 
      // while true with no break
      flowEnv.setAlive(false);
      livenessMap.setAlive(loopNode,false);
      return;
    }
    // while condition or while false
    livenessMap.setAlive(loopNode,true);
  } 
  
  @Override
  public Void visit(LoopWhile loop_while,FlowEnv flowEnv) {
    flowLoop(loop_while,loop_while.getExpr(),loop_while.getParent(),
      loop_while.getInstr(),null,flowEnv);
    return null;
  }
  
  @Override
  public Void visit(LoopFor loop_for,FlowEnv flowEnv) {
    ForLoopInit forLoopInitOpt=loop_for.getForLoopInitOptional();
    if (forLoopInitOpt!=null) {
      forLoopInitOpt.accept(this,flowEnv);
    }
    flowLoop(loop_for,loop_for.getExprOptional(),loop_for.getParent(),
      loop_for.getInstr(),loop_for.getForLoopIncrOptional(),flowEnv);
    
    // detect unused variables
    BlockEnv blockEnv=(BlockEnv)scriptEnv.getSymbolMap().getSymbol(loop_for);
    checkUnusedVariables(blockEnv,flowEnv);
    return null;
  }
  
  @Override
  public Void visit(InstrBreak instr_break,FlowEnv flowEnv) {
    LoopStack<LoopDA> loopStack=flowEnv.getLoopStack();
    
    LoopDA loopDA;
    IdToken labelToken=instr_break.getIdOptional();
    if (labelToken==null) {
      loopDA=loopStack.getCurrentLoopContext();
    } else {
      loopDA=loopStack.getLoopContext(labelToken.getValue());
    }
    if (loopDA==null) {
      // error recovery, break without loop
      return null;
    }
    
    loopDA.addBreakInitSet(flowEnv.getInitSet());
    flowEnv.setAlive(false);
    return null;
  }
  
  @Override
  public Void visit(InstrContinue instr_continue,FlowEnv flowEnv) {
    flowEnv.setAlive(false);
    return null;
  }
  
  @Override
  public Void visit(InstrReturn instr_return,FlowEnv flowEnv) {
    super.visit(instr_return,flowEnv);
    flowEnv.setAlive(false);
    return null;
  }
  
  @Override
  public Void visit(InstrThrow instr_throw,FlowEnv flowEnv)  {
    flowEnv.setAlive(false);
    return null;
  }
  
  @Override
  public Void visit(LvalueId lvalue_id,FlowEnv flowEnv) {
    IdToken tokenId=lvalue_id.getId();
    LocalVar localVar=(LocalVar)scriptEnv.getSymbolMap().getSymbol(tokenId,true);
    if (localVar==null) {
      // error recovery
      return null;
    }
    markInitialized(flowEnv,localVar);
    return null;
  }
  
  @Override
  public Void visit(ExprVar expr_var,FlowEnv flowEnv)  {
    IdToken tokenId=expr_var.getId();
    LocalVar localVar=(LocalVar)scriptEnv.getSymbolMap().getSymbol(tokenId,true);
    if (localVar==null) {
      // error recovery
      return null;
    }
    
    checkInitialized(flowEnv,localVar,tokenId);
    markRead(flowEnv,localVar);
    return null;
  }
  
  @Override
  public Void visit(DeclVarType decl_var_type,FlowEnv flowEnv) {
    VarInit init=decl_var_type.getVarInitOptional();
    if (init!=null) {
      init.accept(this,flowEnv);
      
      BlockLocalVar localVar=(BlockLocalVar)scriptEnv.getSymbolMap().getSymbol(decl_var_type,true);
      if (localVar==null) {
        // error recovery
        return null;
      }
      markInitialized(flowEnv,localVar);
    }
    return null;
  }
}

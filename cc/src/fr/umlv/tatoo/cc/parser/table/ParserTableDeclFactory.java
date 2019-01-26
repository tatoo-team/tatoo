/*
 * Created on 8 juil. 2003
 */
package fr.umlv.tatoo.cc.parser.table;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import fr.umlv.tatoo.cc.common.extension.ExtensionBus;
import fr.umlv.tatoo.cc.parser.grammar.Grammar;
import fr.umlv.tatoo.cc.parser.grammar.GrammarSets;
import fr.umlv.tatoo.cc.parser.grammar.NonTerminalDecl;
import fr.umlv.tatoo.cc.parser.grammar.Priority;
import fr.umlv.tatoo.cc.parser.grammar.ProductionDecl;
import fr.umlv.tatoo.cc.parser.grammar.TerminalDecl;
import fr.umlv.tatoo.cc.parser.grammar.VersionDecl;
import fr.umlv.tatoo.cc.parser.grammar.VersionManager;
import fr.umlv.tatoo.cc.parser.main.ParserDataKeys;
import fr.umlv.tatoo.cc.parser.parser.AcceptActionDecl;
import fr.umlv.tatoo.cc.parser.parser.ActionDecl;
import fr.umlv.tatoo.cc.parser.parser.ActionDeclFactory;
import fr.umlv.tatoo.cc.parser.parser.ActionDeclVisitor;
import fr.umlv.tatoo.cc.parser.parser.BranchActionDecl;
import fr.umlv.tatoo.cc.parser.parser.BranchTableActionDecl;
import fr.umlv.tatoo.cc.parser.parser.EnterActionDecl;
import fr.umlv.tatoo.cc.parser.parser.ErrorActionDecl;
import fr.umlv.tatoo.cc.parser.parser.ExitActionDecl;
import fr.umlv.tatoo.cc.parser.parser.ReduceActionDecl;
import fr.umlv.tatoo.cc.parser.parser.RegularTableActionDecl;
import fr.umlv.tatoo.cc.parser.parser.ShiftActionDecl;
import fr.umlv.tatoo.cc.parser.parser.VersionedActionDecl;

/**
 * @author Julien
 */
public class ParserTableDeclFactory<I extends NodeItem<I>> {  
  private final AbstractConflictDiagnosisReporter reporter;
  private final Grammar grammar;
  private final GrammarSets grammarSets;
  private final TerminalDecl eof;
  private final TerminalDecl error;
  //private final Map<VersionDecl,? extends Set<? extends VersionDecl>> versionMap;
  final VersionManager versionManager;
  private final ActionDeclFactory actionFactory;
  private final ConflictResolverPolicy conflictResolver;
  private final TableFactoryMethod<I> method;
  private final File log;


  ParserTableDeclFactory(ExtensionBus extensionBus,
      AbstractConflictDiagnosisReporter reporter,
      Collection<? extends ProductionDecl> productions,
      Set<? extends NonTerminalDecl> starts, TerminalDecl eof,
      TerminalDecl error,
      VersionManager versionManager,
          ActionDeclFactory actionFactory, 
          TableFactoryMethod<I> method,
          ConflictResolverPolicy conflictResolver, File log)
       {
    this.reporter=reporter;
    this.grammar = method.buildGrammar(productions, starts, eof);
    this.grammarSets = new GrammarSets(grammar);
    this.eof=eof;
    this.versionManager=versionManager;
    this.error=error;
    this.actionFactory=actionFactory;
    this.conflictResolver=conflictResolver;
    this.log=log;
    this.method=method;
    if (extensionBus != null)
      extensionBus.publish(ParserDataKeys.grammarSets, this.grammarSets);
  }

  public static ParserTableDecl buildTable(ExtensionBus extensionBus,AbstractConflictDiagnosisReporter reporter,
      Collection<? extends ProductionDecl> productions,
      Set<? extends NonTerminalDecl> starts, TerminalDecl eof,
      TerminalDecl error,
      VersionManager versionManager,
          ActionDeclFactory actionFactory, 
          TableFactoryMethod<?> method,
          ConflictResolverPolicy conflictResolver, File log) {
    
    return captureBuildTable(extensionBus, reporter,
      productions, starts, eof, error, versionManager, actionFactory,
      method, conflictResolver, log);
  }
  
  private static <I extends NodeItem<I>> ParserTableDecl captureBuildTable(ExtensionBus extensionBus,
      AbstractConflictDiagnosisReporter reporter,
      Collection<? extends ProductionDecl> productions,
      Set<? extends NonTerminalDecl> starts, TerminalDecl eof,
      TerminalDecl error,
      VersionManager versionManager,
          ActionDeclFactory actionFactory, 
          TableFactoryMethod<I> method,
          ConflictResolverPolicy conflictResolver, File log) {
    return new ParserTableDeclFactory<I>(extensionBus,reporter,productions,starts,eof,error,versionManager,actionFactory,method,conflictResolver,log).buildTable();
  }

  
  private static void addToMap(HashMap<TerminalDecl,HashMap<RegularTableActionDecl,Priority>> map,
      TerminalDecl key, RegularTableActionDecl value1, Priority value2) {
    HashMap<RegularTableActionDecl,Priority> map2 = map.get(key);
    if (map2==null) {
      map2=new HashMap<RegularTableActionDecl, Priority>();
      map2.put(value1, value2);
      map.put(key,map2);
      return;
    }
    updateMap(map2,value1,value2);
  }
  
  private static <B extends ActionDecl> void updateMap(HashMap<B,Priority> map2, B value1, Priority value2) {
    Priority oldValue2 = map2.get(value1);
    if (oldValue2 == null || oldValue2.comparePriorityValues(value2)<0) {
      map2.put(value1,value2);
    }
  }
  
  private ParserTableDecl buildTable() {

    // means actions associated to a state and a terminal
    HashMap<NodeDecl<I>, HashMap<TerminalDecl,HashMap<RegularTableActionDecl,Priority>>> tmpTable = 
      new HashMap<NodeDecl<I>, HashMap<TerminalDecl,HashMap<RegularTableActionDecl,Priority>>>();
    // means a set of actions associated to a state caused by a branch terminal: eof or a branching one
    HashMap<NodeDecl<I>,HashMap<BranchTableActionDecl,Priority>> tmpBranch =
      new HashMap<NodeDecl<I>,HashMap<BranchTableActionDecl,Priority>>();

    NodeFactory<I> factory = new NodeFactory<I>(grammar,grammarSets,eof,versionManager,method);
    factory.computeShortestPath();
    Collection<NodeDecl<I>> nodes = factory.getNodes();

    method.initializeComputation(factory,grammar,grammarSets,eof);

    /*
     * compute gotoes : gather all goto map from nodes
     */
    HashMap<NodeDecl<I>, HashMap<NonTerminalDecl,NodeDecl<I>>> buildedGotos =
      new HashMap<NodeDecl<I>, HashMap<NonTerminalDecl,NodeDecl<I>>>();
    for (NodeDecl<I> node : nodes)
      buildedGotos.put(node,node.getGotos());

    /*
     * add action for each nodes
     */
    for (NodeDecl<I> node : nodes) {
      HashMap<TerminalDecl,HashMap<RegularTableActionDecl,Priority>> actions =
        new HashMap<TerminalDecl,HashMap<RegularTableActionDecl,Priority>>();
      tmpTable.put(node,actions);
      HashMap<BranchTableActionDecl,Priority> branchActions =
        new HashMap<BranchTableActionDecl,Priority>();
      tmpBranch.put(node,branchActions);

      HashMap<TerminalDecl, NodeDecl<I>> shifts = node.getShifts();
      /*
       * add shifts
       */
      for (Map.Entry<TerminalDecl, NodeDecl<I>> entry : shifts.entrySet()) {
        TerminalDecl t = entry.getKey();
        NodeDecl<I> to = entry.getValue();

        if (t == eof) {
          addToMap(actions,eof,actionFactory.getAccept(),eof.getPriority());
          updateMap(branchActions,actionFactory.getExit(),eof.getPriority());
        } 
        else {
          if (t.isBranching()) {
            updateMap(branchActions,actionFactory.getEnter(t,node),t.getPriority());
          }
          else
            addToMap(actions,t,actionFactory.getShift(to),t.getPriority());
        }
      }

      /*
       * add actions for reduce
       */
      HashSet<I> reduces = node.getReduces();
      for (I item : reduces) {

        ProductionDecl p = item.getProduction();
        // accept case
        if (grammar.getStarts().contains(p.getLeft())) {
          addToMap(actions,eof,actionFactory.getAccept(),eof.getPriority());
          updateMap(branchActions,actionFactory.getExit(),eof.getPriority());
          continue;
        }
        Set<TerminalDecl> reduceBy = method.getLookaheads(grammar,grammarSets,item,node);
        for(TerminalDecl next: reduceBy) {
          ReduceActionDecl action = actionFactory.getReduce(p);
          if (next.isBranching())
            updateMap(branchActions,action,p.getPriority());
          else {
            if (next==eof)
              updateMap(branchActions,action,p.getPriority());
            addToMap(actions,next,action,p.getPriority());
          }

        }
      }
    }

    if (log != null) {
//    TableWriter.dumpTable(log,grammar,grammarSets,factory,tmpTable,tmpBranch,buildedGotos);
      TableWriter.dumpTable(log,grammar,grammarSets,factory,tmpTable,tmpBranch,buildedGotos);      
    }

    HashMap<NodeDecl<I>, HashMap<TerminalDecl, HashMap<VersionDecl,RegularTableActionDecl>>> map =
      resolveConflictsTable(tmpTable);
    
    // process default (fast) reduce 
    HashMap<NodeDecl<I>,HashMap<VersionDecl,ReduceActionDecl>> defaultReduces =
      new HashMap<NodeDecl<I>, HashMap<VersionDecl,ReduceActionDecl>>();
    
    for(NodeDecl<I> node:nodes) {
      HashMap<VersionDecl, ReduceActionDecl> reduceMap = new HashMap<VersionDecl, ReduceActionDecl>();
      defaultReduces.put(node,reduceMap);
      HashMap<TerminalDecl, HashMap<VersionDecl,RegularTableActionDecl>> actions = map.get(node);
      version: for(VersionDecl version:versionManager.getAllVersions()) {
        /*if (version==null)
          continue;*/
        ReduceActionDecl action=null;
        for(Map.Entry<TerminalDecl, HashMap<VersionDecl, RegularTableActionDecl>> entry:actions.entrySet()) {
          RegularTableActionDecl localAction = entry.getValue().get(version);
          /*
          if (localAction==null) {
            System.err.println(version);
            System.err.println(entry.getKey());
            System.err.println(node.description());
            throw new AssertionError();
          }*/
          if (localAction.isError())
            continue;
          if (action!=null) {
            if (action==localAction)
              continue;
            else
              continue version; // no default for this node/version
          }
          if (localAction.isReduce()) {
            action=(ReduceActionDecl)localAction;
          } else {
            continue version;
          }
        }
        if (action!=null)
          reduceMap.put(version, action);
      }
    }

    HashMap<NodeDecl<I>, HashMap<VersionDecl,BranchTableActionDecl>> branchMap = resolveConflictsBranch(tmpBranch);

    if (reporter.isOnError())
      throw new FatalConflictException("some conflicts occur");


    //convert versioned action map to versionedActions

    HashMap<NodeDecl<I>, HashMap<TerminalDecl, RegularTableActionDecl>> newMap = 
      new HashMap<NodeDecl<I>, HashMap<TerminalDecl,RegularTableActionDecl>>();

    for(Map.Entry<NodeDecl<I>, HashMap<TerminalDecl, HashMap<VersionDecl,RegularTableActionDecl>>> entry:map.entrySet()) {
      HashMap<TerminalDecl,RegularTableActionDecl> map2 = new HashMap<TerminalDecl, RegularTableActionDecl>();
      newMap.put(entry.getKey(),map2);
      for (Map.Entry<TerminalDecl, HashMap<VersionDecl,RegularTableActionDecl>> entry2: entry.getValue().entrySet()) {
        map2.put(entry2.getKey(),computeVersionedAction(RegularTableActionDecl.class,entry.getKey(), entry2.getValue()));
      }
    }

    HashMap<NodeDecl<I>,BranchTableActionDecl> newBranchMap = 
      new HashMap<NodeDecl<I>, BranchTableActionDecl>();
    for(Map.Entry<NodeDecl<I>,HashMap<VersionDecl,BranchTableActionDecl>> entry:branchMap.entrySet()) {
      newBranchMap.put(entry.getKey(), computeVersionedAction(BranchTableActionDecl.class,entry.getKey(),entry.getValue()));
    }


    return createParserTableDecl(newMap,newBranchMap,buildedGotos,factory,defaultReduces);
  }

  private HashMap<NodeDecl<I>, HashMap<VersionDecl,BranchTableActionDecl>> 
  resolveConflictsBranch(HashMap<NodeDecl<I>,HashMap<BranchTableActionDecl,Priority>> tmpBranches) {

    HashMap<NodeDecl<I>, HashMap<VersionDecl,BranchTableActionDecl>> branchMap =
      new HashMap<NodeDecl<I>, HashMap<VersionDecl,BranchTableActionDecl>>();

    for(Map.Entry<NodeDecl<I>,HashMap<BranchTableActionDecl,Priority>> entry:tmpBranches.entrySet()) {
      HashMap<VersionDecl,BranchTableActionDecl> actionMap = new HashMap<VersionDecl, BranchTableActionDecl>();
      HashMap<BranchTableActionDecl,Priority> branches = entry.getValue();
      fillActionMap(BranchTableActionDecl.class, actionMap, branches, entry.getKey(), null,actionFactory.getDefaultErrorAction());
      branchMap.put(entry.getKey(), actionMap);
    }

    return branchMap;
  }

  private <A extends ActionDecl> A computeVersionedAction(Class<A> type,
      NodeDecl<I> state, HashMap<VersionDecl,? extends A> actionMap) {    
    Iterator<VersionDecl> it = state.getCompatibleVersion().iterator();
    
    assert it.hasNext():"versions must at least contains a default version";
    
    A action=actionMap.get(it.next());
    for(;it.hasNext();) {
      A otherAction = actionMap.get(it.next());
      if (action!=otherAction) {
        // more than one action, create a versioned action
        return type.cast(actionFactory.getVersionedAction(actionMap));
      }
    }
    return action;
  }

  private ParserTableDecl createParserTableDecl(
      HashMap<NodeDecl<I>, HashMap<TerminalDecl, RegularTableActionDecl>> table,
      HashMap<NodeDecl<I>, BranchTableActionDecl> branch,
      HashMap<NodeDecl<I>, HashMap<NonTerminalDecl,NodeDecl<I>>> buildedGotos,
      NodeFactory<I> nodeFactory,
      HashMap<NodeDecl<I>,HashMap<VersionDecl,ReduceActionDecl>> defaultReduces) {

    Collection<NodeDecl<I>> nodes = nodeFactory.getNodes();
    int stateNb = nodes.size();

    // transform table
    HashMap<TerminalDecl,RegularTableActionDecl[]> map=new HashMap<TerminalDecl,RegularTableActionDecl[]>();
    for(Map.Entry<NodeDecl<I>,HashMap<TerminalDecl,RegularTableActionDecl>> entry:table.entrySet()) {
      NodeDecl<I> state = entry.getKey();
      for(Map.Entry<TerminalDecl,RegularTableActionDecl> actionEntry:entry.getValue().entrySet()) {

        TerminalDecl terminal = actionEntry.getKey();
        RegularTableActionDecl[] actions = map.get(terminal);
        if (actions == null) {
          actions=new RegularTableActionDecl[stateNb];
          /*for(int i=0;i<stateNb;i++)
            actions[i]=actionFactory.getDefaultBranchAction();*/
          map.put(terminal,actions);
        }    
        if (actionEntry.getValue()!=null)
          actions[state.getStateNo()]=actionEntry.getValue();
      }
    }

    // transform branchTable
    BranchTableActionDecl[] branchMap = new BranchTableActionDecl[stateNb];

    /*BranchTableActionDecl errorAction=actionFactory.getDefaultErrorAction();
    for(int i=0;i<stateNb;i++)
      branchMap[i]=errorAction;*/

    for(Map.Entry<NodeDecl<I>, BranchTableActionDecl> entry:branch.entrySet()) {
      NodeDecl<I> state = entry.getKey();
      branchMap[state.getStateNo()]=entry.getValue();
    }

    // transform gotos
    HashMap<NonTerminalDecl,int[]> gotoMap=new HashMap<NonTerminalDecl,int[]>();
    for(Map.Entry<NodeDecl<I>,HashMap<NonTerminalDecl,NodeDecl<I>>> entry:buildedGotos.entrySet()) {

      for(Map.Entry<NonTerminalDecl,NodeDecl<I>> gotoEntry:entry.getValue().entrySet()) {
        int[] gotoes = gotoMap.get(gotoEntry.getKey());
        if (gotoes == null) {
          gotoes =new int[stateNb];
          Arrays.fill(gotoes,-1);
          gotoMap.put(gotoEntry.getKey(),gotoes);
        }
        gotoes[entry.getKey().getStateNo()]=gotoEntry.getValue().getStateNo();
      }
    }

    // transform start states
    HashMap<NonTerminalDecl,Integer> startStateMap=new HashMap<NonTerminalDecl,Integer>();
    for(Map.Entry<NonTerminalDecl,NodeDecl<I>> entry:nodeFactory.getStartStateMap().entrySet()) {
      startStateMap.put(entry.getKey(),entry.getValue().getStateNo());
    }

    StateMetadataFactory stateMetadataFactory=new StateMetadataFactory();
    StateMetadataDecl[] metadata= new StateMetadataDecl[stateNb];
    for(NodeDecl<I> node : nodes) {
      Set<VersionDecl> compatibleVersion = node.getCompatibleVersion();
      boolean allVersions = true;
      for(VersionDecl v : versionManager.getAllVersions()) {
        if (/*v!=null && */!compatibleVersion.contains(v)) {
          allVersions=false;
          break;
        }
      }
      if (allVersions)
        compatibleVersion=null;
      RegularTableActionDecl defaultReduce = computeVersionedAction(RegularTableActionDecl.class, node, defaultReduces.get(node));
      metadata[node.getStateNo()]=
        stateMetadataFactory.create(compatibleVersion,node.getAssociated(),defaultReduce);
    }

    return new ParserTableDecl(map,branchMap,gotoMap,stateNb,startStateMap,
        actionFactory,nodeFactory,metadata,eof,error);
  }


  private HashMap<NodeDecl<I>, HashMap<TerminalDecl, HashMap<VersionDecl,RegularTableActionDecl>>> resolveConflictsTable(
      HashMap<NodeDecl<I>, HashMap<TerminalDecl,HashMap<RegularTableActionDecl,Priority>>> tmpTable) {
    HashMap<VersionDecl,RegularTableActionDecl> defaultMap = new HashMap<VersionDecl, RegularTableActionDecl>();
    for(VersionDecl decl:versionManager.getAllVersions()) {
      //if (decl!=null)
      defaultMap.put(decl,actionFactory.getDefaultBranchAction());
    }
    HashMap<NodeDecl<I>, HashMap<TerminalDecl, HashMap<VersionDecl,RegularTableActionDecl>>> map =
      new HashMap<NodeDecl<I>, HashMap<TerminalDecl, HashMap<VersionDecl,RegularTableActionDecl>>>();
    for(Map.Entry<NodeDecl<I>, HashMap<TerminalDecl,HashMap<RegularTableActionDecl,Priority>>> entry : tmpTable.entrySet()) {
      NodeDecl<I> state = entry.getKey();
      HashMap<TerminalDecl, HashMap<VersionDecl,RegularTableActionDecl>> map2 = new HashMap<TerminalDecl, HashMap<VersionDecl,RegularTableActionDecl>>();
      map.put(state, map2);
      Set<? extends TerminalDecl> alphabet = grammar.getAlphabet();
      for(TerminalDecl a:alphabet) {
        map2.put(a, defaultMap);
      }
      if (!alphabet.contains(eof)) {
        map2.put(eof, defaultMap);
      }
      for(Map.Entry<TerminalDecl,HashMap<RegularTableActionDecl,Priority>> entry2 : entry.getValue().entrySet()) {
        TerminalDecl terminal = entry2.getKey();
        HashMap<RegularTableActionDecl,Priority> actions = entry2.getValue();
        HashMap<VersionDecl,RegularTableActionDecl> actionMap = new HashMap<VersionDecl, RegularTableActionDecl>();
        map2.put(terminal,actionMap);
        fillActionMap(RegularTableActionDecl.class,actionMap,actions,state,terminal,actionFactory.getDefaultBranchAction());
      }
    }

    if (reporter.isOnError())
      return null;

    return map;
  }
  
  
            
  
  private <A extends ActionDecl> void fillActionMap(Class<A> type, HashMap<VersionDecl,A> actionMap,HashMap<A,Priority> actions,NodeDecl<?> state,TerminalDecl terminal,A defaultAction) {
    for(VersionDecl version : versionManager.getAllVersions()) {
      A action=conflictResolver.priorityAction(type,
          reporter,actionFactory,
          getValidAction(actions,version),
          state,terminal,eof);
      if (action==null)
        action=defaultAction;
      actionMap.put(version,action);
    }
  }

  private static <A extends ActionDecl> ConflictResolverPolicy.ActionEntry<A> convert(final Map.Entry<A, Priority> entry) {
    return new ConflictResolverPolicy.ActionEntry<A>() {
      @Override
      public A getAction() {
        return entry.getKey();
      }
      @Override
      public Priority getPriority() {
        return entry.getValue();
      }
    };
  }

  private <A extends ActionDecl> HashSet<ConflictResolverPolicy.ActionEntry<A>> getValidAction(HashMap<A,Priority> actions, VersionDecl version) {

    HashSet<ConflictResolverPolicy.ActionEntry<A>> compatible = new HashSet<ConflictResolverPolicy.ActionEntry<A>>();
    for(Map.Entry<A,Priority> action : actions.entrySet()) {
      if (action.getKey().accept(validActionVisitor,version))
        compatible.add(convert(action));
    }
    return compatible;
  }

  private final ActionDeclVisitor<Boolean,VersionDecl> validActionVisitor=
    new ActionDeclVisitor<Boolean,VersionDecl>() {
    public Boolean visit(ErrorActionDecl action,VersionDecl version) {
      return true;
    }
    public Boolean visit(BranchActionDecl action,VersionDecl version) {
      return true;
    }
    public Boolean visit(ReduceActionDecl action,VersionDecl version) {
      Set<VersionDecl> compatibleVersions=versionManager.getCompatibleVersion(action.getProduction().getVersion());
      return compatibleVersions.contains(version);
    }
    public Boolean visit(ShiftActionDecl action,VersionDecl version) {
      return action.getState().getCompatibleVersion().contains(version);
    }
    public Boolean visit(AcceptActionDecl action,VersionDecl version) {
      return true;
    }
    public Boolean visit(VersionedActionDecl action,VersionDecl version) {
      throw new AssertionError("illegal action type");
    }

    public Boolean visit(EnterActionDecl action,VersionDecl version) {
      return action.getState().getCompatibleVersion().contains(version);
    }

    public Boolean visit(ExitActionDecl action,VersionDecl version) {
      return true;
    }
  };

}

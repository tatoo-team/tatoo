/*
 * Created on 8 juil. 2003
 */
package fr.umlv.tatoo.cc.parser.lalr;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import fr.umlv.tatoo.cc.common.util.MultiMap;
import fr.umlv.tatoo.cc.parser.grammar.Grammar;
import fr.umlv.tatoo.cc.parser.grammar.GrammarSets;
import fr.umlv.tatoo.cc.parser.grammar.NonTerminalDecl;
import fr.umlv.tatoo.cc.parser.grammar.Priority;
import fr.umlv.tatoo.cc.parser.grammar.TerminalDecl;
import fr.umlv.tatoo.cc.parser.grammar.VariableDecl;
import fr.umlv.tatoo.cc.parser.lr.LR1ClosureComputer;
import fr.umlv.tatoo.cc.parser.lr.LR1Item;
import fr.umlv.tatoo.cc.parser.slr.LR0Item;
import fr.umlv.tatoo.cc.parser.slr.SLRTableFactoryMethod;
import fr.umlv.tatoo.cc.parser.table.Closure;
import fr.umlv.tatoo.cc.parser.table.NodeDecl;
import fr.umlv.tatoo.cc.parser.table.NodeFactory;

/**
 * @author Julien
 * @version $Revision$
 * $Date$
 */
public class LALRTableFactoryMethod extends SLRTableFactoryMethod {
  
  private static class Stuff {
  
    Stuff() {/* nothing */}

    /* MultiMap<(node,item)->lookahead> */
    private MultiMap<NodeItemCouple,TerminalDecl> lookMap 
    = new MultiMap<NodeItemCouple,TerminalDecl>();
    /* Map<(node,item),(node,item)> */
    private MultiMap<NodeItemCouple,NodeItemCouple> propMap
    = new MultiMap<NodeItemCouple,NodeItemCouple>();

    public MultiMap<NodeItemCouple,TerminalDecl> getLookMap() {
      return lookMap;
    }
    
    public MultiMap<NodeItemCouple,NodeItemCouple> getPropMap() {
      return propMap;
    }
  }
  
  private final TerminalDecl sharp = new TerminalDecl("+#+",Priority.getNoPriority(),false);
  
  private static class NodeItemCouple {
    private final NodeDecl<LR0Item> node;
    private final LR0Item item;
    private final int hashcode;
    
    public NodeItemCouple(NodeDecl<LR0Item> node, LR0Item item) {
      this.node = node;
      this.item = item;
      if (item==null)
        hashcode=node.hashCode();
      else
        hashcode=item.hashCode() ^ node.hashCode();
    }
    
    public LR0Item getItem() {
      return item;
    }
    
    public NodeDecl<LR0Item> getNode() {
      return node;
    }
    
    @Override
    public boolean equals(Object obj) {
      if (! (obj instanceof NodeItemCouple))
        return false;
      NodeItemCouple couple = (NodeItemCouple) obj;
      return item.equals(couple.getItem())
      && node.equals(couple.getNode());
    }
    
    @Override
    public int hashCode() {
      return hashcode;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
      return "(" + node.getStateNo() + "," + item + ")";
    }
    
  }
  
  private NodeDecl<LR0Item> to(NodeDecl<LR0Item> node, VariableDecl v) {
    if (v.isTerminal()) {
      return node.getShifts().get(v);
    } else {
      return node.getGotos().get(v);
    }
    
  }
  private NodeDecl<LR0Item> to(NodeDecl<LR0Item> node, TerminalDecl v) {
    return node.getShifts().get(v);
  }
  
  private NodeDecl<LR0Item> to(NodeDecl<LR0Item> node, NonTerminalDecl v) {
    return node.getGotos().get(v);
  }
  
  private static LR0Item advanceToLR0(LR1Item item,int advance) {
    return new LR0Item(item.getProduction(),item.getDotPlace()+advance);
  }
  
  private void setLookaheads(Collection<NodeDecl<LR0Item>> nodes, 
      Stuff stuff,LR1ClosureComputer closureComputer) {
    for (NodeDecl<LR0Item> node : nodes) {

      for (LR0Item item : node.getKernelItems()) {
        
        VariableDecl v = item.getDottedVariable();
        if (v == null)
          continue;
        
        NodeDecl<LR0Item> to = to(node, v);
        stuff.getPropMap().add(
            new NodeItemCouple(node, item),
            new NodeItemCouple(to, item.advance()));
        
        if (v.isTerminal())
          continue;
        
        LR1Item augItem =
          new LR1Item(item.getProduction(), item.getDotPlace(), sharp);
        
        //System.out.println(augItem);
        Closure<LR1Item> closure = closureComputer.getClosure(augItem);
        //System.out.println(closure);
        
        for (Map.Entry<NonTerminalDecl,Set<LR1Item>> entry : 
          closure.getGotos().entrySet()) {
          NonTerminalDecl n = entry.getKey();
          Set<LR1Item> set = entry.getValue();
          
          for (LR1Item sourceItem : set) {
            //System.out.println("treating " + sourceItem);
            if (sourceItem.getLookahead().equals(sharp)) {
              //System.out.println("coucou");
              stuff.getPropMap().add(
                  new NodeItemCouple(node, item),
                  new NodeItemCouple(to(node, n), advanceToLR0(sourceItem,1)));
              continue;
            }
            NodeItemCouple couple =
              new NodeItemCouple(to(node, n), advanceToLR0(sourceItem,1));
            stuff.getLookMap().add(couple, sourceItem.getLookahead());
          }
        }
        
        for (Map.Entry<TerminalDecl,Set<LR1Item>> entry : 
          closure.getShifts().entrySet()) {
          TerminalDecl t = entry.getKey();
          Set<LR1Item> set = entry.getValue();
          
          for (LR1Item sourceItem : set) {
            if (sourceItem.getLookahead().equals(sharp)) {
              stuff.getPropMap().add(
                  new NodeItemCouple(node, item),
                  new NodeItemCouple(to(node, t), advanceToLR0(sourceItem,1)));
              continue;
            }
            NodeItemCouple couple =
              new NodeItemCouple(to(node, t), advanceToLR0(sourceItem,1));
            stuff.getLookMap().add(couple, sourceItem.getLookahead());
          }
        }
        
        for (LR1Item sourceItem : closure.getReduces()) {
          if (sourceItem.getRight().size() == 0) {
            if (sourceItem.getLookahead().equals(sharp)) {
              stuff.getPropMap().add(
                  new NodeItemCouple(node, item),
                  new NodeItemCouple(node, advanceToLR0(sourceItem,0)));
              continue;
            }
            
            NodeItemCouple couple =
              new NodeItemCouple(node, advanceToLR0(sourceItem,0));
            stuff.getLookMap().add(couple, sourceItem.getLookahead());
          }
        }
      }
    }
  }
  
  private void propagateLookaheads(Stuff stuff) {
    MultiMap<NodeItemCouple,TerminalDecl> toPropagate =
      new MultiMap<NodeItemCouple,TerminalDecl>();
    toPropagate.addAll(stuff.getLookMap());
    
    MultiMap<NodeItemCouple,TerminalDecl> buildingToPropagate =
      new MultiMap<NodeItemCouple,TerminalDecl>();
    
    while (!toPropagate.keySet().isEmpty()) {
      //System.out.println("step :");
      //System.out.println(toPropagate);
      for (Map.Entry<NodeItemCouple,Set<TerminalDecl>> entry :
        toPropagate.entrySet()){
        NodeItemCouple couple = entry.getKey();
        
        Set<NodeItemCouple> toSet = stuff.getPropMap().get(couple);
        if (toSet != null) {
          for(NodeItemCouple to : toSet) {
            Set<TerminalDecl> toAdds = entry.getValue();
            for (TerminalDecl toAdd:toAdds) {
              if (stuff.getLookMap().add(to, toAdd))
                buildingToPropagate.add(to, toAdd);
            }
          }
        }
      }
      
      MultiMap<NodeItemCouple,TerminalDecl> tmpMap = toPropagate;
      toPropagate = buildingToPropagate;
      buildingToPropagate = tmpMap;
      buildingToPropagate.clear();
    }
    
  }

  
  
  private Stuff stuff;
  
  @Override
  public void initializeComputation(NodeFactory<LR0Item> factory,
      Grammar grammar, GrammarSets grammarSets,TerminalDecl eof) {
    stuff = new Stuff();
    for(NodeDecl<LR0Item> node : factory.getStartNodes()) {
      LR0Item firstKernelItem = node.getFirstKernelItem();
      stuff.getLookMap().add(new NodeItemCouple(node, firstKernelItem), eof);
    }
    
    setLookaheads(factory.getNodes(), stuff,new LR1ClosureComputer(grammar,grammarSets,eof));
    propagateLookaheads(stuff);
  }
  
  @Override
  public Set<TerminalDecl> getLookaheads(Grammar g, GrammarSets sets, LR0Item item,NodeDecl<LR0Item> node) {
    NodeItemCouple couple =
      new NodeItemCouple(node, item);//new LR0Item(item.getProduction(), item.getRight().size()));
    Set<TerminalDecl> follows = stuff.getLookMap().get(couple);
    /*if(node.getStateNo()==0)
      System.out.println("C:"+couple+" "+follows);*/
    return follows;
  }
}

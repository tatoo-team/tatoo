package fr.umlv.tatoo.cc.parser.grammar;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.umlv.tatoo.cc.common.log.Info;

public class GrammarChecker  {
  
  public Set<NonTerminalDecl> getAccessible(
      Map<NonTerminalDecl,? extends List<ProductionDecl>> productionMap,
          Set<? extends NonTerminalDecl> starts) {
    HashSet<NonTerminalDecl> usefull = new HashSet<NonTerminalDecl>(starts);
    HashSet<NonTerminalDecl> toAdd = new HashSet<NonTerminalDecl>(usefull);
    HashSet<NonTerminalDecl> buildingToAdd = new HashSet<NonTerminalDecl>();
    for(;;) {
      for(NonTerminalDecl use : toAdd) {
        List<ProductionDecl> productions = productionMap.get(use);
        for(ProductionDecl production : productions) {
          List<? extends VariableDecl> rights = production.getRight();
          for(VariableDecl var:rights) {
            if (!var.isTerminal() && !usefull.contains(var)) {
              NonTerminalDecl nonTerminal=(NonTerminalDecl)var;
              buildingToAdd.add(nonTerminal);
              
              Info.fine("Non terminal %s is usefull in production %s",var,production).report();
            }
          }
        }
      }
    HashSet<NonTerminalDecl> tmpToAdd = buildingToAdd;
    buildingToAdd = toAdd;
    toAdd = tmpToAdd;
    
    if(toAdd.isEmpty())
      break;
    usefull.addAll(toAdd);
    buildingToAdd.clear();
    }
    
    
    return usefull;
  }
  
  public Set<NonTerminalDecl> getProducing(
      Set<? extends NonTerminalDecl> nonTerminals,
      Map<NonTerminalDecl,? extends List<ProductionDecl>> productionMap) {
    
    HashSet<NonTerminalDecl> usefull = new HashSet<NonTerminalDecl>();
    HashSet<NonTerminalDecl> nonTerminalsToTest = new HashSet<NonTerminalDecl>(nonTerminals);
    
    // build list of n such that n -> u, u only made of terminals
    nonTerminal: for(Iterator<NonTerminalDecl> i= nonTerminalsToTest.iterator();i.hasNext();) {
      NonTerminalDecl n = i.next();
      List<ProductionDecl> productions = productionMap.get(n);
      if (productions == null) {
        Info.warning("Non terminal %s never appears in the left hand side of a production",n).report();
        
        i.remove();
        continue;
      }
      production: for(ProductionDecl production : productions) {
        List<? extends VariableDecl> rights = production.getRight();
        for(VariableDecl var : rights)
          if (!var.isTerminal())
            continue production;
        // n leads to only terminals
        //System.out.println("Usefull "+n);
        Info.fine("Non terminal %s is usefull in production %s",n,production).report();
        
        usefull.add(n);
        i.remove();
        continue nonTerminal;
      }
    }
    
    // repeat to add usefull non terminals until no more is added.
    
    for(;;) {
      boolean newAdded=false;
      nonTerminal: for(Iterator<NonTerminalDecl> i= nonTerminalsToTest.iterator();i.hasNext();) {
        NonTerminalDecl n = i.next();
        List<ProductionDecl> productions = productionMap.get(n);
        production: for(ProductionDecl production : productions) {
          List<? extends VariableDecl> rights = production.getRight();
          //System.out.println(rights);
          for(VariableDecl var : rights)
            if (!(var.isTerminal() || usefull.contains(var)))
              continue production;
          
          // n leads to only terminals or usefull non terminals
          //System.out.println("Usefull "+n);
          Info.fine("Non terminal %s is usefull in production %s",n,production).report();
          
          usefull.add(n);
          newAdded=true;
          i.remove();
          continue nonTerminal;
        }
      }
      if (!newAdded)
        break;
    }
    return usefull;
  }
}

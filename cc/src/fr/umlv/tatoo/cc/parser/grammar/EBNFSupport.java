package fr.umlv.tatoo.cc.parser.grammar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.umlv.tatoo.cc.common.generator.Type;
import fr.umlv.tatoo.cc.parser.grammar.Priority.Associativity;
import fr.umlv.tatoo.cc.tools.tools.ToolsFactory;

public class EBNFSupport {
  public EBNFSupport(GrammarFactory factory) {
    this.factory=factory;
  }
  
  //FIXME Remi remove boolean debug?
  public boolean debug;
  public Map<ProductionDecl,? extends EBNFSyntheticType> getEBNFTypeMap() {
    if (debug)
      return Collections.unmodifiableMap(ebnfTypeMap);
    return ebnfTypeMap;
  }
  
  public Map<NonTerminalDecl,? extends StarDesc> getStarDescMap() {
    return starDescMap;
  }
  
  public Set<? extends NonTerminalDecl> getStarNonTerminals() {
    return starNonTerminals;
  }
  
  public Set<? extends NonTerminalDecl> getOptionalNonTerminals() {
    return optionalNonTerminals;
  }
  
  public NonTerminalDecl createAnonymousNonTerminal(List<? extends VariableDecl> vars,VersionDecl version) {
    String anonymousId="anonymous_"+nextCount();
    NonTerminalDecl anonymous=factory.createNonTerminal(anonymousId);
    
    ProductionDecl prod=createProduction(anonymous,version,anonymousId,vars);
    ebnfTypeMap.put(prod,EBNFSyntheticType.ANONYMOUS);
    
    return anonymous;
  }
  
  public NonTerminalDecl createOptionnalNonTerminal(String id,VariableDecl element, VersionDecl version) {
    String optionalId=id+"_optional_"+nextCount();
    NonTerminalDecl optional=factory.createNonTerminal(optionalId);
    optionalNonTerminals.add(optional);
    
    ProductionDecl prod1=createProduction(optional,version,optionalId+"_empty",Collections.<VariableDecl>emptyList());
    ebnfTypeMap.put(prod1,EBNFSyntheticType.OPTIONAL_EMPTY);
    
    ProductionDecl prod2=createProduction(optional,version,optionalId+'_'+element.getId(),Collections.singletonList(element));
    ebnfTypeMap.put(prod2,EBNFSyntheticType.OPTIONAL_SINGLETON);
    
    return optional;
  }
  
  public NonTerminalDecl createStarNonTerminalUsingPrefix(String prefix,VariableDecl element,Associativity assoc,VersionDecl version,boolean isStar,VariableDecl separator) {
    String id;
    if (isStar)
      id=prefix+"_star_"+nextCount();
    else
      id=prefix+"_plus_"+nextCount();
    return createStarNonTerminal(id,element,assoc,version,isStar,separator);
  }
  
  public NonTerminalDecl createStarNonTerminal(String id,VariableDecl element,Associativity assoc,VersionDecl version,boolean isStar,VariableDecl separator) {
    NonTerminalDecl nt=factory.createNonTerminal(id);
    starNonTerminals.add(nt);
    
    NonTerminalDecl ntSub;
    ArrayList<ProductionDecl> productionList;
    if (isStar && separator!=null) {
      String subId=nt.getId()+"_sub";
      ntSub=factory.createNonTerminal(subId);
      starNonTerminals.add(ntSub);
      
      productionList = createSeparatorStarList(id,nt,ntSub,element,separator,version,assoc);
    } else {
      
      ntSub=null;
      productionList = createPlusOrStarList(id,nt,element,separator,version,isStar,assoc);
    }
    
    StarDesc desc=new StarDesc(element,separator,nt,ntSub,productionList);
    starDescMap.put(nt,desc);
    return nt;
  }
  
  private ArrayList<ProductionDecl> createSeparatorStarList(String id,NonTerminalDecl nt,NonTerminalDecl ntSub,VariableDecl element,VariableDecl separator,VersionDecl version,Associativity assoc) {
   
    ArrayList<ProductionDecl> productionList=createPlusOrStarList(id,ntSub,element,separator,version,false,assoc);
    
    ProductionDecl prod1=createProduction(nt,version,id+"_empty",Collections.<VariableDecl>emptyList());
    ebnfTypeMap.put(prod1,EBNFSyntheticType.STAR_EMPTY);
    
    // must be the last production (at least after STAR_RECURSIVE_{LEFT|RIGHT}
    ProductionDecl prod2=createProduction(nt,version,id+"_through",Collections.singletonList(ntSub));
    ebnfTypeMap.put(prod2,EBNFSyntheticType.STAR_PASS_THROUGH);
    
    Collections.addAll(productionList,prod1,prod2);
    return productionList;
  }
  
  private ArrayList<ProductionDecl> createPlusOrStarList(String starId,NonTerminalDecl nt,VariableDecl element,VariableDecl separator,VersionDecl version,boolean isStar,Associativity assoc) {
    //System.out.println(starId);
    ProductionDecl prod1;
    if (isStar) {
      prod1=createProduction(nt,version,starId+"_empty",Collections.<VariableDecl>emptyList());
      ebnfTypeMap.put(prod1,EBNFSyntheticType.STAR_EMPTY);
    }
    else {
      prod1=createProduction(nt,version,starId+"_element",Collections.singletonList(element));
      ebnfTypeMap.put(prod1,EBNFSyntheticType.STAR_SINGLETON);
    }
    
    ArrayList<VariableDecl> variables=new ArrayList<VariableDecl>();
    EBNFSyntheticType type;
    if (assoc==Associativity.LEFT) {
      variables.add(nt);
      variables.add(element);
      type=EBNFSyntheticType.STAR_RECURSIVE_LEFT;
    }
    else {
      variables.add(element);
      variables.add(nt);
      type=EBNFSyntheticType.STAR_RECURSIVE_RIGHT;
    }
    
    if (separator!=null)
      variables.add(1,separator);
    ProductionDecl prod2=createProduction(nt,version,starId+"_rec",variables);
    ebnfTypeMap.put(prod2,type);
    
    ArrayList<ProductionDecl> productionList=new ArrayList<ProductionDecl>();
    Collections.addAll(productionList,prod1,prod2);
    return productionList;
  }
  
  private ProductionDecl createProduction(NonTerminalDecl list,VersionDecl version,String productionId,List<? extends VariableDecl> variables) {
    return factory.createProduction(productionId,list,variables,
        factory.findProductionPriority(productionId, variables),version);
  }

  private int nextCount() {
    return count++;
  }
  
  /** Compute EBNF generated non-terminal type
   */
  public void processEBNFType(ToolsFactory toolsFactory) {
    Map<VariableDecl,Type> variableTypeMap=toolsFactory.getVariableTypeMap(); 
    
    Type listType=Type.createQualifiedType("java.util.List");
    for(Map.Entry<ProductionDecl,EBNFSyntheticType> entry:ebnfTypeMap.entrySet()) {
      ProductionDecl production=entry.getKey();
      EBNFSyntheticType starType = entry.getValue();
      Type type;
      switch(starType) {
      case STAR_PASS_THROUGH:
      case OPTIONAL_SINGLETON:
        // for STAR_PASS_THROUGH, we know that STAR_RECURSIVE_LEFT|STAR_RECURSIVE_RIGHT
        // of the corresponding non-terminal are already done
        type=variableTypeMap.get(production.getRight().get(0));
        if (type!=null)
          toolsFactory.declareNonTerminalType(production.getLeft(),type);
        break;
      case STAR_RECURSIVE_LEFT:
        List<? extends VariableDecl> right = production.getRight();
        type=variableTypeMap.get(right.get(right.size()-1));
        if (type!=null)
          toolsFactory.declareNonTerminalType(production.getLeft(),Type.createParametrizedType(listType,type));
        break;
      case STAR_SINGLETON:
        // no break here !!

      case STAR_RECURSIVE_RIGHT:  
        type=variableTypeMap.get(production.getRight().get(0));
        if (type!=null)
          toolsFactory.declareNonTerminalType(production.getLeft(),Type.createParametrizedType(listType,type));
        break;
      case STAR_EMPTY:
        // do nothing, work is done in STAR_RECURSIVE* and STAR_SINGLETON
        
      case OPTIONAL_EMPTY:
        // do nothing, work is done in OPTIONAL_SINGLETON
        break;
        
      case ANONYMOUS:
        // TODO: Remi check
      }
    }
  }
  /*
  public void processASTEBNFType(Map<VariableDecl, Type> variableTypeMap) {
    // compute star non-terminal type 
    for(Map.Entry<ProductionDecl,EBNFSyntheticType> entry:ebnfTypeMap.entrySet()) {
      ProductionDecl production=entry.getKey();
      EBNFSyntheticType starType = entry.getValue();
      Type type;
      switch(starType) {
      case STAR_PASS_THROUGH:
      case OPTIONAL_SINGLETON:
        // for STAR_PASS_THROUGH, we know that STAR_RECURSIVE_LEFT|STAR_RECURSIVE_RIGHT
        // of the corresponding non-terminal are already done
        type=variableTypeMap.get(production.getRight().get(0));
        if (type!=null)
          variableTypeMap.put(production.getLeft(),type);
        
        // System.out.println("prod "+production+" "+type);
        
        break;
      case STAR_RECURSIVE_LEFT:
        // no break here !!

      case STAR_SINGLETON:
        // no break here !!

      case STAR_RECURSIVE_RIGHT:  
        // no break here !!

      case STAR_EMPTY:
        // no break here !!

      case OPTIONAL_EMPTY:
        // do nothing
      case ANONYMOUS:
        // TODO: Remi check
      }
    }
  }*/
  
  private int count;
  private final GrammarFactory factory;
  private final LinkedHashMap<ProductionDecl,EBNFSyntheticType> ebnfTypeMap=
    new LinkedHashMap<ProductionDecl,EBNFSyntheticType>();
  private final LinkedHashMap<NonTerminalDecl,StarDesc> starDescMap=
    new LinkedHashMap<NonTerminalDecl,StarDesc>();
  private final LinkedHashSet<NonTerminalDecl> starNonTerminals=
    new LinkedHashSet<NonTerminalDecl>();
  private final LinkedHashSet<NonTerminalDecl> optionalNonTerminals=
    new LinkedHashSet<NonTerminalDecl>();
  
  public static class StarDesc {
    StarDesc(VariableDecl element,VariableDecl separator,NonTerminalDecl nt,NonTerminalDecl ntSub,ArrayList<ProductionDecl> productions) {
      this.element=element;
      this.separator=separator;
      this.nt=nt;
      this.ntSub=ntSub;
      this.productions=productions;
    }
    
    public VariableDecl getElement() {
      return element;
    }
    public VariableDecl getSeparator() {
      return separator;
    }
    public List<? extends ProductionDecl> getFakeProductions() {
      return Collections.unmodifiableList(productions);
    }
    
    public NonTerminalDecl getNonTerminal() {
      return nt;
    }
    public NonTerminalDecl getSubNonTerminal() {
      return ntSub;
    }
    
    @Override
    public String toString() {
      return "element "+element+" separator "+separator+" fakeProductions "+productions;
    }

    private final VariableDecl element;
    private final VariableDecl separator;
    private final NonTerminalDecl nt;
    private final NonTerminalDecl ntSub;
    private final ArrayList<ProductionDecl> productions;
  }
}

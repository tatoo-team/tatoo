package fr.umlv.tatoo.cc.tools.ast.generator;

import java.util.HashMap;
import java.util.Map;

import fr.umlv.tatoo.cc.common.generator.Type;
import fr.umlv.tatoo.cc.parser.grammar.NonTerminalDecl;
import fr.umlv.tatoo.cc.parser.grammar.ProductionDecl;
import fr.umlv.tatoo.cc.parser.grammar.VariableDecl;

public class ParentTypeFinder {
  private final HashMap<VariableDecl,TypeFinder> typeFinderMap=
    new HashMap<VariableDecl,TypeFinder>();
  
  final Map<VariableDecl, Type> variableTypeMap;
  final Map<ProductionDecl,Type> productionTypeMap;
    
  public ParentTypeFinder(Map<VariableDecl, Type> variableTypeMap,
      Map<ProductionDecl,Type> productionTypeMap) {
    this.variableTypeMap=variableTypeMap;
    this.productionTypeMap=productionTypeMap;
  }
    
  abstract class TypeFinder {
    abstract Type getType();
    abstract TypeFinder merge(ProductionDecl production);
  }
  
  class ProductionTypeFinder extends TypeFinder {
    private final ProductionDecl production;
    
    public ProductionTypeFinder(ProductionDecl production) {
      this.production=production;
    }
    
    @Override
    TypeFinder merge(ProductionDecl production) {
      if (this.production==production)
        return this;
      if (this.production.getLeft()==production.getLeft())
        return new NonTerminalTypeFinder(production.getLeft());
      return nodeTypeFinder;
    }
    
    @Override
    Type getType() {
      return productionTypeMap.get(production);
    }
  }
  
  class NonTerminalTypeFinder extends TypeFinder {
    private final NonTerminalDecl nonTerminal;
    
    public NonTerminalTypeFinder(NonTerminalDecl nonTerminal) {
      this.nonTerminal=nonTerminal;
    }
    
    @Override
    TypeFinder merge(ProductionDecl production) {
      if (nonTerminal==production.getLeft())
        return this;
      return nodeTypeFinder;
    }
    
    @Override
    Type getType() {
      return variableTypeMap.get(nonTerminal);
    }
  }
  
  final TypeFinder nodeTypeFinder=new TypeFinder() {
    @Override
    TypeFinder merge(ProductionDecl production) {
      return this;
    }
    
    @Override
    Type getType() {
      return null; //here null means runtime Node
    }
  };
  
  public void add(ProductionDecl production) {
    for(VariableDecl variable:production.getRight())
      add(variable,production);
  }
  
  public void add(VariableDecl variable, ProductionDecl production) {
    // don't insert variable with no type
    if (!variableTypeMap.containsKey(variable))
      return;
    
    TypeFinder typeFinder=typeFinderMap.get(variable);
    if (typeFinder==null) {
      typeFinder=new ProductionTypeFinder(production);
    } else {
      typeFinder=typeFinder.merge(production);
    }
    typeFinderMap.put(variable, typeFinder);
  }
  
  public Map<VariableDecl,Type> createParentTypeMap() {
    HashMap<VariableDecl,Type> map=new HashMap<VariableDecl,Type>();
    for(Map.Entry<VariableDecl,TypeFinder> entry:typeFinderMap.entrySet()) {
      Type type=entry.getValue().getType();
      if (type!=null) // here null means runtime Node
        map.put(entry.getKey(),type);
    }
    return map;
  }
}

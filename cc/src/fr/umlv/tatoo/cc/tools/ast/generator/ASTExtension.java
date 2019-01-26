package fr.umlv.tatoo.cc.tools.ast.generator;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import fr.umlv.tatoo.cc.common.extension.ExtensionBus;
import fr.umlv.tatoo.cc.common.extension.ExtensionBus.Context;
import fr.umlv.tatoo.cc.common.extension.ExtensionBus.Registry;
import fr.umlv.tatoo.cc.common.generator.GeneratorException;
import fr.umlv.tatoo.cc.common.generator.Type;
import fr.umlv.tatoo.cc.common.main.CommonDataKeys;
import fr.umlv.tatoo.cc.common.main.GeneratorBean;
import fr.umlv.tatoo.cc.common.main.Unit;
import fr.umlv.tatoo.cc.parser.grammar.EBNFSupport;
import fr.umlv.tatoo.cc.parser.grammar.GrammarRepository;
import fr.umlv.tatoo.cc.parser.grammar.NonTerminalDecl;
import fr.umlv.tatoo.cc.parser.grammar.ProductionDecl;
import fr.umlv.tatoo.cc.parser.grammar.TerminalDecl;
import fr.umlv.tatoo.cc.parser.grammar.VariableDecl;
import fr.umlv.tatoo.cc.parser.main.ParserDataKeys;
import fr.umlv.tatoo.cc.parser.table.ParserTableDecl;
import fr.umlv.tatoo.cc.tools.generator.ToolsExtension;
import fr.umlv.tatoo.cc.tools.main.ToolsDataKeys;
import fr.umlv.tatoo.cc.tools.tools.RuleInfo;
import fr.umlv.tatoo.cc.tools.tools.ToolsFactory;

public class ASTExtension extends ToolsExtension {
  @Override
  public void register(Registry registry) {
    registry.register(CommonDataKeys.bean,
        ParserDataKeys.grammarRepository,
        ParserDataKeys.parserTable,
        ParserDataKeys.ebnfSupport,
        ToolsDataKeys.attributeMap,
        ToolsDataKeys.toolsFactory);
  }
  
  @Override
  public void execute(ExtensionBus bus,Context context) {
    GeneratorBean bean = context.getData(CommonDataKeys.bean);
    //RuleFactory ruleFactory=context.getData(LexerDataKeys.ruleFactory);
    ParserTableDecl parserTable=context.getData(ParserDataKeys.parserTable);
    GrammarRepository grammarRepository=context.getData(ParserDataKeys.grammarRepository);
    EBNFSupport ebnfSupport=context.getData(ParserDataKeys.ebnfSupport);
    Map<String,Type> attributeMap=context.getData(ToolsDataKeys.attributeMap);
    ToolsFactory toolsFactory=context.getData(ToolsDataKeys.toolsFactory);
    
    String astPackagePrefix=bean.getPackage(Unit.ast);
    
    HashMap<NonTerminalDecl,NonTerminalKind> nonTerminalKindMap=
      new HashMap<NonTerminalDecl,NonTerminalKind>();
    
    // update toolsFactory types
    HashMap<VariableDecl, Type> astVariableTypeMap=new HashMap<VariableDecl,Type>();
    Map<VariableDecl,Type> variableTypeMap=toolsFactory.getVariableTypeMap();
    
    // compute terminal type
    HashMap<TerminalDecl,Type> terminalValueTypeMap=
      new HashMap<TerminalDecl,Type>();
    for(TerminalDecl terminal:grammarRepository.getAllTerminals()) {
      Type type=variableTypeMap.get(terminal);
      if (type!=null) {
        terminalValueTypeMap.put(terminal,type);
        type=ASTGeneratorUtils.computeVariableOrProductionType(terminal.getId(),
          astPackagePrefix,"Token");
        astVariableTypeMap.put(terminal,type);
      }
    }
    
    HashSet<NonTerminalDecl> ebnfNonTerminalSet=new HashSet<NonTerminalDecl>();
    for(ProductionDecl production:ebnfSupport.getEBNFTypeMap().keySet()) {
      ebnfNonTerminalSet.add(production.getLeft());
    }
    
    //System.out.println("starNonTerminalSet "+ebnfNonTerminalSet);
    
    // compute non terminal and production type
    for(NonTerminalDecl nonTerminal:grammarRepository.getAllNonTerminals()) {
      // doesn't compute ebnf generated non terminals
      if (ebnfNonTerminalSet.contains(nonTerminal)) {
        nonTerminalKindMap.put(nonTerminal,NonTerminalKind.EBNF_DEFINED);
        continue;
      }
      
      Type type=variableTypeMap.get(nonTerminal);
      if (type==null) {
        type=ASTGeneratorUtils.computeVariableOrProductionType(nonTerminal.getId(),
          astPackagePrefix,"");
      } else {
        throw new IllegalArgumentException(
            "AST doesn't allow user defined type on non terminal "+nonTerminal.getId());
        
        //FIXME Remi, allow user-defined type (change ASTGrammarEvaluator.mc)
        /*
        if (!type.isVoid()) {
          if (type.isPrimitive())
            throw new IllegalArgumentException(
              "AST doesn't allow primitive type on non terminal "+nonTerminal.getId());
            
          nonTerminalKindMap.put(nonTerminal,NonTerminalKind.USER_DEFINED);
        }*/
      }
      astVariableTypeMap.put(nonTerminal,type);
    }
    
    // replace declared types by ast ones
    // in variable type map
    toolsFactory.getVariableTypeMap().clear();
    toolsFactory.getVariableTypeMap().putAll(astVariableTypeMap);
    
    // in rule type map
    HashMap<TerminalDecl,Type> astTerminalTypeMap=new HashMap<TerminalDecl,Type>();
    for(RuleInfo ruleInfo:toolsFactory.getRuleInfoMap().values()) {
      TerminalDecl terminal=ruleInfo.getTerminal();
      if (terminal!=null) {
        Type type=astVariableTypeMap.get(terminal);
        if (type!=null) {
          astTerminalTypeMap.put(terminal,type);
        }
      }
    }
    toolsFactory.getTerminalTypeMap().clear();
    toolsFactory.getTerminalTypeMap().putAll(astTerminalTypeMap);
    
    //process production type
    HashMap<String,NonTerminalDecl> idToNonTerminalMap=new HashMap<String,NonTerminalDecl>();
    for(NonTerminalDecl nonTerminal:grammarRepository.getAllNonTerminals())
      idToNonTerminalMap.put(nonTerminal.getId(),nonTerminal);
      
    HashMap<ProductionDecl,Type> productionTypeMap=new HashMap<ProductionDecl,Type>();
    Map<NonTerminalDecl,? extends Collection<? extends ProductionDecl>> productionMap=
      grammarRepository.getProductionsByNonTerminal();
    for(Map.Entry<NonTerminalDecl,? extends Collection<? extends ProductionDecl>> entry:productionMap.entrySet()) {
      NonTerminalDecl nonTerminal=entry.getKey();
      
      // if non-terminal is user defined or ebnf defined
      if (nonTerminalKindMap.containsKey(nonTerminal)) {
        continue;
      }
      
      Collection<? extends ProductionDecl> productions=entry.getValue();
      for(ProductionDecl production:productions) {
        // check if production as the same name than a non-terminal
        String productionId=production.getId();
        NonTerminalDecl nonTerminalClash=idToNonTerminalMap.get(productionId);
        if (nonTerminalClash!=null) {
          // allow that production and non terminal has the same name if
          // the nonterminal has only one production
          if (nonTerminal!=nonTerminalClash || productions.size()!=1) {
            throw new IllegalStateException("a production and a non terminal have the same name \""+productionId+"\"");
          }
          nonTerminalKindMap.put(nonTerminal,NonTerminalKind.HAS_ONE_PRODUCTION);
        }
        
        productionTypeMap.put(
          production,ASTGeneratorUtils.computeVariableOrProductionType(productionId,
            astPackagePrefix,""));
      }
    }
    
    // process parent map
    //HashMap<VariableDecl,Type> parentMap=getParentMap(grammarRepository,astVariableTypeMap);
    ParentTypeFinder parentTypeFinder=new ParentTypeFinder(variableTypeMap,
        productionTypeMap);
    for(ProductionDecl production:grammarRepository.getAllProductions())
      parentTypeFinder.add(production);
    Map<VariableDecl,Type> parentMap=parentTypeFinder.createParentTypeMap();
    
    //System.out.println("parentMap "+parentMap);
    
    // generate tools
    super.execute(bus,context);
    
    // generate ast
    try {
      new ASTGenerator(bean.getDestination(),bean.getClassPath(),bean.getGeneratorDebugDir()).generate(
        bean,grammarRepository,ebnfSupport,parserTable,toolsFactory,attributeMap,
        terminalValueTypeMap,productionTypeMap,nonTerminalKindMap,parentMap);
    } catch (GeneratorException e) {
      throw new IllegalStateException(e);
    }
  }
}

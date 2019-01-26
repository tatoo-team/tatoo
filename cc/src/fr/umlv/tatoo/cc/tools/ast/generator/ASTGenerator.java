/*
 * Created on 4 mars 2006
 *
 */
package fr.umlv.tatoo.cc.tools.ast.generator;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import fr.umlv.tatoo.cc.common.generator.Generator;
import fr.umlv.tatoo.cc.common.generator.GeneratorException;
import fr.umlv.tatoo.cc.common.generator.Type;
import fr.umlv.tatoo.cc.common.main.Alias;
import fr.umlv.tatoo.cc.common.main.AliasPrototype;
import fr.umlv.tatoo.cc.common.main.GeneratorBean;
import fr.umlv.tatoo.cc.common.main.Unit;
import fr.umlv.tatoo.cc.parser.grammar.EBNFSupport;
import fr.umlv.tatoo.cc.parser.grammar.GrammarRepository;
import fr.umlv.tatoo.cc.parser.grammar.NonTerminalDecl;
import fr.umlv.tatoo.cc.parser.grammar.ProductionDecl;
import fr.umlv.tatoo.cc.parser.grammar.TerminalDecl;
import fr.umlv.tatoo.cc.parser.grammar.VariableDecl;
import fr.umlv.tatoo.cc.parser.main.ParserAliasPrototype;
import fr.umlv.tatoo.cc.parser.table.ParserTableDecl;
import fr.umlv.tatoo.cc.tools.main.ToolsAliasPrototype;
import fr.umlv.tatoo.cc.tools.tools.ToolsFactory;

public class ASTGenerator extends Generator{
  public ASTGenerator(File sourceDir,Set<File> classPath,File debugDir) {
    super(sourceDir,ASTGenerator.class,classPath,debugDir);
  }
  
  public void generate(GeneratorBean bean,
      GrammarRepository grammarRepository,
      EBNFSupport ebnfSupport,
      ParserTableDecl parserTable,
      ToolsFactory toolsFactory,
      Map<String,Type> attributeMap,
      Map<TerminalDecl,Type> terminalValueTypeMap,
      Map<ProductionDecl,Type> productionTypeMap,
      Map<NonTerminalDecl,NonTerminalKind> nonTerminalKindMap,
      Map<VariableDecl,Type> parentMap) throws GeneratorException {
      
      Map<VariableDecl, Type> variableTypeMap=toolsFactory.getVariableTypeMap();
      TreeSet<Type> variableImports = new TreeSet<Type>();    
      for(Type type:variableTypeMap.values()) {
        type.addImportsTo(variableImports);
      }
      for(Type type:productionTypeMap.values()) {
        type.addImportsTo(variableImports);
      }
      
      Map<VariableDecl,Type> variabletypeMap=toolsFactory.getVariableTypeMap();
      
      Map<AliasPrototype,? extends Alias> aliasMap=bean.getAliasMap();
      HashMap<String, Object> root = new HashMap<String, Object>();
      root.put("T", aliasMap.get(ParserAliasPrototype.terminal).getType());
      root.put("N", aliasMap.get(ParserAliasPrototype.nonTerminal).getType());
      root.put("P", aliasMap.get(ParserAliasPrototype.production).getType());
      root.put("V", aliasMap.get(ParserAliasPrototype.version).getType());
      
      //FIXME Remi remove and substitute by P, T, etc.
      root.put("productionEnum", aliasMap.get(ParserAliasPrototype.production).getType());
      root.put("terminalEnum", aliasMap.get(ParserAliasPrototype.terminal).getType());
      
      root.put("productions",grammarRepository.getAllProductions());
      root.put("nonTerminals",grammarRepository.getAllNonTerminals());
      root.put("terminals",grammarRepository.getAllTerminals());
      root.put("starts",grammarRepository.getStartNonTerminalSet());
      root.put("ebnfSupport",ebnfSupport);
      root.put("variableTypeMap",variabletypeMap);
      root.put("productionTypeMap",productionTypeMap);
      root.put("nonTerminalKindMap",nonTerminalKindMap);
      root.put("variableImports",variableImports);
      
      String astPackage=bean.getPackage(Unit.ast);
      
      
      // gather attribute import types
      TreeSet<Type> attributeImportSet = new TreeSet<Type>();    
      for(Type type:attributeMap.values()) {
        type.addImportsTo(attributeImportSet);
      }
      root.put("attributeImportSet", attributeImportSet);
      root.put("attributeMap", attributeMap);
      
      // generate abstract common Node, abstractNode, abstractToken
      Type abstractNodeType=Type.createQualifiedType(astPackage+'.'+"Node");
      root.put("abstractNode",abstractNodeType);
      Type abstractInnerNodeType=Type.createQualifiedType(astPackage+'.'+"AbstractInnerNode");
      root.put("abstractInnerNode",abstractInnerNodeType);
      Type abstractTokenType=Type.createQualifiedType(astPackage+'.'+"AbstractToken");
      root.put("abstractToken",abstractTokenType);
      
      generate(root,abstractNodeType.getSimpleName(),abstractNodeType);
      generate(root,abstractInnerNodeType.getSimpleName(),abstractInnerNodeType);
      generate(root,abstractTokenType.getSimpleName(),abstractTokenType);
      
      // generate ast grammar evaluator
      Type grammarEvaluator=aliasMap.get(ToolsAliasPrototype.grammarEvaluator).getType();
      Type astGrammarEvaluator=Type.createQualifiedType(astPackage+'.'+"ASTGrammarEvaluator");
      root.put("grammarEvaluator",grammarEvaluator);
      root.put("astGrammarEvaluator",astGrammarEvaluator);
      generate(root,astGrammarEvaluator.getSimpleName(),astGrammarEvaluator);
      
      // generate visitors and emitter
      Type visitorType=Type.createQualifiedType(astPackage+'.'+"Visitor");
      root.put("visitorNode",visitorType);
      generate(root,visitorType.getSimpleName(),visitorType); 
      Type visitorCopierType=Type.createQualifiedType(astPackage+'.'+"VisitorCopier");
      root.put("visitorCopierNode",visitorCopierType);
      root.put("terminalValueTypeMap",terminalValueTypeMap);
      generate(root,visitorCopierType.getSimpleName(),visitorCopierType); 
      
      Type nonTerminalGotoStateDataTableType=Type.createQualifiedType(astPackage+'.'+"NonTerminalGotoStateDataTable");
      root.put("nonTerminalGotoStateDataTable",nonTerminalGotoStateDataTableType);
      root.put("parserTable", parserTable);
      generate(root,nonTerminalGotoStateDataTableType.getSimpleName(),nonTerminalGotoStateDataTableType); 
      
      Type astEmitterType=Type.createQualifiedType(astPackage+'.'+"ASTEmitter");
      root.put("astEmitter",astEmitterType);
      root.put("terminalEvaluator",aliasMap.get(ToolsAliasPrototype.terminalEvaluator).getType());
      root.put("analyzerProcessor",aliasMap.get(ToolsAliasPrototype.analyzerProcessor).getType());
      root.put("analyzer",aliasMap.get(ToolsAliasPrototype.analyzers).getType());
      generate(root,astEmitterType.getSimpleName(),astEmitterType); 
      
      // generate terminal node
      for(Map.Entry<TerminalDecl,Type> entry:terminalValueTypeMap.entrySet()) {
        TerminalDecl terminal=entry.getKey();
        Type terminalNode=variabletypeMap.get(terminal);
        Type valueType=entry.getValue();
        HashMap<String,Object> localRoot=
          new HashMap<String,Object>(root);
        localRoot.put("terminal",terminal);
        localRoot.put("terminalNode",terminalNode);
        localRoot.put("valueType",valueType);
        localRoot.put("parent",parentMap.get(terminal));
        generate(localRoot,"TerminalNode",terminalNode); 
      }
      
      // generate non terminal nodes and production nodes
      for(Map.Entry<NonTerminalDecl,? extends Collection<? extends ProductionDecl>> entry:
        grammarRepository.getProductionsByNonTerminal().entrySet()) {
        
        NonTerminalDecl nonTerminal=entry.getKey();
        NonTerminalKind nonTerminalKind=nonTerminalKindMap.get(nonTerminal);
        if (nonTerminalKind==NonTerminalKind.USER_DEFINED ||
            nonTerminalKind==NonTerminalKind.EBNF_DEFINED)
          continue;
        
        root.put("parent",parentMap.get(nonTerminal));
        if (nonTerminalKind!=NonTerminalKind.HAS_ONE_PRODUCTION) {
          Type nonTerminalNodeType=variabletypeMap.get(nonTerminal);
          
          //System.out.println("nonTerminal "+nonTerminal);
          //System.out.println("nonTerminalNodeType "+nonTerminalNodeType);
          
          root.put("nonTerminalNode",nonTerminalNodeType);
          generate(root,"NonTerminalNode",nonTerminalNodeType); 
        } else {
          root.put("nonTerminalNode",null);
        }
        
        for(ProductionDecl production:entry.getValue()) {
          Type productionNodeType=productionTypeMap.get(production);
          
          //System.out.println("productionNodeType "+productionNodeType);
          
          root.put("productionNode",productionNodeType);
          root.put("production",production);
          generate(root,"ProductionNode",productionNodeType);   
        }
        root.remove("production");
        root.remove("parent");
      }
    }
}

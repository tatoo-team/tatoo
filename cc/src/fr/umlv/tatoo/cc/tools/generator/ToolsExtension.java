package fr.umlv.tatoo.cc.tools.generator;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;

import fr.umlv.tatoo.cc.common.extension.Extension;
import fr.umlv.tatoo.cc.common.extension.ExtensionBus;
import fr.umlv.tatoo.cc.common.extension.ExtensionBus.Context;
import fr.umlv.tatoo.cc.common.extension.ExtensionBus.Registry;
import fr.umlv.tatoo.cc.common.generator.GeneratorException;
import fr.umlv.tatoo.cc.common.generator.Type;
import fr.umlv.tatoo.cc.common.main.CommonDataKeys;
import fr.umlv.tatoo.cc.common.main.GeneratorBean;
import fr.umlv.tatoo.cc.lexer.lexer.RuleDecl;
import fr.umlv.tatoo.cc.lexer.lexer.RuleFactory;
import fr.umlv.tatoo.cc.lexer.main.LexerDataKeys;
import fr.umlv.tatoo.cc.parser.grammar.EBNFSupport;
import fr.umlv.tatoo.cc.parser.grammar.EBNFSyntheticType;
import fr.umlv.tatoo.cc.parser.grammar.GrammarRepository;
import fr.umlv.tatoo.cc.parser.grammar.ProductionDecl;
import fr.umlv.tatoo.cc.parser.grammar.VariableDecl;
import fr.umlv.tatoo.cc.parser.main.ParserDataKeys;
import fr.umlv.tatoo.cc.tools.main.ToolsDataKeys;
import fr.umlv.tatoo.cc.tools.tools.ToolsFactory;

public class ToolsExtension implements Extension {
  public void register(Registry registry) {
    registry.register(CommonDataKeys.bean,
        LexerDataKeys.ruleFactory,
        ParserDataKeys.grammarRepository,
        ParserDataKeys.ebnfSupport,
        ToolsDataKeys.toolsFactory);
  }
  
  public static void checkOrpheanRules(RuleFactory ruleFactory,
      ToolsFactory toolsFactory) {
    HashSet<RuleDecl> orpheanRules=new HashSet<RuleDecl>(ruleFactory.getAllRules());
    orpheanRules.removeAll(toolsFactory.getRuleInfoMap().keySet());
    for(RuleDecl rule:orpheanRules) {
      // orphean rules are discardable, always active and not spawned by default
      toolsFactory.createRuleInfo(rule,null,null,true,true,false);
    }
  }
  
  public void execute(ExtensionBus bus,Context context) {
    GeneratorBean bean = context.getData(CommonDataKeys.bean);
    RuleFactory ruleFactory=context.getData(LexerDataKeys.ruleFactory);
    GrammarRepository grammarRepository=context.getData(ParserDataKeys.grammarRepository);
    EBNFSupport ebnfSupport = context.getData(ParserDataKeys.ebnfSupport);
    ToolsFactory toolsFactory=context.getData(ToolsDataKeys.toolsFactory);
    
    // check that all rules have a rule info
    checkOrpheanRules(ruleFactory,toolsFactory);
    
    // check that star productions are not typed 
    Map<VariableDecl,Type> variableTypeMap = toolsFactory.getVariableTypeMap();
    for(Map.Entry<ProductionDecl,? extends EBNFSyntheticType> entry:ebnfSupport.getEBNFTypeMap().entrySet()) {
      ProductionDecl production=entry.getKey();
      if (variableTypeMap.get(production.getLeft())!=null) {
        throw new IllegalStateException("Assigning a type to generated non-terminal "+
            production.getLeft().getId()+" is illegal ");
      }
    }
    
    ebnfSupport.processEBNFType(toolsFactory);
    
    try {
      new ToolsGenerator(bean.getDestination(),bean.getClassPath(),bean.getGeneratorDebugDir()).generate(
        bean,ruleFactory,grammarRepository,ebnfSupport,toolsFactory,
        Collections.<ProductionDecl>emptySet());
    } catch (GeneratorException e) {
      throw new IllegalStateException(e);
    }
  }
}

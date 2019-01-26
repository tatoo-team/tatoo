package fr.umlv.tatoo.cc.plugin;

import fr.umlv.tatoo.cc.common.extension.Extension;
import fr.umlv.tatoo.cc.common.extension.ExtensionBus;
import fr.umlv.tatoo.cc.common.extension.ExtensionBus.Context;
import fr.umlv.tatoo.cc.common.extension.ExtensionBus.Registry;
import fr.umlv.tatoo.cc.common.generator.GeneratorException;
import fr.umlv.tatoo.cc.common.main.CommonDataKeys;
import fr.umlv.tatoo.cc.common.main.GeneratorBean;
import fr.umlv.tatoo.cc.lexer.charset.encoding.UTF16Encoding;
import fr.umlv.tatoo.cc.parser.grammar.GrammarRepository;
import fr.umlv.tatoo.cc.parser.main.ParserDataKeys;
import fr.umlv.tatoo.cc.parser.table.ParserTableDecl;
import fr.umlv.tatoo.cc.tools.main.ToolsDataKeys;
import fr.umlv.tatoo.cc.tools.tools.ToolsFactory;

public class PluginExtension implements Extension {
  public void register(Registry registry) {
    registry.register(CommonDataKeys.bean,
      ParserDataKeys.grammarRepository,
      ParserDataKeys.parserTable,
      ToolsDataKeys.toolsFactory);
  }

  public void execute(ExtensionBus bus,Context context) {
    GeneratorBean bean=context.getData(CommonDataKeys.bean);
    GrammarRepository grammarRepository=context.getData(ParserDataKeys.grammarRepository);
    ParserTableDecl parserTable=context.getData(ParserDataKeys.parserTable); 
    
    ToolsFactory toolsFactory=context.getData(ToolsDataKeys.toolsFactory);
    
    /*
    for(RuleDecl rule:ruleFactory.getAllRules()) {
      RuleProposal proposal=ContextProposal.findProposals(rule.getMainRegex(),UTF16Encoding.getInstance());
      System.out.println("rule "+rule.getId()+" "+proposal);
    }*/
    
    StateProposalDecl[] stateProposalArray=StateProposalDeclFactory.getProposals(parserTable,
      grammarRepository.getAllVersions().iterator().next(),
      toolsFactory,
      UTF16Encoding.getInstance());
    
    /*
    for(int i=0;i<proposals.length;i++) {
      System.out.println("state "+i+": "+proposals[i]);  
    }*/
    
    try {
      new PluginGenerator(bean.getDestination(),bean.getClassPath(),bean.getGeneratorDebugDir()).generate(
        bean,parserTable.getStateCount(),stateProposalArray,grammarRepository.getAllProductions());
    } catch (GeneratorException e) {
      throw new IllegalStateException(e);
    }
  }
}

package fr.umlv.tatoo.samples.java.ext;

import fr.umlv.tatoo.cc.common.extension.Extension;
import fr.umlv.tatoo.cc.common.extension.ExtensionBus;
import fr.umlv.tatoo.cc.common.extension.ExtensionBus.Context;
import fr.umlv.tatoo.cc.common.extension.ExtensionBus.Registry;
import fr.umlv.tatoo.cc.common.generator.GeneratorException;
import fr.umlv.tatoo.cc.common.main.CommonDataKeys;
import fr.umlv.tatoo.cc.common.main.GeneratorBean;
import fr.umlv.tatoo.cc.parser.grammar.GrammarRepository;
import fr.umlv.tatoo.cc.parser.main.ParserDataKeys;
import fr.umlv.tatoo.cc.tools.main.ToolsDataKeys;
import fr.umlv.tatoo.cc.tools.tools.ToolsFactory;

public class ToolsExtension implements Extension {
  public void register(Registry registry) {
    registry.register(CommonDataKeys.bean,
      ParserDataKeys.grammarRepository,
      ToolsDataKeys.toolsFactory);
  }

  public void execute(ExtensionBus bus,Context context) {
    GeneratorBean bean=context.getData(CommonDataKeys.bean);
    GrammarRepository grammarRepository=context.getData(ParserDataKeys.grammarRepository);
    
    ToolsFactory toolsFactory=context.getData(ToolsDataKeys.toolsFactory);
    
    try {
      new ToolsExtensionGenerator(bean.getDestination(),bean.getClassPath(),bean.getGeneratorDebugDir()).generate(
        bean,grammarRepository.getAllProductions(), toolsFactory);
    } catch (GeneratorException e) {
      throw new IllegalStateException(e);
    }
  }
}

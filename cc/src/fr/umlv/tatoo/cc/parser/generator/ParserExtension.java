package fr.umlv.tatoo.cc.parser.generator;

import fr.umlv.tatoo.cc.common.extension.Extension;
import fr.umlv.tatoo.cc.common.extension.ExtensionBus;
import fr.umlv.tatoo.cc.common.extension.ExtensionBus.Context;
import fr.umlv.tatoo.cc.common.extension.ExtensionBus.Registry;
import fr.umlv.tatoo.cc.common.generator.GeneratorException;
import fr.umlv.tatoo.cc.common.main.CommonDataKeys;
import fr.umlv.tatoo.cc.common.main.GeneratorBean;
import fr.umlv.tatoo.cc.parser.grammar.GrammarRepository;
import fr.umlv.tatoo.cc.parser.main.ParserDataKeys;
import fr.umlv.tatoo.cc.parser.table.ParserTableDecl;

public class ParserExtension implements Extension {
  public void register(Registry registry) {
    registry.register(CommonDataKeys.bean,ParserDataKeys.grammarRepository,ParserDataKeys.parserTable);
  }

  public void execute(ExtensionBus bus,Context context) {
    GeneratorBean bean = context.getData(CommonDataKeys.bean);
    GrammarRepository factory=context.getData(ParserDataKeys.grammarRepository);
    ParserTableDecl table=context.getData(ParserDataKeys.parserTable);
    
    // System.out.println(this);
    
    try {
      new ParserGenerator(bean.getDestination(),bean.getClassPath(),bean.getGeneratorDebugDir()).generate(bean,factory,table);
    } catch (GeneratorException e) {
      throw new IllegalStateException(e);
    }
  }
}

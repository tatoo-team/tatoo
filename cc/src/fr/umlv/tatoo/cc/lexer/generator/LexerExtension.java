package fr.umlv.tatoo.cc.lexer.generator;

import fr.umlv.tatoo.cc.common.extension.Extension;
import fr.umlv.tatoo.cc.common.extension.ExtensionBus;
import fr.umlv.tatoo.cc.common.extension.ExtensionBus.Context;
import fr.umlv.tatoo.cc.common.extension.ExtensionBus.Registry;
import fr.umlv.tatoo.cc.common.generator.GeneratorException;
import fr.umlv.tatoo.cc.common.main.CommonDataKeys;
import fr.umlv.tatoo.cc.common.main.GeneratorBean;
import fr.umlv.tatoo.cc.lexer.lexer.LexerMap;
import fr.umlv.tatoo.cc.lexer.main.LexerDataKeys;

public class LexerExtension implements Extension {
  public void register(Registry registry) {
    registry.register(CommonDataKeys.bean,LexerDataKeys.lexerMapTable);
  }

  public void execute(ExtensionBus bus,Context context) {
    GeneratorBean bean = context.getData(CommonDataKeys.bean);
    LexerMap.Table lexerMapTable = context.getData(LexerDataKeys.lexerMapTable);
    
    try {
      new LexerGenerator(bean.getDestination(),bean.getClassPath(),bean.getGeneratorDebugDir()).generate(bean,lexerMapTable);
    } catch (GeneratorException e) {
      throw new IllegalStateException(e);
    }
  }
}

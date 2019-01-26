/*
 * Created on 7 avr. 2006
 *
 */
package fr.umlv.tatoo.samples.ext.cc;

import fr.umlv.tatoo.cc.common.extension.Extension;
import fr.umlv.tatoo.cc.common.extension.ExtensionBus;
import fr.umlv.tatoo.cc.common.extension.ExtensionBus.Context;
import fr.umlv.tatoo.cc.common.extension.ExtensionBus.Registry;
import fr.umlv.tatoo.cc.common.generator.GeneratorException;
import fr.umlv.tatoo.cc.common.main.CommonDataKeys;
import fr.umlv.tatoo.cc.common.main.GeneratorBean;
import fr.umlv.tatoo.cc.tools.main.ToolsDataKeys;
import fr.umlv.tatoo.cc.tools.tools.ToolsFactory;

public class SampleExtension implements Extension {

  public void register(Registry registry) {
    registry.register(CommonDataKeys.bean,ToolsDataKeys.toolsFactory);
  }

  public void execute(ExtensionBus bus,Context context) {
    GeneratorBean bean=context.getData(CommonDataKeys.bean);
    ToolsFactory toolsFactory = context.getData(ToolsDataKeys.toolsFactory);
    
    System.out.println("extention "+this);
    System.out.println("basedir "+bean.getDestination());
    
    try {
      new SampleGenerator(bean.getDestination(),bean.getClassPath(),bean.getGeneratorDebugDir()).generate(bean,toolsFactory.getTerminalTypeMap());
    } catch (GeneratorException e) {
      throw new IllegalStateException(e);
    }
  }
}

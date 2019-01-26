package fr.umlv.tatoo.samples.java.ext;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import fr.umlv.tatoo.cc.common.generator.Generator;
import fr.umlv.tatoo.cc.common.generator.GeneratorException;
import fr.umlv.tatoo.cc.common.generator.Type;
import fr.umlv.tatoo.cc.common.main.GeneratorBean;
import fr.umlv.tatoo.cc.parser.grammar.ProductionDecl;
import fr.umlv.tatoo.cc.parser.main.ParserAliasPrototype;
import fr.umlv.tatoo.cc.tools.tools.ToolsFactory;

public class ToolsExtensionGenerator extends Generator {
  protected ToolsExtensionGenerator(File sourceDir,Set<File> classPath,File debugDir) {
    super(sourceDir,ToolsExtensionGenerator.class,classPath,debugDir);
  }

  public void generate(GeneratorBean bean, Collection<? extends ProductionDecl> productions, ToolsFactory toolsFactory) throws GeneratorException {
    HashMap<String,Object> root=new HashMap<String,Object>();
    Type semanticsType=Type.createQualifiedType(
      "fr.umlv.tatoo.samples.java.ext.Semantics");
    root.put("semanticsType",semanticsType);
    
    root.put("P",bean.getAliasMap().get(ParserAliasPrototype.production).getType());
    
    root.put("productions",productions);
    root.put("variableTypeMap",toolsFactory.getVariableTypeMap());
    
    generate(root,semanticsType.getSimpleRawName(),semanticsType);
  }
}

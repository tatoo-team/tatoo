package fr.umlv.tatoo.cc.plugin;

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

public class PluginGenerator extends Generator {
  protected PluginGenerator(File sourceDir,Set<File> classPath,File debugDir) {
    super(sourceDir,PluginGenerator.class,classPath,debugDir);
  }

  public void generate(GeneratorBean bean,int stateCount,StateProposalDecl[] stateProposalArray, Collection<? extends ProductionDecl> productions) throws GeneratorException {
    HashMap<String,Object> root=new HashMap<String,Object>();
    Type pluginProposalType=Type.createQualifiedType(
      "fr.umlv.tatoo.plugin.PluginProposal");
    root.put("pluginProposalType",pluginProposalType);
    
    root.put("T",bean.getAliasMap().get(ParserAliasPrototype.terminal).getType());
    root.put("N",bean.getAliasMap().get(ParserAliasPrototype.nonTerminal).getType());
    root.put("P",bean.getAliasMap().get(ParserAliasPrototype.production).getType());
    root.put("stateCount",stateCount);
    root.put("stateProposalArray",stateProposalArray);
    
    generate(root,pluginProposalType.getSimpleRawName(),pluginProposalType);
    
    root.put("productions",productions);
    Type pluginUtilsType=Type.createQualifiedType(
      "fr.umlv.tatoo.plugin.PluginUtils");
    root.put("pluginUtilsType",pluginUtilsType);
    
    generate(root,pluginUtilsType.getSimpleRawName(),pluginUtilsType);
  }
}

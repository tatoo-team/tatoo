package fr.umlv.tatoo.cc.parser.generator;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.umlv.tatoo.cc.common.generator.Generator;
import fr.umlv.tatoo.cc.common.generator.GeneratorException;
import fr.umlv.tatoo.cc.common.main.Alias;
import fr.umlv.tatoo.cc.common.main.AliasPrototype;
import fr.umlv.tatoo.cc.common.main.GeneratorBean;
import fr.umlv.tatoo.cc.parser.grammar.GrammarRepository;
import fr.umlv.tatoo.cc.parser.main.ParserAliasPrototype;
import fr.umlv.tatoo.cc.parser.table.ParserTableDecl;
import fr.umlv.tatoo.cc.parser.table.StateMetadataDecl;

public class ParserGenerator extends Generator {
  public ParserGenerator(File sourceDir,Set<File> classPath,File debugDir) {
    super(sourceDir,fr.umlv.tatoo.cc.parser.generator.ParserGenerator.class,classPath,debugDir);
  }
  public void generate(GeneratorBean bean,GrammarRepository factory,ParserTableDecl table)
  throws GeneratorException {
    Map<AliasPrototype,? extends Alias> aliasMap=bean.getAliasMap();
    HashMap<String,Object> root = new HashMap<String,Object>();
    root.put("terminalEnum", aliasMap.get(ParserAliasPrototype.terminal).getType());
    root.put("nonTerminalEnum", aliasMap.get(ParserAliasPrototype.nonTerminal).getType());
    root.put("productionEnum", aliasMap.get(ParserAliasPrototype.production).getType());
    root.put("parserDataTable", aliasMap.get(ParserAliasPrototype.parserDataTable).getType());
    root.put("versionEnum", aliasMap.get(ParserAliasPrototype.version).getType());
    root.put("terminals", factory.getAllTerminals());
    root.put("nonTerminals", factory.getAllNonTerminals());
    root.put("productions", factory.getAllProductions());
    root.put("versions", factory.getAllVersions());
    root.put("metadataSet", gatherStateMetadata(table));
    root.put("table", table);
    
    generate(root, aliasMap,ParserAliasPrototype.terminal);
    generate(root, aliasMap,ParserAliasPrototype.nonTerminal);
    generate(root, aliasMap,ParserAliasPrototype.production);
    generate(root, aliasMap,ParserAliasPrototype.version);
    generate(root, aliasMap,ParserAliasPrototype.parserDataTable);
  }
  
  private Set<StateMetadataDecl> gatherStateMetadata(ParserTableDecl table) {
    HashSet<StateMetadataDecl> set=new HashSet<StateMetadataDecl>();
    Collections.addAll(set,table.getMetadata());
    return set;
  }
}

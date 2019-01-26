package fr.umlv.tatoo.cc.lexer.generator;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import fr.umlv.tatoo.cc.common.generator.Generator;
import fr.umlv.tatoo.cc.common.generator.GeneratorException;
import fr.umlv.tatoo.cc.common.main.Alias;
import fr.umlv.tatoo.cc.common.main.AliasPrototype;
import fr.umlv.tatoo.cc.common.main.GeneratorBean;
import fr.umlv.tatoo.cc.lexer.lexer.LexerMap;
import fr.umlv.tatoo.cc.lexer.main.LexerAliasPrototype;

public class LexerGenerator extends Generator {

  public LexerGenerator(File sourceDir,Set<File> classPath,File debugDir) {
    super(sourceDir,fr.umlv.tatoo.cc.lexer.generator.LexerGenerator.class,classPath,debugDir);
  }

  public void generate(GeneratorBean bean,LexerMap.Table lexerMapTable)
      throws GeneratorException {
    
    Map<AliasPrototype,? extends Alias> aliasMap = bean.getAliasMap();
    HashMap<String,Object> root = new HashMap<String,Object>();
    root.put("ruleEnum", aliasMap.get(LexerAliasPrototype.rule).getType());
    root.put("lexerDataTable", aliasMap.get(LexerAliasPrototype.lexerDataTable).getType());
    root.put("lexerMapTable",lexerMapTable);
    root.put("rules",lexerMapTable.getAutomataMap().keySet());
    generate(root,aliasMap,LexerAliasPrototype.rule);
    generate(root,aliasMap,LexerAliasPrototype.lexerDataTable);
  }
}

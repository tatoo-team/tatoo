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

public class SwitchLexerGenerator extends Generator {
  public SwitchLexerGenerator(File sourceDir,Set<File> classPath,File debugDir) {
    super(sourceDir,fr.umlv.tatoo.cc.lexer.generator.SwitchLexerGenerator.class,classPath,debugDir);
  }

  public void generate(GeneratorBean bean,LexerMap.Switch lexerMapSwitch)
      throws GeneratorException {
    
    Map<AliasPrototype,? extends Alias> aliasMap = bean.getAliasMap();
    HashMap<String,Object> root = new HashMap<String,Object>();
    root.put("ruleEnum", aliasMap.get(LexerAliasPrototype.rule).getType());
    root.put("lexerSwitch", aliasMap.get(LexerAliasPrototype.lexerSwitch).getType());
    
    root.put("lexerMapSwitch", lexerMapSwitch);
    root.put("rules", lexerMapSwitch.getAutomataMap().keySet());
    
    generate(root,aliasMap,LexerAliasPrototype.rule);
    generate(root,aliasMap,LexerAliasPrototype.lexerSwitch);
  }
}

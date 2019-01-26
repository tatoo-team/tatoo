package fr.umlv.tatoo.cc.lexer.main;

import fr.umlv.tatoo.cc.common.extension.ExtensionBus.DataKey;
import fr.umlv.tatoo.cc.lexer.lexer.LexerMap;
import fr.umlv.tatoo.cc.lexer.lexer.RuleFactory;

public class LexerDataKeys {
  public static final DataKey<RuleFactory> ruleFactory=
    new DataKey<RuleFactory>();
  public static final DataKey<LexerMap.Table> lexerMapTable=
    new DataKey<LexerMap.Table>();
  public static final DataKey<LexerMap.Switch> lexerMapSwitch=
    new DataKey<LexerMap.Switch>();
}

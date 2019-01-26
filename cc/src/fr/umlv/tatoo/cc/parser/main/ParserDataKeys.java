package fr.umlv.tatoo.cc.parser.main;

import fr.umlv.tatoo.cc.common.extension.ExtensionBus.DataKey;
import fr.umlv.tatoo.cc.parser.grammar.EBNFSupport;
import fr.umlv.tatoo.cc.parser.grammar.GrammarRepository;
import fr.umlv.tatoo.cc.parser.grammar.GrammarSets;
import fr.umlv.tatoo.cc.parser.table.ParserTableDecl;


public class ParserDataKeys {
  public static final DataKey<GrammarRepository> grammarRepository=
    new DataKey<GrammarRepository>();
  public static final DataKey<ParserTableDecl> parserTable=
    new DataKey<ParserTableDecl>();
  public static final DataKey<EBNFSupport> ebnfSupport=
    new DataKey<EBNFSupport>();
  public static final DataKey<GrammarSets> grammarSets=
    new DataKey<GrammarSets>();
}

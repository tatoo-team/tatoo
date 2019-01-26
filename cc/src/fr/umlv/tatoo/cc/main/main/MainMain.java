package fr.umlv.tatoo.cc.main.main;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import fr.umlv.tatoo.cc.Tatoo;
import fr.umlv.tatoo.cc.common.main.AliasPrototype;
import fr.umlv.tatoo.cc.common.main.CommandLineParser;
import fr.umlv.tatoo.cc.common.main.CommonOptions;
import fr.umlv.tatoo.cc.common.main.IllegalCommandLineArgumentException;
import fr.umlv.tatoo.cc.common.main.Unit;
import fr.umlv.tatoo.cc.lexer.main.LexerAliasPrototype;
import fr.umlv.tatoo.cc.lexer.main.LexerOption;
import fr.umlv.tatoo.cc.parser.main.ParserAliasPrototype;
import fr.umlv.tatoo.cc.parser.main.ParserOption;
import fr.umlv.tatoo.cc.tools.main.ToolsAliasPrototype;

public class MainMain {
  private static void usage(CommandLineParser<MainBean> clp) {
    System.err.println(clp.usage(
      "Usage: java fr.umlv.tatoo.cc.main.main.MainMain [options] files\n"+
      "  form1: MainMain [options] <lexer>.xlex\n"+
      "  form2: MainMain [options] <lexer>.xlex <grammar>.xpars\n"+
      "  form3: MainMain [options] <lexer>.xlex <grammar>.xpars <tools>.xtls\n"+
      "  form4: MainMain [options] file.ebnf\n"+
      "  form5: MainMain [options] file.ebnf <tools>.xtls\n"+
      "  "+Tatoo.version()+'\n'));
  }

  public static AliasPrototype[] mainAliases() {
    HashSet<AliasPrototype> aliases=new HashSet<AliasPrototype>();
    Collections.addAll(aliases,LexerAliasPrototype.lexers());
    Collections.addAll(aliases,ParserAliasPrototype.parsers());
    Collections.addAll(aliases,ToolsAliasPrototype.tools());
    return aliases.toArray(new AliasPrototype[aliases.size()]);
  }
  
  public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
    AliasPrototype[] aliases=mainAliases();
    
    @SuppressWarnings("unchecked")
    CommandLineParser<MainBean> clp=
      new CommandLineParser<MainBean>("--",
        CommonOptions.destination,
        CommonOptions.classpath,
        CommonOptions.packaze("lexer","parser","tools"),
        CommonOptions.validating,
        CommonOptions.logLevel,
        CommonOptions.extension,
        CommonOptions.check,
        CommonOptions.version,
        ParserOption.logFile,
        ParserOption.parserType,
        LexerOption.charset,
        LexerOption.generateSwitch,
        CommonOptions.name(aliases),
        CommonOptions.generate(aliases),
        CommonOptions.generatePrefix
        );
    
    if (args.length==0) {
      usage(clp);
      return;
    }
    
    MainBean bean=new MainBean();
    bean.registerDefaults(aliases);
    
    List<? extends String> filenames;
    try {
      filenames=clp.parse(bean,args);
    } catch (IllegalCommandLineArgumentException e) {
      e.printStackTrace();
      usage(clp);
      return;
    }
    bean.addInputFilenames(Unit.ebnf,filenames);
    bean.finish();
    
    MainBatch mainBatch = new MainBatch();
    mainBatch.execute(bean);
  }
}

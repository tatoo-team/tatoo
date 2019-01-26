package fr.umlv.tatoo.cc.lexer.main;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import fr.umlv.tatoo.cc.Tatoo;
import fr.umlv.tatoo.cc.common.main.AliasPrototype;
import fr.umlv.tatoo.cc.common.main.CommandLineParser;
import fr.umlv.tatoo.cc.common.main.CommonOptions;
import fr.umlv.tatoo.cc.common.main.IllegalCommandLineArgumentException;
import fr.umlv.tatoo.cc.common.main.Unit;

public class LexerMain {

  private static void usage(CommandLineParser<LexerBean> clp) {
    System.err.println(clp.usage(
      "Usage: java fr.umlv.tatoo.cc.lexer.main.ParserMain (options) grammar.xml\n"+
      "  "+Tatoo.version()+'\n'));
  }

  
  /**
   * @param args
   * @throws SAXException 
   * @throws ParserConfigurationException 
   * @throws IOException
   */
  public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
    AliasPrototype[] aliases=LexerAliasPrototype.lexers();
    
    @SuppressWarnings("unchecked")
    CommandLineParser<LexerBean> clp=
      new CommandLineParser<LexerBean>("--",
        CommonOptions.destination,
        CommonOptions.classpath,
        CommonOptions.packaze("lexer"),
        CommonOptions.validating,
        CommonOptions.logLevel,
        CommonOptions.extension,
        CommonOptions.check,
        CommonOptions.version,
        LexerOption.charset,
        LexerOption.generateSwitch,
        //LexerOption.name,
        CommonOptions.name(aliases),
        CommonOptions.generate(aliases),
        CommonOptions.generatePrefix
        );
    
    if (args.length==0) {
      usage(clp);
      return;
    }
    
    LexerBean bean=new LexerBean();
    bean.registerDefaults(aliases);
    
    List<? extends String> filenames;
    try {
      filenames=clp.parse(bean,args);
    } catch (IllegalCommandLineArgumentException e) {
      e.printStackTrace();
      usage(clp);
      return;
    }
    bean.addInputFilenames(Unit.lexer,filenames);
    bean.finish();
    LexerBatch batch = new LexerBatch();
    batch.execute(bean);
  }
}

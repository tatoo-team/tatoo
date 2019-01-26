package fr.umlv.tatoo.cc.parser.main;

import java.io.File;
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

public class LogTableMain {

  private static void usage(CommandLineParser<ParserBean> clp) {
    System.err.println(clp.usage(
      "Usage: java fr.umlv.tatoo.cc.parser.main.LogTableMain (options) grammar.xml\n"+
      "  "+Tatoo.version()+'\n'));
  }

  /**
   * @param args
   * @throws SAXException 
   * @throws ParserConfigurationException 
   * @throws IOException 
   */
  @SuppressWarnings("unchecked")
  public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
    AliasPrototype[] aliases=ParserAliasPrototype.parsers();
    
    CommandLineParser<ParserBean> clp=
      new CommandLineParser<ParserBean>("--",
        CommonOptions.validating,
        CommonOptions.logLevel,
        CommonOptions.check,
        CommonOptions.version,
        ParserOption.logFile,
        ParserOption.parserType
        );
    
    if (args.length==0) {
      usage(clp);
      return;
    }
    
    ParserBean bean=new ParserBean();
    bean.registerDefaults(aliases);
    
    // don't generate anything
    bean.setGenerateDefault(false);
    bean.setRegisterDefaultExtensions(false);
    
    List<? extends String> filenames;
    try {
      filenames=clp.parse(bean,args);
    } catch (IllegalCommandLineArgumentException e) {
      e.printStackTrace();
      usage(clp);
      return;
    }
    bean.addInputFilenames(Unit.parser,filenames);
    bean.finish();
    
    // create a log file if no log file is specified
    if (bean.getLogFile()==null)
      bean.setLogFile(new File(filenames.get(0)+".log"));
    ParserBatch batch = new ParserBatch();
    batch.execute(bean);
  }
}

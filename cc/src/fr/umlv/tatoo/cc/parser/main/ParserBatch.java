package fr.umlv.tatoo.cc.parser.main;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import fr.umlv.tatoo.cc.common.extension.ExtensionBus;
import fr.umlv.tatoo.cc.common.log.Info;
import fr.umlv.tatoo.cc.common.log.ReporterFactory;
import fr.umlv.tatoo.cc.common.main.Unit;
import fr.umlv.tatoo.cc.parser.generator.ParserExtension;
import fr.umlv.tatoo.cc.parser.grammar.EBNFSupport;
import fr.umlv.tatoo.cc.parser.grammar.GrammarFactory;
import fr.umlv.tatoo.cc.parser.grammar.TerminalDecl;
import fr.umlv.tatoo.cc.parser.parser.ActionDeclFactory;
import fr.umlv.tatoo.cc.parser.table.LogInfoConflictDiagnosisReporter;
import fr.umlv.tatoo.cc.parser.table.ParserTableDecl;
import fr.umlv.tatoo.cc.parser.xml.ParserXMLDigester;

public class ParserBatch {
  public ParserXMLDigester digest(List<? extends File> grammarFiles,boolean validating,GrammarFactory factory) throws IOException, ParserConfigurationException, SAXException {
    EBNFSupport ebnfSupport=new EBNFSupport(factory);
    ParserXMLDigester digester=new ParserXMLDigester(factory,ebnfSupport);
    for (File file : grammarFiles)
      digester.parse(file,validating);
    return digester;
  }
  
  public void execute(ParserBean bean) throws IOException, ParserConfigurationException, SAXException {
    if (bean.getLogLevel()!=null)
      ReporterFactory.setLogLevel(bean.getLogLevel());
    
    ExtensionBus extensionBus = bean.getExtensionBus();
    
    if (bean.isRegisterDefaultExtensions())
      extensionBus.register(new ParserExtension());
    
    List<? extends File> parserFiles=bean.getInputFiles(Unit.parser);
    GrammarFactory grammarFactory=new GrammarFactory();
    ParserXMLDigester digester = digest(parserFiles,bean.isValidating(),grammarFactory);
    
    // check
    Set<TerminalDecl> unused=grammarFactory.checkAndFixUnusedTerminal();
    if (!unused.isEmpty())
      Info.warning("unused though declared terminal", unused);
    
    extensionBus.publish(ParserDataKeys.grammarRepository,grammarFactory);
    
    ReporterFactory.setAndSealDefaultInfo(ReporterFactory.getDefaultInfo().file(parserFiles));
    ParserTableDecl table=digester.createParserTableDecl(extensionBus,new ActionDeclFactory(),
      bean.getParserType().getMethod(),
      bean.getConflictResolverType().getConflictResolver(),
      new LogInfoConflictDiagnosisReporter(null),
      bean.getLogFile());
    extensionBus.publish(ParserDataKeys.parserTable,table);
  }
}

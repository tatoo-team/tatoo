package fr.umlv.tatoo.cc.parser.main;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import fr.umlv.tatoo.cc.common.main.AbstractTask;
import fr.umlv.tatoo.cc.common.main.AliasPrototype;
import fr.umlv.tatoo.cc.common.main.Unit;


/** 
 * Main of the parser generator.
 * 
 * @author remi
 *
 */
public class ParserTask extends AbstractTask<ParserBean> {

  protected ParserTask(AliasPrototype[] aliases) {
    super(new ParserBean(),aliases);
  }
  
  public ParserTask() {
    this(ParserAliasPrototype.parsers());
  }
  
  public void setParserFile(File parserFile) {
    bean().addInputFile(Unit.parser,parserFile);
  }
  
  @Override
  protected Unit getDefaultUnit() {
    return Unit.parser;
  }
  
  public void setLogFile(File file) {
    bean().setLogFile(file);
  }
  
  public void setParserType(String parserType) {
    bean().setParserType(ParserType.valueOf(parserType));
  }
  
  public void setConflictResolver(String conflictResolverType) {
    bean().setConflictResolverType(ConflictResolverType.valueOf(conflictResolverType.toUpperCase()));
  }
  
  public class Package extends AbstractTask.Package {
    public void setParser(String packageRoot) {
      bean().setPackage(Unit.parser,packageRoot);
    }
  }
  
  @Override public Package createPackage() {
    return new Package();
  }
  
  public void execute() throws IOException, ParserConfigurationException, SAXException {
    bean().finish();
    ParserBatch batch = new ParserBatch();
    batch.execute(bean());
  }
}

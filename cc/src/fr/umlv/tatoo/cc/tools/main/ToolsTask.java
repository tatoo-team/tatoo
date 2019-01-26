package fr.umlv.tatoo.cc.tools.main;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import fr.umlv.tatoo.cc.common.main.AbstractTask;
import fr.umlv.tatoo.cc.common.main.Unit;


/** Task of the parser generator.
 * 
 * @author Remi
 *
 */
public class ToolsTask extends AbstractTask<ToolsBean> {

  public ToolsTask() {
    super(new ToolsBean(),ToolsAliasPrototype.tools());
  }
  
  public void setLexerFile(File lexerFile) {
    bean().addInputFile(Unit.lexer,lexerFile);
  }
  
  public void setParserFile(File parserFile) {
    bean().addInputFile(Unit.parser,parserFile);
  }
  
  
  public void setToolsFile(File toolFile) {
    bean().addInputFile(Unit.tools,toolFile);
  }
  
  @Override
  protected Unit getDefaultUnit() {
    return Unit.tools;
  }
  
  public void setGenerateAST(boolean generateAST) {
    bean().setGenerateAST(generateAST);
  }
  
  public class Package extends AbstractTask.Package {
    public void setLexer(String packageRoot) {
      bean().setPackage(Unit.lexer,packageRoot);
    }
    public void setParser(String packageRoot) {
      bean().setPackage(Unit.parser,packageRoot);
    }
    public void setTools(String packageRoot) {
      bean().setPackage(Unit.tools,packageRoot);
    }
    public void setAST(String packageRoot) {
      bean().setPackage(Unit.ast,packageRoot);
    }
  }
  
  @Override public Package createPackage() {
    return new Package();
  }
  
  public void execute() throws IOException, ParserConfigurationException, SAXException {
    bean().finish();
    
    ToolsBatch toolBatch = new ToolsBatch();
    toolBatch.execute(bean());
  }
}

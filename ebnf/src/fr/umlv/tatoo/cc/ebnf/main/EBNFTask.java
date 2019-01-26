package fr.umlv.tatoo.cc.ebnf.main;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import fr.umlv.tatoo.cc.common.main.AbstractTask;
import fr.umlv.tatoo.cc.common.main.Unit;
import fr.umlv.tatoo.cc.lexer.main.LexerType;
import fr.umlv.tatoo.cc.main.main.MainBatch;
import fr.umlv.tatoo.cc.main.main.MainBean;
import fr.umlv.tatoo.cc.main.main.MainMain;
import fr.umlv.tatoo.cc.parser.main.ConflictResolverType;
import fr.umlv.tatoo.cc.parser.main.ParserType;


/** Task of the EBNF.
 * 
 * @author Remi
 *
 */
public class EBNFTask extends AbstractTask<MainBean> {
  public EBNFTask() {
    super(new MainBean(),MainMain.mainAliases());
  }
  
  public void setEBNFFile(File ebnfFile) {
    bean().addInputFile(Unit.ebnf,ebnfFile);
  }
  
  public void setLexerFile(File lexerFile) {
    bean().addInputFile(Unit.lexer,lexerFile);
  }
  
  public void setParserFile(File parserFile) {
    bean().addInputFile(Unit.parser,parserFile);
  }
  
  public void setToolsFile(File toolsFile) {
    bean().addInputFile(Unit.tools,toolsFile);
  }
  
  @Override
  protected Unit getDefaultUnit() {
    return null; // all units
  }
  @Override
  protected void registerInputFile(Unit unit, File inputFile) {
    super.registerInputFile(unit, inputFile);
  }
  
  public void setLexerType(String type) {
    bean().setLexerType(LexerType.valueOf(type));
  }
  
  public void setGenerateLexerSwitch(boolean generateLexerSwitch) {
    bean().setGenerateLexerSwitch(generateLexerSwitch);
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
    
    MainBatch mainBatch = new MainBatch();
    mainBatch.execute(bean());
  }
}

package fr.umlv.tatoo.cc.lexer.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import fr.umlv.tatoo.cc.common.main.AbstractTask;
import fr.umlv.tatoo.cc.common.main.Unit;

public class LexerTask extends AbstractTask<LexerBean> {
  public LexerTask() {
    super(new LexerBean(),LexerAliasPrototype.lexers());
  }
  
  public void setLexerType(String type) {
    bean().setLexerType(LexerType.valueOf(type));
  }
  
  public void setGenerateLexerSwitch(boolean generateLexerSwitch) {
    bean().setGenerateLexerSwitch(generateLexerSwitch);
  }
  
  @Override
  protected Unit getDefaultUnit() {
    return Unit.lexer;
  }
  
  public void setLexerFile(File lexerFile) {
    bean().addInputFile(Unit.lexer,lexerFile);
  }
  
  public class Package extends AbstractTask.Package {
    public void setLexer(String packageRoot) {
      bean().setPackage(Unit.lexer,packageRoot);
    }
  }
  
  @Override public Package createPackage() {
    return new Package();
  }
  
  public void execute() throws FileNotFoundException, ParserConfigurationException, SAXException, IOException {
    bean().finish();
    LexerBatch batch = new LexerBatch();
    batch.execute(bean());
  }
}

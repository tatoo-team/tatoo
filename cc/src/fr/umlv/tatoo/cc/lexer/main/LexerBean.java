package fr.umlv.tatoo.cc.lexer.main;

import java.io.File;

import fr.umlv.tatoo.cc.common.main.GeneratorBean;
import fr.umlv.tatoo.cc.lexer.main.LexerOption.LexerParam;

public class LexerBean extends GeneratorBean implements LexerParam {
  public LexerBean() {
    setDestination(new File("."));
    setLexerType(LexerType.unicode);
  }
  
  public LexerType getLexerType() {
    return lexerType;
  }
  public void setLexerType(LexerType lexerType) {
    this.lexerType = lexerType;
  }
  public boolean isGenerateLexerSwitch() {
    return generateLexerSwitch;
  }
  public void setGenerateLexerSwitch(boolean generateLexerSwitch) {
    this.generateLexerSwitch=generateLexerSwitch;
  }
  
  private LexerType lexerType;
  private boolean generateLexerSwitch;  
}

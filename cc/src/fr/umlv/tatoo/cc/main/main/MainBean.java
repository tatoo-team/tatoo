/*
 * Created on 13 nov. 2005
 *
 */
package fr.umlv.tatoo.cc.main.main;

import java.io.File;

import fr.umlv.tatoo.cc.common.main.GeneratorBean;
import fr.umlv.tatoo.cc.lexer.main.LexerType;
import fr.umlv.tatoo.cc.lexer.main.LexerOption.LexerParam;
import fr.umlv.tatoo.cc.parser.main.ConflictResolverType;
import fr.umlv.tatoo.cc.parser.main.ParserParam;
import fr.umlv.tatoo.cc.parser.main.ParserType;
import fr.umlv.tatoo.cc.tools.main.ToolsParam;

public class MainBean extends GeneratorBean implements LexerParam, ParserParam, ToolsParam {
  public MainBean() {
    setDestination(new File("."));
    setLexerType(LexerType.unicode);
    setParserType(ParserType.slr);
    setConflictResolverType(ConflictResolverType.DEFAULT);
  }
  
  public LexerType getLexerType() {
    return lexerType;
  }
  public void setLexerType(LexerType lexerType) {
    this.lexerType = lexerType;
  }
  public File getLogFile() {
    return logFile;
  }
  public void setLogFile(File logFile) {
    this.logFile = logFile;
  }
  public ParserType getParserType() {
    return parserType;
  }
  public void setParserType(ParserType parserType) {
    this.parserType = parserType;
  }
  public ConflictResolverType getConflictResolverType() {
    return conflictResolverType;
  }
  public void setConflictResolverType(ConflictResolverType conflictResolverType) {
    this.conflictResolverType=conflictResolverType;
  }
  public boolean isGenerateAST() {
    return generateAST;
  }
  public void setGenerateAST(boolean generateAST) {
    this.generateAST = generateAST;
  }
  public boolean isGenerateLexerSwitch() {
    return generateLexerSwitch;
  }
  public void setGenerateLexerSwitch(boolean generateLexerSwitch) {
    this.generateLexerSwitch=generateLexerSwitch;
  }
  
  private LexerType lexerType;
  private ParserType parserType;
  private ConflictResolverType conflictResolverType;
  private boolean generateAST;
  private boolean generateLexerSwitch;
  private File logFile;
}

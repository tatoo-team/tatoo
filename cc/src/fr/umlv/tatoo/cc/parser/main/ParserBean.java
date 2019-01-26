package fr.umlv.tatoo.cc.parser.main;

import java.io.File;

import fr.umlv.tatoo.cc.common.main.GeneratorBean;

public class ParserBean extends GeneratorBean implements ParserParam {
  public ParserBean() {
    setDestination(new File("."));
    setParserType(ParserType.slr);
    setConflictResolverType(ConflictResolverType.DEFAULT);
  }
  
  public File getLogFile() {
    return logFile;
  }
  
  public void setLogFile(File file) {
    this.logFile=file;
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
  
  private File logFile;
  private ParserType parserType;
  private ConflictResolverType conflictResolverType;
}
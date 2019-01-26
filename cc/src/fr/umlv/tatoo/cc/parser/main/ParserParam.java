/*
 * Created on 13 nov. 2005
 *
 */
package fr.umlv.tatoo.cc.parser.main;

import java.io.File;

public interface ParserParam {
  public void setLogFile(File logFile);
  public void setParserType(ParserType parserType);
  public void setConflictResolverType(ConflictResolverType conflictResolverType);
}
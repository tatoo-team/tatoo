package fr.umlv.tatoo.cc.parser.grammar;

import java.io.File;

import fr.umlv.tatoo.cc.common.extension.ExtensionBus;
import fr.umlv.tatoo.cc.parser.parser.ActionDeclFactory;
import fr.umlv.tatoo.cc.parser.table.AbstractConflictDiagnosisReporter;
import fr.umlv.tatoo.cc.parser.table.ConflictResolverPolicy;
import fr.umlv.tatoo.cc.parser.table.ParserTableDecl;
import fr.umlv.tatoo.cc.parser.table.TableFactoryMethod;

public interface ParserTableBuilder {
  /** True if the parser table builder is not complete.
   * @return true if the parser table builder is not complete.
   */
  public boolean isFatalError();
  
  /** Creates a parser table.
   * 
   * @param actionFactory a factory that numbering actions by type (shift, reduce etc.)
   * @param method the method used to create the table SLR, LR, LALR.
   * @param conflictResolver the conflict resolver.
   * @param reporter the conflict reporter used to report conflicts
   * @param log the log file or null.
   * 
   * @return a parser table.
   */
  public ParserTableDecl createParserTableDecl(ExtensionBus extensionBus, ActionDeclFactory actionFactory,
      TableFactoryMethod<?> method, ConflictResolverPolicy conflictResolver,
      AbstractConflictDiagnosisReporter reporter,File log);
}
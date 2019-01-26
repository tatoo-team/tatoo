package fr.umlv.tatoo.cc.tools.main;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import fr.umlv.tatoo.cc.common.log.Info;
import fr.umlv.tatoo.cc.common.log.ReporterFactory;
import fr.umlv.tatoo.cc.common.main.Unit;
import fr.umlv.tatoo.cc.lexer.charset.encoding.Encoding;
import fr.umlv.tatoo.cc.lexer.charset.encoding.UTF16Encoding;
import fr.umlv.tatoo.cc.lexer.lexer.RuleDecl;
import fr.umlv.tatoo.cc.lexer.lexer.RuleFactory;
import fr.umlv.tatoo.cc.lexer.main.LexerBatch;
import fr.umlv.tatoo.cc.lexer.main.LexerDataKeys;
import fr.umlv.tatoo.cc.parser.grammar.GrammarFactory;
import fr.umlv.tatoo.cc.parser.grammar.TerminalDecl;
import fr.umlv.tatoo.cc.parser.main.ParserBatch;
import fr.umlv.tatoo.cc.parser.main.ParserDataKeys;
import fr.umlv.tatoo.cc.parser.xml.ParserXMLDigester;
import fr.umlv.tatoo.cc.tools.ast.generator.ASTExtension;
import fr.umlv.tatoo.cc.tools.generator.ToolsExtension;
import fr.umlv.tatoo.cc.tools.tools.RuleInfo;
import fr.umlv.tatoo.cc.tools.tools.ToolsChecker;
import fr.umlv.tatoo.cc.tools.tools.ToolsFactory;
import fr.umlv.tatoo.cc.tools.xml.ToolXMLDigester;

public class ToolsBatch {
  public ToolXMLDigester digest(List<? extends File> toolFiles, boolean validating,RuleFactory lexerFactory, GrammarFactory grammarFactory,ToolsFactory toolsFactory) throws IOException, ParserConfigurationException, SAXException {
    ToolXMLDigester toolDigester=new ToolXMLDigester(
        lexerFactory.getRuleMap(),grammarFactory.getVariableMap(),toolsFactory);
    for (File file: toolFiles)
      toolDigester.parse(file,validating);
    return toolDigester;
  }
  
  public void execute(ToolsBean bean)
    throws IOException, ParserConfigurationException, SAXException {
    
    if (bean.getLogLevel()!=null)
      ReporterFactory.setLogLevel(bean.getLogLevel());
    
    if (bean.isRegisterDefaultExtensions())
      if (bean.isGenerateAST())
        bean.getExtensionBus().register(new ASTExtension());
      else
        bean.getExtensionBus().register(new ToolsExtension());
    
    
    boolean validating=bean.isValidating();
    
    // lexer
    Encoding charset = UTF16Encoding.getInstance();
    LexerBatch lexerBatch=new LexerBatch();
    RuleFactory ruleFactory=new RuleFactory();
    lexerBatch.digest(bean.getInputFiles(Unit.lexer),validating,ruleFactory,charset);
    bean.getExtensionBus().publish(LexerDataKeys.ruleFactory,ruleFactory);
    
    // parser
    ParserBatch parserBatch=new ParserBatch();
    GrammarFactory grammarFactory=new GrammarFactory();
    ParserXMLDigester parserDigester = parserBatch.digest(bean.getInputFiles(Unit.parser),validating,grammarFactory);
    bean.getExtensionBus().publish(ParserDataKeys.grammarRepository,grammarFactory);
    bean.getExtensionBus().publish(ParserDataKeys.ebnfSupport,parserDigester.getEBNFSupport());
    
    // tools
    ToolsFactory toolsFactory=new ToolsFactory();
    digest(bean.getInputFiles(Unit.tools),validating,ruleFactory,grammarFactory,toolsFactory);
    
    Map<RuleDecl,RuleInfo> ruleInfoMap = toolsFactory.getRuleInfoMap();
    Set<RuleDecl> rules=ToolsChecker.checkUndefinedRules(ruleFactory,ruleInfoMap);
    if (!rules.isEmpty()) {
      Info.warning("Undefined rule infos for rules %s",rules).report();
    }
    Set<TerminalDecl> unspawnedTerminals=ToolsChecker.checkUnspawnTerminals(grammarFactory,ruleInfoMap);
    if (!unspawnedTerminals.isEmpty()) {
      Info.warning("Unspawned terminals %s",unspawnedTerminals).report();
    }
    bean.getExtensionBus().publish(ToolsDataKeys.toolsFactory,toolsFactory);
  }
}

package fr.umlv.tatoo.cc.ebnf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import fr.umlv.tatoo.cc.common.generator.Type;
import fr.umlv.tatoo.cc.common.log.Info;
import fr.umlv.tatoo.cc.ebnf.ast.ASTGrammarEvaluator;
import fr.umlv.tatoo.cc.ebnf.ast.ASTTerminalEvaluator;
import fr.umlv.tatoo.cc.ebnf.ast.TreeFactory;
import fr.umlv.tatoo.cc.lexer.charset.encoding.Encoding;
import fr.umlv.tatoo.cc.lexer.ebnf.tools.Analyzers;
import fr.umlv.tatoo.cc.lexer.lexer.RuleFactory;
import fr.umlv.tatoo.cc.main.main.EBNFParser;
import fr.umlv.tatoo.cc.parser.grammar.EBNFSupport;
import fr.umlv.tatoo.cc.parser.grammar.GrammarFactory;
import fr.umlv.tatoo.cc.parser.grammar.ParserTableBuilder;
import fr.umlv.tatoo.cc.parser.grammar.TerminalDecl;
import fr.umlv.tatoo.cc.tools.tools.ToolsFactory;
import fr.umlv.tatoo.runtime.buffer.impl.LocationTracker;
import fr.umlv.tatoo.runtime.buffer.impl.ReaderWrapper;

public class EBNFParserImpl extends EBNFParser {
  private final RuleFactory ruleFactory;
  private final Encoding encoding;
  private final GrammarFactory grammarFactory;
  private final EBNFSupport ebnfSupport;
  private final ToolsFactory toolsFactory;
  private final Map<String,Type> attributeMap;
  private final Analysis analysis = new Analysis();
  
  public EBNFParserImpl(RuleFactory ruleFactory,Encoding encoding,GrammarFactory grammarFactory,EBNFSupport ebnfSupport,ToolsFactory toolsFactory,Map<String,Type> attributeMap) {
    this.ruleFactory=ruleFactory;
    this.encoding=encoding;
    this.grammarFactory=grammarFactory;
    this.ebnfSupport=ebnfSupport;
    this.toolsFactory=toolsFactory;
    this.attributeMap=attributeMap;
  }
  
  @Override
  public ParserTableBuilder parse(File source) throws FileNotFoundException {
    return parse(new FileReader(source));
  }
  
  @Override
  public ParserTableBuilder parse(Reader reader)  {
    LocationTracker locationTracker=new LocationTracker();
    ReaderWrapper buffer=new ReaderWrapper(reader,locationTracker);
    
    EBNFASTImpl ast=new EBNFASTImpl();
    EBNFAnnotationComputer locationComputer=new EBNFAnnotationComputer(locationTracker);
    TreeFactory treeFactory=new TreeFactory(ast,locationComputer);
    
    ASTTerminalEvaluator terminalEvaluator=new ASTTerminalEvaluator(treeFactory);
    ASTGrammarEvaluator grammarEvaluator=new ASTGrammarEvaluator(treeFactory);
    
    try {
      Analyzers.run(buffer,terminalEvaluator,grammarEvaluator,null,null);
    } catch(RuntimeException e) {
      Info.error(e.getMessage()).cause(e).
        line(locationTracker.getLineNumber()).column(locationTracker.getColumnNumber()).
        report();
      throw e;
    }
    /*
    LexerBuilder<RuleEnum,ReaderWrapper> builder = 
      LexerBuilder.createReaderBuilder(null,reader);
    
    LexerAndParser<ReaderWrapper,TerminalEnum,NonTerminalEnum,ProductionEnum,VersionEnum> lexerAndParser=
      AnalyzerBuilder.createTokenBufferAnalyzer(builder, grammarEvaluator, terminalEvaluator).createLexerAndParser();
    
    SimpleParser<?> parser=lexerAndParser.getParser();
    lexerAndParser.getLexer().run();
    */
    
    boolean onError=analysis.analyse(ast,ruleFactory,encoding,grammarFactory,ebnfSupport,toolsFactory,attributeMap);
    
    return new EBNFParserTableBuilder(grammarFactory,ebnfSupport,onError);
  }
}

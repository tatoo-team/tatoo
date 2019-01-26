package fr.umlv.tatoo.samples.java.javac;

import java.util.EnumMap;

import com.sun.tools.javac.code.Source;
import com.sun.tools.javac.tree.JCTree.JCCompilationUnit;
import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.tree.JCTree.JCStatement;

import fr.umlv.tatoo.runtime.buffer.impl.CharSequenceWrapper;
import fr.umlv.tatoo.runtime.lexer.Lexer;
import fr.umlv.tatoo.runtime.parser.Parser;
import fr.umlv.tatoo.runtime.tools.builder.LexerAndParser;
import fr.umlv.tatoo.samples.java.parser.NonTerminalEnum;
import fr.umlv.tatoo.samples.java.parser.ProductionEnum;
import fr.umlv.tatoo.samples.java.parser.TerminalEnum;
import fr.umlv.tatoo.samples.java.parser.VersionEnum;
import fr.umlv.tatoo.samples.java.tools.Analyzers;

public class TatooParser implements com.sun.tools.javac.parser.Parser {
  private final Lexer<CharSequenceWrapper> lexer;
  private final Parser<TerminalEnum,NonTerminalEnum,ProductionEnum,VersionEnum> parser;
  private final TreeGrammarEvaluator grammarEvaluator;
  private final PositionStack positionStack;
  
  TatooParser(TatooParserFactory factory, CharSequence input, boolean keepDocComments, boolean keepEndPos, boolean keepLineMap) {
 
    PositionStack positionStack;
    if (keepEndPos) {
      positionStack=new PositionStack.StartEnd();
    } else {
      positionStack=new PositionStack.Start();
    }
    
    TreeGrammarEvaluator grammarEvaluator=new TreeGrammarEvaluator(factory.F, factory.names, positionStack, factory.log);
    
    PositionTrackerAnalyzerProcessor analyzerProcessor=new PositionTrackerAnalyzerProcessor(
      new TreeTerminalEvaluator(),
      grammarEvaluator,
      positionStack
      );
    
    VersionEnum version=getVersion(factory.source);
    
    CharSequenceWrapper buffer=new CharSequenceWrapper(input, null);
    LexerAndParser<CharSequenceWrapper,TerminalEnum,NonTerminalEnum,ProductionEnum,VersionEnum> lexerAndParser = 
      Analyzers.analyzerBuilder().
        buffer(buffer).
        listener(analyzerProcessor).
        version(version).
        createAnalyzer();
    
    this.positionStack=positionStack;
    this.grammarEvaluator=grammarEvaluator;
    lexer=lexerAndParser.getLexer();
    parser=lexerAndParser.getParser();
  }
  
  private static VersionEnum getVersion(Source source) {
    return versionMap.get(source);
  }
  
  private static final EnumMap<Source,VersionEnum> versionMap;
  static {
    versionMap=new EnumMap<Source, VersionEnum>(Source.class);
    versionMap.put(Source.JDK1_2, VersionEnum.jdk1_2);
    versionMap.put(Source.JDK1_3, VersionEnum.jdk1_3);
    versionMap.put(Source.JDK1_4, VersionEnum.jdk1_4);
    versionMap.put(Source.JDK1_5, VersionEnum.jdk1_5);
    versionMap.put(Source.JDK1_6, VersionEnum.jdk1_6);
    versionMap.put(Source.JDK1_7, VersionEnum.jdk1_7);
  }

  @Override
  public JCCompilationUnit parseCompilationUnit() {
    positionStack.clear();
    parser.reset(NonTerminalEnum.CompilationUnit);
    lexer.run();
    
    JCCompilationUnit compilationUnitRoot=grammarEvaluator.getCompilationUnitRoot();
    
    //DEBUG
    /*
    Pretty pretty=new Pretty(new PrintWriter(System.err), true);
    compilationUnitRoot.accept(pretty);
    */
    
    return compilationUnitRoot;
  }

  @Override
  public JCExpression parseExpression() {
    positionStack.clear();
    parser.reset(NonTerminalEnum.Expression);
    lexer.run();
    return grammarEvaluator.getExpressionRoot();
  }

  @Override
  public JCStatement parseStatement() {
    positionStack.clear();
    parser.reset(NonTerminalEnum.Statement);
    lexer.run();
    return grammarEvaluator.getStatementRoot();
  }

  @Override
  public JCExpression parseType() {
    positionStack.clear();
    parser.reset(NonTerminalEnum.Type);
    lexer.run();
    return grammarEvaluator.getTypeRoot();
  }
}

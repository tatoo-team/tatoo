<%@page import="fr.umlv.tatoo.cc.common.generator.ObjectId"%>
<%@page import="fr.umlv.tatoo.cc.common.generator.Type"%>
<%!/*PARAM*/
Type analyzers;
Type ruleEnum;
Type terminalEnum;
Type nonTerminalEnum;
Type productionEnum;
Type versionEnum;
Type grammarEvaluator;
Type terminalEvaluator;
Type lexerDataTable;
Type parserDataTable;
Type toolsDataTable;
Type analyzerProcessor;
ObjectId defaultStart;
ObjectId defaultVersion;
%>
<%!/*COMMENT*/
java.io.PrintWriter out;
%>

package <%=analyzers.getPackageName()%>;

import java.io.InputStreamReader;
import java.io.Reader;

import <%=ruleEnum.getRawName()%>;
import <%=terminalEnum.getRawName()%>;
import <%=nonTerminalEnum.getRawName()%>;
import <%=productionEnum.getRawName()%>;
import <%=versionEnum.getRawName()%>;
import <%=lexerDataTable.getRawName()%>;
import <%=parserDataTable.getRawName()%>;
import <%=toolsDataTable.getRawName()%>;
import <%=grammarEvaluator.getRawName()%>;
import <%=terminalEvaluator.getRawName()%>;

import fr.umlv.tatoo.runtime.buffer.LexerBuffer;
import fr.umlv.tatoo.runtime.buffer.TokenBuffer;
import fr.umlv.tatoo.runtime.buffer.impl.LocationTracker;
import fr.umlv.tatoo.runtime.buffer.impl.ReaderWrapper;
import fr.umlv.tatoo.runtime.lexer.LexerTable;
import fr.umlv.tatoo.runtime.parser.ParserTable;
import fr.umlv.tatoo.runtime.tools.DataViewer;
import fr.umlv.tatoo.runtime.tools.Debug;
import fr.umlv.tatoo.runtime.tools.SemanticStack;
import fr.umlv.tatoo.runtime.tools.ToolsTable;
import fr.umlv.tatoo.runtime.tools.builder.Builder;

/** Helper methods that can be used to run a couple lexer/parser on a text.
 *
 *  This class is generated - please do not edit it 
 */
public class <%=analyzers.getSimpleName()%> {
  /**
   * Runs the analyzer (lexer+parser) on a reader and print recognized tokens and
   * applied parser rules on error input (see {@link Debug}).
   * @param reader the source of standard input if null
   * @param terminalEvaluator the terminal evaluator or just method call printer if null
   * @param grammarEvaluator the grammar evaluator or just method call printer if null
   * @param start the start or default start if null
   * @param version the version of default version if null
   */
  public static void runDebug(Reader reader,
    <%=terminalEvaluator.getSimpleName()%><%="<"%>? super CharSequence> terminalEvaluator,
    <%=grammarEvaluator.getSimpleName()%> grammarEvaluator,
    <%=nonTerminalEnum.getSimpleName()%> start,
    <%=versionEnum.getSimpleName()%> version) {
    if (reader==null)
      reader=new InputStreamReader(System.in);
    @SuppressWarnings("unchecked") <%=terminalEvaluator.getSimpleName()%><%="<"%>CharSequence> debugTerminalEvaluator =
      Debug.createTraceProxy(<%=terminalEvaluator.getSimpleName()%>.class,terminalEvaluator);
    <%=grammarEvaluator.getSimpleName()%> debugGrammarEvaluator = Debug.createTraceProxy(<%=grammarEvaluator.getSimpleName()%>.class,grammarEvaluator);
    run(reader,debugTerminalEvaluator,debugGrammarEvaluator,
        start,version);
  }

  /** Runs the analyzer (lexer+parser) on a reader and sends recognized tokens
   *  as CharSequence. Tokens are transformed to objects by the terminal evaluator.
   *  At last, the grammar evaluator is called with these objects.
   *  
   *  This implementation uses a {@link fr.umlv.tatoo.runtime.buffer.impl.ReaderWrapper}
   *  configured with a location tracker as buffer and calls.
   *  
   * @param reader a reader used to obtain the characters of the text to parse.
   * @param terminalEvaluator an interface that returns the value of a token.
   * @param grammarEvaluator an interface that evaluates the grammar productions. 
   * @param start a start non terminal of the grammar used as root state of the parser.
   *    If start is null,
   *    the {@link fr.umlv.tatoo.runtime.parser.ParserTable#getDefaultStart() default start}
   *    non terminal is used.
   * @param version a version of the grammar used to parse the reader.
   *    If version is null,
   *    the {@link fr.umlv.tatoo.runtime.parser.ParserTable#getDefaultVersion() default version}
   *    of the grammar is used.
   *    
   * @see #run(TokenBuffer, <%=terminalEvaluator.getSimpleName()%>, <%=grammarEvaluator.getSimpleName()%>, <%=nonTerminalEnum.getSimpleName()%>, <%=versionEnum.getSimpleName()%>)
   */
  public static void run(
    Reader reader,
    <%=terminalEvaluator.getSimpleName()%><%="<" %>? super CharSequence> terminalEvaluator,
    <%=grammarEvaluator.getSimpleName()%> grammarEvaluator,
    <%=nonTerminalEnum.getSimpleName()%> start,
    <%=versionEnum.getSimpleName()%> version) {

    run(new ReaderWrapper(reader, new LocationTracker()), terminalEvaluator, grammarEvaluator, start, version);
  }
  
  public static <%="<"%>B extends TokenBuffer<%="<"%>D>&LexerBuffer,D> void runDebug(
    B tokenBuffer,
    <%=terminalEvaluator.getSimpleName()%><%="<"%>? super D> terminalEvaluator,
    <%=grammarEvaluator.getSimpleName()%> grammarEvaluator,
    <%=nonTerminalEnum.getSimpleName()%> start,
    <%=versionEnum.getSimpleName()%> version) {
      @SuppressWarnings("unchecked") <%=terminalEvaluator.getSimpleName()%><%="<"%>? super D> debugTerminalEvaluator =
      Debug.createTraceProxy(<%=terminalEvaluator.getSimpleName()%>.class,terminalEvaluator);
    <%=grammarEvaluator.getSimpleName()%> debugGrammarEvaluator = Debug.createTraceProxy(<%=grammarEvaluator.getSimpleName()%>.class,grammarEvaluator);
    run(tokenBuffer,debugTerminalEvaluator,debugGrammarEvaluator,
        start,version);
    }
  
  /** Runs the analyzer (lexer+parser) on a token buffer and sends recognized tokens
   *  as CharSequence. Tokens are transformed to objects by the terminal evaluator.
   *  At last, the grammar evaluator is called with these objects.
   *  
   *  It is up to the caller to create its buffer and to provide or not a location tracker.
   *
   * @param <%="<"%>B> type of the buffer.
   *  
   * @param tokenBuffer the token buffer used to obtain the characters of the text to parse.
   * @param terminalEvaluator an interface that returns the value of a token.
   * @param grammarEvaluator an interface that evaluates the grammar productions. 
   * @param start a start non terminal of the grammar used as root state of the parser.
   *    If start is null,
   *    the {@link fr.umlv.tatoo.runtime.parser.ParserTable#getDefaultStart() default start}
   *    non terminal is used.
   * @param version a version of the grammar used to parse the reader.
   *    If version is null,
   *    the {@link fr.umlv.tatoo.runtime.parser.ParserTable#getDefaultVersion() default version}
   *    of the grammar is used.
   *
   * @see #run(Reader, <%=terminalEvaluator.getSimpleName()%>, <%=grammarEvaluator.getSimpleName()%>, <%=nonTerminalEnum.getSimpleName()%>, <%=versionEnum.getSimpleName()%>)
   */
  public static <%="<"%>B extends TokenBuffer<%="<"%>D>&LexerBuffer,D> void run(
    B tokenBuffer,
    <%=terminalEvaluator.getSimpleName()%><%="<"%>? super D> terminalEvaluator,
    <%=grammarEvaluator.getSimpleName()%> grammarEvaluator,
    <%=nonTerminalEnum.getSimpleName()%> start,
    <%=versionEnum.getSimpleName()%> version) {
  
    analyzerTokenBufferBuilder(tokenBuffer,terminalEvaluator,grammarEvaluator,new SemanticStack()).
      start(start).version(version).createLexer().run();
  }

  public static Builder.LexerTableBuilder<<%=ruleEnum.getSimpleName()%>> lexerBuilder() {
    return Builder.lexer(<%=lexerDataTable.getSimpleName()%>.createTable());
  }
  
  public static Builder.ParserTableBuilder<<%=terminalEnum.getSimpleName()%>,<%=nonTerminalEnum.getSimpleName()%>,<%=productionEnum.getSimpleName()%>,<%=versionEnum.getSimpleName()%>> parserBuilder() {
    return Builder.parser(<%=parserDataTable.getSimpleName()%>.createTable());
  }
  
  public static Builder.AnalyzerTableBuilder<<%=ruleEnum.getSimpleName()%>,<%=terminalEnum.getSimpleName()%>,<%=nonTerminalEnum.getSimpleName()%>,<%=productionEnum.getSimpleName()%>,<%=versionEnum.getSimpleName()%>> analyzerBuilder() {
    return Builder.analyzer(LEXER_TABLE,PARSER_TABLE,TOOLS_TABLE);
  }
  
  public static <B extends LexerBuffer> Builder.AnalyzerBuilder<<%=ruleEnum.getSimpleName()%>,B,<%=terminalEnum.getSimpleName()%>,<%=nonTerminalEnum.getSimpleName()%>,<%=productionEnum.getSimpleName()%>,<%=versionEnum.getSimpleName()%>> analyzerLexerBufferBuilder(B lexerBuffer,
    <%=terminalEvaluator.getSimpleName()%><%="<? super B>"%> terminalEvaluator, <%=grammarEvaluator.getSimpleName()%> grammarEvaluator,
    SemanticStack semanticStack) {
    return analyzerBuilder().buffer(lexerBuffer).listener(<%=analyzerProcessor.getSimpleName()%>.<B,B>create<%=analyzerProcessor.getSimpleName()%>(terminalEvaluator,grammarEvaluator,
      DataViewer.<B>getIdentityDataViewer(),semanticStack));
  }
  
  public static <B extends TokenBuffer<D>&LexerBuffer,D> Builder.AnalyzerBuilder<<%=ruleEnum.getSimpleName()%>,B,<%=terminalEnum.getSimpleName()%>,<%=nonTerminalEnum.getSimpleName()%>,<%=productionEnum.getSimpleName()%>,<%=versionEnum.getSimpleName()%>> analyzerTokenBufferBuilder(B tokenBuffer,
    <%=terminalEvaluator.getSimpleName()%><%="<? super D>"%> terminalEvaluator, <%=grammarEvaluator.getSimpleName()%> grammarEvaluator,
    SemanticStack semanticStack) {
    return analyzerBuilder().buffer(tokenBuffer).listener(<%=analyzerProcessor.getSimpleName()%>.<B,D>create<%=analyzerProcessor.getSimpleName()%>(terminalEvaluator,grammarEvaluator,
      DataViewer.<D>getTokenBufferViewer(),semanticStack));
  }
  
  private static final LexerTable<<%=ruleEnum.getSimpleName()%>> LEXER_TABLE;
  private static final ParserTable<<%=terminalEnum.getSimpleName()%>,<%=nonTerminalEnum.getSimpleName()%>,<%=productionEnum.getSimpleName()%>,<%=versionEnum.getSimpleName()%>> PARSER_TABLE;
  private static final ToolsTable<<%=ruleEnum.getSimpleName()%>,<%=terminalEnum.getSimpleName()%>> TOOLS_TABLE;

  static {
    LEXER_TABLE = <%=lexerDataTable.getSimpleName()%>.createTable();
    PARSER_TABLE = <%=parserDataTable.getSimpleName()%>.createTable();
    TOOLS_TABLE = <%=toolsDataTable.getSimpleName()%>.createToolsTable();
  }

  /* sample main method
  
  public static void main(String[] args) throws java.io.IOException {
    java.io.Reader reader;
    if (args.length>0) {
      reader = new java.io.FileReader(args[0]);
    } else {
      reader = new java.io.InputStreamReader(System.in);
    }
    //TODO implements the terminal attribute evaluator here
    <%=terminalEvaluator.getSimpleName()%><%="<"%>CharSequence> terminalEvaluator = fr.umlv.tatoo.runtime.tools.Debug.createTraceProxy(<%=terminalEvaluator.getSimpleName()%>.class);

    //TODO implements the grammar evaluator here
    <%=grammarEvaluator.getSimpleName()%> grammarEvaluator = fr.umlv.tatoo.runtime.tools.Debug.createTraceProxy(<%=grammarEvaluator.getSimpleName()%>.class);

    //TODO choose a start non terminal and a version here
    <%=versionEnum.getSimpleName()%> version = <%=versionEnum.getSimpleName()%>.<%=defaultVersion.getId()%>;
    <%=nonTerminalEnum.getSimpleName()%> start = <%=nonTerminalEnum.getSimpleName()%>.<%=defaultStart.getId()%>;

    <%=analyzers.getSimpleName()%>.run(reader, terminalEvaluator, grammarEvaluator, start, version);
  }*/
}

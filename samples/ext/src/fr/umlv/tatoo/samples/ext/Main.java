package fr.umlv.tatoo.samples.ext;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;

import fr.umlv.tatoo.samples.ext.tools.GrammarEvaluator;
import fr.umlv.tatoo.samples.ext.tools.TerminalEvaluator;

public class Main {
  public static void main(String[] args) throws FileNotFoundException {
    Reader reader;
    if (args.length>0) {
      reader = new FileReader(args[0]);
    } else {
      reader = new InputStreamReader(System.in);
    }
    
    TerminalEvaluator<CharSequence> terminalEvaluator = new ExtTerminalAttributeEvaluator();
    GrammarEvaluator grammarEvaluator = new ExtGrammarEvaluator();
    /*FIXME Remi replace AnalyzerBuilder by Builder
    AnalyzerBuilder<ReaderWrapper> builder = AnalyzerBuilder.createAnalyzer(
        new ReaderWrapper(reader, new LocationTracker()),
        terminalEvaluator,grammarEvaluator);
    
    
    ShiftErrorRecoveryPolicy<TerminalEnum,NonTerminalEnum,ProductionEnum,VersionEnum> errorRecoveryPolicy=
      new ShiftErrorRecoveryPolicy<TerminalEnum,NonTerminalEnum,ProductionEnum,VersionEnum>(
      new DefaultShiftErrorRecoveryListener<TerminalEnum>(
        TerminalEnum.class,
        new TypeInformationsImpl(),
        parserProcessor.getAttributeHolder()),
        parserProcessor);
    
    builder.setParserErrorRecoveryPolicy(errorRecoveryPolicy);
    
    LexerAndParser<ReaderWrapper,TerminalEnum,NonTerminalEnum,ProductionEnum,VersionEnum> lexerAndParser=builder.createLexerAndParser();
    //builder.setLexerErrorPolicy(new ForwarderLexerErrorRecoveryPolicy<Object, ReaderWrapper>(new ParserForwarder<ReaderWrapper>(lexerAndParser.getParser(),false)));
    
    lexerAndParser.getLexer().run();
    */
  }
}

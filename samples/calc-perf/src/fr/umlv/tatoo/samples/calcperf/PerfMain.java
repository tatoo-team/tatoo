package fr.umlv.tatoo.samples.calcperf;

import java.io.StringReader;
import java.util.EnumMap;
import java.util.Random;

import fr.umlv.tatoo.runtime.buffer.LexerBuffer;
import fr.umlv.tatoo.runtime.buffer.impl.LocationTracker;
import fr.umlv.tatoo.runtime.buffer.impl.ReaderWrapper;
import fr.umlv.tatoo.runtime.lexer.DefaultLexerErrorRecoveryPolicy;
import fr.umlv.tatoo.runtime.lexer.Lexer;
import fr.umlv.tatoo.runtime.lexer.LexerListener;
import fr.umlv.tatoo.runtime.parser.Parser;
import fr.umlv.tatoo.runtime.parser.ParserErrorRecoveryPolicy;
import fr.umlv.tatoo.runtime.parser.ParserTable;
import fr.umlv.tatoo.runtime.tools.LookaheadMapFactory;
import fr.umlv.tatoo.runtime.tools.ParserForwarder;
import fr.umlv.tatoo.runtime.tools.ParserLookaheadActivator;
import fr.umlv.tatoo.runtime.tools.ToolsListener;
import fr.umlv.tatoo.runtime.tools.ToolsProcessor;
import fr.umlv.tatoo.runtime.tools.ToolsTable;
import fr.umlv.tatoo.samples.calcperf.lexer.PerfLexerDataTable;
import fr.umlv.tatoo.samples.calcperf.lexer.PerfRuleEnum;
import fr.umlv.tatoo.samples.calcperf.parser.PerfNonTerminalEnum;
import fr.umlv.tatoo.samples.calcperf.parser.PerfParserDataTable;
import fr.umlv.tatoo.samples.calcperf.parser.PerfProductionEnum;
import fr.umlv.tatoo.samples.calcperf.parser.PerfTerminalEnum;
import fr.umlv.tatoo.samples.calcperf.parser.PerfVersionEnum;
import fr.umlv.tatoo.samples.calcperf.tools.PerfToolsDataTable;

public class PerfMain {
  public static void main(String[] args) {
    StringBuilder builder=new StringBuilder();
    Random random=new Random(0);
    for(int i=0;i<100000;i++) {
      builder.append(random.nextInt(1000)).append('+').
        append(random.nextInt(1000)).append('\n');
    }
    String text=builder.toString();
    
    long startStartUp = System.nanoTime();
    
    ToolsProcessor<PerfRuleEnum,LexerBuffer,PerfTerminalEnum,PerfNonTerminalEnum,PerfProductionEnum> processor=
      ToolsProcessor.createProcessor(new ToolsListener<PerfRuleEnum,LexerBuffer,PerfTerminalEnum,PerfNonTerminalEnum,PerfProductionEnum>() {
      @Override
      public void accept(PerfNonTerminalEnum nonTerminal) {
        //do nothing
      }
      @Override
      public void comment(PerfRuleEnum rule,LexerBuffer buffer) {
        //do nothing 
      }
      @Override
      public void reduce(PerfProductionEnum production) {
        //do nothing
      }
      @Override
      public void shift(PerfTerminalEnum terminal,PerfRuleEnum rule,LexerBuffer buffer) {
        //do nothing
      }
    });
    
    ParserTable<PerfTerminalEnum,PerfNonTerminalEnum,PerfProductionEnum,PerfVersionEnum> parserTable=
      PerfParserDataTable.createTable();
    Parser<PerfTerminalEnum,PerfNonTerminalEnum,PerfProductionEnum,PerfVersionEnum> parser=
      Parser.createParser(parserTable,
        processor,
        ParserErrorRecoveryPolicy.<PerfTerminalEnum,PerfNonTerminalEnum,PerfProductionEnum,PerfVersionEnum>getNoErrorRecoveryPolicy(null),
        PerfNonTerminalEnum.start,
        PerfVersionEnum.DEFAULT,
        LookaheadMapFactory.enumLookaheadMap(parserTable));
    
    ToolsTable<PerfRuleEnum,PerfTerminalEnum> toolsTable=PerfToolsDataTable.createToolsTable();
    LexerListener<PerfRuleEnum,LexerBuffer> lexerListener=
      processor.createLexerListener(parser,toolsTable);
    
    ParserForwarder<PerfTerminalEnum,LexerBuffer> parserForwarder=
      new ParserForwarder<PerfTerminalEnum,LexerBuffer>(parser);
    Lexer<LexerBuffer> lexer=Lexer.createLexer(PerfLexerDataTable.createTable(),
      (ReaderWrapper)null,
      lexerListener,
      ParserLookaheadActivator.create(parser,toolsTable,PerfRuleEnum.class),
      parserForwarder,
      new DefaultLexerErrorRecoveryPolicy<PerfRuleEnum,LexerBuffer>(parserForwarder,null));
    
    long endStartUp = System.nanoTime();
    System.out.println("startup time "+(endStartUp-startStartUp));
    
    LocationTracker tracker=null;
    //tracker=new LocationTracker();
    
    //warm-up
    System.out.println("warmup !");
    for(int i=0;i<5;i++) {
      StringReader reader=new StringReader(text);
      lexer.reset(new ReaderWrapper(reader,tracker));
      long start=System.nanoTime();
      lexer.run();
      long end=System.nanoTime();
      long elapsed=end-start;
      System.out.println("warmup elapsed "+elapsed);
    }
    System.out.println("end of warmup !");
    
    long count=0;
    for(int i=0;i<10;i++) {
      StringReader reader=new StringReader(text);
      lexer.reset(new ReaderWrapper(reader,tracker));
      
      long start=System.nanoTime();
      lexer.run();
      long end=System.nanoTime();
      long elapsed=end-start;
      System.out.println("elapsed "+elapsed);
      count+=elapsed;
    }
    
    System.out.println("means "+count/10);
  }
}

package fr.umlv.tatoo.samples.httpserver.banzai.parser;

import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Set;

import fr.umlv.tatoo.runtime.lexer.Lexer;
import fr.umlv.tatoo.runtime.lexer.LexerErrorRecoveryPolicy;
import fr.umlv.tatoo.runtime.lexer.LexerListener;
import fr.umlv.tatoo.runtime.lexer.LexerTable;
import fr.umlv.tatoo.runtime.lexer.LifecycleHandler;
import fr.umlv.tatoo.runtime.lexer.NoLexerErrorRecoveryPolicy;
import fr.umlv.tatoo.runtime.lexer.rules.ActionProcessor;
import fr.umlv.tatoo.runtime.parser.ActionReturn;
import fr.umlv.tatoo.runtime.parser.LookaheadMap;
import fr.umlv.tatoo.runtime.parser.Parser;
import fr.umlv.tatoo.runtime.parser.ParserErrorRecoveryPolicy;
import fr.umlv.tatoo.runtime.parser.ParserTable;
import fr.umlv.tatoo.runtime.tools.LookaheadMapFactory;
import fr.umlv.tatoo.runtime.tools.ToolsProcessor;
import fr.umlv.tatoo.runtime.tools.ToolsTable;
import fr.umlv.tatoo.runtime.util.IntArrayList;
import fr.umlv.tatoo.samples.httpserver.banzai.Buffers;
import fr.umlv.tatoo.samples.httpserver.banzai.Emitter;
import fr.umlv.tatoo.samples.httpserver.banzai.RequestAnalyzer;
import fr.umlv.tatoo.samples.httpserver.banzai.Response;
import fr.umlv.tatoo.samples.httpserver.lexer.LexerDataTable;
import fr.umlv.tatoo.samples.httpserver.lexer.RuleEnum;
import fr.umlv.tatoo.samples.httpserver.parser.NonTerminalEnum;
import fr.umlv.tatoo.samples.httpserver.parser.ParserDataTable;
import fr.umlv.tatoo.samples.httpserver.parser.ProductionEnum;
import fr.umlv.tatoo.samples.httpserver.parser.TerminalEnum;
import fr.umlv.tatoo.samples.httpserver.parser.VersionEnum;
import fr.umlv.tatoo.samples.httpserver.tools.ToolsDataTable;

public class ParserRequestAnalyzer extends RequestAnalyzer {
  ByteBuffer prefix;
  
  final ProtocolHandler protocolHandler;
  final Lexer<AdHocByteBufferWrapper> lexer;
  
  private static final boolean DEBUG=false;
  
  ParserRequestAnalyzer(ProtocolHandler protocolHandler) {
    // don't call directly, use the pool instead
    this.protocolHandler=protocolHandler;
    lexer=createLexer(protocolHandler);
  }
  
  @Override
  protected void init(SocketChannel channel,ByteBuffer inBuffer,
      ByteBuffer outBuffer,Selector writeSelector,Emitter<Response> emitter) {
    super.init(channel,inBuffer,outBuffer,writeSelector,emitter);
    
    AdHocByteBufferWrapper lexerBuffer=lexer.getBuffer();
    lexerBuffer.setByteBuffer(inBuffer);
    lexerBuffer.setPreviousWasNewLine(true);
    
    assert prefix==null;
  }
  
  @Override
  protected void restore(ByteBuffer buffer) {
    if (prefix!=null) {
      //System.out.println("restore: available prefix "+prefix);
      
      buffer.put(prefix);
      Buffers.POOL_SMALL.recycle(prefix);
      prefix=null;
    }
  }
  
  @Override
  protected boolean parse(ByteBuffer buffer) {
    // parse all available tokens
    try {
      lexer.step();
    } catch(EndRequestException e) {
      if (DEBUG) {
        System.out.println("exception in step");
        e.printStackTrace(System.out);
      }
      
      // shortpath to end the request
      AdHocByteBufferWrapper lexerBuffer=lexer.getBuffer();
      lexerBuffer.setByteBuffer(null);
      lexerBuffer.resetTokenOffsets();
      asyncClose();
      return false; 
    }
     
    AdHocByteBufferWrapper lexerBuffer=lexer.getBuffer();
    int tokenEnd=lexerBuffer.getTokenEnd();
    
    //System.out.println("tokenEnd "+tokenEnd+" limit "+buffer.limit());
    
    if (tokenEnd<buffer.limit()) {
      // save prefix
      buffer.position(tokenEnd);
      
      prefix=Buffers.POOL_SMALL.extract();
      prefix.put(buffer);
      prefix.flip();
      
      //System.out.println("save prefix "+tokenEnd+" "+buffer.limit());
    }
    
    lexerBuffer.resetTokenOffsets();
    return true;
  }
  
  @Override
  protected void close() {
    //System.out.println("end of stream");
    
    // end parsing
    try {
      lexer.close();
    } catch(EndRequestException e) {
      if (DEBUG) {
        System.out.println("exception in close");
        e.printStackTrace(System.out);
      }
    }
    
    AdHocByteBufferWrapper lexerBuffer=lexer.getBuffer();
    lexerBuffer.setByteBuffer(null);
    lexerBuffer.resetTokenOffsets();
    
    asyncClose();
  }
  
  @Override
  protected void expire() {
    asyncClose();
    if (prefix!=null)
      Buffers.POOL_SMALL.recycle(prefix);
  }
  
  @SuppressWarnings("serial")
  static class EndRequestException extends RuntimeException {
    EndRequestException() { super(); } 
    EndRequestException(String message) { super(message); } 
    EndRequestException(Throwable t) { super(t); }
  }
  static final EndRequestException END_REQUEST_EXCEPTION=new EndRequestException();
  
  public void endRequest(Exception e) {
    // this will close the stream
    if (DEBUG)
      throw new EndRequestException(e);
    throw END_REQUEST_EXCEPTION;
  }
  
  public static Lexer<AdHocByteBufferWrapper> createLexer(ProtocolHandler handler) {
    ToolsProcessor<RuleEnum, AdHocByteBufferWrapper, TerminalEnum, NonTerminalEnum, ProductionEnum> processor =
      ToolsProcessor.createProcessor(handler);
    
    final Parser<TerminalEnum,NonTerminalEnum,ProductionEnum,VersionEnum> parser=
      Parser.createParser(PARSER_TABLE,
        processor,
        PARSER_ERROR_RECOVERY_POLICY,
        NonTerminalEnum.start,
        VersionEnum.DEFAULT,
        LOOKAHEAD_MAP);
    
    LexerListener<RuleEnum,AdHocByteBufferWrapper> lexerHandler=
      processor.createLexerListener(parser,TOOLS_TABLE);
    
    AdHocParserLookaheadActivator<RuleEnum> activator=
      new AdHocParserLookaheadActivator<RuleEnum>(parser,ACTIVATOR_RULES_ARRAY);
    
    AdHocByteBufferWrapper buffer=new AdHocByteBufferWrapper();
    LifecycleHandler<AdHocByteBufferWrapper> lifecycleHandler=new LifecycleHandler<AdHocByteBufferWrapper>() {
      @Override
      public void lexerClosed(Lexer<AdHocByteBufferWrapper> lexer) {
        parser.close();
      }
      @Override
      public void lexerReset(Lexer<AdHocByteBufferWrapper> lexer) {
        parser.reset();
      }
    };
    return Lexer.createLexer(LEXER_TABLE,
      buffer,
      lexerHandler,
      activator,
      lifecycleHandler,
      LEXER_ERROR_RECOVERY_POLICY);
  }
  
  private static final ParserTable<TerminalEnum,NonTerminalEnum,ProductionEnum,VersionEnum> PARSER_TABLE;
  private static final LookaheadMap<TerminalEnum,VersionEnum> LOOKAHEAD_MAP;
  private static final RuleEnum[][] ACTIVATOR_RULES_ARRAY;
  private static final ToolsTable<RuleEnum,TerminalEnum> TOOLS_TABLE;
  private static final ParserErrorRecoveryPolicy<TerminalEnum, NonTerminalEnum, ProductionEnum, VersionEnum> PARSER_ERROR_RECOVERY_POLICY;
  private static final LexerTable<RuleEnum> LEXER_TABLE;
  private static final LexerErrorRecoveryPolicy<RuleEnum,AdHocByteBufferWrapper> LEXER_ERROR_RECOVERY_POLICY;
  
  static {
    TOOLS_TABLE=ToolsDataTable.createToolsTable();
    PARSER_TABLE=ParserDataTable.createTable();
    
    LOOKAHEAD_MAP=
      LookaheadMapFactory.enumLookaheadMap(PARSER_TABLE);
    
    ACTIVATOR_RULES_ARRAY=fr.umlv.tatoo.runtime.tools.SingleVersionParserLookaheadActivator.activatorRulesArray(
        PARSER_TABLE.getStateNb(),
        PARSER_TABLE.getDefaultVersion(),
        LOOKAHEAD_MAP,
        RuleEnum.class,
        TerminalEnum.class,
        TOOLS_TABLE.getUnconditionalRuleSet(),
        TOOLS_TABLE.getRuleToTerminalMap());
    
    PARSER_ERROR_RECOVERY_POLICY=
      new ParserErrorRecoveryPolicy<TerminalEnum, NonTerminalEnum, ProductionEnum, VersionEnum>() {
      
      @Override
      public void reset() {
        // do nothing
      }
    
      @Override
      public ActionReturn recoverOnError(
          Parser<TerminalEnum,NonTerminalEnum,ProductionEnum,VersionEnum> parser,
          IntArrayList states,TerminalEnum terminal,String message) {
        if (DEBUG)
          throw new EndRequestException(message+" "+terminal+" "+states);
        throw END_REQUEST_EXCEPTION;
      }
    
      @Override
      public Set<? extends TerminalEnum> getLookahead(
          Parser<TerminalEnum,NonTerminalEnum,ProductionEnum,VersionEnum> parser,
          Set<? extends TerminalEnum> proposedLookaheads) {
        return proposedLookaheads;
      }
    
      @Override
      public boolean errorRecoveryNeedsContinuation() {
        return false;
      }
    
      @Override
      public ActionReturn continueRecoverOnError(
          Parser<TerminalEnum,NonTerminalEnum,ProductionEnum,VersionEnum> parser,
          IntArrayList states,TerminalEnum terminal) {
        throw new AssertionError();
      }
    
      @Override
      public boolean closeParser(
          Parser<TerminalEnum,NonTerminalEnum,ProductionEnum,VersionEnum> parser) {
        throw new AssertionError();
      }
    };
    
    LEXER_TABLE=LexerDataTable.createTable();
    LEXER_ERROR_RECOVERY_POLICY=
      new NoLexerErrorRecoveryPolicy<RuleEnum,AdHocByteBufferWrapper>() {
      
      @Override
      public void recoverOnError(Lexer<AdHocByteBufferWrapper> lexer, ActionProcessor<RuleEnum> processor) {
        if (DEBUG)
          throw new EndRequestException("unexpected char "+lexer.getBuffer().lastChar());
        throw END_REQUEST_EXCEPTION;
      }

      @Override
      public void recoverOnUnexpectedEndOfFile(Lexer<AdHocByteBufferWrapper> lexer, ActionProcessor<RuleEnum> processor) {
        if (DEBUG)
          throw new EndRequestException("unexpected end of file");
        throw END_REQUEST_EXCEPTION;
      }
    };
  }
}

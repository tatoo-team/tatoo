package fr.umlv.tatoo.samples.httpserver.banzai.http;

import java.io.IOException;
import java.nio.ByteBuffer;

import fr.umlv.tatoo.samples.httpserver.banzai.parser.AdHocByteBufferWrapper;
import fr.umlv.tatoo.samples.httpserver.banzai.parser.ParserRequestAnalyzer;
import fr.umlv.tatoo.samples.httpserver.banzai.parser.ProtocolHandler;
import fr.umlv.tatoo.samples.httpserver.lexer.RuleEnum;
import fr.umlv.tatoo.samples.httpserver.parser.NonTerminalEnum;
import fr.umlv.tatoo.samples.httpserver.parser.ProductionEnum;
import fr.umlv.tatoo.samples.httpserver.parser.TerminalEnum;

public class HttpProtocolHandler extends ProtocolHandler { 
  private final Decoder decoder=new Decoder();
  private final Encoder encoder=new Encoder();
  
  private boolean http11;
  private boolean responseSent;
  private String url;
  
  @Override
  public void reset() {
    http11=false;
    responseSent=false;
  }
  
  public void accept(NonTerminalEnum nonTerminal) {
    //System.out.println("accept "+nonTerminal);
  }

  public void reduce(ProductionEnum production) {
    //System.out.println("reduce "+production);
    if (production==ProductionEnum.firstline) {
      if (http11) { // true if HTTP/1.1
        request(url,true);
        responseSent=true;
      }
      return;
    }
    
    if (production==ProductionEnum.request) {
      if (!responseSent)
        request(url,false);
      reset();
      return;
    }
  }
  
  public void shift(TerminalEnum terminal,RuleEnum rule,AdHocByteBufferWrapper lexerBuffer) {
    //System.out.println("shift "+terminal+" \'"+decode()+'\'');
    if (terminal==TerminalEnum.version) {
      //found HTTP/1.1
      http11=lexerBuffer.getByteBuffer().get(
        lexerBuffer.getTokenStart())==(byte)'1';
      return;
    }
    if (terminal==TerminalEnum.url) {
      url=decode(lexerBuffer);
      //System.out.println("url "+url);
      return;
    }
    if (terminal==TerminalEnum.keepalive) {
      request(url,true);
      responseSent=true;
      return;
    }
  }
  
  private String decode(AdHocByteBufferWrapper lexerBuffer) {
    return decoder.decodeURL(
        lexerBuffer.getByteBuffer(),
        lexerBuffer.getTokenStart(),
        lexerBuffer.getTokenEnd());
  }
  
  private void request(String url,boolean keepalive) {
    ParserRequestAnalyzer analyzer=this.analyzer;
    Encoder encoder=this.encoder;
    FileCacheEntry cacheEntry=FileCache.CACHE.get(url,encoder);
    
    //System.out.println("service get "+url+" "+keepalive);
    ByteBuffer outBuffer=analyzer.outBuffer;
    try {
      if (cacheEntry==null) {
        HttpHeaders.putResponseNotFound(outBuffer,keepalive);
        outBuffer.flip();

        analyzer.asyncWrite(outBuffer);
        outBuffer.clear();
        return;
      }

      if (keepalive)
        cacheEntry.writeKeepAliveResponse(analyzer,outBuffer);
      else
        cacheEntry.writeResponse(analyzer,encoder,outBuffer);
      outBuffer.clear();
    
    } catch(IOException e) {
      analyzer.endRequest(e);
    }
    
    if (!keepalive)
      analyzer.endRequest(null);
  }
}

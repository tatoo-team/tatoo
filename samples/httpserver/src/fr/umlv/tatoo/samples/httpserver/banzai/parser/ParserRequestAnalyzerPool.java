/**
 * 
 */
package fr.umlv.tatoo.samples.httpserver.banzai.parser;

import fr.umlv.tatoo.samples.httpserver.banzai.RequestAnalyzerPool;

public abstract class ParserRequestAnalyzerPool extends RequestAnalyzerPool<ParserRequestAnalyzer> {
  @Override
  protected ParserRequestAnalyzer create() {
    ProtocolHandler protocolHandler=createProtocolHandler();
    ParserRequestAnalyzer analyzer=new ParserRequestAnalyzer(protocolHandler);
    protocolHandler.setParserRequestAnalyzer(analyzer);
    return analyzer;
  }
  
  protected abstract ProtocolHandler createProtocolHandler();

  @Override
  protected void reset(ParserRequestAnalyzer requestAnalyzer) {
    super.reset(requestAnalyzer);
    requestAnalyzer.prefix=null;

    // reset protocol handler
    requestAnalyzer.protocolHandler.reset();

    // reset action processor
    requestAnalyzer.lexer.reset(requestAnalyzer.lexer.getBuffer());
  }
}
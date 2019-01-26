package fr.umlv.tatoo.samples.httpserver.banzai.parser;

import fr.umlv.tatoo.runtime.tools.ToolsListener;
import fr.umlv.tatoo.samples.httpserver.lexer.RuleEnum;
import fr.umlv.tatoo.samples.httpserver.parser.NonTerminalEnum;
import fr.umlv.tatoo.samples.httpserver.parser.ProductionEnum;
import fr.umlv.tatoo.samples.httpserver.parser.TerminalEnum;

public abstract class ProtocolHandler implements ToolsListener<RuleEnum, AdHocByteBufferWrapper, TerminalEnum, NonTerminalEnum, ProductionEnum> { 
  protected ParserRequestAnalyzer analyzer;
  
  protected ProtocolHandler() {
    // do nothing
  }
  
  @Override
  public void comment(RuleEnum rule,AdHocByteBufferWrapper buffer) {
    // do nothing
  }
  
  public void setParserRequestAnalyzer(ParserRequestAnalyzer analyzer) {
    this.analyzer=analyzer;
  }
  
  public abstract void reset();
}

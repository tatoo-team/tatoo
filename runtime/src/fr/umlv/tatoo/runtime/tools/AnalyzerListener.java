package fr.umlv.tatoo.runtime.tools;

import fr.umlv.tatoo.runtime.parser.ParserErrorRecoveryListener;


public interface AnalyzerListener<R,B,T,N,P> extends ToolsListener<R,B,T,N,P>, ParserErrorRecoveryListener<T,N> {
  // a union interface
}

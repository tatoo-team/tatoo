/**
 * 
 */
package fr.umlv.tatoo.samples.httpserver.banzai;

import fr.umlv.tatoo.samples.httpserver.banzai.util.ConcurrentPool;

public abstract class RequestAnalyzerPool<A extends RequestAnalyzer> extends ConcurrentPool<A> {
  @Override
  protected void reset(A requestAnalyzer) {
    requestAnalyzer.expirationTime=0;
    requestAnalyzer.channel=null;
    requestAnalyzer.outBuffer=null;
    requestAnalyzer.writeSelector=null;
    requestAnalyzer.emitter=null;
    requestAnalyzer.secondaryEmitter=null;
  }
}
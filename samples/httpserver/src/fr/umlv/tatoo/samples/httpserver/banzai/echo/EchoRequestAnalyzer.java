package fr.umlv.tatoo.samples.httpserver.banzai.echo;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import fr.umlv.tatoo.samples.httpserver.banzai.Emitter;
import fr.umlv.tatoo.samples.httpserver.banzai.RequestAnalyzer;
import fr.umlv.tatoo.samples.httpserver.banzai.RequestAnalyzerPool;
import fr.umlv.tatoo.samples.httpserver.banzai.Response;

public class EchoRequestAnalyzer extends RequestAnalyzer {
  EchoRequestAnalyzer() {
    // don't call directly, use the pool instead
  }
  
  @Override
  protected void init(SocketChannel channel,ByteBuffer inBuffer,
      ByteBuffer outBuffer,Selector writeSelector,Emitter<Response> emitter) {
    super.init(channel,inBuffer,outBuffer,writeSelector,emitter);
  }
  
  @Override
  public void restore(ByteBuffer buffer) {
    // no context restoration
  }
  
  @Override
  public boolean parse(ByteBuffer buffer) {
    ByteBuffer outBuffer=this.outBuffer;
    outBuffer.put(buffer);
    outBuffer.flip();
    
    try {
      asyncWrite(outBuffer);
      return true;
    } catch(IOException e) {
      asyncClose(); 
    }
    return false;
  }
  
  @Override
  public void close() {
    //System.out.println("end of stream");
    asyncClose();
  }
  
  @Override
  public void expire() {
    asyncClose();
  }
  
  public static final RequestAnalyzerPool<EchoRequestAnalyzer> POOL=new RequestAnalyzerPool<EchoRequestAnalyzer>() {
    @Override
    protected EchoRequestAnalyzer create() {
      return new EchoRequestAnalyzer();
    }
    @Override
    protected void reset(EchoRequestAnalyzer requestAnalyzer) {
      super.reset(requestAnalyzer);
    }
  };
}

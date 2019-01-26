package fr.umlv.tatoo.samples.httpserver.banzai;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import fr.umlv.tatoo.samples.httpserver.banzai.util.ConcurrentPool;

public class Response {
  SocketChannel channel;
  // this buffer will not be recycled
  ByteBuffer buffer;
  
  Response() {
    // use the pool  
  }
  
  static final ConcurrentPool<Response> POOL=new ConcurrentPool<Response>() {
    @Override
    public Response create() {
      return new Response();
    }
    @Override
    public void reset(Response response) {
      response.channel=null;
      response.buffer=null;
    }
  };
}

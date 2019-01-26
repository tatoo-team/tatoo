package fr.umlv.tatoo.samples.httpserver.banzai;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public abstract class RequestAnalyzer {
  long expirationTime;
  
  SocketChannel channel;
  public ByteBuffer outBuffer;
  Selector writeSelector;
  
  Emitter<Response> emitter;
  Emitter<Response> secondaryEmitter;
  
  /** Initialize the request analyzer.
   * @param channel the channel
   * @param inBuffer the in buffer
   * @param outBuffer the out buffer
   * @param writeSelector the write selector
   * @param emitter the response emitter
   */
  protected void init(SocketChannel channel,
                      ByteBuffer inBuffer,
                      ByteBuffer outBuffer,
                      Selector writeSelector,
                      Emitter<Response> emitter) {
    this.channel=channel;
    this.outBuffer=outBuffer;
    this.writeSelector=writeSelector;
    this.emitter=emitter;
  }
  
  protected abstract void restore(ByteBuffer inBuffer);
  
  protected abstract boolean parse(ByteBuffer inBuffer);
  
  protected abstract void close();
  
  protected abstract void expire();
  
  // --------------------------------------------------------------------
  
  /** 
   * @param buffer must be a buffer managed explicitly
   * @throws IOException buffer is recycled
   */
  public void asyncWrite(ByteBuffer buffer) throws IOException {
    if (secondaryEmitter!=null) {
      sendWrite(secondaryEmitter,channel,buffer);
      return;
    }
    
    SelectionKey key=null;
    try {
      for(;;) {
        // try to write the buffer immediately if possible
        while(channel.write(buffer)>0) {
          if (!buffer.hasRemaining()) {
            if (key!=null && key.isValid()) {
              key.cancel();
              writeSelector.selectNow(); 
            }
            
            //System.out.println("exit sync write");
            return;
          }
        }
        // register in the write selector if not already registered
        if (key==null) {
          //System.out.println("register in selector");
          key=channel.register(writeSelector,SelectionKey.OP_WRITE);
        }
        
        writeSelector.select(5);  // 5 ms
        if (writeSelector.selectedKeys().isEmpty()) {
          //System.out.println("timeout temporary selector");
          
          if (key.isValid()) {
            key.cancel();
            writeSelector.selectNow();
          }
          
          secondaryEmitter=emitter.getNextSecondaryEmitter();
          sendWrite(secondaryEmitter,channel,buffer);
          return;
        }
      }
      
    } catch(IOException e) {
      if (key!=null && key.isValid())
        key.cancel();
      throw e;
    }
  }
  
  public void asyncClose() {
    if (secondaryEmitter!=null) {
      sendClose(secondaryEmitter,channel); 
      return;
    }
    IOUtils.closeWitoutException(channel);
  }
  
  private static void sendWrite(Emitter<Response> secondaryEmitter,SocketChannel channel,ByteBuffer buffer) {
    // if buffer contains the header
    if (!buffer.isReadOnly()) {
      //System.out.println("buffer not read only");
      
      ByteBuffer b=Buffers.POOL.extract();
      b.put(buffer).flip();
      buffer=b;
    }
    
    Response response=Response.POOL.extract();
    response.channel=channel;
    response.buffer=buffer;
    secondaryEmitter.send(response);
  }
  
  private static void sendClose(Emitter<Response> secondaryEmitter,SocketChannel channel) {
    Response response=Response.POOL.extract();
    response.channel=channel;
    secondaryEmitter.send(response);
  }
}

package fr.umlv.tatoo.samples.httpserver.banzai;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.LinkedBlockingQueue;

public class WriteWorkerTask extends WorkerTask<Response> {
  private final Selector writeSelector;
  private final LinkedBlockingQueue<Response> queue=
    new LinkedBlockingQueue<Response>();
  
  public WriteWorkerTask() throws IOException {
    writeSelector=Selector.open();
  }
  
  @Override
  public void send(Response element) {
    try {
      queue.put(element);
    } catch(InterruptedException e) {
      throw new AssertionError(e);
    }
  }
  
  public void run() {
    for(;;) {
      try {
        perform(queue.take());
      } catch(InterruptedException e) {
        throw new AssertionError(e);
      }
    }
  }
  
  private void perform(Response response) {
    SocketChannel channel=response.channel;
    ByteBuffer buffer=response.buffer;
    Response.POOL.recycle(response);
    
    // if no buffer
    if (buffer==null) {
      //System.out.println("writer task: close");
      IOUtils.closeWitoutException(channel);
      return;
    }
    
    SelectionKey key=null;
    try {
      for(;;) {
        while(channel.write(buffer)>0) {
          if (!buffer.hasRemaining()) {
            if (key!=null && key.isValid()) {
              key.cancel();
              writeSelector.selectNow();
            }
            if (!buffer.isReadOnly())
              Buffers.POOL.recycle(buffer);
            //System.out.println("writer task: sync write");
            return;
          }
        }
        if (key==null) {
          key=channel.register(writeSelector,SelectionKey.OP_WRITE);
          //System.out.println("writer task: register");
        }

        writeSelector.select(30*1000);  // 30 000 ms
        if (writeSelector.selectedKeys().isEmpty()) {
          //System.out.println("writer task: timeout");
          
          if (key.isValid()) {
            key.cancel();
            writeSelector.selectNow();
          }
          IOUtils.closeWitoutException(channel);
          if (!buffer.isReadOnly())
            Buffers.POOL.recycle(buffer);
          return;
        }
      }
      
    } catch(IOException e) {
      if (key!=null)
        key.cancel();
      IOUtils.closeWitoutException(channel);
      if (!buffer.isReadOnly())
        Buffers.POOL.recycle(buffer);
    }
  }
}

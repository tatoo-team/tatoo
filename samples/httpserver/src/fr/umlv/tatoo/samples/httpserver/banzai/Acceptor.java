package fr.umlv.tatoo.samples.httpserver.banzai;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Acceptor implements Runnable {
  private final Selector selector;
  private final Emitter<SocketChannel> emitter;
  
  public Acceptor(Emitter<SocketChannel> emitter) throws IOException {
    this.selector=Selector.open();
    this.emitter=emitter;
  }
  
  public void addEndPoint(SocketAddress address) throws IOException {
    ServerSocketChannel channel=ServerSocketChannel.open();
    channel.configureBlocking(false);
    ServerSocket socket=channel.socket();
    socket.setReuseAddress(true);
    socket.bind(address,4096);
    
    channel.register(selector,SelectionKey.OP_ACCEPT);
    selector.wakeup();
  }
  
  public void run() {
    Set<SelectionKey> selectedKeys=selector.selectedKeys();
    for(;;) {
      int nbSelect;
      try {
        nbSelect=selector.select();
      } catch(IOException e) {
        e.printStackTrace();
        throw new AssertionError(e);
      }
      if (nbSelect==0)
        continue;

      for(Iterator<SelectionKey> it=selectedKeys.iterator();it.hasNext();) {
        SelectionKey key=it.next();
        it.remove();
        
        if (!key.isValid())
          continue;

        ServerSocketChannel serverChannel=(ServerSocketChannel)key.channel();
        SocketChannel channel;
        try {
          channel=serverChannel.accept();
          if (channel==null)
            continue;
          
          channel.configureBlocking(false);
        } catch(IOException e) {
          e.printStackTrace();
          continue;
        }
        
        emitter.send(channel);
      }
    }
  }
}

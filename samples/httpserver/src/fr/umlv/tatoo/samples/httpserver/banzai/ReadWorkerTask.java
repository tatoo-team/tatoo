package fr.umlv.tatoo.samples.httpserver.banzai;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import fr.umlv.tatoo.samples.httpserver.banzai.util.ConcurrentPool;

public class ReadWorkerTask<A extends RequestAnalyzer> extends WorkerTask<SocketChannel> {
  private final Selector selector;
  private final Selector writeSelector;
  private final Emitter<Response> emitter;
  private final ConcurrentLinkedQueue<SocketChannel> queue=
    new ConcurrentLinkedQueue<SocketChannel>();
  
  private final ByteBuffer inBuffer=ByteBuffer.allocateDirect(Buffers.BUFFER_SIZE);
  private final ByteBuffer outBuffer=ByteBuffer.allocateDirect(Buffers.BUFFER_SIZE);
  private final ConcurrentPool<A> analyzerPool;
  private long nextExpirationTime;
  
  private static final long SELECT_TIMEOUT=30 * 1000; // in ms
  private static final long EXPIRATION_DELAY=30 * 1000; //ms
  
  public ReadWorkerTask(ConcurrentPool<A> analyzerPool,Emitter<Response> emitter) throws IOException {
    selector=Selector.open();
    writeSelector=Selector.open();
    this.analyzerPool=analyzerPool;
    this.emitter=emitter;
  }
  
  @Override
  public void send(SocketChannel channel) {
    queue.offer(channel);
    selector.wakeup();
  }
  
  public void run() {
    ConcurrentLinkedQueue<SocketChannel> queue=this.queue;
    Selector selector=this.selector;
    Set<SelectionKey> selectorKeySet=selector.keys();
    Set<SelectionKey> selectedKeySet=selector.selectedKeys();
    for(;;) {
      SocketChannel channel;
      long time=System.currentTimeMillis();
      while((channel=queue.poll())!=null) {
        perform(channel,time);
      }
      
      time=System.currentTimeMillis();
      cleanup(selectorKeySet,time);
      
      try {
        while(queue.isEmpty() && selector.select(SELECT_TIMEOUT)!=0) {
          time=System.currentTimeMillis();
          doSelect(selectedKeySet,time);
        }
      } catch(IOException e) {
        throw new AssertionError(e);
      }
    }
  }
  
  private void perform(SocketChannel channel, long currentTime) {
    try {
      firstread(channel, currentTime);
    } catch(IOException e) {
      IOUtils.closeWitoutException(channel);
    } 
  }
  
  private void cleanup(Set<SelectionKey> keys,long currentTime) {
    long nextExpirationTime=this.nextExpirationTime;
    if (currentTime<nextExpirationTime)
      return;
    
    nextExpirationTime=currentTime+EXPIRATION_DELAY;
    if (!keys.isEmpty()) {
      //System.out.println("cleanup keys size "+keys.size());
      
      // manage expirations
      for(Iterator<SelectionKey> it=keys.iterator();it.hasNext();) {
        SelectionKey key=it.next();

        if (!key.isValid())
          continue;

        @SuppressWarnings("unchecked") A requestAnalyzer=(A)key.attachment();
        long expirationTime=requestAnalyzer.expirationTime;
        if (expirationTime<currentTime) {
          key.cancel();
          key.attach(null);
          
          //System.out.println("key expired");

          requestAnalyzer.expire();
          //outBuffer.clear();
          analyzerPool.recycle(requestAnalyzer);
        } else {
          if (expirationTime<nextExpirationTime)
            nextExpirationTime=expirationTime;
        }
      }
    }
    this.nextExpirationTime=nextExpirationTime;  
  }
  
  private void doSelect(Set<SelectionKey> selectedKeySet, long currentTime) {
    // manage selected keys
    for(Iterator<SelectionKey> it=selectedKeySet.iterator();it.hasNext();) {
      SelectionKey key=it.next();

      // remove from the selected key
      it.remove();

      if (!key.isValid())
        continue;

      try {
        read(key,currentTime);
      } catch(IOException e) {
        IOUtils.closeWitoutException(key.channel());
        if (key.isValid())
          key.cancel();
        @SuppressWarnings("unchecked") A requestAnalyzer=(A)key.attachment();
        key.attach(null);
        if (requestAnalyzer!=null)
          analyzerPool.recycle(requestAnalyzer);
      }
    }
  }
  
  private void firstread(SocketChannel channel, long currentTime) throws IOException {
    ByteBuffer inBuffer=this.inBuffer;
    
    // assert buffers clear
    assert inBuffer.position()==0 &&
           inBuffer.limit()==inBuffer.capacity();
    assert outBuffer.position()==0 &&
           outBuffer.limit()==outBuffer.capacity();
    
    // initialize
    A analyzer=analyzerPool.extract();
    analyzer.init(channel,inBuffer,outBuffer,writeSelector,emitter);
    
    int nbRead=channel.read(inBuffer);
    if (nbRead==-1) {
        analyzer.close();
        outBuffer.clear();
        analyzerPool.recycle(analyzer);
        return;
    }
    
    boolean needRegister;
    if (nbRead!=0) {
      // do parsing
      inBuffer.flip();
      needRegister=analyzer.parse(inBuffer);
        
      inBuffer.clear();
      outBuffer.clear();
    } else {
      needRegister=true;
    }
    
    // registering, if needed
    if (needRegister) {
      analyzer.expirationTime=currentTime+EXPIRATION_DELAY;
      try {
        channel.register(selector,SelectionKey.OP_READ,analyzer);
      } catch(ClosedChannelException e) {
        analyzerPool.recycle(analyzer);
        throw e;
      }
    } else {
      analyzerPool.recycle(analyzer);
    }
  }
  
  private void read(SelectionKey key,long currentTime) throws IOException {
    @SuppressWarnings("unchecked") A analyzer=(A)key.attachment();
    SocketChannel channel=(SocketChannel)key.channel();
    ByteBuffer inBuffer=this.inBuffer;
    
    // remove from the interest set
    //key.interestOps(0);
    
    // restore
    analyzer.restore(inBuffer);
    
    int nbRead=channel.read(inBuffer);
    if (nbRead==-1) {
      if (key.isValid())
        key.cancel();
      key.attach(null);
      
      inBuffer.flip();
      analyzer.close();  
      inBuffer.clear();
      outBuffer.clear();
      
      analyzerPool.recycle(analyzer);
      return;
    }
    
    // do the parsing
    inBuffer.flip();
    boolean registerAgain=analyzer.parse(inBuffer);
    
    // reset buffers
    inBuffer.clear();
    outBuffer.clear();
    
    // re-registering in the interest set, if needed
    if (registerAgain) {
      //key.interestOps(SelectionKey.OP_READ);
      analyzer.expirationTime=currentTime+EXPIRATION_DELAY;
    } else {
      if (key.isValid())
        key.cancel();
      key.attach(null);
      
      analyzerPool.recycle(analyzer);
    }
  }
}

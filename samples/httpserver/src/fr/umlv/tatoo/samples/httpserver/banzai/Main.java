package fr.umlv.tatoo.samples.httpserver.banzai;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

import fr.umlv.tatoo.samples.httpserver.banzai.http.HttpProtocolHandler;
import fr.umlv.tatoo.samples.httpserver.banzai.parser.ParserRequestAnalyzer;
import fr.umlv.tatoo.samples.httpserver.banzai.parser.ParserRequestAnalyzerPool;
import fr.umlv.tatoo.samples.httpserver.banzai.parser.ProtocolHandler;

// use jdk7b10 at least because
// http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6524172
// use jdk7b8 at least because
// http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6503430
public class Main {

  public static void main(String[] args) throws IOException {
    int readWorkerCount=20;
    int writeWorkerCount=8;
    int acceptorCount=2;
    
    /*new Thread(new Runnable() {
      public void run() {
        for(;;) {
          try {
            Thread.sleep(1000);
          } catch(InterruptedException e) {
            // do nothing
          }
          System.out.println("buffers "+Buffers.POOL.size());
          System.out.println("httpRequestAnalyzer "+HttpRequestAnalyzer.POOL.size());
          System.out.println("response "+Response.POOL.size());
          System.out.println("pipeline "+Pipeline.POOL.size());
        }
      }
    }).start();*/
    
    ParserRequestAnalyzerPool pool=new ParserRequestAnalyzerPool() {
      @Override
      protected ProtocolHandler createProtocolHandler() {
        return new HttpProtocolHandler();
      }
    };
    
    WriteWorkerTask[] writeWorkerTasks=new WriteWorkerTask[writeWorkerCount];
    for(int i=0;i<writeWorkerCount;i++) {
      WriteWorkerTask writeWorkerTask=
        new WriteWorkerTask();
      new Thread(writeWorkerTask).start();
      writeWorkerTasks[i]=writeWorkerTask;
    }
    
    RoundRobin<Response> responseRoundRobin=
      new RoundRobin<Response>(writeWorkerTasks);
    
    ReadWorkerTask<?>[] readWorkerTasks=new ReadWorkerTask<?>[readWorkerCount];
    for(int i=0;i<readWorkerCount;i++) {
      ReadWorkerTask<ParserRequestAnalyzer> readWorkerTask=
        new ReadWorkerTask<ParserRequestAnalyzer>(pool,responseRoundRobin);
      new Thread(readWorkerTask).start();
      readWorkerTasks[i]=readWorkerTask;
    }
    
    RoundRobin<SocketChannel> requestRoundRobin=
      new RoundRobin<SocketChannel>(readWorkerTasks);
    
    InetSocketAddress localPort=new InetSocketAddress(8080);
    Acceptor acceptor=new Acceptor(requestRoundRobin);
    acceptor.addEndPoint(localPort);
    
    for(int i=0;i<acceptorCount;i++) {
      new Thread(acceptor).start();
    }
  }
}

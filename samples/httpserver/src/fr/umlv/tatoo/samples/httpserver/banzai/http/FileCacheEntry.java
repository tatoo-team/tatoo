package fr.umlv.tatoo.samples.httpserver.banzai.http;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;

import fr.umlv.tatoo.samples.httpserver.banzai.Buffers;
import fr.umlv.tatoo.samples.httpserver.banzai.parser.ParserRequestAnalyzer;

public class FileCacheEntry {
  private final byte[] header;
  private final MappedByteBuffer content;
  private final boolean optimized;
  
  private FileCacheEntry(byte[] header,MappedByteBuffer content, boolean optimized) {
    this.header=header;
    this.content=content;
    this.optimized=optimized;
  }
  
  public void writeKeepAliveResponse(ParserRequestAnalyzer analyzer, ByteBuffer outBuffer) throws IOException {
    //System.out.println("write keepalive optimized "+optimized);
    //System.out.println("outBuffer "+outBuffer+" header.length "+header.length);
    
    outBuffer.put(header).flip();
    analyzer.asyncWrite(outBuffer);
    
    if (!optimized) {
      analyzer.asyncWrite(content.duplicate());
    }
  }
  
  public void writeResponse(ParserRequestAnalyzer analyzer,Encoder encoder, ByteBuffer outBuffer) throws IOException {
    //System.out.println("write non keepalive optimized "+optimized);
    //System.out.println("outBuffer "+outBuffer);
    //System.out.println("content capacity "+content.capacity());
    
    HttpHeaders.putResponseOK(outBuffer,false,content.capacity(),encoder);
    
    outBuffer.flip();
    analyzer.asyncWrite(outBuffer);
    
    analyzer.asyncWrite(content.duplicate());
  }
  
  public static FileCacheEntry create(MappedByteBuffer content,Encoder encoder) {
    ByteBuffer localBuffer=ByteBuffer.allocate(Buffers.BUFFER_SIZE);
    long length=content.capacity();
    HttpHeaders.putResponseOK(localBuffer,true,length,encoder);
    boolean optimized=localBuffer.remaining()>=length;
    if (optimized) {
      localBuffer.put(content);
    }
    localBuffer.flip();
    byte[] array=new byte[localBuffer.limit()];
    localBuffer.get(array);
    
    //System.out.println("array length "+array.length);
    /*char[] debugArray=new char[array.length];
    for(int i=0;i<array.length;i++)
      debugArray[i]=(char)array[i];
    System.out.println("array "+new String(debugArray));
    */
    return new FileCacheEntry(array,content,optimized);
  }
}

package fr.umlv.tatoo.samples.httpserver.banzai.http;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class Decoder {
  private final CharsetDecoder decoder=
    Charset.forName("US-ASCII").newDecoder();
  
  //valid URL are not greater than 1024 byte
  private final char[] array=new char[1024];
  private final CharBuffer out=CharBuffer.wrap(array);
                                   
  public String decodeURL(ByteBuffer buffer,int tokenStart,int tokenEnd) {
    int position=buffer.position();
    int limit=buffer.limit();
    
    buffer.position(tokenStart).limit(tokenEnd);
    
    // only one decode is need because we know here that
    // all data are available
      
    //decoder.reset(); //not needed for US-ASCII
    decoder.decode(buffer,out,true);
    //decoder.flush(out); //not needed for US-ASCII
    
    out.flip();
    String s=out.toString();
    out.clear();
    
    buffer.position(position).limit(limit);
    return s;
  }
}

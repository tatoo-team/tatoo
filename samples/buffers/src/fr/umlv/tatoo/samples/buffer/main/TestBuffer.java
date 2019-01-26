package fr.umlv.tatoo.samples.buffer.main;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.charset.Charset;

import fr.umlv.tatoo.runtime.buffer.LexerBuffer;
import fr.umlv.tatoo.runtime.buffer.impl.ReadableByteChannelWrapper;

public class TestBuffer {
  
  public static char next(LexerBuffer b) {
    if (!b.hasRemaining()) {
      try {
        b.read();
      } catch (IOException e) {
        throw new AssertionError(e);
      }
    }
    return (char)b.next();
    
  }
  
  public static String view(InputStream in) {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    byte[] buf = new byte[100];
    int read;
    try {
      while((read = in.read(buf))!=-1) {
        out.write(buf,0,read);
      }
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    try {
      byte[] byteArray = out.toByteArray();
      return new String(byteArray,"US-ASCII");
    } catch (UnsupportedEncodingException e) {
      throw new AssertionError(e);
    }
  }
  
  public static String view(ByteBuffer buffer) {
    int e = buffer.position();
    String s = Charset.forName("US-ASCII").decode(buffer).toString();
    buffer.position(e);
    return s;
  }
  
  public static void main(String[] args) throws UnsupportedEncodingException {
    //CharSequenceWrapper wra = new CharSequenceWrapper("azertyuiopqsdfghjklm",null);
    //ReaderWrapper wra = new ReaderWrapper(2,2,2,new StringReader("azertyuiopqsdfghjklm"),null);
    ReadableByteChannelWrapper wra = new ReadableByteChannelWrapper(true,2,2,2,Channels.newChannel(new ByteArrayInputStream("azertyuiopqsdfghjklm".getBytes("US-ASCII"))),null);
    //InputStreamWrapper wra = new InputStreamWrapper(2,2,2,new ByteArrayInputStream("azertyuiopqsdfghjklm".getBytes("US-ASCII")),null);
    for(int i=0;i<7;i++)
       System.out.print(next(wra)); // azertyu
     System.out.println();
     wra.unwind(3); 
     System.out.println(view(wra.view())); // aze
     wra.discard();
     for(int i=0;i<7;i++) 
       System.out.print(next(wra)); // rtyuiop
     System.out.println();
     wra.unwind(3);
     System.out.println(view(wra.view())); // rty
     wra.restart();
     for(int i=0;i<7;i++) 
       System.out.print(next(wra)); // rtyuiop
     System.out.println();
     wra.unwind(3);
     System.out.println(view(wra.view())); // rty
  }
  

}

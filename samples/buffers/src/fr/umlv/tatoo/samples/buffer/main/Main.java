package fr.umlv.tatoo.samples.buffer.main;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;

import fr.umlv.tatoo.runtime.buffer.impl.LocationTracker;
import fr.umlv.tatoo.runtime.buffer.impl.ReadableByteChannelWrapper;
import fr.umlv.tatoo.runtime.lexer.LexerListener;
import fr.umlv.tatoo.runtime.tools.builder.Builder;
import fr.umlv.tatoo.samples.buffers.latin1.LexerDataTable;
import fr.umlv.tatoo.samples.buffers.latin1.RuleEnum;

public class Main {
  public static void main(String[] args) throws FileNotFoundException {
    ReadableByteChannelWrapper buffer = new ReadableByteChannelWrapper(
      new FileInputStream("latin1.txt").getChannel(), new LocationTracker()); /* {
      @Override
      public int next() {
        int a = super.next();
        System.out.println((char)(a&0xFF));
        return a;
      }
      
      @Override
      public void unwind(int l) {
        System.out.println("u:"+l);
        super.unwind(l);
      }
    };*/
    
    final Charset latin1=Charset.forName("latin1");
    LexerListener<RuleEnum, ReadableByteChannelWrapper>  listener = new LexerListener<RuleEnum, ReadableByteChannelWrapper>() {
    
      public void ruleVerified(RuleEnum rule, int lastTokenLength,
          ReadableByteChannelWrapper buffer) throws RuntimeException {
        System.out.println(rule+"->"+latin1.decode(buffer.view()));
        buffer.restart();
      }
    
    };
  
    Builder.lexer(LexerDataTable.createTable()).
      buffer(buffer).listener(listener).createLexer().run();
  }
}

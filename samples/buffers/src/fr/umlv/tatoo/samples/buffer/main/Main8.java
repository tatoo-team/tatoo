package fr.umlv.tatoo.samples.buffer.main;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import fr.umlv.tatoo.runtime.buffer.impl.LocationTracker;
import fr.umlv.tatoo.runtime.buffer.impl.UTF8Wrapper;
import fr.umlv.tatoo.runtime.lexer.LexerListener;
import fr.umlv.tatoo.runtime.tools.builder.Builder;
import fr.umlv.tatoo.samples.buffers.utf8.LexerDataTable;
import fr.umlv.tatoo.samples.buffers.utf8.RuleEnum;

public class Main8 {
  public static void main(String[] args) throws FileNotFoundException {
    UTF8Wrapper<ByteBuffer> wrapper = UTF8Wrapper.wrap(
      new FileInputStream("utf8.txt").getChannel(),new LocationTracker());

    final Charset utf8=Charset.forName("UTF-8");
    LexerListener<RuleEnum, UTF8Wrapper<ByteBuffer>>  listener = new LexerListener<RuleEnum, UTF8Wrapper<ByteBuffer>>() {

      public void ruleVerified(RuleEnum rule, int lastTokenLength,
          UTF8Wrapper<ByteBuffer> buffer) throws RuntimeException {
        System.out.println(rule+"->"+utf8.decode(buffer.view()));
      }
    };
    
    Builder.lexer(LexerDataTable.createTable()).
      buffer(wrapper).listener(listener).createLexer().run();
  }
}

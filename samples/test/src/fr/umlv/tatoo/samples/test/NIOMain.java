package fr.umlv.tatoo.samples.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import fr.umlv.tatoo.runtime.buffer.impl.CharBufferWrapper;
import fr.umlv.tatoo.runtime.buffer.impl.LocationTracker;
import fr.umlv.tatoo.runtime.lexer.Lexer;
import fr.umlv.tatoo.runtime.tools.SemanticStack;
import fr.umlv.tatoo.samples.test.tools.Analyzers;
import fr.umlv.tatoo.samples.test.tools.GrammarEvaluator;
import fr.umlv.tatoo.samples.test.tools.TerminalEvaluator;

public class NIOMain {
  public static void main(String[] args) throws IOException {
    GrammarEvaluator grammarEvaluator=new IntGrammarEvaluator();
    TerminalEvaluator<CharSequence> terminalEvaluator=new IntAttributeEvaluator();
    
    ReadableByteChannel channel;
    if (args.length>0)
      channel=new FileInputStream(args[0]).getChannel();
    else
      channel=Channels.newChannel(System.in);

    Charset charset=Charset.defaultCharset();
    CharsetDecoder decoder=charset.newDecoder();
    
    ByteBuffer bbuffer=ByteBuffer.allocateDirect(8192);
    CharBuffer cbuffer=CharBuffer.allocate(
      (int)Math.ceil(bbuffer.capacity()*decoder.maxCharsPerByte()));
    
    CharBufferWrapper wrapper=new CharBufferWrapper(cbuffer, new LocationTracker());
    
    Lexer<CharBufferWrapper> lexer=
      Analyzers.analyzerTokenBufferBuilder(wrapper,
        terminalEvaluator, grammarEvaluator,
        new SemanticStack()).createLexer();
    
    boolean eof;
    do {
      eof=channel.read(bbuffer)==-1;
      bbuffer.flip();
      decoder.decode(bbuffer,cbuffer,eof);
      if (eof)
        decoder.flush(cbuffer);
      wrapper.flip();
      
      lexer.step();
   
      bbuffer.compact();
      wrapper.compact();
    } while(!eof);
    
    lexer.close();
  }
}

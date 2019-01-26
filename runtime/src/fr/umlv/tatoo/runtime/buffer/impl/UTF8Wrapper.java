package fr.umlv.tatoo.runtime.buffer.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

import fr.umlv.tatoo.runtime.buffer.LexerBuffer;
import fr.umlv.tatoo.runtime.buffer.TokenBuffer;

/**
 * The class provides a buffer for using with a lexer build with UTF-8 encoding
 * ({@link fr.umlv.tatoo.cc.lexer.charset.encoding.UTF8Encoding}).
 * It can wrap {@link InputStream} and {@link ReadableByteChannel}
 * @author Julien Cervelle
 * @param <T> the type of the token view of the wrapper
 */
public class UTF8Wrapper<T> implements LexerBuffer, TokenBuffer<T> {
  
  UTF8Wrapper(ByteProvider<T> byteProvider) {
    this.byteProvider = byteProvider;
  }
  
  /**
   * Construct a {@link UTF8Wrapper} used by the lexer to process the {@code InputStream}.
   * The parameters specify how the underlying buffer which stores data in allocated and reallocated
   * when more space is needed.
   * @param capacity the initial capacity
   * @param increment how much to increment the size of the buffer when more space is required
   * @param chunkSize how many bytes are read at once is the input
   * @param inputStream the input
   * @param tracker the location tracker
   */
  public static UTF8Wrapper<InputStream> wrap(int capacity,int increment,int chunkSize,InputStream inputStream,LocationTracker tracker) {
    return new UTF8Wrapper<InputStream>(new InputStreamWrapper(capacity,increment,chunkSize,inputStream, tracker));
  }
  
  /**
   * Constructs an {@link UTF8Wrapper} wrapper used by the lexer to process the {@code InputStream}.
   * @param inputStream the {@code InputStream} to wrap
   * @param tracker the location tracker
   */
  public static UTF8Wrapper<InputStream> wrap(InputStream inputStream,LocationTracker tracker) {
    return new UTF8Wrapper<InputStream>(new InputStreamWrapper(inputStream,tracker));
  }
  
  /**
   * Construct a {@link UTF8Wrapper} used by the lexer to process the {@code ReadableByteChannel}.
   * The parameters specify how the underlying buffer which stores data in allocated and reallocated
   * when more space is needed.
   * @param direct if the {@link ByteBuffer} must be direct
   * @param capacity the initial capacity
   * @param increment how much to increment the size of the buffer when more space is required
   * @param chunkSize how many bytes are read at once is the input
   * @param readable the input
   * @param tracker the location tracker
   * @return a new lexer buffer
   */
  public static UTF8Wrapper<ByteBuffer> wrap(boolean direct,int capacity,int increment,int chunkSize,
      ReadableByteChannel readable,LocationTracker tracker) {
    return new UTF8Wrapper<ByteBuffer>(new ReadableByteChannelWrapper(direct,capacity,increment,chunkSize,readable,tracker));
  }
  
  /**
   * Constructs an {@link UTF8Wrapper} wrapper used by the lexer to process the {@code ReadableByteChannel}.
   * @param readable the {@code ReadableByteChannel} to wrap
   * @param tracker the location tracker
   * @return a new lexer buffer
   */
  public static UTF8Wrapper<ByteBuffer> wrap(ReadableByteChannel readable,LocationTracker tracker) {
    return new UTF8Wrapper<ByteBuffer>(new ReadableByteChannelWrapper(readable,tracker));
  }
  
  private int nextChar() {
    return byteProvider.next();
  }
  
  public int next() {
    int a=nextChar();
    if ((a&0x80)==0) // 10000000 last bit is 0 1 byte
      return lastChar=a;
    int b=nextChar();
    if ((a&0x20)==0) // 100000 third bit is 0, 2 bytes
      return lastChar= (a |  (b<<8));
    int c=nextChar();
    return lastChar= (a | (b<<8) | (c<<16));
  }
  
  public boolean hasRemaining() {
    int availableBytes = byteProvider.limit()-byteProvider.position();
    if (availableBytes == 0)
      return false;
    byte a=byteProvider.getByte(byteProvider.position());
    return availableBytes>=bytesForChar(a);
  }
  
  private int bytesForChar(byte first) {
    if ((first&0x80)==0)
      return 1;
    if ((first&0x20)==0)
      return 2;
    return 3;
  }
  
  
//  private int tokenLength;
//  private int lexerPosition;
  
  public void discard() {
    byteProvider.discard();
  }
  
  public void restart() {
    byteProvider.restart();
  }

  public void unwind(int count) {
    int pos=byteProvider.tokenLimit();
    for(int i=count;i>1;i--) {
      byte b = byteProvider.getByte(pos);
      pos+=bytesForChar(b);
    }
    byte last = byteProvider.getByte(pos);
    newLine = last == '\n'
      || last == '\r';
    pos+=bytesForChar(last);
    byteProvider.unwind(pos-byteProvider.tokenLimit());
  }

  public void reset() {
    byteProvider.reset();
  }

  public boolean previousWasNewLine() {
    return newLine;
  }

  public boolean read() throws IOException {
    return byteProvider.read();
  }
  
  /**
   * Return a view of the tokens recognized in the buffer. If the view has methods to modify
   * its limit, they must not be called.
   * @return a view of the tokens recognized in the buffer.
   * @see ReadableByteChannelWrapper#view
   */
  public T view() {
    return byteProvider.view();
  }
  
  public int lastChar() {
    return lastChar;
  }
  
  public LocationTracker getLocationProvider() {
    return byteProvider.getLocationProvider();
  }

  
  private boolean newLine = true;
  private final ByteProvider<T> byteProvider;
  private int lastChar = -1;
}

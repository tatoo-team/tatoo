/*
 * Created on 11 juil. 2005
 */
package fr.umlv.tatoo.runtime.buffer.impl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;

/**
 * A {@code ReadableByteChannelWrapper} wraps a {@link ReadableByteChannel} in order to allow
 * a lexer to process its content in order to have its tokens extracted.
 * 
 * @see fr.umlv.tatoo.runtime.lexer.Lexer
 * @author Julien
 */
/* This class cuts and pastes ReadableWrapper and both classes must be modified altogether */
public class ReadableByteChannelWrapper extends ByteProvider<ByteBuffer> {
  
  private ByteBuffer buffer;
  private ByteBuffer tokenBuffer;
  
  //private int availableTokensLimit;
  
  boolean newLine = true;
  
  private ReadableByteChannel readable;
  
  private final int increment;
  
  //private int availableTokensStart;
  
  private final int chunkSize;
  
  // 0 -> availableTokensStart : to discard
  // availableTokensStart -> availableTokensLimit : previously recognized tokens
  // availableTokensLimit -> lexerPosition : characters already processed by the
  // lexer
  // lexerPosition -> limit : available characters in the buffer
  
  /**
   * Constructs a {@link ReadableByteChannelWrapper} with default behavior.
   * @param readable the readable to wrap
   */
  public ReadableByteChannelWrapper(ReadableByteChannel readable, LocationTracker tracker) {
    this(true, 1000, 100, 100, readable, tracker);
  }
  
  /**
   * Construct a {@link ReadableByteChannelWrapper} used by the lexer to process the {@code ReadableByteChannel}.
   * The parameters specify how the underlying buffer which stores data in allocated and reallocated
   * when more space is needed.
   * @param direct if the {@link ByteBuffer} must be direct
   * @param capacity the initial capacity
   * @param increment how much to increment the size of the buffer when more space is required
   * @param chunkSize how many bytes are read at once is the input
   * @param readable the input
   * @param tracker the location tracker
   */
  public ReadableByteChannelWrapper(boolean direct, int capacity, int increment,int chunkSize, ReadableByteChannel readable, LocationTracker tracker) {
    super(tracker);
    this.readable = readable;
    this.increment = Math.max(chunkSize,increment);
    this.chunkSize = chunkSize;
    buffer=allocateBuffer(direct,capacity+1);
    buffer.limit(1);
    buffer.put((byte)'\n');
    this.tokenBuffer = buffer.asReadOnlyBuffer();
  }
  
  protected ByteBuffer allocateBuffer(boolean direct,int capacity) {
    if (direct)
      return ByteBuffer.allocateDirect(capacity);
    else
      return ByteBuffer.allocate(capacity);
  }
  
  @Override
  protected void resetImpl() {
    buffer.position(tokenBuffer.limit());
  }
  
  /*
   * @see fr.umlv.tatoo.runtime.buffer.CharacterBuffer
   */
  @Override
  protected void unwindImpl(int l) {
    int tokenLimit = tokenBuffer.limit()+l;
    tokenBuffer.limit(tokenLimit);
    buffer.position(tokenLimit-1);
    byte lastChar = buffer.get();
    newLine = lastChar == '\n'
      || lastChar == '\r';
  }
  
  public boolean previousWasNewLine() {
    return newLine;
  }
  
  public boolean hasRemaining() {
    return buffer.hasRemaining();
  }
  
  @Override
  public boolean read() throws IOException {
    if (hasRemaining())
      return true;
    ensureCapacity();
    buffer.mark();
    int read = readable.read(buffer);
    buffer.limit(buffer.position());
    buffer.reset();
    return read!=-1;
  }
  
  // Ensure that capacity is enough.
  // must be called with limit=position
  // At return, limit is set at capacity, position unchanged
  private void ensureCapacity() {
    
    int n = chunkSize;
    // if capacity is enough, do nothing
    if (buffer.capacity()>buffer.position()+n) {
      buffer.limit(buffer.capacity());
      return;
    }
    int tokenStartMinusOne = tokenBuffer.position()-1;
    int tokenLimit = tokenBuffer.limit();
    
    // if capacity after compacting is enough, compact and do nothing 
    if (buffer.capacity()>buffer.position()+n-tokenStartMinusOne) {
      buffer.position(tokenStartMinusOne);
      buffer.compact();
      tokenBuffer.position(1);
      tokenBuffer.limit(tokenLimit-tokenStartMinusOne);
      return;
    }
    
    ByteBuffer oldBuffer = buffer;
    
    buffer=allocateBuffer(oldBuffer.isDirect(),oldBuffer.capacity() + increment);
    oldBuffer.position(tokenStartMinusOne);
    buffer.put(oldBuffer);
    tokenBuffer = buffer.asReadOnlyBuffer();
    tokenBuffer.position(1);
    tokenBuffer.limit(tokenLimit-tokenStartMinusOne);
  }
  
  @Override
  protected int nextImpl() {
    return buffer.get();
  }
  
  public void restart(ReadableByteChannel readableByteChannel) {
    this.readable=readableByteChannel;
    newLine=true;
    buffer.position(0).limit(0);
    tokenBuffer.position(0).limit(0);
    locationClear();
  }
  
  public ReadableByteChannel getReadableByteChannel() {
    return readable;
  }
  
  @Override
  protected void discardImpl() {
    tokenBuffer.position(tokenBuffer.limit());
  }
  
  /**
   * Returns a String representation of the current token using the platform default encoding. This method
   * must only be used for debug since it is not efficient since it recreates the {@link java.nio.charset.CharsetDecoder}
   * at each call.
   * @return a String representation of the current token
   * @see Charset#defaultCharset()
   */
  @Override
  public String toString() {
    return Charset.defaultCharset().decode(tokenBuffer).toString();
  }
  
  /**
   * Returns a String representation of the current token using the specified encoding. This method
   * must only be used for debug since it is not efficient since it recreates the {@link java.nio.charset.CharsetDecoder}
   * at each call.
   * @param encoding the character encoding
   * @return a String representation of the current token
   * @see Charset
   */
  public String toString(String encoding) {
    CharBuffer buffer = Charset.forName(encoding).decode(tokenBuffer);
    return buffer.toString();
  }

  @Override
  byte getByte(int position) {
    return buffer.get(position);
  }

  @Override
  int limit() {
    return buffer.limit();
  }

  @Override
  int position() {
    return buffer.position();
  }
  
  @Override
  int tokenLimit() {
    return tokenBuffer.limit();
  }
  
  @Override
  protected void restartImpl() {
    int position = tokenBuffer.position();
    tokenBuffer.position(position-1);
    newLine=isEoln(tokenBuffer.get());
    tokenBuffer.limit(position);
    buffer.position(tokenBuffer.position());
  }
  
  /**
   * Returns the token part of the buffer as a ByteBuffer. The {@link ByteBuffer#limit()}
   * index must not be moved, the {@link ByteBuffer#position()} can be moved, but any data
   * before it can be discarded by the wrapper.
   * @return the token part of the buffer as a ByteBuffer
   */
  public ByteBuffer view() {
    return tokenBuffer;
  }
  
  public int lastChar() {
    if (buffer.position()==0)
      return -1;
    return buffer.get(buffer.position()-1);
  }
   
}

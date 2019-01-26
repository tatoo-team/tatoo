/*
 * Created on 11 juil. 2005
 */
package fr.umlv.tatoo.runtime.buffer.impl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;

import fr.umlv.tatoo.runtime.buffer.TokenBuffer;

/**
 * A {@code ReadableWrapper} wraps a {@link Readable} in order to allow
 * a lexer to process its content in order to have its tokens extracted.
 * 
 * @see fr.umlv.tatoo.runtime.lexer.Lexer
 * @author Julien
 */

/* This class is a cut&paste of ReadableByteChannelWrapper and both classes must be modified altogether */
public class ReadableWrapper extends AbstractLexerBuffer implements TokenBuffer<CharBuffer> {
  
  private CharBuffer buffer;
  private CharBuffer tokenBuffer;
  
  boolean newLine = true;
  
  private final Readable readable;
  
  private final int increment;
  
  private final int chunkSize;
  
  private final boolean direct;
  
  /**
   * Constructs a {@link ReadableWrapper} with default behavior.
   * 
   * @param readable the {@code Readable} to wrap
   * @param tracker the location tracker
   */
  public ReadableWrapper(Readable readable, LocationTracker tracker) {
    this(true, 1000, 100, 100, readable, tracker);
  }
  
  /**
   * Construct a {@link ReadableWrapper} used by the lexer to process the {@code Readable}.
   * The parameters specify how the underlying buffer which stores data in allocated and reallocated
   * when more space is needed.
   * @param direct if the {@link CharBuffer} must be direct
   * @param capacity the initial capacity
   * @param increment how much to increment the size of the buffer when more space is required
   * @param chunkSize how many bytes are read at once is the input
   * @param readable the input
   */
  public ReadableWrapper(boolean direct, int capacity, int increment,int chunkSize, Readable readable, LocationTracker tracker) {
    super(tracker);
    this.readable = readable;
    this.increment = Math.max(chunkSize,increment);
    this.chunkSize = chunkSize;
    this.direct = direct;
    allocateBuffer(capacity+1);
    buffer.limit(1);
    buffer.put('\n');
    this.tokenBuffer = buffer.duplicate();
  }
  
  private void allocateBuffer(int capacity) {
    if (direct)
      buffer = ByteBuffer.allocateDirect(2*capacity).asCharBuffer();
    else
      buffer = CharBuffer.allocate(capacity);
  }
  
  @Override
  protected void resetImpl() {
    buffer.position(tokenBuffer.limit());
  }

  @Override
  protected void unwindImpl(int l) {
    int tokenLimit = tokenBuffer.limit()+l;
    tokenBuffer.limit(tokenLimit);
    buffer.position(tokenLimit-1);
    char lastChar = buffer.get();
    newLine = lastChar == '\n'
      || lastChar == '\r';
  }
  
  public boolean previousWasNewLine() {
    return newLine;
  }
  
  public boolean hasRemaining() {
    return buffer.hasRemaining();
  }
  
  public boolean read() throws IOException {
    if (hasRemaining())
      return true;
    ensureCapacity();
    buffer.mark();
    int read = readable.read(buffer);
    buffer.limit(buffer.position());
    buffer.reset();
    if (read==-1)
      return false;
    return true;
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
    
    CharBuffer oldBuffer = buffer;
    
    allocateBuffer(oldBuffer.capacity() + increment);
    oldBuffer.position(tokenStartMinusOne);
    buffer.put(oldBuffer);
    tokenBuffer = buffer.duplicate();
    tokenBuffer.position(1);
    tokenBuffer.limit(tokenLimit-tokenStartMinusOne);
  }
  
  @Override
  protected int nextImpl() {
    return buffer.get();
  }
  
  @Override
  protected void discardImpl() {
    tokenBuffer.position(tokenBuffer.limit());
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
   * Returns the token part of the buffer as a CharBuffer. The {@link CharBuffer#limit()}
   * index must not be moved, the {@link CharBuffer#position()} can be moved, but any data
   * before it can be discarded by the wrapper.
   * @return the token part of the buffer as a CharBuffer
   */
  public CharBuffer view() {
    return tokenBuffer;
  }
  
  /**
   * Returns a String representation of the current token
   * @return a String representation of the current token
   */
  @Override
  public String toString() {
    return tokenBuffer.toString();
  }
  
  public int lastChar() {
    if (buffer.position()==0)
      return -1;
    return buffer.get(buffer.position()-1);
  }
  
}

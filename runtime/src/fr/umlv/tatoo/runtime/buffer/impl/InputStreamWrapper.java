/*
 * Created on 11 juil. 2005
 */
package fr.umlv.tatoo.runtime.buffer.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.IllegalCharsetNameException;

/**
 * A <code>ReadWrapper</code> wraps a <code>Reader</code> in order to allow
 * a lexer to process it and a developper to extract tokens recognized.
 * 
 * @see fr.umlv.tatoo.runtime.lexer.Lexer
 * @author <a href="mailto:jcervell@univ-mlv.fr">Julien Cervelle</a>
 * @version $Revision$
 */
public class InputStreamWrapper extends ByteProvider<InputStream> {

  private static class MutableByteArrayInputStream extends ByteArrayInputStream {

    MutableByteArrayInputStream(byte[] buf, int offset, int length) {
      super(buf, offset, length);
    }
    
    void position(int offset) {
      pos = offset;
    }
    
    void limit(int limit) {
      count = limit;
    }
    
    int position() {
      return pos;
    }
    
    int limit() {
      return count;
    }
    
    void setBuffer(byte[] buffer) {
      this.buf = buffer;
    }
    
  }
  
  private byte[] buffer;
  private final MutableByteArrayInputStream view;

  private int limit;

  private int lexerPosition;

  //private int availableTokensLimit;

  boolean newLine = true;

  private final InputStream inputStream;

  private final int increment;

  //private int availableTokensStart;

  private final int chunkSize;

  // 0 -> availableTokensStart : to discard
  // availableTokensStart -> availableTokensLimit : previously recognized tokens
  // availableTokensLimit -> lexerPosition : characters already processed by the
  // lexer
  // lexerPosition -> limit : available characters in the buffer

  /**
   * Constructs an {@link InputStreamWrapper} wrapper used by the lexer to process the {@code InputStream}.
   * @param inputStream the {@link InputStream} to wrap
   * @param tracker the location tracker
   */
  public InputStreamWrapper(InputStream inputStream, LocationTracker tracker) {
    this(1000, 100, 100, inputStream, tracker);
  }

  /**
   * Construct a {@link InputStreamWrapper} used by the lexer to process the {@code InputStream}.
   * The parameters specify how the underlying buffer which stores data in allocated and reallocated
   * when more space is needed.
   * @param capacity the initial capacity
   * @param increment how much to increment the size of the buffer when more space is required
   * @param chunkSize how many bytes are read at once is the input
   * @param inputStream the input
   * @param tracker the location tracker
   */
  public InputStreamWrapper(int capacity, int increment,int chunkSize, InputStream inputStream, LocationTracker tracker) {
    super(tracker);
    this.inputStream = inputStream;
    this.increment = increment;
    this.chunkSize = chunkSize;
    buffer = new byte[capacity+1];
    lexerPosition = limit = 1;
    buffer[0]=(byte)'\n';
    view = new MutableByteArrayInputStream(buffer,1,0);
  }
  
  @Override
  protected void unwindImpl(int l) {
    assert l <= limit : "out of bounds";
    int newPosition = view.limit();
    view.limit(newPosition+l);
    newLine = isEoln(buffer[newPosition - 1]);
    lexerPosition = newPosition;
  }
  
  @Override
  protected void resetImpl() {
    lexerPosition = view.limit();
  }

  public boolean previousWasNewLine() {
    return newLine;
  }
  
  public boolean hasRemaining() {
    return lexerPosition != limit;
  }
  
  @Override
  public boolean read() throws IOException {
    ensureCapacity();
    int read = inputStream.read(buffer, limit, buffer.length - limit);
    if (read==-1)
      return false;
    limit += read;
    return true;
  }

  private void ensureCapacity() {
    int n = chunkSize;
    if (limit+n < buffer.length)
      return;
    clean();
    if (limit+n < buffer.length)
      return;

    byte[] oldBuffer = buffer;
    buffer = new byte[Math.max(oldBuffer.length + increment,limit+n)];
    System.arraycopy(oldBuffer, 0, buffer, 0, oldBuffer.length);
    view.setBuffer(buffer);
  }

  @Override
  protected int nextImpl() {
    if (!hasRemaining())
      throw new IllegalStateException("No remaining character in buffer, read should be called");
    return buffer[lexerPosition++];
  }

  @Override
  protected void discardImpl() {
    view.position(view.limit());
  }

  private void clean() {
    int l = view.position()-1;
    System.arraycopy(buffer, l, buffer, 0, limit - l);
    limit -= l;
    lexerPosition -= l;
    view.position(1);
    view.limit(view.limit()-l);
  }
  
  /**
   * Returns a String representation of the current token using the platform default encoding. This method
   * must only be used for debug since it is not efficient since it recreates the {@link java.nio.charset.CharsetDecoder}
   * at each call.
   * @return a String representation of the current token
   * @see java.nio.charset.Charset#defaultCharset()
   */
  @Override public String toString() {
    return new String(buffer,view.position(),view.limit()-view.position());
  }
  
  /**
   * Returns a String representation of the current token using the specified encoding. This method
   * must only be used for debug since it is not efficient since it recreates the {@link java.nio.charset.CharsetDecoder}
   * at each call.
   * @param encoding the character encoding
   * @return a String representation of the current token
   * 
   * @throws IllegalCharsetNameException if the encoding is not supported
   * 
   * @see java.nio.charset.Charset
   */
  public String toString(String encoding) {
    try {
      return new String(buffer,view.position(),view.limit()-view.position(),encoding);
    } catch (UnsupportedEncodingException e) {
      throw new IllegalCharsetNameException(encoding);
    }
  }

  @Override
  byte getByte(int position) {
    return buffer[position];
  }

  @Override
  int limit() {
    return limit;
  }

  @Override
  int position() {
    return lexerPosition;
  }
  
  @Override
  protected void restartImpl() {
    lexerPosition = view.position();
    view.limit(view.position());
    newLine=isEoln(buffer[lexerPosition-1]);
  }

  
  /**
   * Returns the token part of the buffer as a InputStream. Data read from the stream are discarded by
   * the buffer.
   * @return the token part of the buffer as a InputStream.
   */
  public InputStream view() {
    return view;
  }
  
  @Override
  int tokenLimit() {
    return view.limit();
  }
  
  public int lastChar() {
    if (lexerPosition==0)
      return -1;
    return buffer[lexerPosition-1];
  }
  
}

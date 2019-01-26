/*
 * Created on 11 juil. 2005
 */
package fr.umlv.tatoo.runtime.buffer.impl;

import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;

import fr.umlv.tatoo.runtime.buffer.CharSequenceTokenBuffer;

/**
 * A {@code ReaderWrapper} wraps a {@link Reader} in order to allow
 * a lexer to process its content in order to have its tokens extracted.
 * 
 * @see fr.umlv.tatoo.runtime.lexer.Lexer
 * @author Julien
 */
public class ReaderWrapper extends AbstractLexerBuffer implements CharSequenceTokenBuffer, CharSequence {

  private char[] buffer;

  private int limit;

  private int lexerPosition;

  private int availableTokensLimit;

  boolean newLine = true;

  private final Reader reader;

  private final int increment;

  private int availableTokensStart;

  private final int chunkSize;

  // 0 -> availableTokensStart : to discard
  // availableTokensStart -> availableTokensLimit : previously recognized tokens
  // availableTokensLimit -> lexerPosition : characters already processed by the
  // lexer
  // lexerPosition -> limit : available characters in the buffer

  /**
   * Constructs a {@link ReaderWrapper} with default behavior.
   * 
   * @param reader the {@code Reader} to wrap
   * @param tracker the location tracker
   */
  public ReaderWrapper(Reader reader, LocationTracker tracker) {
    this(1000, 100, 100, reader, tracker);
  }

  /**
   * Construct a {@link ReaderWrapper} used by the lexer to process the {@code Reader}.
   * The parameters specify how the underlying buffer which stores data in allocated and reallocated
   * when more space is needed.
   * @param capacity the initial capacity
   * @param increment how much to increment the size of the buffer when more space is required
   * @param chunkSize how many bytes are read at once is the input
   * @param reader the input
   */
  public ReaderWrapper(int capacity, int increment,int chunkSize, Reader reader, LocationTracker tracker) {
    super(tracker);
    this.reader = reader;
    this.increment = increment;
    this.chunkSize = chunkSize;
    buffer = new char[capacity+1];
    availableTokensLimit = availableTokensStart = lexerPosition = limit = 1;
    buffer[0]='\n';
  }
  
  @Override
  protected void unwindImpl(int l) {
    assert l <= limit : "out of bounds";
    availableTokensLimit += l;
    newLine = isEoln(buffer[availableTokensLimit - 1]);
    lexerPosition = availableTokensLimit;
  }
  
  @Override
  protected void resetImpl() {
    lexerPosition = availableTokensLimit;
  }

  public boolean previousWasNewLine() {
    return newLine;
  }
  
  public boolean hasRemaining() {
    return lexerPosition != limit;
  }
  
  public boolean read() throws IOException {
    ensureCapacity();
    int read = reader.read(buffer, limit, buffer.length - limit);
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

    char[] oldBuffer = buffer;
    buffer = new char[Math.max(oldBuffer.length + increment,limit+n)];
    System.arraycopy(oldBuffer, 0, buffer, 0, oldBuffer.length);
  }

  @Override
  protected int nextImpl() {
    if (!hasRemaining())
      throw new IllegalStateException("No remaining character in buffer, read should be called");
    return buffer[lexerPosition++];
  }

  /*
   * @see fr.umlv.tatoo.runtime.buffer.TokenBuffer
   */
  @Override
  public void discardImpl() {
    availableTokensStart = availableTokensLimit;
  }

  private void clean() {
    int l = availableTokensStart-1;
    System.arraycopy(buffer, l, buffer, 0, limit - l);
    limit -= l;
    lexerPosition -= l;
    availableTokensLimit -= l;
    availableTokensStart = 1;
  }

  public CharSequence view() {
    return this;
  }
  
  /*
   * @see java.lang.CharSequence
   */
  public int length() {
    return availableTokensLimit-availableTokensStart;
  }

  public char charAt(int index) {
    return buffer[availableTokensStart+index];
  }

  public CharSequence subSequence(int start, int end) {
    return CharBuffer.wrap(buffer,availableTokensStart+start,end-start);
  }
  
  /**
   * Returns a String representation of the current token
   * @return a String representation of the current token
   */
  @Override public String toString() {
    //System.out.println(availableTokensLimit+" "+availableTokensStart+" "+lexerPosition+" "+limit);
    return new String(buffer,availableTokensStart,availableTokensLimit-availableTokensStart);
  }
  
  public int lastChar() {
    if (lexerPosition==0)
      return -1;
    return buffer[lexerPosition-1];
  }

  @Override
  protected void restartImpl() {
    lexerPosition = availableTokensStart;
    availableTokensLimit = availableTokensStart;
    newLine=isEoln(buffer[lexerPosition-1]);
  }
}

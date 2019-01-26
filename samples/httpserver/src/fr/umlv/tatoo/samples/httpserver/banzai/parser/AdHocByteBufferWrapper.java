/*
 * Created on 18 nov. 2005
 *
 */
package fr.umlv.tatoo.samples.httpserver.banzai.parser;

import java.nio.ByteBuffer;

import fr.umlv.tatoo.runtime.buffer.LexerBuffer;
import fr.umlv.tatoo.runtime.buffer.LocationProvider;

/**
 * A {@code ByteBufferWrapper} wraps a {@code ByteBuffer} in order to allow
 * a lexer to process its content in order to have its tokens extracted.
 * 
 * @see fr.umlv.tatoo.runtime.lexer.Lexer
 * @author Julien
 */
public class AdHocByteBufferWrapper implements LexerBuffer {
  /**
   * Constructs a {@link AdHocByteBufferWrapper} with no location provider.
   * 
   */  
  public AdHocByteBufferWrapper() {
    // do nothing
  }
  
  /** {@inheritDoc}
   * 
   *  This implementation always return null.
   */
  public LocationProvider getLocationProvider() {
    return null;
  }
  
  /** Must be called before the first start
   * @param previousWasNewLine
   */
  public void setPreviousWasNewLine(boolean previousWasNewLine) {
    this.previousWasNewLine=previousWasNewLine;
  }
  
  public void setByteBuffer(ByteBuffer buffer) {
    this.buffer=buffer;
  }
  
  public ByteBuffer getByteBuffer() {
    return buffer;
  }
  
  public void resetTokenOffsets() {
    tokenStart=tokenEnd=0;
  }
  
  public int getTokenStart() {
    return tokenStart;
  }
  public int getTokenEnd() {
    return tokenEnd;
  }
  
  public void unwind(int l) {
    //System.out.println("unwind "+l);
    tokenEnd+=l;
    buffer.position(tokenEnd-1);
    byte lastChar=buffer.get(); //position++
    previousWasNewLine=lastChar=='\n' || lastChar=='\r';
  }
  
  public void reset() {
    //buffer.position(tokenEnd);
    throw new UnsupportedOperationException(
      "reset is not allowed");
  }

  public boolean previousWasNewLine() {
    return previousWasNewLine;
  }

  public boolean hasRemaining() {
    return buffer.hasRemaining();
  }
  
  public boolean read() {
    throw new UnsupportedOperationException(
      "this buffer must be used in a non-blocking way");
  }

  public int next() {
    return buffer.get();
  }
  
  public void discard() {
    //System.out.println("discard "+tokenEnd);
    tokenStart=tokenEnd;
  }
  
  public int lastChar() {
    int pos=buffer.position();
    if (pos==0)
      return -1;
    return buffer.get(pos-1);
  }
  
  public void restart() {
    throw new UnsupportedOperationException("this buffer can't be restarted");
  }

  private boolean previousWasNewLine;
  private int tokenStart;
  private int tokenEnd;
  private ByteBuffer buffer;
}

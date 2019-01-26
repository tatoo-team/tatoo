/*
 * Created on 18 nov. 2005
 *
 */
package fr.umlv.tatoo.runtime.buffer.impl;

import java.nio.CharBuffer;

import fr.umlv.tatoo.runtime.buffer.TokenBuffer;

/**
 * A {@code CharBufferWrapper} wraps a {@code CharBuffer} in order to allow
 * a lexer to process its content in order to have its tokens extracted.
 * 
 * @see fr.umlv.tatoo.runtime.lexer.Lexer
 * @author Julien
 */
public class CharBufferWrapper extends AbstractLexerBuffer implements TokenBuffer<CharBuffer> {
  /**
   * Constructs a {@link CharBufferWrapper} which wraps specified {@code CharBuffer}.
   * 
   * @param buffer the {@code CharBuffer} to wrap
   * @param tracker the location tracker
   */  
  public CharBufferWrapper(CharBuffer buffer, LocationTracker tracker) {
    super(tracker);
    this.buffer=buffer;
    this.tokens=buffer.duplicate();
    tokens.limit(tokens.position());
    previousWasNewLine=true;
    buffer.mark();
  }
  
  @Override
  protected void unwindImpl(int l) {
    int tokenLimit=tokens.limit()+l;
    tokens.limit(tokenLimit);
    buffer.position(tokenLimit-1);
    char lastChar=buffer.get(); // position++
    previousWasNewLine=lastChar=='\n' || lastChar=='\r';
  }
  
  @Override
  protected void resetImpl() {
    buffer.position(tokens.limit());
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

  @Override
  protected int nextImpl() {
    char c=buffer.get();
    buffer.mark();
    return c;
  }
  
  public void compact() {
    int lexerStart = tokens.limit();
    tokens.compact();
    tokens.limit(tokens.position());
    tokens.position(0);
    
    buffer.position(buffer.position()-(lexerStart-tokens.limit()));
    buffer.mark();
    buffer.limit(buffer.capacity());
  }

  public void flip() {
    buffer.limit(buffer.position());
    buffer.reset();
  }
  
  @Override
  protected void discardImpl() {
    tokens.position(tokens.limit());
  }

  public CharBuffer view() {
    return tokens;
  }
  
  public int getTokenStart() {
    return tokens.position();
  }
  
  public int getTokenEnd() {
    return tokens.limit();
  }
  
  public int lastChar() {
    if (buffer.position()==0)
      return -1;
    return buffer.get(buffer.position()-1);
  }
  
  @Override
  protected void restartImpl() {
    int position = tokens.position();
    buffer.position(position);
    tokens.limit(position);
    if (position==0) {
      previousWasNewLine=true;
    } else {
      tokens.position(position-1);
      previousWasNewLine=isEoln(tokens.get());
    }
  }
  
  private boolean previousWasNewLine;
  
  private final CharBuffer tokens;
  private final CharBuffer buffer;
}

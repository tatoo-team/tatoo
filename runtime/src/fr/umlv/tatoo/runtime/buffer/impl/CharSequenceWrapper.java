/*
 * Created on 18 nov. 2005
 *
 */
package fr.umlv.tatoo.runtime.buffer.impl;

import fr.umlv.tatoo.runtime.buffer.CharSequenceTokenBuffer;

/**
 * A {@code CharSequenceWrapper} wraps a {@code CharSequence} in order to allow
 * a lexer to process its content in order to have its tokens extracted.
 * 
 * If this class is used with an mutable {@link CharSequence} like {@link StringBuilder},
 * the underlying mutable {@link CharSequence} must not be modified
 * during the lexing and all other mutable operations that
 * {@link StringBuilder#append(char) StringBuilder.append()}
 * will lead to unspecified behaviors.
 *  
 * This buffer doesn't support {@link #read()} operation thus
 * {@link fr.umlv.tatoo.runtime.lexer.Lexer#run()} will throw an
 * {@link UnsupportedOperationException}.
 * 
 * @see fr.umlv.tatoo.runtime.lexer.Lexer
 * @author Julien
 */
public class CharSequenceWrapper extends AbstractLexerBuffer implements CharSequenceTokenBuffer, CharSequence {
  /**
   * Constructs a {@link CharSequenceWrapper} which wraps specified {@code CharSequence}.
   * 
   * @param seq the {@code CharSequence} to wrap
   * @param tracker the location tracker
   */ 
  public CharSequenceWrapper(CharSequence seq, LocationTracker tracker) {
    super(tracker);
    this.seq=seq;
    newLine=true;
  }
  
  /** Returns the current position
   * @return the current position.
   */
  public int getPosition() {
    return position;
  }
  
  /** Returns the start offset of the token currently recognized.
   * @return the start offset of the token currently recognized.
   */
  public int getTokenStart() {
    return tokenStart;
  }
  
  /** Returns the end offset of the token currently recognized.
   * @return the end offset of the token currently recognized.
   */
  public int getTokenEnd() {
    return tokenEnd;
  }
  
  /** Returns the underlying char sequence.
   * @return the underlying char sequence.
   */
  public CharSequence getSequence() {
    return seq;
  }
  
  @Override
  protected void unwindImpl(int l) {
    tokenEnd += l;
    char lastChar=seq.charAt(tokenEnd-1);
    newLine = isEoln(lastChar);
    position=tokenEnd;
  }
  
  @Override
  protected void resetImpl() {
    position=tokenEnd;
  }

  public boolean previousWasNewLine() {
    return newLine;
  }

  public boolean hasRemaining() {
    return position<seq.length();
  }
  
  public boolean read() {
    return hasRemaining();
  }

  @Override
  protected int nextImpl() {
    return seq.charAt(position++);
  }
  
  @Override
  protected void discardImpl() {
    tokenStart=tokenEnd;
  }

  public CharSequence view() {
    return seq.subSequence(tokenStart,tokenEnd);
  }
  
  public char charAt(int position) {
    checkIndex(position);
    return seq.charAt(tokenStart+position);
  }
  
  public int length() {
    return tokenEnd-tokenStart;
  }
  
  public CharSequence subSequence(int start,int end) {
    checkIndex(start);
    checkIndex(end);
    if(start>end)
      throw new IndexOutOfBoundsException();
    return seq.subSequence(tokenStart+start, tokenStart+end);
  }
  
  private void checkIndex(int index) {
    if (index<0 || index>=tokenEnd-tokenStart)
      throw new IndexOutOfBoundsException();
  }
  
  public int lastChar() {
    if (position==0)
      return -1;
    return seq.charAt(position-1);
  }
  
  @Override
  protected void restartImpl() {
    position = tokenStart;
    tokenEnd = tokenStart;
    if (position==0)
      newLine=true;
    else {
      newLine=isEoln(seq.charAt(position-1));
    }
  }
  
  private int position;
  private int tokenStart;
  private int tokenEnd;
  private boolean newLine;
  
  private final CharSequence seq;
}

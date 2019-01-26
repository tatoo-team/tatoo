/*
 * Created on 14 janv. 2006
 *
 */
package fr.umlv.tatoo.samples.editor;

import javax.swing.text.BadLocationException;
import javax.swing.text.Segment;
import javax.swing.text.AbstractDocument.Content;

import fr.umlv.tatoo.runtime.buffer.CharSequenceTokenBuffer;
import fr.umlv.tatoo.runtime.buffer.LexerBuffer;
import fr.umlv.tatoo.runtime.buffer.LocationProvider;

public class ContentTokenBuffer implements CharSequenceTokenBuffer,LexerBuffer {
  public ContentTokenBuffer(Content content) {
    this.content=content;
    previousWasNewLine=true;
  }
  
  public LocationProvider getLocationProvider() {
    return null;
  }
  
  public void unwind(int l) {
    tokenEnd += l;
    char c=getCharAt(tokenEnd-1);
    previousWasNewLine = c== '\n';
    position = tokenEnd;
  }
  
  public void reset() {
    position = tokenEnd;
  }
  
  public void restart() {
    position=tokenEnd=tokenStart;
    if (position==0)
      previousWasNewLine=true;
    else
      previousWasNewLine=isEoln(getCharAt(position-1));
  }
  
  public void discard() {
    tokenStart=tokenEnd;
  }

  public CharSequence view() {
    try {
      return content.getString(tokenStart,tokenEnd-tokenStart);
    } catch (BadLocationException e) {
      throw new AssertionError(e);
    }
  }

  public boolean previousWasNewLine() {
    return previousWasNewLine;
  }

  public int tokenStart() {
    return tokenStart;
  }
  
  public int tokenEnd() {
    return tokenEnd;
  }
  
  private char getCharAt(int pos) {
    try {
      content.getChars(pos,1,text);
    } catch (BadLocationException e) {
      throw new AssertionError(e);
    }
    return text.array[text.offset];
  }
  
  public int next() {
    char c=getCharAt(position++);
    previousWasNewLine= c== '\n';
    return c;
  }

  public boolean hasRemaining() {
    return position<length();
  }
  
  public boolean read() {
    return false;
  }
  
  public int length() {
    // content has an extra '\n' at the end of its buffer
    return content.length()-1;
  }
  
  public int position(int position) {
    return position;
  }
  public int position() {
    return position;
  }
  public int lastChar() {
    return -1;
  }
  
  static boolean isEoln(int c) {
    return c=='\n'||c=='\r';
  }
  
  private int position;
  private int tokenStart;
  private int tokenEnd;
  private boolean previousWasNewLine;
  private final Segment text=new Segment();
  private final Content content;
}

package fr.umlv.tatoo.runtime.buffer.impl;

import fr.umlv.tatoo.runtime.buffer.LocationProvider;
import fr.umlv.tatoo.runtime.util.IntArrayList;

/** A class that is able to map buffer position to text location.
 *
 * @author Julien Cervelle
 */
public class LocationTracker implements LocationProvider {
  private final IntArrayList newLines;
  private int columnNumber,lineNumber;
  private int unwindedColumnNumber,unwindedLineNumber;
  private int discardedColumnNumber,discardedLineNumber;
  private int advance,tokenlen;
  private int tokenoffset=-1;

  /**
   * Creates a location tracker.
   */
  public LocationTracker() {
    newLines = new IntArrayList();
  }
  
  /** Update the columnNumber and lineNumber location.
   *  This method is called by method
   *  {@link fr.umlv.tatoo.runtime.buffer.LexerBuffer#next()}
   *  of a buffer to update line and column of the current tracker. 
   * 
   * @param character the current character of the buffer. 
   * 
   * @see fr.umlv.tatoo.runtime.buffer.LexerBuffer#next()
   */
  public void bufferNext(int character) {
    advance++;
    if (character=='\n') {
      newLines.add(advance);
      columnNumber=0;
      lineNumber++;
    }
    else
      columnNumber++;
  }
  
  /** Update the current and unwinded location.
   *  This method is called by method
   *  {@link fr.umlv.tatoo.runtime.buffer.LexerBuffer#unwind(int)}
   *  of a buffer to update line and column of the current tracker. 
   * 
   * @param count the number of characters to unwind
   * 
   * @see fr.umlv.tatoo.runtime.buffer.LexerBuffer#unwind(int)
   */
  public void bufferUnwind(int count) {
    tokenlen+=count;
    tokenoffset = findGreatestLessThan(tokenlen);
    if (tokenoffset != -1) {
      unwindedColumnNumber = tokenlen-newLines.get(tokenoffset);
      unwindedLineNumber= discardedLineNumber+tokenoffset+1;
      newLines.trim(tokenoffset+1);
    } else {
      unwindedColumnNumber+=count;
      newLines.clear();
    }
    columnNumber = unwindedColumnNumber;
    lineNumber = unwindedLineNumber;
    advance = 0;
  }
  
  /* performs a binary search in newLines, to find the greatest
   * index which newLines[i]<=l
   */
  private int findGreatestLessThan(int l) {
    if (newLines.isEmpty() || newLines.get(0)>l)
      return -1;
    int min=0;
    int max = newLines.size()-1;
    while(min!=max) {
      int average = (min+max+1)/2;
      int value = newLines.get(average);
      if(value>l)
        max=average-1;
      else
        min=average;
    }
    return min;
  }

  /** Update the unwinded location.
   *  This method is called by method
   *  {@link fr.umlv.tatoo.runtime.buffer.LexerBuffer#reset()}
   *  of a buffer to update line and column of the current tracker. 
   * 
   * @see fr.umlv.tatoo.runtime.buffer.LexerBuffer#reset()
   */
  public void bufferReset() {
    //tokenoffset = findGreatestLessThan(tokenlen);
    // should not be usefull since it has correct value from last unwind or initialization
    newLines.trim(tokenoffset+1);
    advance=tokenlen;
    columnNumber = unwindedColumnNumber;
    lineNumber = unwindedLineNumber;
  }
  
  /** Set the current and unwinded location to zero.
   */
  public void clear() {
    newLines.clear();
    tokenlen=advance=0;
    tokenoffset=-1;
    discardedColumnNumber = unwindedColumnNumber = columnNumber=0;
    discardedLineNumber = unwindedLineNumber = lineNumber=0;
  }

  /**
   * Returns the current column number in the stream.
   * @return the column number. 
   */
  public int getColumnNumber() {
    return columnNumber;
  }
  
  /** Changes the current column number.
   * @param columnNumber the new value.
   */
  public void setColumnNumber(int columnNumber) {
    this.columnNumber = columnNumber;
  }
  
  /**
   * Returns the current line number in the stream.
   * @return the line number. 
   */
  public int getLineNumber() {
    return lineNumber;
  }

  /** Changes the current line number.
   * @param lineNumber the new line number
   */
  public void setLineNumber(int lineNumber) {
    this.lineNumber = lineNumber;
  }
  
  /**
   * Returns the column number of the last unwind operation.
   * @return the column number
   */
  public int getUnwindedColumnNumber() {
    return unwindedColumnNumber;
  }
  
  /** Changes the column number of the last unwind operation.
   * @param unwindedColumnNumber the new column number.
   */
  public void setUnwindedColumnNumber(int unwindedColumnNumber) {
    this.unwindedColumnNumber = unwindedColumnNumber;
  }

  /**
   * Returns the line number of the last unwind operation.
   * @return the line number
   */
  public int getUnwindedLineNumber() {
    return unwindedLineNumber;
  }
  
  /** Changes the line number of the last unwind operation.
   * @param unwindedLineNumber the new line number.
   */
  public void setUnwindedLineNumber(int unwindedLineNumber) {
    this.unwindedLineNumber = unwindedLineNumber;
  }
  
  public int getDiscardedColumnNumber() {
    return discardedColumnNumber;
  }
  
  public int getDiscardedLineNumber() {
    return discardedLineNumber;
  }
  
  public void setDiscardedColumnNumber(int discardedColumnNumber) {
    this.discardedColumnNumber = discardedColumnNumber;
  }
  
  public void setDiscardedLineNumber(int discardedLineNumber) {
    this.discardedLineNumber = discardedLineNumber;
  }

  public void bufferRestart() {
    newLines.clear();
    tokenlen=advance=0;
    tokenoffset=-1;
    unwindedColumnNumber = columnNumber=discardedColumnNumber;
    unwindedLineNumber = lineNumber=discardedLineNumber;
  }

  public void bufferDiscard() {
    assert columnNumber==unwindedColumnNumber && lineNumber==unwindedLineNumber:"discard during step";
    newLines.clear();
    tokenlen=advance=0;
    tokenoffset=-1;
    columnNumber=discardedColumnNumber = unwindedColumnNumber;
    lineNumber=discardedLineNumber=unwindedLineNumber;
  }
}

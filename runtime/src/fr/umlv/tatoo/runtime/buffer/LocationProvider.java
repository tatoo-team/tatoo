package fr.umlv.tatoo.runtime.buffer;

/** A class that is able to map buffer positions to text locations.
 *
 * @author Julien Cervelle
 */
public interface LocationProvider {
  /**
   * Returns the current column number in the stream.
   * @return the column number. 
   */
  public int getColumnNumber();
  
  /**
   * Returns the current line number in the stream.
   * @return the line number. 
   */
  public int getLineNumber();
  
  /**
   * Returns the column number of the last unwind operation.
   * @return the column number
   */
  public int getUnwindedColumnNumber();
  
  /**
   * Returns the line number of the last unwind operation.
   * @return the line number
   */
  public int getUnwindedLineNumber();
}

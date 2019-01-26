/*
 * Created on 18 nov. 2005
 *
 */
package fr.umlv.tatoo.runtime.buffer.impl;

import fr.umlv.tatoo.runtime.buffer.LexerBuffer;
import fr.umlv.tatoo.runtime.buffer.LocationProvider;

/** Abstract base class for buffer that can tracks line and
 *  column position in a text.
 *  This class use a {@link LocationTracker} as
 *  {@link LocationProvider}
 *  
 * @author Remi Forax
 * 
 * @see LocationTracker
 */
public abstract class AbstractLexerBuffer implements LexerBuffer {
  /** Create a buffer with a location tracker.
   * @param locationTracker the location tracker may be null
   */
  public AbstractLexerBuffer(LocationTracker locationTracker) {
    this.locationTracker=locationTracker;
  }
  
  /**
   * Performs unwind operation on buffer without taking care of locations
   * @see LexerBuffer#unwind(int)
   * @param count the number of characters to unwind 
   */
  protected abstract void unwindImpl(int count);
  
  /**
   * Performs unwind operation on buffer without taking care of locations
   * @see LexerBuffer#unwind(int)
   * @param count the number of characters to unwind 
   */
  public final void unwind(int count) {
    unwindImpl(count);
    if (locationTracker!=null)
      locationTracker.bufferUnwind(count);
  }
  
  
  protected abstract void discardImpl();
  
  public final void discard() {
    discardImpl();
    if (locationTracker!=null)
      locationTracker.bufferDiscard();
  }
  
  protected abstract void restartImpl();
  
  public final void restart() {
    restartImpl();
    if (locationTracker!=null)
      locationTracker.bufferRestart();
  }

  /**
   * Returns next character in buffer without taking care of locations
   * @return the next character in the buffer
   * @throws IllegalStateException when called while hasRemaining returns false
   * 
   * @see LexerBuffer#next()
   */
  protected abstract int nextImpl();
  
  public final int next() {
    int character = nextImpl();
    if (locationTracker!=null)
      locationTracker.bufferNext(character);
    return character;
  }
  
  /**
   * Moves current position back to first non unwinded character without taking care of locations
   * @see LexerBuffer#reset()
   */
  protected abstract void resetImpl();
  
  public final void reset() {
    resetImpl();
    if (locationTracker!=null)
      locationTracker.bufferReset();
  }
  
  /** Service method that can be called by the current buffer to update the
   *  underlying location tracker.
   *  This method sets columns and lines numbers to zero.  
   * 
   * @see LocationProvider
   */
  protected final void locationClear() {
    if (locationTracker!=null)
      locationTracker.clear();
  }
  
  /** Returns the location tracker.
   * @return the location tracker or null if the buffer
   *         was initialized with no location tracker.
   */
  public LocationTracker getLocationProvider() {
    return locationTracker;
  }
  
  static boolean isEoln(int c) {
    return c=='\n'||c=='\r';
  }
  
  private final LocationTracker locationTracker;
}

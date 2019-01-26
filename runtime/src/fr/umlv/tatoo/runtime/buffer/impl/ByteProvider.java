package fr.umlv.tatoo.runtime.buffer.impl;

import java.io.IOException;

import fr.umlv.tatoo.runtime.buffer.TokenBuffer;

/**
 * 
 * @author Julien Cervelle
 * 
 * @param <T> type of the items of the token buffer view.
 * 
 * @see UTF8Wrapper
 */
abstract class ByteProvider<T> extends AbstractLexerBuffer implements TokenBuffer<T> {
  ByteProvider(LocationTracker tracker) {
    super(tracker);
  }
  abstract int tokenLimit();
  abstract int position();
  abstract int limit();
  abstract byte getByte(int position);
  public abstract boolean read() throws IOException;
}

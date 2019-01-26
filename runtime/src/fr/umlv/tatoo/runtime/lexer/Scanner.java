package fr.umlv.tatoo.runtime.lexer;

import java.io.IOException;
import java.util.Iterator;

import fr.umlv.tatoo.runtime.buffer.LexerBuffer;

/**
 *  Provide a simple scanner that implements {@link Iterator} and {@link java.lang.Iterable}
 *  The buffer must support the {@link LexerBuffer#read read} operation.
 *  
 *  If you don't have an {@link RuleActivator activator} because
 *  you don't use the {@link fr.umlv.tatoo.runtime.parser.Parser parser}
 *  provided by Tatoo, you can use a {@link Tokenizer} instead.
 * 
 * @author Remi Forax
 *
 * @param <R> type of rules.
 * @param <B> type of buffer.
 * 
 * @see Tokenizer
 */
public class Scanner<R,B extends LexerBuffer> extends Tokenizer<R, B> implements Iterator<R>, Iterable<R> {
  private Scanner(LexerTable<R> lexerTable, B buffer,RuleActivator<? extends R> activator) {
    super(lexerTable, buffer);
    this.activator = activator;
  }
  
  public boolean hasNext() {
    try {
      return hasNext(activator.activeRules());
    } catch(IOException e) {
      throw new IllegalStateException(e);
    }
  }
  
  public R next() {
    try {
      return next(activator.activeRules());
    } catch(IOException e) {
      throw new IllegalStateException(e);
    }
  }
  
  /**
   * {@inheritDoc}
   * 
   * This implementation always returns {@code this}.
   */
  public Iterator<R> iterator() {
    return this;
  }
  
  public void remove() {
    throw new UnsupportedOperationException();
  }
  
  private final RuleActivator<? extends R> activator;
  
  public static <R,B extends LexerBuffer> Scanner<R,B> createScanner(LexerTable<R> lexerTable,B buffer,RuleActivator<? extends R> activator) {
    return new Scanner<R,B>(lexerTable,buffer,activator);
  }
}

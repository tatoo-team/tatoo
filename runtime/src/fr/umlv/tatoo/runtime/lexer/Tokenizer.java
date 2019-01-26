/*
 * Created on 19 janv. 2006
 *
 */
package fr.umlv.tatoo.runtime.lexer;

import java.io.IOException;
import java.util.Arrays;
import java.util.NoSuchElementException;

import fr.umlv.tatoo.runtime.buffer.LexerBuffer;
import fr.umlv.tatoo.runtime.lexer.rules.ActionProcessor;
import fr.umlv.tatoo.runtime.lexer.rules.ProcessReturn;

/** Provide a simple tokenizer that can be used to implement LL algorithm.
 *  The buffer must support the {@link LexerBuffer#read read} operation.
 *  
 *  If you want an iterator, use {@link Scanner} instead.
 *
 * @param <R> type of rules
 * @param <B> type of the buffer
 * 
 * @author Julien
 *
 * @see Scanner
 */
public class Tokenizer<R, B extends LexerBuffer> {
  Tokenizer(LexerTable<R> lexerTable, B buffer) {
    this.buffer = buffer;
    processor = new ActionProcessor<R>(lexerTable);
  }
  
  /** Creates a tokenizer that extracts character from a buffer and matches
   *  them against rules. The buffer must support the {@link LexerBuffer#read read} operation.
   *
   * @param <R> type of rules.
   * @param <B> type of buffer
   * 
   * @param lexerTable data table of the scanner.
   * @param buffer the buffer used to extract characters.
   *   the lexing process
   * @return a new scanner
   */
  public static <R,B extends LexerBuffer> Tokenizer<R,B> createTokenizer(
    LexerTable<R> lexerTable, B buffer) {
    return new Tokenizer<R,B>(lexerTable,buffer);
  }
  
  /** Creates a scanner that extracts character from a buffer and matches
   *  them against rules.
   *
   * @param <R> type of rules.
   * @param <B> type of buffer
   * 
   * @param lexerTable data table of the scanner.
   * @param buffer the buffer used to extract characters.
   *   the lexing process
   * @param blanks the tokens to be skipped
   * @return a new scanner
   */
  public static <R,B extends LexerBuffer> Tokenizer<R,B> createTokenizer(
    LexerTable<R> lexerTable, B buffer,R... blanks) {
    return new BlankSkippingTokenizer<R,B>(lexerTable,buffer,blanks);
  }
  
  /** Indicates if input contains one of the tokens passed as argument.
   *  
   *  A call to this method {@link LexerBuffer#discard() discards}
   *  the previous token from the buffer.  
   *  
   * @param rules the tokens to match
   * @return true if input contains one of these tokens
   */
  public boolean hasNext(R... rules) throws IOException {
    boolean val = findNext(rules);
    buffer.reset();
    return val;
  }
  
  /** Returns true if the next token verified one of the rules taken as argument.
   * @param rules rules that will be matched
   * @return true if the next token verified one of the rules taken as argument.
   * @throws IOException if the buffer can not the read
   */
  boolean findNext(R... rules) throws IOException {
    buffer.discard();
    ProcessReturn ret;
    for(;;) {
      ret = processor.step(buffer, rules);
      if (ret != ProcessReturn.MORE)
        break;
      if (!buffer.read()) {
        ret = processor.stepClose();
        break;
      }
    }
    switch(ret) {
      case TOKEN :
        return true;
      case ERROR :
      case NOTHING:
        return false;
      default:
    }
    
    throw new AssertionError("ActionProcessor.stepClose cannot return "+ret.name());
  }

  /** Returns the next matching rule.
   *  To extract the character corresponding to the rule,
   *  the developer needs to use the method {@link #getBuffer()}
   *  and the devoted method of the buffer.
   *  
   *  A call to this method {@link LexerBuffer#discard() discards}
   *  the previous token from the buffer.
   *  
   * @param rules the tokens to match
   * @return the next matching rule.
   * 
   * @see fr.umlv.tatoo.runtime.buffer.TokenBuffer#view()
   */
  public R next(R... rules) throws IOException {
    if (!findNext(rules)) {
      buffer.reset();
      throw new NoSuchElementException("no matching rules for "+Arrays.toString(rules));
    }
    buffer.unwind(processor.tokenLength());
    return processor.winningRule();
  }
  
  /**
   * Returns the rule successfully matched by a prior call to {@link #hasNext(R...) hasNext}. This
   * method can only be called after {@link #hasNext(R...) hasNext} has returned true.
   * @return the rule successfully matched by a prior call to {@link #hasNext(R...) hasNext}.
   * @see #hasNext(R...)
   */
  public R getNext() {
    buffer.unwind(processor.tokenLength());
    return processor.winningRule();
  }
   
  /** Returns the underlying buffer.
   * @return the underlying buffer.
   */
  public B getBuffer() {
    return buffer;
  }
  
  /**
   * Returns the rule tables for this process
   * @return the rule tables for this process
   */
  public LexerTable<R> getLexerTable() {
    return processor.getLexerTable();
  }
  
  /**
   * Reset the tokenizer to perform a new analysis. All current states are discarded.
   * The new buffer is the buffer supplied.
   */
  public void reset(B buffer) {
    this.buffer=buffer;
    processor.reset();
  }
  
  private static class BlankSkippingTokenizer<R, B extends LexerBuffer> extends Tokenizer<R,B> {
    private final R[] blanks;

    public BlankSkippingTokenizer(LexerTable<R> lexerTable, B buffer, R... blanks) {
      super(lexerTable, buffer);
      this.blanks = blanks;
    }
    
    @Override
    boolean findNext(R... rules) throws IOException {
      skipBlank();
      return super.findNext(rules);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean eof() throws IOException {
      skipBlank();
      return super.eof();
    }
    
    private void skipBlank() throws IOException {
      while(super.findNext(blanks)) {
        buffer.unwind(processor.tokenLength());
      }
      buffer.reset();
    }

  }

  /**
   * Indicated wether eod-of-file is reached.
   * @return true if end-of-file is reached
   * @throws IOException when an i/o error occurs
   */
  public boolean eof() throws IOException {
    return ! (buffer.hasRemaining() || buffer.read());
  }
  
  final ActionProcessor<R> processor;
  B buffer;
}

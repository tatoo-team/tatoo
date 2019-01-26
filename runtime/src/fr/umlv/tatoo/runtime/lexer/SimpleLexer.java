package fr.umlv.tatoo.runtime.lexer;


/** A simple interface that can be used to manipulate
 *  a lexer after its creation.
 *  
 *  To use the lexer in pull mode, one will use the method {@link #run()},
 *  in that case the underlying buffer used must implements the optional operation
 *  {@link fr.umlv.tatoo.runtime.buffer.LexerBuffer#read()}.
 *  
 *  To use the lexer in push mode, on will use {@link #step()} to proceed
 *  all available characters of the buffer and {@link #close()} when
 *  the whole text has been read.
 * 
 * @author Remi
 * 
 * @see Lexer
 */
public interface SimpleLexer {
   /**
    * Proceed all available characters from the underlying buffer.
    * 
    * In order to end the lexing, after several calls to {@link #step()}
    * the developer must call {@link #close()}.
    * 
    * During the execution of this method, changing the buffer has
    * no effect on the current execution.
    * A next call to this method will used the newly set buffer.
    *  
    * @see #close()
    * @see fr.umlv.tatoo.runtime.buffer.LexerBuffer#hasRemaining()
    */
   public void step();
   
   /**
    * Does all the lexing in one shot.
    * 
    * @throws UnsupportedOperationException if the optional operation
    *  {@link fr.umlv.tatoo.runtime.buffer.LexerBuffer#read()} 
    *  is not supported by the underlying buffer.
    *  
    * @see #step()
    */
   public void run();
   
   /**
    * Closes the lexing process.
    * This method must be called after some calls to {@link #step()}.
    * Note that this method is likely to recognize some supplementary tokens 
    * and thus to call {@link LexerListener#ruleVerified(Object, int, Object)}.
    *  during the lexing process, by example, if the lexer states doesn't accept
    *  the end of file.
    *  
    * During the execution of this method, changing the buffer has
    * no effect on the current execution.
    * A next call to this method will used the newly set buffer.
    *  
    * @see #step()
    * @see #run()
    */
   public void close();
}

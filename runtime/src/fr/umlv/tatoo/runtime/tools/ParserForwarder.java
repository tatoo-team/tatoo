package fr.umlv.tatoo.runtime.tools;

import fr.umlv.tatoo.runtime.buffer.LexerBuffer;
import fr.umlv.tatoo.runtime.lexer.LexerErrorForwarder;
import fr.umlv.tatoo.runtime.lexer.ForwardReturn;
import fr.umlv.tatoo.runtime.lexer.Lexer;
import fr.umlv.tatoo.runtime.lexer.LifecycleHandler;
import fr.umlv.tatoo.runtime.parser.ActionReturn;
import fr.umlv.tatoo.runtime.parser.SimpleParser;

/**
 * This class implements a lexer error forwarder and a lexer lifecycle handler.
 * The lexer error forwarder part forwards error to the parser branching mechanism
 * (see {@link SimpleParser#branchOnError(Object, String)}) or
 * the error recovery mechanism.
 * 
 * The lifecycle handler part forwards the {@link #lexerClosed(Lexer) close} (resp.
 * {@link #lexerReset(Lexer) reset}) event in order to
 * {@link SimpleParser#close() close} (resp. {@link SimpleParser#reset() reset})
 * the parser.
 * 
 * @author Remi Forax
 */
public class ParserForwarder<T,B extends LexerBuffer> implements LexerErrorForwarder<B>, LifecycleHandler<B> {
  /**
   * Creates a parser forwarder.
   * 
   * @param parser
   *          the parser that will be notified
   */
  public ParserForwarder(SimpleParser<T> parser) {
    this.parser = parser;
  }

  /**
   * {@inheritDoc}
   * 
   * This implementation tries to {@link SimpleParser#branchOnError(Object, String)}
   * @return {@code DISCARD} if lexer must discard input or {@code RETRY} otherwise
   */
  public ForwardReturn forwardUnexpectedCharacter(Lexer<B> lexer) {
      lexer.getBuffer().reset();
      ActionReturn ret = parser.branchOnError(null, "lexer forward error");
      if (ret==ActionReturn.KEEP || ret==ActionReturn.RELEX)
        return ForwardReturn.RETRY;
      else
        return ForwardReturn.DISCARD;
  }

  /**
   * {@inheritDoc}
   * 
   * This implementation simply tries to
   * {@link SimpleParser#branchOnError(Object, String)}.
   */
  public void forwardUnexpectedEndOfFile(Lexer<B> lexer) {
    ActionReturn ret;
    ret = parser.branchOnError(parser.getTable().getEof(),"lexer eof forward error");
    if (ret==ActionReturn.KEEP || ret==ActionReturn.RELEX)
      throw new IllegalStateException("policy should not return KEEP on end-of-input");
  }

  /**
   * {@inheritDoc}
   * 
   * This implementation reset the parser.
   */
  public void lexerReset(Lexer<B> lexer) {
    parser.reset();
  }

  /**
   * {@inheritDoc}
   * 
   * This implementation close the parser.
   */
  public void lexerClosed(Lexer<B> lexer) {
    parser.close();
  }

  private final SimpleParser<T> parser;

}
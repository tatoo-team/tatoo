package fr.umlv.tatoo.runtime.tools;

import java.util.Map;
import java.util.Set;

import fr.umlv.tatoo.runtime.buffer.LexerBuffer;
import fr.umlv.tatoo.runtime.lexer.LexerListener;
import fr.umlv.tatoo.runtime.lexer.LifecycleHandler;
import fr.umlv.tatoo.runtime.parser.ParserListener;
import fr.umlv.tatoo.runtime.parser.SimpleParser;

/** Glue between lexer and parser.
 *  How lexer's rules are transformed to parser's terminal
 *  is described by {@link ToolsTable}.
 *  
 *  A mini-tools processor is created using the factory method
 *  {@link #createProcessor(ToolsListener)}.
 *  This processor called a unique listener {@link ToolsListener}
 *  for both lexer events and parser events.
 *  
 *  This processor is itself a {@link ParserListener}
 *  and {@link #createLexerListener(SimpleParser, ToolsTable)} is able to create
 *  a {@link LexerListener} that submit its rules transformed to terminal to
 *  a parser.
 *  
 * @author Remi Forax
 *
 * @param <R> type of rules.
 * @param <B> type of buffer.
 * @param <T> type of terminals.
 * @param <N> type of non terminals.
 * @param <P> type of productions.
 */
public class ToolsProcessor<R,B extends LexerBuffer,T,N,P> implements ParserListener<T, N, P> {
  final ToolsListener<? super R,? super B,? super T,? super N,? super P> listener;
 
  transient B buffer;
  transient R lastRule;
  
  private ToolsProcessor(ToolsListener<? super R,? super B,? super T,? super N,? super P> listener) {
    this.listener=listener;
  }
  
  /** 
   *  Creates a new processor that acts as a parser listener and is able
   *  to create a {@link LexerListener lexer listener} and
   *  a {@link LifecycleHandler lifecycle handler}.
   *  
   * @param <R> type of rules
   * @param <B> type of buffer.
   * @param <T> type of terminals.
   * @param <N> type of non terminals
   * @param <P> type of productions.
   * @param listener a listener that will be called each time
   *  a rule is not send as a terminal,
   *  a shift/reduce is performed,
   *  the grammar is accepted. 
   * @return a new mini-tools processor. 
   */
  public static <R,B extends LexerBuffer,T,N,P> ToolsProcessor<R,B,T,N,P> createProcessor(ToolsListener<? super R,? super B,? super T,? super N,? super P> listener) {
    return new ToolsProcessor<R,B,T,N,P>(listener);
  }
  
  /** A {@link LexerListener lexer listener}
   *  that transer rule from lexer to terminal to the parser.
   */
  class LexerHandler implements LexerListener<R,B> {
    private final SimpleParser<? super T> parser;
    private final Map<? super R,? extends T> ruleToTerminalMap;
    private final Set<?> discardSet;
    private final Set<?> spawnSet;
    
    LexerHandler(SimpleParser<? super T> parser, ToolsTable<? super R,? extends T> table) {
      this.parser=parser;
      this.discardSet=table.getDiscardSet();
      this.spawnSet=table.getSpawnSet();
      this.ruleToTerminalMap=table.getRuleToTerminalMap();
    }
    
    public void ruleVerified(R rule, int lastTokenLength, B buffer) {
      ToolsProcessor.this.buffer=buffer;
        
      if (spawnSet.contains(rule)) {
        T terminal=ruleToTerminalMap.get(rule);
        if (terminal!=null) {
          lastRule=rule;
          boolean done = parser.push(terminal);
          if (!done) {
            buffer.reset();
            return;
          }
        } else {
          listener.comment(rule,buffer);
        }
      }
      if (discardSet.contains(rule))
        buffer.discard();
    }
  }
    
  /** Create a {@link fr.umlv.tatoo.runtime.tools.ToolsProcessor.LexerHandler}
   *  from a {@link ToolsTable} and a parser.
   *  
   * @param parser a parser.
   * @param table a tools table.
   * @return a new lexer handler.
   */
  public LexerListener<R,B> createLexerListener(SimpleParser<? super T> parser, ToolsTable<? super R,? extends T> table) {
    return new LexerHandler(parser, table);
  }
  
  public void shift(T terminal) {
    listener.shift(terminal,lastRule,buffer);
  }
  
  public void reduce(P production) {
    listener.reduce(production);
  }
  
  public void accept(N nonTerminal) {
    listener.accept(nonTerminal);
  }
}

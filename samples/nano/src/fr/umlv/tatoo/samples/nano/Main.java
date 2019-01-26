package fr.umlv.tatoo.samples.nano;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;

import fr.umlv.tatoo.runtime.buffer.impl.LocationTracker;
import fr.umlv.tatoo.runtime.buffer.impl.ReaderWrapper;
import fr.umlv.tatoo.runtime.lexer.LexerListener;
import fr.umlv.tatoo.runtime.lexer.RuleActivator;
import fr.umlv.tatoo.runtime.lexer.nano.LexerLifecycleHandler;
import fr.umlv.tatoo.runtime.lexer.nano.NanoLexer;
import fr.umlv.tatoo.runtime.lexer.nano.RuleFeeder;
import fr.umlv.tatoo.runtime.lexer.nano.SimpleRuleFeeder;
import fr.umlv.tatoo.samples.nano.lexer.LexerDataTable;
import fr.umlv.tatoo.samples.nano.lexer.RuleEnum;

public class Main {
  public static void main(String[] args) throws FileNotFoundException {
    Reader reader;
    if (args.length == 0)
      reader = new InputStreamReader(System.in);
    else
      reader = new FileReader(args[0]);
    
    ReaderWrapper buffer = new ReaderWrapper(reader, new LocationTracker());
    
    LexerLifecycleHandler lifecycleHandler = new LexerLifecycleHandler() {
      @Override
      public void lexerReset() {
        throw new AssertionError();
      }
      
      @Override
      public void lexerClosed() {
        System.out.println("lexer closed");
      }
    };
    
    final RuleEnum[] allRules = RuleEnum.values();
    
    RuleFeeder<ReaderWrapper> ruleFeeder =
      new SimpleRuleFeeder<RuleEnum, ReaderWrapper>(LexerDataTable.createTable(), new LexerListener<RuleEnum, ReaderWrapper>() {
        @Override
        public void ruleVerified(RuleEnum rule, int lastTokenLength, ReaderWrapper buffer) {
          System.out.println("rule verified "+rule+" value "+buffer.view());
          buffer.discard();
        }
      }, new RuleActivator<RuleEnum>() {
        @Override
        public RuleEnum[] activeRules() {
          return allRules;
        }
      });
    
    NanoLexer<ReaderWrapper> lexer =
      NanoLexer.createNanoLexer(buffer, ruleFeeder, lifecycleHandler);
    lexer.run();
  }
}

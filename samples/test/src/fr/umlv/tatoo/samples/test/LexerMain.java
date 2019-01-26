package fr.umlv.tatoo.samples.test;

import java.io.FileReader;
import java.io.IOException;

import fr.umlv.tatoo.runtime.buffer.TokenBuffer;
import fr.umlv.tatoo.runtime.lexer.LexerListener;
import fr.umlv.tatoo.runtime.lexer.LexerTable;
import fr.umlv.tatoo.runtime.tools.builder.Builder;
import fr.umlv.tatoo.samples.test.lexer.LexerDataTable;
import fr.umlv.tatoo.samples.test.lexer.RuleEnum;

public class LexerMain {

  public static void main(String[] args) throws IOException {
    if (args.length != 1) {
      System.err.println("Usage: LexerMain <file>");
      System.exit(1);
    }
    
    final LexerTable<RuleEnum> table = LexerDataTable.createTable();
    
    final LexerListener<RuleEnum, TokenBuffer<CharSequence>> listener = new LexerListener<RuleEnum, TokenBuffer<CharSequence>>() {
      public void ruleVerified(RuleEnum t, int lastTokenLength, TokenBuffer<CharSequence> buffer) {
        switch (t) {
        case value:
          System.out.println(buffer.view());
          buffer.discard();
          break;
        default:
          buffer.discard();
        }
      }
    };
    
    Builder.lexer(table).reader(new FileReader(args[0]))
      .listener(listener)
      .createLexer().run();
  }
}

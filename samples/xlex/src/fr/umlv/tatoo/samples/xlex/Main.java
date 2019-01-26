package fr.umlv.tatoo.samples.xlex;

import java.io.FileNotFoundException;
import java.io.FileReader;

import fr.umlv.tatoo.runtime.buffer.TokenBuffer;
import fr.umlv.tatoo.runtime.lexer.LexerListener;
import fr.umlv.tatoo.runtime.lexer.LexerTable;
import fr.umlv.tatoo.runtime.tools.builder.Builder;
import fr.umlv.tatoo.samples.xlex.lexer.LexerDataTable;
import fr.umlv.tatoo.samples.xlex.lexer.RuleEnum;

public class Main {
  public static void main(String[] args) throws FileNotFoundException {
    LexerTable<RuleEnum> table = LexerDataTable.createTable();
    
    FileReader input = new FileReader("samples/xlex/xlex-input.txt");
    
    LexerListener<RuleEnum, TokenBuffer<?>> listener = new LexerListener<RuleEnum, TokenBuffer<?>>() {
      public void ruleVerified(RuleEnum rule, int lastTokenLength,
          TokenBuffer<?> buffer) {
        switch (rule) {
        default:
          System.out.println(rule+": "+buffer.view());
          buffer.discard();
        }
      }
    };

    Builder.lexer(table).reader(input)
      .listener(listener).createLexer().run();
  }
}

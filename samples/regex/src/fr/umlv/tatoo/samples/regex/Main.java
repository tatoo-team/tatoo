package fr.umlv.tatoo.samples.regex;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

import fr.umlv.tatoo.runtime.buffer.TokenBuffer;
import fr.umlv.tatoo.runtime.lexer.LexerListener;
import fr.umlv.tatoo.runtime.lexer.LexerTable;
import fr.umlv.tatoo.runtime.tools.builder.Builder;
import fr.umlv.tatoo.samples.regex.lexer.LexerDataTable;
import fr.umlv.tatoo.samples.regex.lexer.RuleEnum;

public class Main {
  public static void main(String[] args) throws FileNotFoundException {
    LexerTable<RuleEnum> table = LexerDataTable.createTable();

    Reader input = new FileReader("regex-input.txt");
    
    LexerListener<RuleEnum, TokenBuffer<?>> listener = new LexerListener<RuleEnum, TokenBuffer<?>>() {
      public void ruleVerified(RuleEnum rule, int lastTokenLength,
          TokenBuffer<?> buffer) {
        switch (rule) {
        case comment:
          System.out.println(buffer.view());
          buffer.discard();
          break;
        default:
          buffer.discard();
        }
      }
    };
    
    Builder.lexer(table).reader(input)
      .listener(listener).createLexer().run();
  }

}

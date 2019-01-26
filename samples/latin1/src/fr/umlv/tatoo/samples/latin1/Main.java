package fr.umlv.tatoo.samples.latin1;


import java.io.FileInputStream;
import java.io.FileNotFoundException;

import fr.umlv.tatoo.runtime.buffer.impl.InputStreamWrapper;
import fr.umlv.tatoo.runtime.buffer.impl.LocationTracker;
import fr.umlv.tatoo.runtime.lexer.LexerListener;
import fr.umlv.tatoo.runtime.lexer.LexerTable;
import fr.umlv.tatoo.runtime.tools.builder.Builder;
import fr.umlv.tatoo.samples.latin1.lexer.LexerDataTable;
import fr.umlv.tatoo.samples.latin1.lexer.RuleEnum;

public class Main {
  public static void main(String[] args) throws FileNotFoundException {
    LexerTable<RuleEnum> table = LexerDataTable.createTable();

    InputStreamWrapper buffer =new InputStreamWrapper(
      new FileInputStream("latin1-input.txt"),new LocationTracker());
    
    LexerListener<RuleEnum, InputStreamWrapper> listener = 
      new LexerListener<RuleEnum, InputStreamWrapper>() {
      public void ruleVerified(RuleEnum rule, int lastTokenLength,
          InputStreamWrapper buffer) {
        switch (rule) {
        case comment:
          System.out.println(buffer.toString("iso8859-1"));
          buffer.discard();
          break;
        default:
          buffer.discard();
        }
      }
    };
    
    Builder.lexer(table).buffer(buffer)
      .listener(listener).createLexer().run();
  }

}

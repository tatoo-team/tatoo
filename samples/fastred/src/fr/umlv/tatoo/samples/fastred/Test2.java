package fr.umlv.tatoo.samples.fastred;

import fr.umlv.tatoo.runtime.buffer.impl.CharSequenceWrapper;
import fr.umlv.tatoo.runtime.lexer.SimpleLexer;
import fr.umlv.tatoo.runtime.tools.builder.Builder;
import fr.umlv.tatoo.samples.fastred.lexer.LexerDataTable;

public class Test2 {
  public static void main(String[] args) {
    SimpleLexer lexer=Builder.lexer(LexerDataTable.createTable()).
      buffer(new CharSequenceWrapper("\n",null)).debugListener().createLexer();
     
    System.out.println("step");
    lexer.step();
    System.out.println("close");
    lexer.close();
  }
}

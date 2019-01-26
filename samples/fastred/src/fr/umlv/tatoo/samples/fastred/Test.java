package fr.umlv.tatoo.samples.fastred;

import fr.umlv.tatoo.runtime.parser.SimpleParser;
import fr.umlv.tatoo.runtime.tools.builder.Builder;
import fr.umlv.tatoo.samples.fastred.parser.ParserDataTable;
import fr.umlv.tatoo.samples.fastred.parser.TerminalEnum;

public class Test {
  public static void main(String[] args) {
    SimpleParser<TerminalEnum> parser=Builder.parser(ParserDataTable.createTable()).
      debugListener().createParser();
    
    System.err.println("--> step get");
    parser.step(TerminalEnum.get);
    System.err.println("--> step eoln");
    parser.step(TerminalEnum.eoln);
    System.err.println("--> step eoln");
    parser.step(TerminalEnum.eoln);
  }
}

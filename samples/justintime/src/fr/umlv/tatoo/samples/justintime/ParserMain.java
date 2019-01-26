package fr.umlv.tatoo.samples.justintime;

import java.util.Arrays;

import fr.umlv.tatoo.cc.parser.grammar.GrammarFactory;
import fr.umlv.tatoo.cc.parser.grammar.NonTerminalDecl;
import fr.umlv.tatoo.cc.parser.grammar.Priority;
import fr.umlv.tatoo.cc.parser.grammar.ProductionDecl;
import fr.umlv.tatoo.cc.parser.grammar.TerminalDecl;
import fr.umlv.tatoo.cc.parser.grammar.VariableDecl;
import fr.umlv.tatoo.cc.parser.grammar.VersionDecl;
import fr.umlv.tatoo.runtime.parser.ParserListener;
import fr.umlv.tatoo.runtime.parser.SimpleParser;

public class ParserMain {
  public static void main(String[] args) {
    GrammarFactory factory=new GrammarFactory();
    
    VersionDecl version = factory.createVersion("DEFAULT",null);
    Priority noPriority=Priority.getNoPriority();
    
    TerminalDecl value = factory.createTerminal("value",noPriority,false);
    
    NonTerminalDecl start = factory.createNonTerminal("start");
    
    factory.createProduction("start_values", start,Arrays.<VariableDecl>asList(start,value),noPriority,version);
    factory.createProduction("start_value", start,Arrays.asList(value),noPriority,version);
    
    ParserListener<TerminalDecl,NonTerminalDecl,ProductionDecl> listener=
      new ParserListener<TerminalDecl,NonTerminalDecl,ProductionDecl>() {
        public void shift(TerminalDecl terminal) {
          System.out.println("shift "+terminal);
        }
        public void reduce(ProductionDecl production) {
          System.out.println("production "+production);
        }
        public void accept(NonTerminalDecl nonTerminal) {
          System.out.println("accept "+nonTerminal);
        }
    };
    SimpleParser<TerminalDecl> parser=
      RuntimeParserFactory.createRuntimeParser(factory,start,version,listener);
    
    parser.step(value);
    parser.step(value);
    parser.step(value);
    parser.close();
  } 
}

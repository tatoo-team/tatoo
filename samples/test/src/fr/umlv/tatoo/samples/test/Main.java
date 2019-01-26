package fr.umlv.tatoo.samples.test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;

import fr.umlv.tatoo.samples.test.parser.NonTerminalEnum;
import fr.umlv.tatoo.samples.test.parser.VersionEnum;
import fr.umlv.tatoo.samples.test.tools.Analyzers;
import fr.umlv.tatoo.samples.test.tools.GrammarEvaluator;
import fr.umlv.tatoo.samples.test.tools.TerminalEvaluator;

public class Main {
  public static void main(String[] args) throws FileNotFoundException {
    final Reader reader;
    if (args.length>0) {
      reader = new FileReader(args[0]);
    } else {
      reader = new InputStreamReader(System.in);
    }
    final GrammarEvaluator grammarEvaluator=new IntGrammarEvaluator();
    final TerminalEvaluator<CharSequence> attributeEvaluator=new IntAttributeEvaluator();
    
    Analyzers.run(reader,attributeEvaluator,grammarEvaluator,NonTerminalEnum.start,VersionEnum.V2);
  }
}

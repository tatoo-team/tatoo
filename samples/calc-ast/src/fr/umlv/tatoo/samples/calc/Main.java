package fr.umlv.tatoo.samples.calc;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;

import fr.umlv.tatoo.samples.calc.tools.Analyzers;

public class Main {
  public static void main(String[] args) throws FileNotFoundException {
    Reader reader;
    if (args.length>0) {
      reader = new FileReader(args[0]);
    } else {
      reader = new InputStreamReader(System.in);
    }
    IntTerminalEvaluator terminalEvaluator = new IntTerminalEvaluator();
    IntGrammarEvaluator grammarEvaluator = new IntGrammarEvaluator();
    Analyzers.run(reader,terminalEvaluator,grammarEvaluator,null,null);
  }
}

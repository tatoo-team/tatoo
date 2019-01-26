package fr.umlv.tatoo.samples.java.javac;

import java.util.Map;

import fr.umlv.tatoo.runtime.buffer.impl.CharSequenceWrapper;
import fr.umlv.tatoo.runtime.tools.DataViewer;
import fr.umlv.tatoo.runtime.tools.SemanticStack;
import fr.umlv.tatoo.samples.java.ext.Semantics;
import fr.umlv.tatoo.samples.java.lexer.RuleEnum;
import fr.umlv.tatoo.samples.java.parser.NonTerminalEnum;
import fr.umlv.tatoo.samples.java.parser.ProductionEnum;
import fr.umlv.tatoo.samples.java.parser.TerminalEnum;
import fr.umlv.tatoo.samples.java.tools.AnalyzerProcessor;

public class PositionTrackerAnalyzerProcessor extends AnalyzerProcessor<CharSequenceWrapper,CharSequence> {
  private final PositionStack positionStack;
  
  private static final Map<ProductionEnum,Integer> numberOfTypedVariableMap=
    Semantics.createNumberOfTypedVariableMap();
  
  public PositionTrackerAnalyzerProcessor(
      TreeTerminalEvaluator terminalEvaluator,
      TreeGrammarEvaluator grammarEvaluator,
      PositionStack positionStack) {
    
    super(terminalEvaluator,
        grammarEvaluator,
        DataViewer.<CharSequence>getTokenBufferViewer(),
        new SemanticStack());
    this.positionStack=positionStack;
  }

  @Override
  public void shift(TerminalEnum terminal, RuleEnum rule, CharSequenceWrapper buffer) {
    positionStack.push(buffer.getTokenStart(), buffer.getTokenEnd());
    super.shift(terminal,rule,buffer);
  }
  
  @Override
  public void reduce(ProductionEnum production) {
    int slots=numberOfTypedVariableMap.get(production);
    positionStack.merge(slots);
    super.reduce(production);
  }
  
  @Override
  public void popTerminalOnError(TerminalEnum terminal) {
    positionStack.pop();
    super.popTerminalOnError(terminal);
  }
  
  @Override
  public void popNonTerminalOnError(NonTerminalEnum nonTerminal) {
    positionStack.pop();
    super.popNonTerminalOnError(nonTerminal);
  }
}

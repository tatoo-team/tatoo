package fr.umlv.tatoo.samples.calc;

import fr.umlv.tatoo.samples.calc.tools.TerminalEvaluator;

public class IntTerminalEvaluator implements TerminalEvaluator<CharSequence> {
  @Override
  public int value(CharSequence data) {
    return Integer.parseInt(data.toString());
  }
  
  @Override
  public void comment(CharSequence data) {
    System.out.println("found comment "+data);
  }
}

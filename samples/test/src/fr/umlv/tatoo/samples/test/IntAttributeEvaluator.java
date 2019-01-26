package fr.umlv.tatoo.samples.test;

import fr.umlv.tatoo.samples.test.tools.TerminalEvaluator;

public class IntAttributeEvaluator implements TerminalEvaluator<CharSequence> {
  public int value(CharSequence seq) {
    return Integer.parseInt(seq.toString());
  }
  public boolean comma(CharSequence seq) {
    return true;
  }
  @Override
  public void comment(CharSequence data) {
    System.out.println("A comment "+data);
  }
}
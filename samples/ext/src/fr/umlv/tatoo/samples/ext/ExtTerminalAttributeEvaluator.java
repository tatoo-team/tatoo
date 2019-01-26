package fr.umlv.tatoo.samples.ext;

import fr.umlv.tatoo.samples.ext.tools.TerminalEvaluator;

public class ExtTerminalAttributeEvaluator implements TerminalEvaluator<CharSequence> {
  public int value(CharSequence seq) {
    return Integer.parseInt(seq.toString());
  }
}

package fr.umlv.tatoo.samples.partial;


public class BranchTerminalEvaluator
  implements fr.umlv.tatoo.samples.partial.calc.tools.TerminalEvaluator<CharSequence>,
             fr.umlv.tatoo.samples.partial.expr.tools.TerminalEvaluator<CharSequence> {
	@Override
	public int value(CharSequence data) {
		return Integer.parseInt(data.toString());
	}
}

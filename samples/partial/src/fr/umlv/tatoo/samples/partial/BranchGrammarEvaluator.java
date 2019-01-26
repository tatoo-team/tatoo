package fr.umlv.tatoo.samples.partial;

public class BranchGrammarEvaluator
  implements fr.umlv.tatoo.samples.partial.calc.tools.GrammarEvaluator,
             fr.umlv.tatoo.samples.partial.expr.tools.GrammarEvaluator {
	@Override
	public void acceptStart() {
	    //do nothing
	}
	
	@Override
	public void acceptExpr(int expr) {
		//do nothing
	}

	@Override
	public void instr(int expr) {
		System.out.println("instr = "+expr);
	}

	@Override
	public int parens(int expr) {
		return expr;
	}

	@Override
	public int plus(int expr, int expr2) {
		return expr+expr2;
	}

	@Override
	public int value(int value) {
		return value;
	}
}

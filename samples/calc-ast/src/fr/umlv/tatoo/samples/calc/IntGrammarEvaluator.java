package fr.umlv.tatoo.samples.calc;

import fr.umlv.tatoo.samples.calc.tools.GrammarEvaluator;

public class IntGrammarEvaluator implements GrammarEvaluator {
  @Override
  public void acceptStart() {
    System.out.println("source recognized");
  }

  @Override
  public int expr_equals(int expr, int expr2) {
    return (expr==expr2)?1:0;
  }

  @Override
  public int expr_error() {
    return 0;
  }

  @Override
  public int expr_minus(int expr, int expr2) {
    return expr-expr2;
  }

  @Override
  public int expr_parens(int expr) {
    return expr;
  }

  @Override
  public int expr_plus(int expr, int expr2) {
    return expr+expr2;
  }

  @Override
  public int expr_star(int expr, int expr2) {
    return expr*expr2;
  }

  @Override
  public int expr_value(int value) {
    return value;
  }

  @Override
  public void line_expr(int expr) {
    System.out.println("value ="+expr);
  }
}

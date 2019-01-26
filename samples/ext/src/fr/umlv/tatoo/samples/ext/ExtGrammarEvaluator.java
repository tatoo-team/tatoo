package fr.umlv.tatoo.samples.ext;

import fr.umlv.tatoo.samples.ext.tools.GrammarEvaluator;

public class ExtGrammarEvaluator implements GrammarEvaluator {

  public void acceptStart() {
    // do nothing
  }
  
  public void start_list() {
    // do nothing
  }
  
  public void line_error() {
    System.err.println("line error");
  }

  public void line_expr(int expr) {
    System.out.println(expr);
  }

  public int expr_equals(int expr, int expr2) {
    return (expr==expr2)?1:0;
  }

  public int expr_error() {
    System.err.println("expr error");
    return 0;
  }

  public int expr_minus(int expr, int expr2) {
    return expr-expr2;
  }

  public int expr_par(int expr) {
    return expr;
  }

  public int expr_plus(int expr, int expr2) {
    return expr+expr2;
  }

  public int expr_star(int expr, int expr2) {
    return expr*expr2;
  }

  public int expr_value(int value) {
    return value;
  }
}

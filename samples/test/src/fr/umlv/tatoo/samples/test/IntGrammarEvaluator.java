package fr.umlv.tatoo.samples.test;

import fr.umlv.tatoo.samples.test.tools.GrammarEvaluator;

public class IntGrammarEvaluator implements GrammarEvaluator {

  
  public void line_expr(int expr,boolean commaOptional) {
    System.out.println("v1 result="+expr+" comma="+commaOptional);
  }
  public void line_expr2(int expr) {
    System.out.println("v2 result="+expr);
  }
  public int expr_value(int value) {
    return value;
  }
  public int expr_plus(int expr, int expr2) {
    return expr+expr2;
  }
  public int expr_equals(int expr, int expr2) {
    return (expr==expr2)?1:0;
  }
  public int expr_minus(int expr, int expr2) {
    return expr-expr2;
  }
  public int expr_star(int expr, int expr2) {
    return expr*expr2;
  }
  public int expr_par(int expr) {
    return expr;
  }
  public void line_error() {
    System.err.println("Syntax error");
  }
  public int expr_error() {
    return 0;
  }
  public void acceptExpr(int expr) {
    System.out.println(expr);
  }
  public void line_plus() {
    System.out.println("plus V2 line");
  }
  public void v2start(int expr) {
    System.out.println("v2 result "+expr);
  }
  public void acceptV2() {
    System.out.println("youpi youpi v2.0");
  }
  @Override
  public void acceptStart() {
    // do nothing
  }
}
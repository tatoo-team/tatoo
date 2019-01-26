package fr.umlv.tatoo.samples.test;

import java.util.List;

import fr.umlv.tatoo.samples.test.tools.tree.GrammarEvaluator;

public class TreeGrammarEvaluator implements GrammarEvaluator {
  private final List<? super Expr> exprs;

  public TreeGrammarEvaluator(List<? super Expr> exprs) {
    this.exprs = exprs;
  }

  public void acceptStart(List<Expr> start) {
    System.out.println("accept");
    
    exprs.addAll(start);
  }
  
  public List<Expr> start_list(List<Expr> slist) {
    return slist;
  }
  
  public Expr line_expr(Expr expr, boolean comma_optional) {
    return expr;
  }
  public Expr line_expr2(Expr expr) {
    return expr;
  }

  public Expr expr_value(int value) {
    return Expr.value(value);
  }

  public Expr expr_plus(Expr expr, Expr expr2) {
    return Expr.plus(expr,expr2);
  }

  public Expr expr_equals(Expr expr, Expr expr2) {
    return Expr.equals(expr,expr2);
  }

  public Expr expr_minus(Expr expr, Expr expr2) {
    return Expr.minus(expr,expr2);
  }

  public Expr expr_star(Expr expr, Expr expr2) {
    return Expr.star(expr,expr2);
  }

  public Expr expr_par(Expr expr) {
    return expr;
  }

  public Expr line_error() {
    System.out.println("Syntax error");
    return null;
  }

  public Expr expr_error() {
    return Expr.error();
  }

  public void acceptExpr(Expr expr) {
    exprs.add(expr);
  }

  public Expr line_plus() {
    return Expr.value(12);
  }

  public void v2start(Expr expr) {
    exprs.add(expr);
  }

  public void acceptV2() {
    // nothing
  }
}
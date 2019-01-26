/*
 * Created on 20 nov. 2005
 *
 */
package fr.umlv.tatoo.samples.test;

public abstract class Expr {
  public abstract int eval();
  
  public static Expr value(final int value) {
    return new Expr() {
      @Override public int eval() {
        return value;
      }
      @Override public String toString() {
        return Integer.toString(value);
      }
    };
  }
  public static Expr plus(final Expr left,final Expr right) {
    return new Expr() {
      @Override public int eval() {
        return left.eval()+right.eval();
      }
      @Override public String toString() {
        return '('+left.toString()+'+'+right+')';
      }
    };
  }
  public static Expr equals(final Expr left,final Expr right) {
    return new Expr() {
      @Override public int eval() {
        return (left.eval()==right.eval())?1:0;
      }
      @Override public String toString() {
        return '('+left.toString()+"=="+right+')';
      }
    };
  }
  public static Expr minus(final Expr left,final Expr right) {
    return new Expr() {
      @Override public int eval() {
        return left.eval()-right.eval();
      }
      @Override public String toString() {
        return '('+left.toString()+'-'+right+')';
      }
    };
  }
  public static Expr star(final Expr left,final Expr right) {
    return new Expr() {
      @Override public int eval() {
        return left.eval()*right.eval();
      }
      @Override public String toString() {
        return '('+left.toString()+'*'+right+')';
      }
    };
  }
  public static Expr error() {
    return new Expr() {
      @Override
      public int eval() {
        return 0;
      }
      @Override
      public String toString() {
        return "error";
      }
    };
  }
  
}

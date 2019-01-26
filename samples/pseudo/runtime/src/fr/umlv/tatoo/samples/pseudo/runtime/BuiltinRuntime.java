package fr.umlv.tatoo.samples.pseudo.runtime;

import static fr.umlv.tatoo.samples.pseudo.comp.BuiltInFunction.OperatorKind.*;

import java.util.Arrays;

import fr.umlv.tatoo.samples.pseudo.comp.builtin.BuiltinOperator;
import fr.umlv.tatoo.samples.pseudo.comp.builtin.SideEffect;

public class BuiltinRuntime {
  private BuiltinRuntime() {
    // no instantiation allowed
  }
  
  @BuiltinOperator(eq)
  public static Boolean eq(Object o1, Object o2) {
    return (o1==null)?o2==null:o1.equals(o2);
  }
  
  @BuiltinOperator(neq)
  public static Boolean neq(Object o1, Object o2) {
    return !eq(o1,o2);
  }
  
  @BuiltinOperator(lt)
  public static Boolean lt(Double d1, Double d2) {
    return d1<d2;
  }
  
  @BuiltinOperator(le)
  public static Boolean le(Double d1, Double d2) {
    return d1<=d2;
  }
  
  @BuiltinOperator(gt)
  public static Boolean gt(Double d1, Double d2) {
    return d1>d2;
  }
  
  @BuiltinOperator(ge)
  public static Boolean ge(Double d1, Double d2) {
    return d1>=d2;
  }
  
  @BuiltinOperator(lt)
  public static Boolean lt(Integer i1, Integer i2) {
    return i1<i2;
  }
  
  @BuiltinOperator(le)
  public static Boolean le(Integer i1, Integer i2) {
    return i1<=i2;
  }
  
  @BuiltinOperator(gt)
  public static Boolean gt(Integer i1, Integer i2) {
    return i1>i2;
  }
  
  @BuiltinOperator(ge)
  public static Boolean ge(Integer i1, Integer i2) {
    return i1>=i2;
  }
  
  @BuiltinOperator(neg)
  public static Boolean neg(Boolean b) {
    return !b;
  }
  
  @BuiltinOperator(band)
  public static Boolean band(Boolean b1, Boolean b2) {
    return b1.booleanValue() && b2.booleanValue();
  }
  
  @BuiltinOperator(bor)
  public static Boolean bor(Boolean b1, Boolean b2) {
    return b1.booleanValue() || b2.booleanValue();
  }
  
  @BuiltinOperator(plus)
  public static Integer plus(Integer i1, Integer i2) {
    return i1.intValue() + i2.intValue();
  }

  @BuiltinOperator(minus)
  public static Integer minus(Integer i1, Integer i2) {
    return i1.intValue() - i2.intValue();
  }
  
  @BuiltinOperator(star)
  public static Integer star(Integer i1, Integer i2) {
    return i1.intValue() * i2.intValue();
  }
  
  @BuiltinOperator(slash)
  public static Integer slash(Integer i1, Integer i2) {
    return i1.intValue() / i2.intValue();
  }
  
  @BuiltinOperator(mod)
  public static Integer mod(Integer i1, Integer i2) {
    return i1.intValue() % i2.intValue();
  }
  
  @BuiltinOperator(plus)
  public static Double plus(Double d1, Double d2) {
    return d1.doubleValue() + d2.doubleValue();
  }

  @BuiltinOperator(minus)
  public static Double minus(Double d1, Double d2) {
    return d1.doubleValue() - d2.doubleValue();
  }
  
  @BuiltinOperator(star)
  public static Double star(Double d1, Double d2) {
    return d1.doubleValue() * d2.doubleValue();
  }
  
  @BuiltinOperator(slash)
  public static Double slash(Double d1, Double d2) {
    return d1.doubleValue() / d2.doubleValue();
  }
  
  @BuiltinOperator(mod)
  public static Double mod(Double d1, Double d2) {
    return d1.doubleValue() % d2.doubleValue();
  }
  
  @BuiltinOperator(plus)
  public static String plus(String s, Object any) {
    return s+any;
  }
  
  @BuiltinOperator(plus)
  public static String plus(Object any, String s) {
    return any+s;
  }
  
  @BuiltinOperator(plus)
  public static String plus(String s1, String s2) {
    return s1+s2;
  }
  
  public static Integer length(Object[] anyArray) {
    return anyArray.length;
  }
  
  static String toString(Object any) {
    if (any instanceof Object[]) {
      return Arrays.deepToString((Object[])any);
    } else {
      return String.valueOf(any);
    }
  }
  
  @BuiltinOperator(print)
  @SideEffect
  public static void print(Object any) {
    System.out.println(toString(any));
  }
}

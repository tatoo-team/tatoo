package fr.umlv.tatoo.samples.pseudo.runtime;

import java.util.Scanner;

import fr.umlv.tatoo.samples.pseudo.comp.builtin.SideEffect;

public class BuiltinIORuntime {
  private static final ThreadLocal<Scanner> scanner=
    new ThreadLocal<Scanner>() {
    @Override
    protected Scanner initialValue() {
     return new Scanner(System.in); 
    }
  };
  
  private BuiltinIORuntime() {
    // no instantiation allowed
  }
  
  private static Scanner getScanner() {
    return scanner.get();
  }
  
  @SideEffect
  public static Boolean scanBoolean() {
    return getScanner().nextBoolean();
  }
  
  @SideEffect
  public static Boolean canScanBoolean() {
    return getScanner().hasNextBoolean();
  }
  
  @SideEffect
  public static Integer scanInt() {
    return getScanner().nextInt();
  }
  
  @SideEffect
  public static Boolean canScanInt() {
    return getScanner().hasNextInt();
  }
  
  @SideEffect
  public static Double scanDouble() {
    return getScanner().nextDouble();
  }
  
  @SideEffect
  public static Boolean canScanDouble() {
    return getScanner().hasNextDouble();
  }
  
  @SideEffect
  public static String scanWord() {
    return getScanner().next();
  }
  
  @SideEffect
  public static Boolean canScanWord() {
    return getScanner().hasNext();
  }
  
  @SideEffect
  public static String scanLine() {
    return getScanner().nextLine();
  }
  
  @SideEffect
  public static Boolean canScanLine() {
    return getScanner().hasNextLine();
  }
}

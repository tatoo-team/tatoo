package fr.umlv.tatoo.samples.pseudo.runtime;


public class BuiltinLangRuntime {
  private BuiltinLangRuntime() {
    // no instantiation allowed
  }
  
  public static Boolean parseBoolean(String text) {
    return Boolean.parseBoolean(text);
  }
  
  public static Integer parseInt(String text) {
    return Integer.parseInt(text);
  }
  
  public static Double parseDouble(String text) {
    return Double.parseDouble(text);
  }
  
  public static String toString(Object any) {
    return BuiltinRuntime.toString(any);
  }
  
  public static Integer convertToInt(Double d) {
    return (int)(double)d;
  }
  public static Integer convertToInt(Character c) {
    return (int)c;
  }
  
  public static Double convertToDouble(Integer i) {
    return (double)i;
  }
  public static Double convertToDouble(Character c) {
    return (double)c;
  }
  
  public static Character convertToChar(Double d) {
    return (char)(double)d;
  }
  public static Character convertToChar(Integer i) {
    return (char)(int)i;
  }
  
  public static Integer length(String s) {
    return s.length();
  }
  
  public static Character charAt(String s, Integer offset) {
    return s.charAt(offset);
  }
  
  public static Character[] convertToCharArray(String s) {
    Character[] charactersArray=new Character[s.length()];
    for(int i=0;i<s.length();i++) {
      charactersArray[i]=s.charAt(i);
    }
    return charactersArray;
  }
  
  public static String convertToString(Character[] charactersArray) {
    char[] charArray=new char[charactersArray.length];
    for(int i=0;i<charArray.length;i++) {
      charArray[i]=charactersArray[i];
    }
    return new String(charArray);
  }
  
  public static String substring(String s, Integer beginIndex) {
    return s.substring(beginIndex);
  }
  
  public static String substring(String s, Integer beginIndex, Integer endIndex) {
    return s.substring(beginIndex,endIndex);
  }
}

package fr.umlv.tatoo.cc.lexer.charset;

public class Triplet {
  private final CharacterSet first, second, third;
  
  public Triplet(CharacterSet first,
      CharacterSet second,
      CharacterSet third) {
    this.first = first;
    this.second = second;
    this.third = third;
  }
  
  public CharacterSet getFirst() {
    return first;
  }
  
  public CharacterSet getSecond() {
    return second;
  }
  
  public CharacterSet getThird() {
    return third;
  }
  
  @Override
  public String toString() {
    return "("+first+","+second+","+third+")";
  }
  
}
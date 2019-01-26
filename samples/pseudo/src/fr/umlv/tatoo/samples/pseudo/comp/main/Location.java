package fr.umlv.tatoo.samples.pseudo.comp.main;

public class Location {
  private int line;
  private int column;
  
  public Location(int line,int column) {
    this.line=line;
    this.column=column;
  }
  
  public int getLine() {
    return line;
  }
  
  public int getColumn() {
    return column;
  }
  
  @Override
  public String toString() {
    return line+","+column;
  }
}

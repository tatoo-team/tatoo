package fr.umlv.tatoo.cc.ebnf;

public class LineColumnLocation {
  private final int lineNumber;
  private final int columnNumber;
  
  public LineColumnLocation(int lineNumber,int columnNumber) {
    this.lineNumber=lineNumber;
    this.columnNumber=columnNumber;
  }
  
  public int getLineNumber() {
    return lineNumber;
  }
  public int getColumnNumber() {
    return columnNumber;
  }
}

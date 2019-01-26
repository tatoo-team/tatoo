package fr.umlv.tatoo.samples.editor;

public class Location {
  public Location(int start,int end) {
    this.start=start;
    this.end=end;
  }
  
  public int getStart() {
    return start;
  }
  public int getEnd() {
    return end;
  }
  
  private final int start;
  private final int end;
}

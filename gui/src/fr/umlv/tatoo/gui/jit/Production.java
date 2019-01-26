package fr.umlv.tatoo.gui.jit;


public class Production {
  
  final int rightSize;
  final NamedObject left;

  public int getRightSize() {
    return rightSize;
  }

  public Production(int rightSize,NamedObject left) {
    this.rightSize = rightSize;
    this.left = left;
  }
  
  public NamedObject getLeft() {
    return left;
  }
}
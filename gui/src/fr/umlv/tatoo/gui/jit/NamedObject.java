package fr.umlv.tatoo.gui.jit;


public class NamedObject {
  private final String name;

  public String getName() {
    return name;
  }

  NamedObject(String name) {
    this.name = name;
  }
  
  @Override
  public String toString() {
    return name;
  }
}

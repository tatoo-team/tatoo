package fr.umlv.tatoo.samples.pseudo.comp;

public interface Var extends Symbol {
  public String getName();
  public Type getType();
  public boolean isConstant();
}

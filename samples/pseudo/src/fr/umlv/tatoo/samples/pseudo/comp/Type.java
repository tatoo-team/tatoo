package fr.umlv.tatoo.samples.pseudo.comp;

import fr.umlv.tatoo.samples.pseudo.comp.Types.TypeVisitor;

public interface Type extends Scope.Item {
  public String getName();
  public Type getTypeArray();
  
  public <R,P,E extends Exception> R accept(TypeVisitor<R,P,E> visitor, P param) throws E;
}

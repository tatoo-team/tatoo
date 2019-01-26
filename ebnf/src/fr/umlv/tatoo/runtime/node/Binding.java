package fr.umlv.tatoo.runtime.node;

import java.util.List;

public interface Binding {
  public BindingSite getDeclaringSite();
  public Object getDomainObject();
  public List<? extends BindingSite> getRefereeList();
}

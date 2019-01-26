package fr.umlv.tatoo.samples.pseudo.comp.analysis;

import fr.umlv.tatoo.samples.pseudo.ast.Node;

public interface ErrorReporter {
  public boolean isOnError();
  
  public void error(Node node, String message);
  public void warning(Node node, String message);
}

package fr.umlv.tatoo.samples.java.javac;

import com.sun.tools.javac.tree.JCTree.JCVariableDecl;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;

public class MethodDeclarator {
  private final List<JCVariableDecl> parameters;
  private int dimension;
  private final Name name;
  
  public MethodDeclarator(Name name, List<JCVariableDecl> parameters, int dimension) {
    this.name=name;
    this.parameters=parameters;
    this.dimension=dimension;
  }
  
  public Name getIdentifier() {
    return name;
  }
  public List<JCVariableDecl> getParameters() {
    return parameters;
  }
  public int getDimension() {
    return dimension;
  }
}

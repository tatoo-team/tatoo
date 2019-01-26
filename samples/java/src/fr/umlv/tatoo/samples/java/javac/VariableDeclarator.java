package fr.umlv.tatoo.samples.java.javac;

import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.util.Name;

public class VariableDeclarator {
  private final int endPosition;
  private final Name identifier;
  private final int dimension;
  private final JCExpression initExpression;
  
  public VariableDeclarator(int endPosition, Name identifier, int dimension, JCExpression initExpression) {
    this.endPosition=endPosition;
    this.identifier=identifier;
    this.dimension=dimension;
    this.initExpression=initExpression;
  }
  
  public int getEndPosition() {
    return endPosition;
  }
  public Name getIdentifier() {
    return identifier;
  }
  public int getDimension() {
    return dimension;
  }
  public JCExpression getInitExpression() {
    return initExpression;
  }
}

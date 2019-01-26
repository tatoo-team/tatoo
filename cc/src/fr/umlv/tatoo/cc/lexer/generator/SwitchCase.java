/*
 * DestDecl.java
 *
 * Created: 28 avr. 2006
 */
package fr.umlv.tatoo.cc.lexer.generator;
//FIXME Remi genilogize

import java.util.List;

public class SwitchCase {
  public SwitchCase(int to, List<Integer> labels) {
    this.to = to;
    this.labels = labels;
  }

  public boolean isDefault() {
    return labels==null;
  }

  public void setDefault() {
    labels = null;
  }
  
  public int getState() {
    return to;
  }
  
  public List<Integer> getLabels() {
    return labels;
  }
  
  private final int to;
  private List<Integer> labels;
}

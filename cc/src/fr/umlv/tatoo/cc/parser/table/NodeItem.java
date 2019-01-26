package fr.umlv.tatoo.cc.parser.table;

import java.util.List;

import fr.umlv.tatoo.cc.parser.grammar.NonTerminalDecl;
import fr.umlv.tatoo.cc.parser.grammar.ProductionDecl;
import fr.umlv.tatoo.cc.parser.grammar.VariableDecl;

/** Represents a LR table item
 *  @param <I> type of the grammar item
 */
public abstract class NodeItem<I extends NodeItem<I>> {

  private final ProductionDecl core;
  private final int dotPlace;

  public NodeItem(ProductionDecl production, int dotPlace) {
    core= production;
    this.dotPlace= dotPlace;
  }
  
  public abstract I advance();

  @Override
  public int hashCode() {
    return core.hashCode() ^ dotPlace;
  }

  public int getDotPlace() {
    return dotPlace;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof NodeItem<?>))
      return false;
    NodeItem<?> item= (NodeItem<?>)o;
    return core.equals(item.getProduction()) && item.dotPlace == dotPlace;
  }

  public ProductionDecl getProduction() {
    return core;
  }

  public NonTerminalDecl getLeft() {
    return core.getLeft();
  }

  public List<? extends VariableDecl> getRight() {
    return core.getRight();
  }
    
  /**
   * @throws ArrayIndexOutOfBoundsException if dot at end of item
   * @return the variable after the dot or null if dot is after the last variable
   */
  public VariableDecl getDottedVariable() {
    if (dotPlace==getRight().size()) {
        return null;
    }
    return getRight().get(dotPlace);
  }
  
  @Override
  public String toString() {
    StringBuilder builder=new StringBuilder();
    
    builder.append(core.getLeft()).append(" ->");
    List<? extends VariableDecl> right=core.getRight();
    for (int i= 0; i < right.size(); i++) {
      if (i == dotPlace)
        builder.append(" .");
      builder.append(' ').append(right.get(i));
    }
    if (dotPlace == right.size())
      builder.append(" .");
      
    return builder.toString();
  }
  
}

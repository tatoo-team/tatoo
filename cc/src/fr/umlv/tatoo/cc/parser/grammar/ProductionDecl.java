package fr.umlv.tatoo.cc.parser.grammar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.umlv.tatoo.cc.common.generator.AbstractObjectId;

public class ProductionDecl extends AbstractObjectId implements PriorityOwner {
  public ProductionDecl(String id,NonTerminalDecl left, List<? extends VariableDecl> right, Priority priority, VersionDecl version) {
    super(id);
    this.left=left;
    this.right=Collections.unmodifiableList(new ArrayList<VariableDecl>(right));
    this.priority=priority;
    this.version=version;
  }
  public List<? extends VariableDecl> getRight() {
    return right;
  }
  public NonTerminalDecl getLeft() {
    return left;
  }
  public Priority getPriority() {
    return priority;
  }
  public VersionDecl getVersion() {
    return version;
  }
  
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(getId()).append(' ');
    builder.append(left.getId()).append(" ::= ");
    for (VariableDecl d : right)
      builder.append(d).append(' ');
    builder.setLength(builder.length()-1);
    return builder.toString();
  }
  
  private final NonTerminalDecl left;
  private final List<VariableDecl> right;
  private final Priority priority;
  private final VersionDecl version;
}

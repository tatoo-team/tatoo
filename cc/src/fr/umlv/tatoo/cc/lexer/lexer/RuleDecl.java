/*
 * Created on 6 juil. 2005
 */
package fr.umlv.tatoo.cc.lexer.lexer;

import fr.umlv.tatoo.cc.common.generator.AbstractObjectId;
import fr.umlv.tatoo.cc.lexer.regex.Regex;

public class RuleDecl extends AbstractObjectId  {
  public RuleDecl(String id,Regex main, Regex follow, int priority, boolean beginningOfLineRequired) {
    super(id);
    this.main = main;
    this.follow = follow;
    this.priority = priority;
    this.beginningOfLineRequired = beginningOfLineRequired;
  }
  
  public Regex getMainRegex() {
    return main;
  }

  public boolean isBeginningOfLineRequired() {
    return beginningOfLineRequired;
  }

  public Regex getFollowRegex() {
    return follow;
  }
  
  public int getPriority() {
    return priority;
  }
  
  @Override
  public String toString() {
    /*
    StringBuilder builder = new StringBuilder();
    builder.append(getId()).append(":\n");
    if (beginningOfLineRequired)
      builder.append("^\n");
    builder.append(main);
    if (follow!=null)
      builder.append("/\n").append(follow);
    return builder.toString();
    */
    return getId();
  }
  
  private final Regex main;
  private final Regex follow;
  private final int priority;
  private final boolean beginningOfLineRequired;
}

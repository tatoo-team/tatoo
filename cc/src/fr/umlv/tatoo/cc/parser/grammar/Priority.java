package fr.umlv.tatoo.cc.parser.grammar;

import fr.umlv.tatoo.cc.common.generator.AbstractObjectId;

/**
 * @author Remi Forax
 *
 */
public class Priority extends AbstractObjectId {
  
  public enum Associativity {LEFT,RIGHT,NONE,NON_ASSOCIATIVE}
  
  public Priority(String id,double priorityValue,Associativity associativity) {
    super(id);
    this.priorityValue= priorityValue;
    this.associativity= associativity;
  }
  
  public Associativity getAssociativity() {
    return associativity;
  }
  
  public int comparePriorityValues(Priority priority) {
    return Double.compare(this.priorityValue, priority.priorityValue);
  }
  
  @Override
  public String toString() {
    return "("+getId()+' '+priorityValue+','+associativityToString(associativity)+')';
  }
  
  private final double priorityValue;
  private final Associativity associativity;
  
  private static String associativityToString(Associativity associativity) {
    switch(associativity) {
    case LEFT:
      return "left";
    case RIGHT:
      return "right";
    case NONE:
      return "none";
    case NON_ASSOCIATIVE:
      return "non associative";
    default:
      throw new AssertionError("unknown associativity code");
    }
  }
  
  /**
   * @exception IllegalArgumentException 
   */
  public static Associativity parseAssociativity(String associativity) {
    if ("nonassoc".equals(associativity)) {
      return Associativity.NON_ASSOCIATIVE;
    }
    return Associativity.valueOf(associativity.toUpperCase());  
  }
  
  public static Priority getNoPriority() {
    return NO_PRIORITY;
  }
  
  private static final Priority NO_PRIORITY=
    new Priority("no_priority",Double.NaN,Associativity.NON_ASSOCIATIVE);

}
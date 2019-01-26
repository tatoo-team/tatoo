package fr.umlv.tatoo.cc.common.generator;

/**
 * Abstract class for all object that have a name.
 * This 
 *  
 * @author Remi
 *
 */
public abstract class AbstractObjectId implements ObjectId {
  /** Create an object with an id.
   * @param id the id of the object.
   */
  protected AbstractObjectId(String id) {
    this.id=id;
  }
  public String getId() {
    return id;
  }
  
  @Override
  public String toString() {
    return id+'('+getClass().getSimpleName()+')';
  }
  
  private final String id;
}

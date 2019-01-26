package fr.umlv.tatoo.cc.parser.grammar;

import fr.umlv.tatoo.cc.common.generator.AbstractObjectId;

/** Represents a version of a production.
 * 
 * @author Remi Forax
 */
public class VersionDecl extends AbstractObjectId {
  /** Create a vesion with an id and a parent version.
   * @param id
   * @param parent
   */
  public VersionDecl(String id,VersionDecl parent) {
    super(id);
    this.parent=parent;
  }
  
  /** Returns the parent version.
   * @return the parent version or null if it's the root version.
   */
  public VersionDecl getParent() {
    return parent;
  }
  
  private final VersionDecl parent;
}

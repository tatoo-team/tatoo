package fr.umlv.tatoo.cc.parser.table;

import java.util.Set;

import fr.umlv.tatoo.cc.common.generator.AbstractObjectId;
import fr.umlv.tatoo.cc.parser.grammar.VariableDecl;
import fr.umlv.tatoo.cc.parser.grammar.VersionDecl;
import fr.umlv.tatoo.cc.parser.parser.ActionDecl;
import fr.umlv.tatoo.cc.parser.parser.RegularTableActionDecl;

public class StateMetadataDecl extends AbstractObjectId {
  /**
   * Construct on objet used to declare state metadata
   * @param name the name of runtime object
   * @param compatibleVersions set of version compatible with the state, of null, if all are compatible
   * @param associated associated terminal or non terminal
   */
  public StateMetadataDecl(String name,Set<? extends VersionDecl> compatibleVersions,VariableDecl associated,RegularTableActionDecl defaultReduce) {
    super(name);
    this.associated = associated;
    this.compatibleVersions = compatibleVersions;
    this.defaultReduce = defaultReduce;
  }
    
  public Set<? extends VersionDecl> getCompatibleVersions() {
    return compatibleVersions;
  }
  
  public ActionDecl getDefaultReduce() {
    return defaultReduce;
  }
  
  public boolean isUnversionedReduce() {
    return defaultReduce==null;
  }
  
  public VariableDecl getAssociated() {
    return associated;
  }
  
  public boolean isFullversion() {
    return compatibleVersions==null;
  }
  
  private final Set<? extends VersionDecl> compatibleVersions;
  private final VariableDecl associated;
  private final RegularTableActionDecl defaultReduce;
  
  @Override
  public String toString() {
    return "associated: "+associated+" compatible versions: "+compatibleVersions;
  }
}

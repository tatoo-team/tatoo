package fr.umlv.tatoo.cc.parser.table;

import java.util.HashMap;
import java.util.Set;

import fr.umlv.tatoo.cc.common.generator.ObjectId;
import fr.umlv.tatoo.cc.common.util.Pair;
import fr.umlv.tatoo.cc.parser.grammar.VariableDecl;
import fr.umlv.tatoo.cc.parser.grammar.VersionDecl;
import fr.umlv.tatoo.cc.parser.parser.RegularTableActionDecl;

public class StateMetadataFactory {
  /**
   * Create a state meta-data.
   * @param compatibleVersions set of version compatible with the state, of null, if all are compatible
   * @param variable associated terminal or non terminal
   */
  public StateMetadataDecl create(Set<? extends VersionDecl> compatibleVersions,VariableDecl variable,RegularTableActionDecl defaultReduce) {
    StateMetadataMap map=versionsMap.get(compatibleVersions);
    if (map==null) {
      map=new StateMetadataMap(versionsMap.size(),compatibleVersions);
      versionsMap.put(compatibleVersions,map);
    }
    return map.createStateMetadata(variable,defaultReduce);
  }
  
  static class StateMetadataMap {
    public StateMetadataMap(int index,Set<? extends VersionDecl> compatibleVersions) {
      this.index=index;
      this.compatibleVersions=compatibleVersions;
    }
    public StateMetadataDecl createStateMetadata(VariableDecl variable,RegularTableActionDecl defaultAction) {
      Pair<VariableDecl,RegularTableActionDecl> pair = Pair.pair(variable,defaultAction);
      StateMetadataDecl stateMetadata=map.get(pair);
      if (stateMetadata==null) {
        String name=getId(variable)+"_"+getId(defaultAction);
        stateMetadata=new StateMetadataDecl(name,compatibleVersions,variable,defaultAction);
        map.put(pair,stateMetadata);
      }
      return stateMetadata;
    }
    
    private String getId(ObjectId variable) {
      String id=(variable!=null)?variable.getId():"null";
      return "metadata"+index+id;
    }
    
    private final int index;
    private final Set<? extends VersionDecl> compatibleVersions;
    private final HashMap<Pair<VariableDecl,RegularTableActionDecl>,StateMetadataDecl> map=
      new HashMap<Pair<VariableDecl,RegularTableActionDecl>,StateMetadataDecl>();
  }
  
  private final HashMap<Set<? extends VersionDecl>,StateMetadataMap> versionsMap=
    new HashMap<Set<? extends VersionDecl>,StateMetadataMap>();
}
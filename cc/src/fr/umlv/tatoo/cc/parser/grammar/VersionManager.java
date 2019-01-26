package fr.umlv.tatoo.cc.parser.grammar;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/** Manage version and dependencies between them.
 *  Version are created by {@link GrammarFactory#createVersion(String, VersionDecl)}.
 * 
 * @author Julien
 *
 */
public class VersionManager {
  private final LinkedHashMap<VersionDecl, LinkedHashSet<VersionDecl>> map =
    new LinkedHashMap<VersionDecl, LinkedHashSet<VersionDecl>>();
  private final Map<VersionDecl, LinkedHashSet<VersionDecl>> unmodifiableMap=
    Collections.unmodifiableMap(map);

  VersionManager() {
    // a version manager can only be created by GrammarFactory
  }
  
  /**
   * Returns the set of all version implying the passed argument version ({v, v=>version}).
   * If version==null, returns all versions
   * @param version the version
   * @return the set of all version implying the passed argument version
   */
  public Set<VersionDecl> getCompatibleVersion(VersionDecl version) {
    if (version==null)
      return unmodifiableMap.keySet();
    return unmodifiableMap.get(version);
  }

  /**
   * Returns the set of all version (without null) 
   * @return the set of all version (without null) 
   */
  public Set<VersionDecl> getAllVersions() {
    return unmodifiableMap.keySet();
  }

  /**
   * Register a new version and its implied version or null if none (version=>parent).
   * @param version the version
   * @param parent the parent version or null
   */
  void registerVersion(VersionDecl version, VersionDecl parent) {
    LinkedHashSet<VersionDecl> set = new LinkedHashSet<VersionDecl>();
    set.add(version);
    map.put(version, set);
    if (parent==null)
      return;
    for(HashSet<VersionDecl> entry:map.values()) {
      if (entry.contains(parent))
        entry.add(version);
    }
  }
}

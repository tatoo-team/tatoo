/*
 * Created on 27 nov. 2005
 *
 */
package fr.umlv.tatoo.runtime.tools;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.umlv.tatoo.runtime.parser.Action;
import fr.umlv.tatoo.runtime.parser.LookaheadMap;
import fr.umlv.tatoo.runtime.parser.ParserTable;

/** Helper methods to create a {@link LookaheadMap}.
 *  This class provide two implementations :
 *  <ol>
 *   <li>a generic one {@link #hashLookaheadMap(ParserTable)}
 *       that use a classical hash table.
 *   <li>a enum specific one {@link #enumLookaheadMap(ParserTable)}
 *       that only works if terminals are specified using an enum.
 *       By default, the Tatoo java backend generates terminals as an enum.
 *  </ol>
 * 
 * @author Remi Forax
 */
public class LookaheadMapFactory {
  private LookaheadMapFactory() {
    //helper class with factory methods
  }
  
  /** Returns a map that contains, for a specific version, the mapping
   * between a terminal and its lookahead set.
   * The terminal must be represented by an enum values.
   *  
   * @param <T> type of terminal enumeration.
   * @param <V> type of version.
   * 
   * @param table the table produced by Tatoo.
   * @return a lookahead map
   * 
   * @see #hashLookaheadMap(ParserTable)
   */
  
  public static <V extends Enum<V>,T extends Enum<T>> LookaheadMap<T,V> enumLookaheadMap(ParserTable<T,?,?,V> table) {
    int stateNb=table.getStateNb();
    T eofTerminal=table.getEof();
    V defaultVersion=table.getDefaultVersion();
    final EnumMap<V,EnumSet<T>[]> lookaheadMap=
      new EnumMap<V,EnumSet<T>[]>(defaultVersion.getDeclaringClass());
    for(V version:table.getVersions()) {
      @SuppressWarnings("unchecked") EnumSet<T>[] lookaheadSet = (EnumSet<T>[]) new EnumSet<?>[stateNb];
      for (int i = 0; i < stateNb; i++) {
        lookaheadSet[i] = EnumSet.noneOf(eofTerminal.getDeclaringClass());
      }
      fillLookaheads(lookaheadSet,table,version);
      lookaheadMap.put(version,lookaheadSet);
    }
    
    // distincts inner classes for faster map access, lookaheadMap if correctly typed.
    return new LookaheadMap<T,V>() {
      @Override
      public Set<? extends T> getLookahead(int state,V version) {
       return lookaheadMap.get(version)[state];
      }
    };
  }
  
  /** Returns a map that contains, for a specific version, the mapping
   * between a terminal and its lookahead set.
   *  
   * @param <T> type of terminal enumeration.
   * @param <V> type of version.
   * 
   * @param table the table produced by Tatoo.
   *.
   * @return a lookahead map
   */
  @SuppressWarnings("unchecked")
  public static <V,T> LookaheadMap<T,V> hashLookaheadMap(ParserTable<T,?,?,V> table) {
    
    int stateNb=table.getStateNb();
    final HashMap<V,HashSet<T>[]> lookaheadMap=new HashMap<V,HashSet<T>[]>();
    for(V version:table.getVersions()) {
      HashSet<T>[] lookaheadSet = (HashSet<T>[]) new HashSet<?>[stateNb];
      for (int i = 0; i < stateNb; i++) {
        lookaheadSet[i] = new HashSet<T>();
      }
      
      fillLookaheads(lookaheadSet,table,version);
      lookaheadMap.put(version,lookaheadSet);
    }
    
    //  distincts inner classes for faster map access, lookaheadMap if correctly typed.
    return new LookaheadMap<T,V>() {
      @Override
      public Set<? extends T> getLookahead(int state,V version) {
       return lookaheadMap.get(version)[state];
      }
    };
  }

  /* Populate the lookahead map.
   */
  private static <V,T> void fillLookaheads(Set<T>[] lookaheadSet,ParserTable<T,?,?,V> table,V version) {
    Map<T,? extends Action<T,?,V>[]> map=table.getTable();
    
    //FIXME Remi try to share sets between versions
    for (Map.Entry<T,? extends Action<T,?,V>[]> entry : map.entrySet()){
      T terminal= entry.getKey();
      Action<T,?,V>[] actTab = entry.getValue();
      int stateNb=table.getStateNb();
      for (int i = 0; i < stateNb; i++) {
        Action<T,?,V> action = actTab[i];
        if (!action.isError(version))
          lookaheadSet[i].add(terminal);
      }
    }
  }
}

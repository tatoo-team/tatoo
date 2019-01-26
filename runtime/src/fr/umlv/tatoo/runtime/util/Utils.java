package fr.umlv.tatoo.runtime.util;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Utils {
  /** Inverse a Map.
   * @param <K> type of the key.
   * @param <V> type of the value.
   * 
   * @param map a map
   * @return a new map which for every values of the map taken as argument
   *         associate the set of all keys.
   */
  public static <K,V> Map<V,Set<K>> inverse(Map<? extends K,? extends V> map, Class<?> keyClass, Class<?> valueClass) {
    Map<V,Set<K>> newMap=createMap(valueClass);
    for(Map.Entry<? extends K,? extends V> entry:map.entrySet()) {
      V second=entry.getValue();
      Set<K> set=newMap.get(second);
      if (set==null) {
        set=createSet(keyClass);
        newMap.put(second,set);
      }
      set.add(entry.getKey());
    }
    return newMap;
  }
  
  public static <K,V> Map<K,V> createMap(Class<?> keyClass) {
    return (keyClass.isEnum())? new EnumMap(keyClass): new HashMap<K,V>();
  }
  
  public static <E> Set<E> createSet(Class<?> elementClass) {
    return (elementClass.isEnum())? EnumSet.noneOf((Class)elementClass): new HashSet<E>();
  }
}

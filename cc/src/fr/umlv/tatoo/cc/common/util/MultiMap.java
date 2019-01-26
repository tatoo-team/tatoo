package fr.umlv.tatoo.cc.common.util;

import java.util.*;

/** Maps which associates an object to a set of values.
 * @param <K> type of the keys
 * @param <V> type of the values
 * 
 * @author remi
 * @author julien
 */
public class MultiMap<K,V> implements Map<K,Set<V>> {
  
  public MultiMap() {
    map=createMap();
  }
  
  protected Map<K,Set<V>> createMap() {
    return new HashMap<K,Set<V>>();
  }
  
  protected Set<V> createSet() {
    return new HashSet<V>();
  }
  
  /**
   * Clear all entries of this map.
   */
  public void clear() {
    map.clear();
  }
  
  public Collection<Set<V>> values() {
    return map.values();
  }
  
  /**
   * Adds en entry for a given key.  
   * @param key key of the entry.
   * @param value value of the entry.
   * @return true if the value is not already present.
   */
  public boolean add(K key, V value) {
    return getOrCreateSet(key).add(value);
  }
  
  /**
   * Adds all entries for a given key.  
   * @param key key of the entries.
   * @param values values corresponding to the key.
   * @return true if values have been added
   */
  public boolean addSet(K key, Set<? extends V> values) {
    return getOrCreateSet(key).addAll(values);
  }
  
  private Set<V> getOrCreateSet(K key) {
    Set<V> set=map.get(key);
    if (set==null) {
      set=createSet();
      map.put(key, set);
    }
    return set;
  }
  
  public Set<K> keySet() {
    return map.keySet();
  }
  
  /**
   * @return set of values associated with key
   */
  public Set<V> get(Object key) {
    Set<V> set=map.get(key);
    if (set!=null)
      return set;
    else 
      return Collections.<V>emptySet(); 
  }
  
  public Set<Map.Entry<K,Set<V>>> entrySet() {
    return map.entrySet();
  }
  
  /** This become the union of this and m
   * @param map the map that contains entry to add.
   * @return true if the current multi-map is modified.
   */
  public boolean addAll(MultiMap<? extends K,? extends V> map) {
    boolean changed=false;
    for (Map.Entry<? extends K,? extends Set<? extends V>> entry:map.entrySet()) {
      K key= entry.getKey();
      Set<? extends V> set=entry.getValue();
      changed=addSet(key, set) || changed;
    }
    return changed;
  }
  
  public Set<V> remove(Object key) {
    return map.remove(key);
  }
  
  @Override
  public String toString() {
    StringBuilder builder=new StringBuilder();
    for (K key:map.keySet()) {
      builder.append(key).append(" : ").append(get(key)).append('\n');
    }
    return builder.toString();
  }
  
  public int size() {
    return map.size();
  }

  public boolean isEmpty() {
    return map.isEmpty();
  }

  public boolean containsKey(Object key) {
    return map.containsKey(key);
  }

  public boolean containsValue(Object value) {
    return map.containsKey(value);
  }

  public Set<V> put(K key, Set<V> value) {
    return map.put(key,value);
  }

  public void putAll(Map<? extends K, ? extends Set<V>> m) {
    map.putAll(m);
  }
  
  private final Map<K,Set<V>> map;
}

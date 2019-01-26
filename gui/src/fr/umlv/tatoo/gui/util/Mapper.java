package fr.umlv.tatoo.gui.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Mapper {
  
  public static interface Function<T,U> {
    U apply(T entry);
  }
  
  private static final Function<Object,Object> ID = new Function<Object, Object>() {

    public Object apply(Object entry) {
      return entry;
    }
    
  };
  
  @SuppressWarnings("unchecked")
  public static <T> Function<T,T> id() {
    return (Function<T,T>)ID;
  }
  
  private static Function<? extends Set<?>,? extends HashSet<?>> setToHashSet = 
    new Function<Set<?>,HashSet<?>>() {
    
      public HashSet<Object> apply(Set<?> entry) {
        return new HashSet<Object>(entry);
      }
    
    };
    
  @SuppressWarnings("unchecked")
  public static <T> Function<Set<? extends T>,HashSet<T>> setToHashSet() {
    return (Function<Set<? extends T>,HashSet<T>>)setToHashSet;
  }
  
  public static <K1,V1,K2,V2> HashMap<K2,V2> apply(Map<K1,V1> map,
      Function<? super K1,? extends K2> keyFunction,
      Function<? super V1,? extends V2> valueFunction) {
    HashMap<K2,V2> newMap = new HashMap<K2, V2>();
    for(Map.Entry<K1,V1> entry : map.entrySet()) {
      K2 key = keyFunction.apply(entry.getKey());
      V2 value = valueFunction.apply(entry.getValue());
      newMap.put(key,value);
    }
    return newMap;
  }
  
  public static <T,U> HashSet<U> apply(Set<? extends T> source,Function<T,U> function) {
    HashSet<U> dest = new HashSet<U>();
    for(T t:source) {
      dest.add(function.apply(t));
    }
    return dest;
  }
  
  public static <T,U> U[] toArray(Collection<? extends T> source,U[] destination,Function<T,U> function) {
    Iterator<? extends T> iterator = source.iterator();
    for(int i=0;i<destination.length;i++)
      destination[i] =function.apply(iterator.next());
    return destination;
  }
}

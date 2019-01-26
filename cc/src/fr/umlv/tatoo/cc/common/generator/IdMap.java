package fr.umlv.tatoo.cc.common.generator;

import java.util.Collection;
import java.util.HashMap;

public class IdMap<O extends ObjectId> {
  public void put(O value) {
    put(value.getId(),value);
  }
  
  public void put(String id,O value) {
    ObjectId old=map.put(id,value);
    if (old!=null)
      throw new IllegalArgumentException("Two object declared with same id ("+id+
        "): "+value+" and "+old);
  }
  
  public boolean is(Class<? extends ObjectId> clazz,String id) {
    ObjectId value=map.get(id);
    if (value==null)
      return false;
    return clazz.isInstance(value);
  }
  
  public <T extends O> T get(Class<T> clazz,String id) {
    ObjectId value=map.get(id);
    if (value==null)
      return null;
    if (!clazz.isInstance(value))
      throw new ClassCastException(id+" is declared as a "+
        value.getClass().getSimpleName()+" and not as a "+clazz.getSimpleName());
    return clazz.cast(value);
  }
  
  public Collection<O> getAllValues() {
    return map.values();
  }
  
  @Override public String toString() {
    return map.toString();
  }
  
  private final HashMap<String,O> map=
    new HashMap<String,O>();
}

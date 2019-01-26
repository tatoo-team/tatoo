/*
 * Created on 8 mars 2006
 *
 */
package fr.umlv.tatoo.cc.tools.generator;

import java.util.HashMap;

public class Naming {
  public String name(String id) {
    Integer count=map.get(id);
    if (count==null) {
      map.put(id,2);
      return id;
    }
    map.put(id,count+1);
    return id+count;
  }
  
  private final HashMap<String,Integer> map=
    new HashMap<String,Integer>();
}

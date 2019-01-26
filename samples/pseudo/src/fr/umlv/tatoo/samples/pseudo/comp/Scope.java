package fr.umlv.tatoo.samples.pseudo.comp;

import java.util.LinkedHashMap;

public class Scope<I extends Scope.Item> {
  public interface Item {
    public String getName();
  }
  
  public static abstract class AbstractItem implements Item {
    private final String name;
    public AbstractItem(String name) {
      this.name=name;
    }
    
    @Override
    public String getName() {
      return name;
    }
    
    @Override
    public String toString() {
      return getName();
    }
  }
  
  private final Scope<I> parent;
  private final LinkedHashMap<String,I> itemMap=
    new LinkedHashMap<String,I>();
  
  public Scope() {
    this(null);
  }
  public Scope(Scope<I> parent) {
    this.parent=parent;
  }
  
  public Scope<I> getParent() {
    return parent;
  }
  
  public void add(I item) {
    itemMap.put(item.getName(), item);
  }
  
  public boolean localExists(String name) {
    return itemMap.containsKey(name);
  }
  
  public I lookupItem(String name) {
    I item=itemMap.get(name);
    if (item!=null || parent==null)
      return item;
    return parent.lookupItem(name);
  }
  
  public Iterable<I> getItems() {
    return itemMap.values();
  }
}

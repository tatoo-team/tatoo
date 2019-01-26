package fr.umlv.tatoo.samples.pseudo.comp.analysis;

import java.util.HashMap;

public class ConstantMap<E> {
  private final HashMap<E,Constant> map=
    new HashMap<E,Constant>();
  
  public static class Constant {
    private final Object constant;
    
    public Constant(Object constant) {
      this.constant=constant; 
    }
    
    public Object getConstant() {
      return constant;
    }
    
    @Override
    public String toString() {
      return String.valueOf(constant);
    }
  }
  
  public static Constant NO_CONST=new Constant(null) {
    @Override
    public Object getConstant() {
      throw new IllegalStateException("NO_CONST has no value");
    }
    @Override
    public String toString() {
      return "NO_CONST";
    }
  };
  
  public boolean isConstant(E element) {
    return map.containsKey(element);
  }
  
  /**
   * @return return value is never null.
   */
  public Constant getConstant(E element) {
    if (element==null)
      throw new IllegalArgumentException("null element");
    
    Constant constant=map.get(element);
    if (constant!=null)
      return constant;
    return NO_CONST;
  }
  
  public void putConstant(E element, Constant constant) {
    if (constant==null)
      throw new IllegalStateException("null is not an acceptable constant");
    if (constant==NO_CONST)
      throw new IllegalStateException("NO_CONST is not an acceptable constant");
    map.put(element,constant);
  }
}

/**
 * 
 */
package fr.umlv.tatoo.runtime.plugin;

public abstract class Context<P> {
  public abstract P getProduction();
  
  public abstract <R> R accept(Visitor<? extends R,P> visitor);
  
  public static class Shift<P> extends Context<P> {
    private final P production;
    private final int dot;

    public Shift(P production,int dot) {
      this.production=production;
      this.dot=dot;
    }
    
    @Override
    public <R> R accept(Visitor<? extends R,P> visitor) {
      return visitor.visit(this);
    }
    
    @Override
    public P getProduction() {
      return production;
    }
    public int getDot() {
      return dot;
    }
    
    @Override
    public boolean equals(Object obj) {
      if (!(obj instanceof Shift<?>)) {
        return false;
      }
      Shift<?> shift=(Shift<?>)obj;
      return dot==shift.dot &&
             production.equals(shift.production);
    }
    
    @Override
    public int hashCode() {
      return Integer.rotateLeft(production.hashCode(),dot);
    }
    
    @Override
    public String toString() {
      return "shift "+production+"("+dot+')';
    }
  }
  
  public static class Reduce<P> extends Context<P> {
    private final P production;
    
    public Reduce(P production) {
      this.production=production;
    }
    
    @Override
    public <R> R accept(Visitor<? extends R,P> visitor) {
      return visitor.visit(this);
    }
    
    @Override
    public P getProduction() {
      return production;
    }
    
    @Override
    public boolean equals(Object obj) {
      if (!(obj instanceof Reduce<?>)) {
        return false;
      }
      Reduce<?> reduce=(Reduce<?>)obj;
      return production.equals(reduce.production);
    }
    
    @Override
    public int hashCode() {
      return production.hashCode();
    }
    
    @Override
    public String toString() {
      return "reduce "+production;
    }
  }
  
  public static interface Visitor<R,P> {
    public R visit(Shift<P> shift); 
    public R visit(Reduce<P> reduce); 
  }
}
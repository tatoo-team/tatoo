package fr.umlv.tatoo.cc.common.util;

public class Pair<E,F> {
  private final E first;
  private final F second;
  
  public static <E,F> Pair<E,F> pair(E first, F second) {
    return new Pair<E,F>(first,second);
  }
  
  public Pair(E first, F second) {
    this.first = first;
    this.second = second;
  }
  public E getFirst() {
    return first;
  }
  public F getSecond() {
    return second;
  }
  @Override
  public int hashCode() {
    int firstHash = first==null?1876487629:first.hashCode();
    int secondHash = second==null?341768768:second.hashCode();
    return firstHash^(secondHash*(1<<16));
  }
  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Pair<?,?>))
      return false;
    Pair<?,?> pair = (Pair<?,?>)obj;
    return equals(first,pair.first)&&equals(second,pair.second);
  }
  private static boolean equals(Object one,Object two) {
    if (one==null)
      return two==null;
    else
      return one.equals(two);
  }
  @Override
  public String toString() {
    return "("+first+','+second+')';
  }
}

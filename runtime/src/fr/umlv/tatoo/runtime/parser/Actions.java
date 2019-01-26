package fr.umlv.tatoo.runtime.parser;

import java.util.Map;

public final class Actions {
  private Actions() {
    // prevent instantiation
  }
  
  public static <T,P,V> Action<T,P,V> createAccept() {
    return AcceptAction.getInstance();
  }
  
  public static <T,P,V> Action<T,P,V> createBranch(String message) {
    return new BranchAction<T,P,V>(message);
  }
  
  public static <T,P,V> Action<T,P,V> createEnter(T terminal,int shift) {
    return new EnterAction<T,P,V>(terminal,shift);
  }
  
  public static <T,P,V> Action<T,P,V> createError(String message) {
    return new ErrorAction<T,P,V>(message);
  }
  
  public static <T,P,V> Action<T,P,V> createExit() {
    return ExitAction.getInstance();
  }
  
  public static <T,P,V> Action<T,P,V> createReduce(P production,int rightSize,int[] gotos) {
    return new ReduceAction<T,P,V>(production,rightSize,gotos);
  }
  
  public static <T,P,V> Action<T,P,V> createShift(int shift) {
    return new ShiftAction<T,P,V>(shift);
  }
  
  public static <T,P,V> Action<T,P,V> createVersioned(Map<V,? extends Action<T,P,V>> actionMap) {
    return new VersionedAction<T,P,V>(actionMap);
  }
}

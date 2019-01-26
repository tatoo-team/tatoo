package fr.umlv.tatoo.samples.pseudo.comp.analysis;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

import fr.umlv.tatoo.samples.pseudo.comp.LocalVar;

public final class DASet {
  private final HashMap<LocalVar,Integer> localVarMap;
  private final BitSet bitSet;
  
  private DASet(HashMap<LocalVar,Integer> localVarMap, BitSet bitSet) {
    this.localVarMap=localVarMap;
    this.bitSet=bitSet;
  }
  
  public DASet() {
    this(new HashMap<LocalVar,Integer>(),new BitSet());
  }
  
  public DASet duplicate() {
    return new DASet(localVarMap,(BitSet)bitSet.clone());
  }
  
  public void reset(DASet set) {
    this.bitSet.clear();
    this.bitSet.or(set.bitSet);
  }
  
  private int getOrCreateSlot(LocalVar localVar) {
    Integer slotOrNull=localVarMap.get(localVar);
    if (slotOrNull==null) {
      int slot=localVarMap.size();
      localVarMap.put(localVar,slot);
      return slot;
    } else {
      return slotOrNull;
    }
  }
  
  public void mark(LocalVar localVar) {
    int slot=getOrCreateSlot(localVar);
    bitSet.set(slot);
  }
  
  public void unmark(LocalVar localVar) {
    int slot=getOrCreateSlot(localVar);
    bitSet.clear(slot);
  }
  
  public boolean isMarked(LocalVar localVar) {
    Integer slotOrNull=localVarMap.get(localVar);
    if (slotOrNull==null)
      return false;
    return bitSet.get(slotOrNull);
  }
  
  public void and(DASet set) {
    if (localVarMap!=set.localVarMap)
      throw new IllegalArgumentException("set not constructed with the same local variable map");
    bitSet.and(set.bitSet);
  }
  
  public void or(DASet set) {
    if (localVarMap!=set.localVarMap)
      throw new IllegalArgumentException("set not constructed with the same local variable map");
    bitSet.or(set.bitSet);
  }
  
  @Override
  public String toString() {
    if (bitSet.isEmpty())
      return "[]";
    
    LocalVar[] localVarArray=new LocalVar[localVarMap.size()];
    for(Map.Entry<LocalVar,Integer> entry:localVarMap.entrySet()) {
      localVarArray[entry.getValue()]=entry.getKey();
    }
    StringBuilder builder=new StringBuilder().append('[');
    for(int i=bitSet.nextSetBit(0);i>=0;i=bitSet.nextSetBit(i+1)) {
      builder.append(localVarArray[i]).append(", ");
    }
    builder.setLength(builder.length()-2);
    return builder.append(']').toString();
  }
}

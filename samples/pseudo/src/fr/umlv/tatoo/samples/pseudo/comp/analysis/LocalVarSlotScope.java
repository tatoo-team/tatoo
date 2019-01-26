package fr.umlv.tatoo.samples.pseudo.comp.analysis;

import java.util.HashMap;

import org.objectweb.asm.Label;

import fr.umlv.tatoo.samples.pseudo.comp.LocalVar;

public class LocalVarSlotScope {
  private final LocalVarSlotScope parent;
  private final HashMap<LocalVar,Slot> slotMap=
    new HashMap<LocalVar,Slot>();
  private int slotCount;
  
  public static class Slot {
    private final LocalVar localVar;
    private final int slotIndex;
    private final Label start;
    private Label end;
    
    Slot(LocalVar localVar,int slotIndex, Label start) {
      this.localVar=localVar;
      this.slotIndex=slotIndex;
      this.start=start;
      this.end=start;
    }
    
    void update(Label end) {
      this.end=end;
    }
    
    public LocalVar getLocalVar() {
      return localVar;
    }
    public int getSlotIndex() {
      return slotIndex;
    }
    public Label getStart() {
      return start;
    }
    public Label getEnd() {
      return end;
    }
  }
  
  public LocalVarSlotScope() {
    this(null);
  }
  
  public LocalVarSlotScope(LocalVarSlotScope parent) {
    this.parent=parent;
    if (parent!=null)
      this.slotCount=parent.slotCount;
  }
  
  public LocalVarSlotScope getParent() {
    return parent;
  }
  
  public Slot getSlot(LocalVar localVar, Label label) {
    Slot slot=slot(localVar);
    if (slot!=null) {
      slot.update(label);
      return slot;
    }
    slot=new Slot(localVar,slotCount++,label);
    slotMap.put(localVar,slot);
    return slot;
  }
  
  private Slot slot(LocalVar localVar) {
    if (parent!=null) {
      Slot slot=parent.slot(localVar);
      if (slot!=null)
        return slot;
    }
    return slotMap.get(localVar);
  }
  
  public Iterable<Slot> slots() {
    return slotMap.values();
  }
  
  @Override
  public String toString() {
    return slotMap.toString();
  }
}

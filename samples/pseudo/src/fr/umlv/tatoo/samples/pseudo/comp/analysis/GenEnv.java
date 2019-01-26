package fr.umlv.tatoo.samples.pseudo.comp.analysis;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

// there is one GenEnv by top level block
public class GenEnv {
  private final MethodVisitor methodVisitor;
  private final LoopStack<LoopContext> loopContextStack=
    new LoopStack<LoopContext>();
  private LocalVarSlotScope slotScope=
    new LocalVarSlotScope(); 
  
  public static class LoopContext {
    private final Label breakLabel;
    private final Label continueLabel;
    
    public LoopContext(Label breakLabel,Label continueLabel) {
      this.breakLabel=breakLabel;
      this.continueLabel=continueLabel;
    }
    
    public Label getBreakLabel() {
      return breakLabel;
    }
    public Label getContinueLabel() {
      return continueLabel;
    }
  }
  
  public GenEnv(MethodVisitor methodVisitor) {
    this.methodVisitor=methodVisitor;
  }
  
  public MethodVisitor getMethodVisitor() {
    return methodVisitor;
  }
  
  public LoopStack<LoopContext> getLoopContextStack() {
    return loopContextStack;
  }
  
  public LocalVarSlotScope getSlotScope() {
    return slotScope;
  }
  
  public void enterScope() {
    slotScope=new LocalVarSlotScope(slotScope);
  }
  
  public void exitScope() {
    slotScope=slotScope.getParent(); 
  }
}

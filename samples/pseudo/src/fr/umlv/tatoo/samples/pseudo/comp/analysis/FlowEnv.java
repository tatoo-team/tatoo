package fr.umlv.tatoo.samples.pseudo.comp.analysis;



public class FlowEnv {
  private boolean alive=true;
  private final DASet initSet;
  private final DASet readSet;
  private final LoopStack<LoopDA> loopStack;
  
  public static class LoopDA {
    private DASet breakInitSet;
    
    public void addBreakInitSet(DASet initSet) {
      if (this.breakInitSet==null) {
        this.breakInitSet=initSet;
        return;
      }
      this.breakInitSet.and(initSet);
    }
    
    // may returns null
    public DASet getBreakInitSet() {
      return breakInitSet;
    }
  }
  
  private FlowEnv(DASet initSet,DASet readSet,LoopStack<LoopDA> loopStack) {
    this.initSet=initSet;
    this.readSet=readSet;
    this.loopStack=loopStack;
  }
  
  public FlowEnv() {
    this(new DASet(),new DASet(),new LoopStack<LoopDA>());
  }
  
  public FlowEnv(FlowEnv flowEnv) {
    this(flowEnv.initSet.duplicate(),flowEnv.readSet.duplicate(),flowEnv.loopStack);
  }
  
  public boolean isAlive() {
    return alive;
  }
  public void setAlive(boolean alive) {
    this.alive=alive; 
  }
  
  public DASet getInitSet() {
    return initSet;
  }
  
  public DASet getReadSet() {
    return readSet;
  }
  
  public LoopStack<LoopDA> getLoopStack() {
    return loopStack;
  }
}

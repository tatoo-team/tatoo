package fr.umlv.tatoo.samples.java.javac;

import java.util.Arrays;

/** Stack that contains offset (of terminal and non terminal).
 *  There are two implementations depending if stack should only
 *  track start offset or start offset and end offset.
 *  
 * @author forax
 *
 * @see Start
 * @see StartEnd
 */
public abstract class PositionStack {
  int[] array;
  int top;
  
  protected PositionStack(int initialCapacity) {
    array=new int[initialCapacity];
  }
  
  void growIfNecessary() {
    if (array.length == top) {
      array=Arrays.copyOf(array, top*2);
    }
  }
  
  /** Clear the whole stack
   */
  public void clear() {
    top=0;
  }
  
  /** Push an offset pair to the stack.
   * @param startOffset start offset
   * @param endOffset end offset
   */
  public abstract void push(int startOffset, int endOffset);
  
  /** Pop an offset pair from the stack.
   */
  public abstract void pop();
  
  /** Merge offset pairs to one pair
   * @param slots number of pairs to merge.
   */
  public abstract void merge(int slots);
  
  /** Must only be called after a pop with a number of slot greater than zero.
   */
  public abstract int currentStartOffset();
  
  /** Stack specialized to handle the case where
   *  the start offset is stored in the stack.
   */
  public static class Start extends PositionStack {
    /** Create a new stack.
     */
    public Start() {
      super(32);
    }
    
    @Override
    public void push(int startOffset, int endOffset) {
      growIfNecessary();
      array[top++]=startOffset;
    }
    
    @Override
    public void pop() {
      top--;
    }
    
    @Override
    public void merge(int slots) {
      if (slots==0)
        return;
      top-=slots-1;
    }
    
    @Override
    public int currentStartOffset() {
      return array[top-1];
    }
  }
  
  /** Stack specialized to handle the case where
   *  the start offset and the end offset are store in the stack.
   */
  public static class StartEnd extends PositionStack {
    public StartEnd() {
      super(64);  //WARNING, must be a multiple of 2
    }
    
    @Override
    public void push(int startOffset, int endOffset) {
      growIfNecessary();
      int top=this.top;
      array[top]=startOffset;
      array[top+1]=endOffset;
      this.top=top+2;
    }
    
    @Override
    public void pop() {
      top-=2;
    }
    
    @Override
    public void merge(int slots) {
      if (slots==0)
        return;
      
      int endTop=top-1;
      top-=slots*2-2;
      array[top-1]=array[endTop];
    }
    
    @Override
    public int currentStartOffset() {
      return array[top-1];
    }
    
    /** Must only be called after a pop.
     */
    public int currentEndOffset() {
      return array[top-2];
    }
  }
}

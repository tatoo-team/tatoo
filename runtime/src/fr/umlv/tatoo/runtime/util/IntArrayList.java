package fr.umlv.tatoo.runtime.util;


/** A specific array list of integer that globally works as
 *  a stack to store parser state.
 *  
 *  @author Julien
 *
 */
public class IntArrayList implements ReadOnlyIntStack {
  
  private int[] data;
  private int height = 0;
  
  /** Creates the list with an initial capacity.
   * @param initialCapacity the initiali capacity
   */
  public IntArrayList(int initialCapacity) {
    data=new int[initialCapacity];
  }
  
  /** Creates the list with 10 as initial capacity.
   *  
   * @see #IntArrayList(int)
   */
  public IntArrayList() {
    this(10);
  }
  
  public IntArrayList duplicate() {
    IntArrayList list=new IntArrayList(height);
    System.arraycopy(data,0,list.data,0,height);
    list.height=height;
    return list;
  }

  /** Adds a value on top of the list.
   * @param value the value to add.
   */
  public void add(int value) {
    if (height == data.length) {
      int[] newData = new int[data.length*2];
      System.arraycopy(data, 0, newData, 0, data.length);
      data=newData;
    }
    data[height]=value;
    height++;
  }

  /** Removes the last values on top of the stack.
   * @param number the number of values to remove.
   */
  public void removeLast(int number) {
    assert number<=height : "empty stack";
    height-=number;
  }
  
  /**
   * Reduce the size of the stack to number
   * @param number the new size of the stack
   */
  public void trim(int number) {
    if (number>height)
      throw new IllegalArgumentException("trimming array to higher size");
    if (number<0)
      throw new IllegalArgumentException("negative array size");
    height=number;
  }
  
  /** Removes the last value on top of the stack and returns it.
   * @return the value on top of the stack.
   * 
   * @see #removeLast(int)
   */
  public int removeLast() {
    height--;
    return data[height];
  }

  public int last() {
    assert height>0 : "empty stack";
    return data[height-1];
  }
  
  /** Clear the whole list.
   */
  public void clear() {
    height = 0;
  }
  
  public int get(int index) {
    return data[index];
  }
  
  /** Changes the value at index index by a new one.
   *  This call assume that the value at index index if already allowed
   *  so it doesn't ensure a capacity, you can use {@link #add(int)} instead.
   * 
   * @param index index of value to set
   * @param value value to set
   */
  public void set(int index,int value) {
    data[index]=value;
  }
  
  public int size() {
    return height;
  }

  public boolean isEmpty() {
    return height==0;
  }
  
  @Override
  public String toString() {
    StringBuilder builder=new StringBuilder(height*3);
    for(int i=0;i<height;i++)
      builder.append(data[i]).append(',');
    if (height!=0)
      builder.setLength(builder.length()-1);
    return builder.toString();
  }
}

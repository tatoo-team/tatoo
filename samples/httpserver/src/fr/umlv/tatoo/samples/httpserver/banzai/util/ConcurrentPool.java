package fr.umlv.tatoo.samples.httpserver.banzai.util;

import java.util.concurrent.ConcurrentLinkedQueue;


public abstract class ConcurrentPool<E> {
  private final ConcurrentLinkedQueue<E> queue=
    new ConcurrentLinkedQueue<E>();
  protected ConcurrentPool() {
    // do nothing
  }
  
  protected abstract E create();
  
  
  public void preload(int size) {
    for(int i=0;i<size;i++) {
      recycle(create()); 
    }
  }
  
  public E extract() {
    E element=queue.poll();
    if (element!=null)
      return element;
    return create();
  }
  
  protected abstract void reset(E element);
  
  public void recycle(E element) {
    reset(element);
    queue.offer(element);
  }
}

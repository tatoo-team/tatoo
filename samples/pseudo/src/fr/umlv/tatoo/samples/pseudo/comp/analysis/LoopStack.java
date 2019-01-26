package fr.umlv.tatoo.samples.pseudo.comp.analysis;

import java.util.ArrayDeque;
import java.util.HashMap;

public class LoopStack<C> {
  final ArrayDeque<LoopEntry<C>> loopEntryStack=
    new ArrayDeque<LoopEntry<C>>();
  private final HashMap<String,LoopEntry<C>> loopEntryMap=
    new HashMap<String,LoopEntry<C>>();
  
  private static class LoopEntry<C> {
    final String label;
    final C context;
    
    LoopEntry(String label,C context) {
      this.label=label;
      this.context=context;
    }
    
    @Override
    public String toString() {
      return label+" "+context;
    }
  }
  
  // label can be null
  public void push(String label,C context) {
    if (context==null)
      throw new IllegalArgumentException("context can't be null");
    
    LoopEntry<C> loopEntry=new LoopEntry<C>(label,context);
    loopEntryStack.push(loopEntry);
    if (label!=null) {
      loopEntryMap.put(label,loopEntry);
    }
  }
  
  public void pop() {
    LoopEntry<C> loopContext=loopEntryStack.pop();
    String label=loopContext.label;
    if (label!=null) {
      loopEntryMap.remove(label);
    }
  }
  
  private C context(LoopEntry<C> loopEntry) {
    if (loopEntry==null)
      return null;
    return loopEntry.context; 
  }
  
  // may return null if there is no current loop context
  public C getCurrentLoopContext() {
    return context(loopEntryStack.peek());
  }
  
  // may return null
  public C getLoopContext(String label) {
    if (label==null)
      throw new IllegalArgumentException("label is null");
    return context(loopEntryMap.get(label));
  }
  
  @Override
  public String toString() {
    return loopEntryStack.toString();
  }
  
  /*
  // an iterable of the stack in descending order
  public Iterable<C> descendingIterable() {
    return new Iterable<C>() {
      @Override
      public Iterator<C> iterator() {
        final Iterator<LoopEntry<C>> it=loopEntryStack.descendingIterator();
        return new Iterator<C>() {
          @Override
          public boolean hasNext() {
            return it.hasNext();
          }
          @Override
          public C next() {
            return it.next().context;
          }
          @Override
          public void remove() {
            throw new UnsupportedOperationException();
          }
        };
      }
    };
  }*/
}

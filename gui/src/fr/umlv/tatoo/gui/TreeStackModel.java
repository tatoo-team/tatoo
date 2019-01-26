package fr.umlv.tatoo.gui;

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import fr.umlv.tatoo.gui.jit.AttributeHolder;
import fr.umlv.tatoo.gui.jit.NamedObject;
import fr.umlv.tatoo.gui.jit.Production;
import fr.umlv.tatoo.runtime.parser.ParserErrorRecoveryListener;
import fr.umlv.tatoo.runtime.parser.ParserListener;

public class TreeStackModel {

  static class ArrayListNode extends ArrayList<Node> {
    private static final long serialVersionUID = -6988216278264062454L;

    @Override
    public void removeRange(int fromIndex, int toIndex) {
      super.removeRange(fromIndex, toIndex);
    }
  }
  
  private final Object monitor = new Object();
  private boolean notified = false;
  private final GUI gui;
  private final ArrayList<ChangeListener> listeners = new ArrayList<ChangeListener>();
  final ArrayListNode data = new ArrayListNode();
  final List<? extends Node> view = Collections.unmodifiableList(data);
  final AttributeHolder holder;
  
  

  public TreeStackModel(AttributeHolder holder, GUI gui) {
    this.holder = holder;
    this.gui = gui;
  }

  public void addChangeListener(ChangeListener listener) {
    listeners.add(listener);
  }
  
  public void removeChangeListener(ChangeListener listener) {
    listeners.remove(listener);
  }
  
  private final ChangeEvent event = new ChangeEvent(this);
  
  protected void fireStateChanged() {
    try {
    if (listeners.size()==0)
      return;
    for(ChangeListener listener: listeners)
      listener.stateChanged(event);
    }
    catch(RuntimeException e) {
      gui.resetOnError(e);
    }
  }
  
  protected void invokeFireStateChanged() {
    try {
      EventQueue.invokeAndWait(new Runnable() {
        public void run() {
          fireStateChanged();
        }
      });
    } catch (InterruptedException e) {
      throw new AssertionError(e);
    } catch (InvocationTargetException e) {
      throw new AssertionError(e);
    }
  }
  
  public void step() {
    synchronized(monitor) {
      monitor.notify();
      notified=true;
    }
  }
  
  void waitStep() {
    synchronized(monitor) {
      try {
        while(!notified) {
          monitor.wait();
        }
        notified = false;
      } catch (InterruptedException e) {
        throw new AssertionError(e);
      }
    }
  }

  private final ParserListener<NamedObject, NamedObject, Production> parserListener = 
    new ParserListener<NamedObject, NamedObject, Production>() {
    
    
    public void shift(NamedObject terminal) {
      System.err.println("shift "+terminal);
      data.add(new Node(null,terminal+" ("+holder.getAttribute()+")"));
      invokeFireStateChanged();
      waitStep();
    }
    
    public void reduce(Production production) {
      System.err.println("reduce");
      int len = production.getRightSize();
      int size = data.size();
      int bottom = size-len;
      Node[] nodes = new Node[len];
      for(int i=0;i<len;i++) {
        nodes[i] = data.get(bottom+i);
      }
      data.removeRange(bottom, size);
      data.add(new Node(nodes,production.getLeft()));
      invokeFireStateChanged();
      waitStep();
    }
    
    public void accept(NamedObject nonTerminal) {
      // nothing
    }
  };
  
  public ParserListener<NamedObject, NamedObject, Production> getParserListener() {
    return parserListener;
  }
  
  private final ParserErrorRecoveryListener<NamedObject, NamedObject> errorRecoveryListener =
    new ParserErrorRecoveryListener<NamedObject, NamedObject>() {
    
    public void popTerminalOnError(NamedObject terminal) {
      data.remove(data.size()-1);
      invokeFireStateChanged();
      waitStep();
    }
  
    public void popNonTerminalOnError(NamedObject nonTerminal) {
      data.remove(data.size()-1);
      invokeFireStateChanged();
      waitStep();
    }
  
  };
  
  public ParserErrorRecoveryListener<NamedObject, NamedObject> getErrorRecoveryListener() {
    return errorRecoveryListener;
  }
  
  public void reset() {
    data.clear();
    fireStateChanged();
  }
  
  public List<? extends Node> nodes() {
    return view;
  }
  
}

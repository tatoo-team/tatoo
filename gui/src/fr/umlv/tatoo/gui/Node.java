package fr.umlv.tatoo.gui;

import java.util.Arrays;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

public class Node {
  private final Node[] children;
  private final Object data;
  private Node parent;
  
  //private static final Node[] EPSILON = new Node[] { new Node(null,"\u025b") };
  
  public Node(Node[] children,Object data) {
    if (children != null)
      System.out.println(Arrays.asList(children)+" "+data);
    else 
      System.out.println(data);
    if (children != null /*&& children.length != 0*/) {
      //if (children.length == 0)
      //  children = EPSILON;
      this.children = children.clone();
      for(Node node : children)
        node.setParent(this);
    }
    else 
      this.children = null;
    this.data = data;
  }
  
  public Node getChild(int index) {
    return children[index];
  }
  
  public int childrenCount() {
    if (hasChildren())
      return children.length;
    else
      return 0;
  }
  
  public boolean hasChildren() {
    return children != null;
  }
  
  public TreeModel getModel() {
    return new TreeModel() {
      
      
      public Node getRoot() {
        return Node.this;
      }
      
      public Object getChild(Object parent, int index) {
        return ((Node)parent)
        .getChild(index);
      }
      
      public int getChildCount(Object parent) {
        return ((Node)parent).childrenCount();
      }
      
      public boolean isLeaf(Object node) {
        return ! ((Node)node).hasChildren();
      }
      
      public void valueForPathChanged(TreePath path, Object newValue) {
        throw new AssertionError("Non mutable tree");
      }
      
      public int getIndexOfChild(Object parent, Object child) {
        Node node = (Node)parent;
        int len = node.childrenCount();
        for(int i=0;i<len;i++) {
          if (child == node.getChild(i))
            return i;
        }
        return -1;
      }
      
      public void addTreeModelListener(TreeModelListener l) {
        // nothing, tree is not mutable;
      }
      
      public void removeTreeModelListener(TreeModelListener l) {
        //    nothing, tree is not mutable;
      }
      
    };
  }
  
  @Override
  public String toString() {
    return data.toString();
  }

  public Node getParent() {
    return parent;
  }
  
  private void setParent(Node parent) {
    this.parent = parent;
  }
}

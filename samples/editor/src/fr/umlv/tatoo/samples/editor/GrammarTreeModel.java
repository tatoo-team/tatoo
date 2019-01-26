package fr.umlv.tatoo.samples.editor;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

public class GrammarTreeModel implements TreeModel {
  public GrammarTreeModel(GrammarElement root) {
    this.root=root;
  }
  
  public GrammarElement getRoot() {
    return root;
  }
  
  public Object getChild(Object parent, int index) {
    return ((GrammarElement)parent).elements().get(index);
  }

  public int getChildCount(Object parent) {
    return ((GrammarElement)parent).elements().size();
  }

  public boolean isLeaf(Object parent) {
    return ((GrammarElement)parent).elements().isEmpty();
  }

  public void valueForPathChanged(TreePath path, Object newValue) {
    throw new UnsupportedOperationException();
  }

  public int getIndexOfChild(Object parent, Object child) {
    return ((GrammarElement)parent).elements().indexOf(child);
  }

  public void addTreeModelListener(TreeModelListener l) {
    // do nothing
  }

  public void removeTreeModelListener(TreeModelListener l) {
    // do nothing
  }
  
  private final GrammarElement root;
}

package fr.umlv.tatoo.gui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.TreePath;

public class TreeViewBuilder {
  private final JPanel container;
  private final GridBagConstraints constraints;
  private final TreeStackModel model;
  
  public TreeViewBuilder(TreeStackModel model) {
    this.model = model;
    container = new JPanel(new GridBagLayout());
    constraints = new GridBagConstraints();
    constraints.weightx = constraints.weighty = 1;
    constraints.insets = new Insets(0,5,0,5);
    constraints.fill = GridBagConstraints.BOTH;
    model.addChangeListener(new ChangeListener() {
    
      public void stateChanged(ChangeEvent e) {
        update();
      }
    
    });
  }
  
  public void update() {
    container.removeAll();
    for(Node node : model.nodes())
      container.add(makeTree(node),constraints);
    container.validate();
    container.repaint();
  }
  
  private Component makeTree(Node node) {
    JTree tree = new JTree(node.getModel());
    tree.setEditable(false);
    tree.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
    expand(tree,node);
    return tree;
  }

  private void expand(JTree tree, Node node) {
    if (!node.hasChildren())
      return;
    tree.expandPath(buildTreePath(node));
    int max = node.childrenCount();
    for(int i=0;i<max;i++)
      expand(tree,node.getChild(i));
  }

  private TreePath buildTreePath(Node node) {
    ArrayList<Node> list = new ArrayList<Node>();
    while(node != null) {
      list.add(node);
      node=node.getParent();
    }
    Node[] nodes = new Node[list.size()];
    for(int i=0;i<nodes.length;i++)
      nodes[i]=list.get(nodes.length-i-1);
    return new TreePath(nodes);
  }
  
  public JPanel getView() {
    return container;
  }

  
}

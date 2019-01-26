/*
 * Created on 15 janv. 2006
 *
 */
package fr.umlv.tatoo.samples.editor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AbstractDocument.Content;
import javax.swing.tree.TreePath;

public class Main {

  public static void main(String[] args) {
    final JTextArea area=new JTextArea("2+3;\n");
   
    final Content content;
    try {
      Method method= AbstractDocument.class.getDeclaredMethod("getContent");
      method.setAccessible(true);
      content=(Content)method.invoke(area.getDocument(),(Object[])null);
    } catch (NoSuchMethodException e) {
      throw new AssertionError(e);
    } catch (IllegalAccessException e) {
      throw new AssertionError(e);
    } catch (InvocationTargetException e) {
      throw new AssertionError(e);
    }
    
    Compiler compiler=new Compiler(content);
    
    final JTree tree=new JTree(new GrammarTreeModel(compiler.getRoot()));
    tree.putClientProperty("fr.umlv.ui.autoselect",false);
    tree.putClientProperty("fr.umlv.ui.movecaret",false);
    
    area.getDocument().addDocumentListener(new DocumentListener() {
      public void changedUpdate(DocumentEvent e) {
        update();
      }
      public void removeUpdate(DocumentEvent e) {
        update();
      }
      public void insertUpdate(DocumentEvent e) {
        update();
      }
      private void update() {
        Compiler compiler=new Compiler(content);
        tree.setModel(new GrammarTreeModel(compiler.getRoot()));
      }
    });
    
    area.addCaretListener(new CaretListener() {
      public void caretUpdate(CaretEvent e) {
        if ((Boolean)tree.getClientProperty("fr.umlv.ui.autoselect"))
          return;
        
        GrammarTreeModel model=(GrammarTreeModel)tree.getModel();
        GrammarElement element=model.getRoot().getElementAtOffset(e.getDot());
        
        tree.putClientProperty("fr.umlv.ui.autoselect",true);
        tree.setSelectionPath(createTreePath(element));
        tree.putClientProperty("fr.umlv.ui.autoselect",false);
      }
      
      private TreePath createTreePath(GrammarElement element) {
        GrammarElement parent=element.getParent();
        if (parent==null)
          return new TreePath(element);
          
        return createTreePath(parent).pathByAddingChild(element);
      }
    });
    
    tree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
      public void valueChanged(TreeSelectionEvent e) {
        TreePath path=tree.getSelectionPath();
        if (path==null || (Boolean)tree.getClientProperty("fr.umlv.ui.autoselect"))
          return;
        
        GrammarElement element=(GrammarElement)path.getLastPathComponent();
        tree.putClientProperty("fr.umlv.ui.autoselect",true);
        area.setCaretPosition(element.getStartOffset());
        area.moveCaretPosition(element.getEndOffset()); 
        area.getCaret().setSelectionVisible(true);
        tree.putClientProperty("fr.umlv.ui.autoselect",false);
      }
    });
    
    JFrame frame=new JFrame("test");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.add(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,true,tree,new JScrollPane(area)));
    frame.setSize(400,300);
    frame.setVisible(true);
  }

}

package fr.umlv.tatoo.gui;

import java.awt.EventQueue;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractListModel;

import fr.umlv.tatoo.gui.jit.NamedObject;
import fr.umlv.tatoo.gui.jit.ParserSettings;
import fr.umlv.tatoo.gui.jit.ParserSettings.Property;

public class ComboBoxModel extends AbstractListModel implements javax.swing.ComboBoxModel {
  
  private static final long serialVersionUID = 6020346736080283550L;

  private static final NamedObject[] NULL_ARRAY = new NamedObject[0];
  
  private NamedObject[] objects = NULL_ARRAY;
  private int selected = -1;
  private final ParserSettings settings;
  private final Property property;
  
  public ComboBoxModel(ParserSettings settings,final Property property) {
    this.settings = settings;
    this.property = property;
    settings.addPropertyChangeListener(new PropertyChangeListener() {
    
      public void propertyChange(final PropertyChangeEvent evt) {
        if (evt.getPropertyName() != property.getProperty())
          return;
        EventQueue.invokeLater(new Runnable() {
        
          public void run() {
            changeSelectedItem(evt.getNewValue());
          }
        
        });
        
      }
    
    });
  }
  
  public void setSelectedItem(Object anItem) {
    settings.set(property,(NamedObject)anItem);
  }
  
  void changeSelectedItem(Object anItem) {
    for(int i=0;i<objects.length;i++)
      if (anItem == objects[i]) {
        selected = i;
        fireContentsChanged(this, -1, -1);
        return;
      }
    // if not found, objects are not yet updated, wait before fire
    selected = -1;
  }
  
  public Object getSelectedItem() {
    if (selected == -1)
      return "---";
    return objects[selected];
  }

  public int getSize() {
    return objects.length;
  }

  public NamedObject getElementAt(int index) {
    return objects[index];
  }
  
  public void setObjects(NamedObject[] objects) {
    int size = Math.max(this.objects.length,objects.length);
    this.objects = objects;
    fireContentsChanged(this, -1, size-1);
    changeSelectedItem(settings.get(property));
  }

}

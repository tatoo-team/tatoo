package fr.umlv.tatoo.gui.jit;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

public class ParserSettings {
  public static enum Property {version("version") {
    @Override
    void setProperty(ParserSettings settings, NamedObject property) {
      settings.setVersion(property);
    }
    @Override NamedObject getProperty(ParserSettings settings) {
      return settings.getVersion();
    }
  },start("start") {
    @Override
    void setProperty(ParserSettings settings, NamedObject property) {
      settings.setStart(property);
    }
    @Override NamedObject getProperty(ParserSettings settings) {
      return settings.getStart();
    }
  };
    Property(String property) {
      this.property = property;
    }
    private final String property;
    public String getProperty() {
      return property;
    }
    abstract void setProperty(ParserSettings settings, NamedObject property);
    abstract NamedObject getProperty(ParserSettings settings);
  }
  
  private NamedObject version,start;
  
  ArrayList<PropertyChangeListener> listeners = new ArrayList<PropertyChangeListener>();
  
  public void addPropertyChangeListener(PropertyChangeListener listener) {
    listeners.add(listener);
  }

  public void removePropertyChangeListener(PropertyChangeListener listener) {
    listeners.remove(listener);
  }  
  
  protected void firePropertyChanged(String property,Object oldValue, Object newValue) {
    if (listeners.size()==0)
      return;
    PropertyChangeEvent evt = new PropertyChangeEvent(this,property,oldValue,newValue);
    for(PropertyChangeListener listener : listeners)
      listener.propertyChange(evt);
  }
  
  public NamedObject getVersion() {
    return version;
  }
  
  public void set(Property property,NamedObject value) {
    property.setProperty(this, value);
  }
  public NamedObject get(Property property) {
    return property.getProperty(this);
  }
  
  public void setVersion(NamedObject version) {
    if (this.version == version)
      return;
    NamedObject oldVersion = this.version;
    this.version = version;
    firePropertyChanged("version", oldVersion, version);
  }
  public NamedObject getStart() {
    return start;
  }
  public void setStart(NamedObject start) {
    if (this.start == start)
      return;
    NamedObject oldStart = this.start;
    this.start = start;
    firePropertyChanged("start", oldStart, start);
  }
}

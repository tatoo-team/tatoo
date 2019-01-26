/*
 * Created on 7 avr. 2006
 *
 */
package fr.umlv.tatoo.cc.common.extension;

import fr.umlv.tatoo.cc.Tatoo;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Remi
 */
public class ExtensionBus {
  /** Marker class for all data keys.
   * 
   * @author Remi
   */
  public static class DataKey<D> {
    // marker class
  }
  
  /** Extension specific registry used by the
   *  extension bus to indicate which data keys are required
   *  by an extension.
   *  
   * @author Remi 
   * 
   * @see Extension#register(ExtensionBus.Registry)
   */
  public interface Registry {
    public void register(DataKey<?>... dataKeys);
  }
  
  /** Extension bus context used by the extension
   *  to retrieve all data using the data keys previously registered
   *  by the for extension.
   *  
   * @author Remi
   * 
   * @see Extension#execute(ExtensionBus,ExtensionBus.Context)
   */
  public interface Context {
    public <D> D getData(DataKey<D> dataKey);
  }
  
  class ExtensionInfo implements Registry {
    public ExtensionInfo(SimpleExtension extension) {
      this.extension=extension;
    }
    void dataPublished(Iterator<ExtensionInfo> it,Object dataKey) {
      if (keys.remove(dataKey) && keys.isEmpty()) {
        performs(extension);
        
        // remove current info
        it.remove();
      }
    }
    public void register(DataKey<?>... dataKeys) {
      if (registrationClosed)
        throw new IllegalStateException("registration closed");
      
      //System.out.println("register "+java.util.Arrays.toString(dataKey));
      
      Collections.addAll(keys,dataKeys);
    }
    
    /**
     * @return true is all data keys are already available
     *  for the current extension.
     */
    boolean canFinishRegistration(Set<DataKey<?>> availableDataKeys) {
      if (keys.isEmpty())
        throw new IllegalStateException(
          "the extension "+extension+" must at least register one data key");
      
      registrationClosed=true;
      
      // removes already available keys
      keys.removeAll(availableDataKeys);
      return keys.isEmpty();
    }
    
    private final SimpleExtension extension;
    private boolean registrationClosed;
    private final HashSet<DataKey<?>> keys=new HashSet<DataKey<?>>();
  }
  
  //TODO Remi restrict data key/value available to the
  // registered one
  static class ContextImpl implements Context {
    @SuppressWarnings("unchecked")
    public <D> D getData(DataKey<D> dataKey) {
      Object value=datas.get(dataKey);
      if (value==null)
        throw new IllegalArgumentException("data "+dataKey+" doesn't exists");
      return (D)value;
    }
    void addData(DataKey<?> key,Object data) {
      if (data==null)
        throw new IllegalArgumentException("null is not a valid data");
      if (datas.containsKey(key))
        throw new IllegalStateException("data "+key+'('+data+") already published");
      datas.put(key,data);
    }
    Set<DataKey<?>> availableDataKeys() {
      return datas.keySet();
    }
    private final HashMap<DataKey<?>,Object> datas=
      new HashMap<DataKey<?>,Object>();
  }
  
  /** Registers an extension to the current bus.
   *  This method called {@link Extension#register(ExtensionBus.Registry)}
   *  in order to register all data keys needed by the extension.
   *  
   *  The same extension object can be registered several times
   *  using this method.
   *  
   * @param extension the extension to register.
   */
  public void register(Extension extension) {
    ExtensionInfo info=new ExtensionInfo(extension);
    extension.register(info);
    
    finishRegistration(info,extension);
  }
  
  /** Registers a simple extension to the current bus.
   *  The same simple extension object can be registered several times
   *  using this method.
   *  
   * @param extension the simple extension to register.
   */
  public void register(SimpleExtension extension,DataKey<?>... dataKeys) {
    if (dataKeys.length==0)
      throw new IllegalArgumentException("at least one data keys must be present");
      
    ExtensionInfo info=new ExtensionInfo(extension);
    info.register(dataKeys);
    
    finishRegistration(info,extension);
  }
  
  private void finishRegistration(ExtensionInfo info,SimpleExtension extension) {
    if (info.canFinishRegistration(context.availableDataKeys()))
      performs(extension);
    else
      infos.add(info);
  }
  
  /** Publishes a data with its data-key.
   *  This method can call some extensions if
   *  the data published was the last data wait
   *  by the extension.
   *  
   * @param dataKey the key of the data
   * @param data the data
   */
  public <D> void publish(DataKey<D> dataKey,D data) {
    //System.out.println("publish "+dataKey+" "+data);
    
    context.addData(dataKey,data);
    Iterator<ExtensionInfo> it=infos.iterator();
    while(it.hasNext())
      it.next().dataPublished(it,dataKey);
  }
  
  // avoid generated accessor by the compiler
  void performs(SimpleExtension extension) {
    extension.execute(this,context);
  }
  
  /** Loads an extension and register it on the bus.
   *  The classe take as argument must have a default public constructor
   *  and must be a subtype of {@link Extension}.
   * @param set 
   * 
   * @param extensionClassName the class name of the extension.
   * 
   * @see #loadAndRegisterExtension(Class)
   * @see #register(Extension)
   */
  @SuppressWarnings("unchecked")
  public void loadAndRegisterExtension(Set<File> set, String extensionClassName) {
    URL[] urls;
    if (set==null) 
      urls = new URL[0];
    else {
      urls= new URL[set.size()];
      {
        int i=0;
        for(File file:set) {
          try {
            urls[i] = file.toURI().toURL();
          } catch (MalformedURLException e) {
            throw new IllegalArgumentException("invalid file",e);
          }
          i++;
        }
      }
    }
    URLClassLoader classLoader = new URLClassLoader(urls,Tatoo.class.getClassLoader());
    Class<? extends Extension> clazz;
    try {
      clazz=(Class<? extends Extension>)classLoader.loadClass(extensionClassName);
    } catch (ClassNotFoundException e) {
      throw new IllegalStateException(e);
    }
    loadAndRegisterExtension(clazz);
  }
  
  /** Loads an extension an register it on the bus.
   *  The classe take as argument must have a default public constructor
   *  and must be a subtype of {@link Extension}.
   * 
   * @param extensionClass the class of the extension.
   * 
   * @see #loadAndRegisterExtension(Set, String)
   * @see #register(Extension)
   */
  public void loadAndRegisterExtension(Class<? extends Extension> extensionClass) {
    Extension extension;
    try {
      extension=extensionClass.newInstance();
    } catch (InstantiationException e) {
      throw new IllegalStateException(e);
    } catch (IllegalAccessException e) {
      throw new IllegalStateException(e);
    }
    register(extension);
  }
  
  private final LinkedHashSet<ExtensionInfo> infos=
    new LinkedHashSet<ExtensionInfo>();
  private final ContextImpl context=
    new ContextImpl();
}

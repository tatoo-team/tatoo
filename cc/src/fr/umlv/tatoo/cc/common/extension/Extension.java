/*
 * Created on 7 avr. 2006
 *
 */
package fr.umlv.tatoo.cc.common.extension;

import fr.umlv.tatoo.cc.common.extension.ExtensionBus.Registry;

/**
 * @author Remi
 *
 * @see ExtensionBus#loadAndRegisterExtension(java.util.Set, String)
 * @see ExtensionBus#loadAndRegisterExtension(Class)
 * @see ExtensionBus#register(Extension)
 */
public interface Extension extends SimpleExtension {
  /** Called by the extention bus in order to register data
   *  key needed by the current extension.
   *  This method is called when the current extension
   *  is registered by the extension bus.
   *  
   * @param registry the registry associated with the current extension
   *  allocated by the extension bus.
   * 
   * @see ExtensionBus.Registry#register(fr.umlv.tatoo.cc.common.extension.ExtensionBus.DataKey[])
   */
  public void register(Registry registry);
}

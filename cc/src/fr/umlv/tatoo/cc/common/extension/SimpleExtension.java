package fr.umlv.tatoo.cc.common.extension;

import fr.umlv.tatoo.cc.common.extension.ExtensionBus.Context;

/**
 * 
 * @author Remi
 * 
 * @see ExtensionBus#register(Extension)
 * @see ExtensionBus#register(SimpleExtension, fr.umlv.tatoo.cc.common.extension.ExtensionBus.DataKey[])
 */
public interface SimpleExtension {
  /** Called by the extension bus when all data keys
   * registered by the current extension are available.
   * 
   * @param bus the bus on which the current extensiuon is registered
   * @param context the bus context, all datas required by the
   *  current extension are available using the Context's method
   *  {@link ExtensionBus.Context#getData(fr.umlv.tatoo.cc.common.extension.ExtensionBus.DataKey)}.
   */
  public void execute(ExtensionBus bus, Context context);
}
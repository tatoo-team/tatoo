package fr.umlv.tatoo.cc.tools.main;

import java.util.Map;

import fr.umlv.tatoo.cc.common.extension.ExtensionBus.DataKey;
import fr.umlv.tatoo.cc.common.generator.Type;
import fr.umlv.tatoo.cc.tools.tools.ToolsFactory;

public class ToolsDataKeys  {
  public static final DataKey<ToolsFactory> toolsFactory=
    new DataKey<ToolsFactory>();
  public static DataKey<Map<String,Type>> attributeMap =
    new DataKey<Map<String,Type>>();
}

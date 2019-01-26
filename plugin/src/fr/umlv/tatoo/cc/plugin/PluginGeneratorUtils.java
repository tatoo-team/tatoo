package fr.umlv.tatoo.cc.plugin;

import java.util.Iterator;

public class PluginGeneratorUtils {
  private static void append(StringBuilder builder,String text) {
    builder.append('"').
      append(text.replace("/","//")).
      append('"');
  }
  
  public static String join(String separator, Iterable<String> iterable) {
    Iterator<String> it=iterable.iterator();
    if (!it.hasNext())
      return "";
    StringBuilder builder=new StringBuilder();
    append(builder,it.next());
    while(it.hasNext()) {
      append(builder.append(separator),it.next());
    }
    return builder.toString();
  }
}

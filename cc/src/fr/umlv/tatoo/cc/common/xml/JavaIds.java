/*
 * Created on 7 juil. 2005
 */
package fr.umlv.tatoo.cc.common.xml;

import java.util.regex.Pattern;

/**
 * @author remi
 * @author julien
 */
public class JavaIds {
  /**
   * Verifies if the name is a valid id.
   * @param id the id to verify
   * @return <code>true</code> if the <code>id</code> is a valid Java identifier.
   */
  public static boolean validateId(String id) {
    if ("".equals(id))
      return false;
    
    char first=id.charAt(0);
    if (!Character.isJavaIdentifierStart(first))
      return false;
    
    for(int i=1;i<id.length();i++)
      if (!Character.isJavaIdentifierPart(id.charAt(i)))
        return false;
    if (keyWord.matcher(id).matches())
      return false;
    return true;
  }
  
  public static boolean validatePackageName(String packageName) {
    if (packageName==null || packageName.endsWith("."))
      return false;
    for(String name:packageName.split("\\."))
      if (!validateId(name))
        return false;
    return true;
  }
  final private static Pattern keyWord = Pattern.compile(
      "(class)|" +
      "(interface)|" +
      "(extends)|" +
      "(implements)|" +
      "(byte)|" +
      "(char)|" +
      "(int)|" +
      "(long)|" +
      "(float)|" +
      "(double)|" +
      "(abstract)|" +
      "(public)|" +
      "(private)|" +
      "(protected)|" +
      "(static)|" +
      "(final)|" +
      "(if)|" +
      "(else)|" +
      "(for)|" +
      "(while)|" +
      "(switch)|" +
      "(case)|" +
      "(default)|" +
      "(break)|" +
      "(continue)|" +
      "(goto)|" +
      "(true)|" +
      "(false)");
}

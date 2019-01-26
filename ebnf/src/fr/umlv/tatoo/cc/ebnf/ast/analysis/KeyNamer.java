package fr.umlv.tatoo.cc.ebnf.ast.analysis;

import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

/**
 *  Try to find a valid Java name for a sequence of unknown characters
 *  using the name of keyboard key.
 */
public class KeyNamer {
  private KeyNamer() {
    // no object allowed
  }
  
  
  /** Return a name for a sequence of characters using the name of the
   *  corresponding keyboard key.
   *   
   * @param text a sequence of characters
   * @return a valid Java name or null if at least one character is not known.
   */
  public static String getKeyName(String text) {
    int length = text.length();
    if (length==0)
      throw new IllegalArgumentException("empty text");
      
    StringBuilder nameBuilder=new StringBuilder();
    for(int i=0;i<length;i++) {
      char keyChar=text.charAt(0);
      
      KeyStroke keyStroke = KeyStroke.getKeyStroke(keyChar);
      int keyCode = keyStroke.getKeyCode();
      if (keyCode==KeyEvent.VK_UNDEFINED) {
        return null;
      }
      String keyText = KeyEvent.getKeyText(keyCode);
      nameBuilder.append(keyText.toLowerCase().replace(' ', '_')).append('_');
    }
    
    // remove trailing underscore
    nameBuilder.setLength(nameBuilder.length()-1);
    return nameBuilder.toString();
  }
}

/*
 * Charset.java
 *
 * Created: 23 mars 2006
 */
package fr.umlv.tatoo.cc.lexer.charset.encoding;

import java.util.Collections;
import fr.umlv.tatoo.cc.lexer.charset.CharacterInterval;
import fr.umlv.tatoo.cc.lexer.charset.CharacterSet;


public abstract class Encoding {
  Encoding() {
    this.any = new CharacterSet(Collections.singletonList(new CharacterInterval(getMinValue(), getMaxValue()))) {
      @Override
      public String toString() {
        return "<any>";
      }
    };
  }
  /**
   * Returns a constant corresponding to the minimum 
   * value of the charset.
   * Implementation of this method should not access 
   * to fields.
   * @return the minimum value of the charset
   */
  public abstract int getMinValue();
  /**
   * Returns a constant corresponding to the maximum 
   * value of the charset.
   * Implementation of this method should not access 
   * to fields.
   * @return the maximum value of the charset
   */
  public abstract int getMaxValue();
  /**
   * Returns the encoding of a single character.
   * @param c the character to encode.
   * @return the encoding of the character.
   */
  public abstract int encode(char c);
  /**
   * Returns the character corresponding to
   * a specific code.
   * @param c the code
   * @return the character decoded.
   */
  public abstract char decode(int c);
  
  
//  public String toString(CharacterSet set) {
//    StringBuilder buffer = new StringBuilder("[");
//    Iterator<CharacterInterval> iterator =  set.getList().iterator();
//    if (iterator.hasNext()) {
//      buffer.append(iterator.next().toString(this));
//      while(iterator.hasNext()) {
//        buffer.append(',').append(iterator.next().toString(this));
//      }
//    }
//    buffer.append(']');
//    return buffer.toString();
//  }

  /**
   * Returns the charset coresponding to any character.
   * @return the charset for any character.
   */
  public CharacterSet getAny() {
    return any;
  }
  
  private final CharacterSet any;
}

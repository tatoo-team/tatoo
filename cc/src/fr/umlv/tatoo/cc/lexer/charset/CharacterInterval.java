package fr.umlv.tatoo.cc.lexer.charset;

import fr.umlv.tatoo.cc.lexer.charset.encoding.Encoding;
import fr.umlv.tatoo.cc.lexer.charset.encoding.UTF16Encoding;

/**
 * @author jcervell
 *
 */
public class CharacterInterval {
  private final int begin, end;

  public CharacterInterval(char c, Encoding encoding) {
    this(c,c,encoding);
  }
  
  public CharacterInterval(char begin, char end, Encoding encoding) {
    if (end<begin)
      throw new IllegalArgumentException("interval end is lower than its beginning "+
					 begin+","+end);
    
    this.begin = encoding.encode(begin);
    this.end = encoding.encode(end);
  } // CharacterInterval constructor

  public CharacterInterval(int begin, int end) {
    this.begin = begin;
    this.end = end;
  }
  
  public int getBegin() {
    return begin;
  }

  public int getEnd() {
    return end;
  }

  public boolean in(int c) {
    return c >= begin && c <= end;
  }

  public boolean joinable(CharacterInterval interval) {
    return (begin <= interval.getEnd() + 1) && (end >= interval.getBegin() - 1);
  }

  public CharacterInterval join(CharacterInterval interval) {
    return new CharacterInterval(
      Math.min(begin, interval.getBegin()),
      Math.max(end, interval.getEnd()));
  }

  public CharacterInterval inter(CharacterInterval interval) {
    return new CharacterInterval(
      Math.max(begin, interval.getBegin()),
      Math.min(end, interval.getEnd()));
  }

  public String toString(Encoding encoding) {
    if (begin == end)
      return toString(begin,encoding);
    else
      return toString(begin,encoding)+'-'+toString(end,encoding);
  }
  
  /**
   * Returns a String representation as if {@link UTF16Encoding} was used
   * @return a String representation as if {@link UTF16Encoding} was used
   */
  @Override
  public String toString() {
    return toString(UTF16Encoding.getInstance());
  }

  public static String toString(int v, Encoding encoding) {
    char c = encoding.decode(v);
    switch (c) {
      case '\b' :
        return "\\b";
      case '\f' :
        return "\\f";
      case '\n' :
        return "\\n";
      case '\r' :
        return "\\r";
      case '\t' :
        return "\\t";
      case '\0' :
        return "\\0";
      case '\\' :
        return "\\\\";
    }
    if (c>='\u0020'&&c<='\u007E')
      return Character.toString(c);
    return "\\u" + Integer.toHexString(c).toUpperCase();
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof CharacterInterval))
      return false;
    CharacterInterval i = (CharacterInterval)obj;
    return begin == i.getBegin() && end == i.getEnd();
  }

  @Override
  public int hashCode() {
    return end | begin << 16;
  }
}

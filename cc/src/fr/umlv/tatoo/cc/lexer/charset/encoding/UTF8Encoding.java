package fr.umlv.tatoo.cc.lexer.charset.encoding;

public class UTF8Encoding extends Encoding {
  
  private UTF8Encoding() { /* enforce singleton */ }
  
  private static final UTF8Encoding INSTANCE = new UTF8Encoding();
  
  public static UTF8Encoding getInstance() {
    return INSTANCE;
  }

  @Override
  public int getMinValue() {
    return 0;
  }

  @Override
  public int getMaxValue() {
    return 0xEFBFBF;
  }

  @Override
  public int encode(char c) {
    if (c<=0x7f) // one byte (c<=7bits)
      return c;
    int sixLowBits = c&0x3f; // 111111
    int sixNextBits = c&0xfc0; // 111111000000
    if (c<=0x7ff) { // two bytes (c<11bits)
      return sixLowBits|(sixNextBits<<2)|0xc080; //1100000010000000
    }
    int fourLastBits = c&0xf000; // 1111000000000000
    return sixLowBits|(sixNextBits<<2)|(fourLastBits<<4)|0xe08080; //111000001000000010000000
  }

  @Override
  public char decode(int c) {
    if ((c&0x80)==0) // one byte (eighth bit = 0)
      return (char)c;
    int sixLowBits=c&0x3f; // 111111
    int sixNextBits=c&0x3f00; // 11111100000000
    if ((c&0x4000)!=0) { // two bytes (fifteenth bit = 1)
      return (char)(sixLowBits|(sixNextBits>>2));
    }
    int fourLastBits=c&0xf0000; // 11110000000000000000
    return (char)(sixLowBits|(sixNextBits>>2)|(fourLastBits>>4));
  }

}

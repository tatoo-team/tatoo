package fr.umlv.tatoo.samples.httpserver.banzai.http;

import java.nio.ByteBuffer;

public class Encoder {
  /*
  private final CharsetEncoder encoder=
    Charset.forName("US-ASCII").newEncoder();
  
  public ByteBuffer encode(long value,ByteBuffer buffer) {
    String s=Long.toString(value);
    // encoder.reset(); not needed for US-ASCII
    encoder.encode(CharBuffer.wrap(s),buffer,true);
    //encoder.flush(buffer); not needed for US-ASCII
    return buffer;
  }*/
  
  private final byte[] array=new byte[20];

  public ByteBuffer encode(long value,ByteBuffer buffer) {
    
    if (value == Long.MIN_VALUE) {
      buffer.put(MIN_VALUE);
      return buffer;
    }
    int pos=encodeLong(value,array);
    
    
    //for(byte b:array)
    //  System.out.printf("%d ",b);
    //System.out.println();
    
    //System.out.println("pos "+(pos)+" length "+(20-pos));
    
    return buffer.put(array,pos,20-pos);
  }

  // dup from Long.toString(long)
  private static int encodeLong(long i, byte[] array) {
    int r;
    int charPos = 20;
    boolean sign;

    if (i < 0) {
      sign = true;
      i = -i;
    } else {
      sign = false;
    }

    // Get 2 digits/iteration using longs until quotient fits into an int
    while (i > Integer.MAX_VALUE) { 
      long q = i / 100;
      // really: r = i - (q * 100);
      r = (int)(i - ((q << 6) + (q << 5) + (q << 2)));
      i = q;
      array[--charPos]=DIGIT_ONES[r];
      array[--charPos]=DIGIT_TENS[r];
    }

    // Get 2 digits/iteration using ints
    int q2;
    int i2 = (int)i;
    while (i2 >= 65536) {
      q2 = i2 / 100;
      // really: r = i2 - (q * 100);
      r = i2 - ((q2 << 6) + (q2 << 5) + (q2 << 2));
      i2 = q2;
      array[--charPos]=DIGIT_ONES[r];
      array[--charPos]=DIGIT_TENS[r];
    }

    // Fall thru to fast mode for smaller numbers
    // assert(i2 <= 65536, i2);
    for (;;) {
      q2 = (i2 * 52429) >>> (16+3);
      r = i2 - ((q2 << 3) + (q2 << 1));  // r = i2-(q2*10) ...
      array[--charPos]=DIGIT_ONES[r];
      i2 = q2;
      if (i2 == 0) break;
    }
    if (sign) {
      array[--charPos]='-';
    }
    return charPos;
  }

  private static final byte[] MIN_VALUE={
    '-','9','2','2','3','3','7','2','0','3','6','8','5','4','7','7','5','8','0','8'
  };

  private final static byte[] DIGIT_TENS = {
    '0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
    '1', '1', '1', '1', '1', '1', '1', '1', '1', '1',
    '2', '2', '2', '2', '2', '2', '2', '2', '2', '2',
    '3', '3', '3', '3', '3', '3', '3', '3', '3', '3',
    '4', '4', '4', '4', '4', '4', '4', '4', '4', '4',
    '5', '5', '5', '5', '5', '5', '5', '5', '5', '5',
    '6', '6', '6', '6', '6', '6', '6', '6', '6', '6',
    '7', '7', '7', '7', '7', '7', '7', '7', '7', '7',
    '8', '8', '8', '8', '8', '8', '8', '8', '8', '8',
    '9', '9', '9', '9', '9', '9', '9', '9', '9', '9',
  } ; 

  private final static byte[] DIGIT_ONES = { 
    '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
    '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
    '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
    '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
    '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
    '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
    '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
    '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
    '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
    '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
  } ;
}

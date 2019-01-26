package fr.umlv.tatoo.cc.common.xml;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.NoSuchElementException;


/** Convert a String into a specific type.
 * @author remi
 *
 */
public class Converter {
  protected Converter() {
    HashMap<Class<?>,Method> map=new HashMap<Class<?>,Method>();
    for(Class<?> clazz = getClass();clazz!=Object.class;clazz=clazz.getSuperclass()) {
      
      for(Method method:clazz.getDeclaredMethods()) {
        String name = method.getName();
        //System.out.println(name);
        if (!name.startsWith("parse"))
          continue;
        
        try {
          method.setAccessible(true);
        } catch(SecurityException e) {
          // no problem, just an optimization trick
        }
        
        //System.out.println("register converter "+method);
        map.put(method.getReturnType(),method);
      }
    }
    this.map=map;
  }
  
  private static class StringIterator {
    public StringIterator(String text) {
      this.text = text;
    }
    public boolean hasNext() {
      return pos != text.length();
    }
    public char next() {
      if (!hasNext()) 
        throw new NoSuchElementException("End of string reached: "+ text);
      return text.charAt(pos++);
    }
    private final String text;
    private int pos=0;
  }
  
  protected char parseChar(String text) {
    StringIterator it = new StringIterator(text);
    char c = interpretChar(it);
    if (it.hasNext()) {
      throw new IllegalArgumentException(text + " is not a legal character");
    }
    return c;
  }
  
  protected String parseString(String text) {
    StringBuilder buffer = new StringBuilder();
    StringIterator it = new StringIterator(text);
    while(it.hasNext()) {
      buffer.append(interpretChar(it));
    }
    return buffer.toString();
  }

  private char interpretChar(StringIterator it) {
    char c = it.next();
    if (c != '\\') {
      return c;
    }
    if (!it.hasNext())
      throw new IllegalArgumentException("Illegal string ending character: \\");
    c = it.next();
    switch(c) {
      case 'b': return '\b';
      case 't': return '\t';
      case 'n': return '\n';
      case 'f': return '\f';
      case 'r': return '\r';
      case '"': return '"';
      case '\'': return '\'';
      case '\\': return '\\';
      default : throw new IllegalArgumentException("Illegal escaped character: \\"+c);
      case 'u':
        int n=0;
        for(int i=0;i<4;i++) {
          c = it.next();
          int tmp = Character.digit(c,16);
          if (tmp == -1)
            throw new IllegalArgumentException("Illegal unicode character: \\u"+n+c);
          n = n*10+tmp;
        }
        if ((n>Character.MAX_VALUE) || (n<Character.MIN_VALUE))
          throw new IllegalArgumentException("Illegal unicode character: \\u"+n);
        return (char)n;
    }
  }
  
  protected int parseInt(String text) {
    return Integer.parseInt(text);
  }
  protected double parseDouble(String text) {
    return Double.parseDouble(text);
  }
  protected boolean parseBoolean(String text) {
    return Boolean.parseBoolean(text);
  }
  protected <E extends Enum<E>> E parseEnum(String text,Class<E> clazz) {
    return Enum.valueOf(clazz,text.toUpperCase().replace('-','_'));
  }
  
  private Method findMethod(Class<?> clazz) {
    for(Class<?> c=clazz;c!=null;c=c.getSuperclass()) {
      Method method=map.get(c);
      if (method!=null)
        return method;
    }
    throw new IllegalArgumentException("no converter for "+clazz);
  }
  
  @SuppressWarnings("unchecked")
  public <T> T convert(String value,Class<T> clazz) {
    Method method=findMethod(clazz);
    try {
      if (method.getParameterTypes().length==1)
        return (T)method.invoke(this,value);
      else
        return (T)method.invoke(this,value,clazz);
    } catch (IllegalAccessException e) {
      throw new AssertionError(e);
    } catch (InvocationTargetException e) {
      throw new AssertionError(e.getCause());
    }
  }
  
  private final HashMap<Class<?>,Method> map;

  public static Converter getDefaultConverter() {
    if (defaultConverter==null)
      defaultConverter=new Converter();
    return defaultConverter;
  }
  private static Converter defaultConverter=null;
}

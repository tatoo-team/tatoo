/*
 * Created on 17 dec. 2005
 *
 */
package fr.umlv.tatoo.runtime.tools;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;

/** A bunch of static methods that help to debug Tatoo evaluators.
 * 
 * @author Remi Forax
 */
public class Debug {
  /** Creates a proxy that prints out all methods, of the interface, called.
   *  The returned value of each method is the default value of its return type.
   *  
   * @param <T> the type of the interface
   * @param type the class of the interface.
   * 
   * @return a proxy that trace all method calls.
   */
  public static <T> T createTraceProxy(Class<T> type) {
    return createTraceProxy(type,null);
  }
  
  /** Creates a proxy that prints outs all methods, of the interface, called
   *  and then delegates the method call to <tt>impl</tt>.
   * 
   * @param <T> the type of the interface
   * @param type the class of the interface.
   * @param impl the implementation used to delegate.
   * 
   * @return a proxy that trace all method calls.
   */
  public static <T> T createTraceProxy(Class<T> type, final T impl) {
    return createGenericProxy(type,new InvocationHandler() {
        public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {
          
          System.err.println(method+" called with "+((args==null)?"[]":Arrays.toString(args)));
          
          if (impl==null)
            return defaultValues.get(method.getReturnType());  
            
          try {
            return method.invoke(impl,args);
          } catch (InvocationTargetException e) {
            throw e.getCause();
          }
        }
      });
  }
  
  private static <T> T createGenericProxy(Class<T> type,InvocationHandler handler) {
    return type.cast(Proxy.newProxyInstance(
      type.getClassLoader(),new Class<?>[]{type},handler));
  }
  
  static final HashMap<Class<?>,Object> defaultValues;
  static {
    HashMap<Class<?>,Object> values=
      new HashMap<Class<?>,Object>();
    values.put(boolean.class,false);
    values.put(byte.class,(byte)0);
    values.put(short.class,(short)0);
    values.put(char.class,(char)0);
    values.put(int.class,0);
    values.put(long.class,0L);
    values.put(float.class,0f);
    values.put(double.class,0.0);
    defaultValues=values;
  }
}

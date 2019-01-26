package fr.umlv.tatoo.runtime.log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class LoggingLoggerFactory {
  
  public interface LoggingLogger extends Logger
  {
    void setLevel(Level level);
    void setHandler(Handler handler);
  }
  public static Logger getLogger() {
    return (Logger)Proxy.newProxyInstance(LoggingLoggerFactory.class.getClassLoader(),
        new Class<?>[]{LoggingLogger.class}, new InvocationHandler(){
          private Level level = Level.WARNING;
          private Handler handler = new SyserrHandler();
          private final Object object = new Object();
          @Override
          public Object invoke(Object proxy, Method method, Object[] args)
              throws Throwable {
            if (method.getDeclaringClass().equals(Object.class)) {
              return method.invoke(object, args);
            }
            if (method.getName().equals("setLevel")) {
              level = (Level)args[0];
              return null;
            }
            if (method.getName().equals("setHandler")) {
              handler = (Handler)args[0];
              return null;
            }
            Level methodLevel = Level.valueOf(method.getName().toUpperCase());
            if (methodLevel.compareTo(level)>=0)
              handler.print(args[0]);
            return null;
          }
      
    });
  }
  
}

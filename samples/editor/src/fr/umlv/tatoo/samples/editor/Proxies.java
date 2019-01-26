package fr.umlv.tatoo.samples.editor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;

public class Proxies {
  private Proxies() {
    // do nothing
  }
  
  public static <T> T createTerminalAttributeEvaluator(final ContentTokenBuffer buffer,Class<T> attributeEvaluatorType) {
    return attributeEvaluatorType.cast(Proxy.newProxyInstance(
        attributeEvaluatorType.getClassLoader(),
        new Class<?>[]{attributeEvaluatorType},
        new InvocationHandler() {
          public Object invoke(Object proxy, Method method, Object[] args)
              throws Throwable {
            
            if (method.getReturnType()!=Location.class)
              return null;
            
            return new Location(buffer.tokenStart(),buffer.tokenEnd());
          }
        }));
  }
  
  public static <T> T createParserVisitor(Class<? extends Enum<?>> productionClass,Class<T> parserVisitorType) {
    final HashMap<String,Enum<?>> map=new HashMap<String,Enum<?>>();
    for(Enum<?> prod:productionClass.getEnumConstants()) {
      map.put(prod.name(),prod);
    }
    
    return parserVisitorType.cast(Proxy.newProxyInstance(
        parserVisitorType.getClassLoader(),
        new Class<?>[]{parserVisitorType},
        new ParserHandler(map)));
  }
  
  public static GrammarElement getRoot(Object parserVisitor) {
    return ((ParserHandler)Proxy.getInvocationHandler(parserVisitor)).getRoot();
  }
  
  private static class ParserHandler implements InvocationHandler {
    ParserHandler(HashMap<String, Enum<?>> map) {
      this.map = map;
    }

    public Object invoke(Object proxy, Method method, Object[] args)
        throws Throwable {
      
      Class<?>[] types=method.getParameterTypes();
      if (types.length==0)
        return null;
      
      Enum<?> production=map.get(method.getName());
      int start=getStart(args[0]);
      int end=getEnd(args[args.length-1]);
      
      //System.out.println(production+" "+start+" "+end);
      
      GrammarElement element = new GrammarElement(production,start,end);
      for(Object arg:args) {
        if (arg instanceof GrammarElement) {
          element.add((GrammarElement)arg);
        }
      }
      return root=element;
    }
    
    public GrammarElement getRoot() {
      return root;
    }
    
    private int getStart(Object arg) {
      if (arg instanceof Location) {
        return ((Location)arg).getStart();
      }
      return -1;
    }
    private int getEnd(Object arg) {
      if (arg instanceof Location) {
        return ((Location)arg).getEnd();
      }
      return -1;
    }
    
    private GrammarElement root;
    private final HashMap<String, Enum<?>> map;
  }
}

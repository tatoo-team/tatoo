package fr.umlv.tatoo.cc.common.xml;

import static fr.umlv.tatoo.cc.common.log.Level.ERROR;
import static fr.umlv.tatoo.cc.common.log.Level.FATAL_ERROR;
import static fr.umlv.tatoo.cc.common.log.Level.WARNING;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;

import fr.umlv.tatoo.cc.common.log.Info;
import fr.umlv.tatoo.cc.common.log.ReporterFactory;
import fr.umlv.tatoo.cc.common.main.Unit;

/** XML Digester that {@link #registerSaxlets(fr.umlv.tatoo.cc.common.xml.AbstractXMLDigester.SAXlet[]) register}
 *  {@link SAXlet} used to process XML tags.
 * 
 * @author Remi
 */
public abstract class AbstractXMLDigester extends XMLDigester {
  /** Creates a digester that use {@link SAXlet}.
   */
  protected AbstractXMLDigester() {
    this.map=new HashMap<String,SAXlet>();
  }
  
  /** Register saxlets used to parse the XML file.
   * @param saxlets the saxlets to register.
   * 
   * @see #getSAXLets()
   */
  public void registerSaxlets(SAXlet... saxlets) {
    for (SAXlet saxlet:saxlets)
      saxlet.register(map);
  }
  
  /** Unregister saxlets used to parse the XML file.
   * @param saxlets the saxlets to unregister.
   * 
   * @see #getSAXLets()
   */
  public void unregisterSaxlets(SAXlet... saxlets) {
    for (SAXlet saxlet:saxlets)
      saxlet.unregister(map);
  }
  
  /** Returns true if at least a saxlet is registered.
   * @return true is a saxlet is registered.
   */
  public boolean isSaxletsRegistered() {
    return !map.isEmpty();
  }
  
  /** Retruns the corresponding unit type.
   * @return the unit type to parse.
   */
  public abstract Unit getUnit();
  
  /** Retruns the saxlets registered by the digester.
   * @return the saxlets registered by the digester.
   */
  protected abstract SAXlet[] getSAXLets();
  
  /** Retruns the name of the root element of the XML file.
   * @return the name of the root element of the XML file.
   */
  public abstract String getRootElementName();
  
  /** 
   * {@inheritDoc}
   * 
   * This implementation ensures that if no saxlet is registered, the saxlets returned by
   *  {@link #getSAXLets()} are registered before return the handler.
   */
  @Override
  public DefaultHandler handler() {
    if (!isSaxletsRegistered())
      registerSaxlets(getSAXLets());
    
    final ArrayList<AttributesImpl> stack=new ArrayList<AttributesImpl>();
    return new DefaultHandler() {
      @Override public void startElement(String namespace, String localName,
        String qName, Attributes atts) throws SAXException {
        SAXlet saxlet = map.get(localName);
        if (saxlet==null) {
          reportError(ERROR,"unknown tag "+localName);
        }
        else {
          stack.add(new AttributesImpl(atts));
          
//        auto-locate error
          Info info=ReporterFactory.getDefaultInfo();
          Locator locator=getLocator();
          ReporterFactory.setAndSealDefaultInfo(
            info.line(locator.getLineNumber()).column(locator.getColumnNumber()));
          
          try {
            saxlet.start(localName,atts);
          } catch(Exception e) {
            reportError(ERROR,e);
          } finally {
            ReporterFactory.setAndSealDefaultInfo(info);
          }
        }
      }
      @Override public void endElement(String namespace, String localName, String qName) 
        throws SAXException {
        
        SAXlet saxlet = map.get(localName);
        if (saxlet!=null) {
          Attributes attr=stack.remove(stack.size()-1);
          
          // auto-locate error
          Info info=ReporterFactory.getDefaultInfo();
          Locator locator=getLocator();
          ReporterFactory.setAndSealDefaultInfo(
            info.line(locator.getLineNumber()).column(locator.getColumnNumber()));
          
          try {
            saxlet.end(localName,attr);
          } catch(Exception e) {
            reportError(ERROR,e);
          } finally {
            ReporterFactory.setAndSealDefaultInfo(info); 
          }
        }
      }
      @Override public void setDocumentLocator(Locator documentLocator) {
        setLocator(documentLocator);
      }
      @Override public void error(SAXParseException e) throws SAXException {
        reportError(ERROR,findCause(e));
      }
      @Override public void warning(SAXParseException e) throws SAXException {
        reportError(WARNING,findCause(e));
      }
      @Override public void fatalError(SAXParseException e) throws SAXException {
        reportError(FATAL_ERROR,findCause(e));
      }
      private Throwable findCause(Throwable t) {
        Throwable cause=t.getCause();
        if (cause==null)
          return t;
        return cause;
      }
    };
  }
  
  final HashMap<String,SAXlet> map;
  
  /** Processing unit used to process one or more XML element.
   */
  public interface SAXlet {
    /** Register all tags processed by the saxlets.
     * 
     *  @param map the map used by the digester to associate a
     *   saxlet to an XML element.
     *  
     *  @see #unregister(Map)
     */
    public void register(Map<String,SAXlet> map);
    
    /** Unregister all tags processed by the saxlets.
     *  @param map the map used by the digester to associate a
     *   saxlet to an XML element.
     *  
     *  @see #register(Map)
     */
    public void unregister(Map<String,SAXlet> map);
    
    /** Called by the digester when the start tag of an element is found.
     * 
     * @param localName the name of the lement.
     * @param attr the element attribute.
     *  This parameter must be escape from the stack.
     * 
     * @throws Exception raised during the processing of the element.
     * 
     * @see #end(String, Attributes)
     * @see #register(Map)
     */
    public void start(String localName,Attributes attr) throws Exception;
    
    /** Called by the digester when the end tag of an element is found.
     * @param localName the name of the element.
     * @param attr the element attribute.
     *  This parameter must be escape from the stack.
     *  
     * @throws Exception raised during the processing of the element.
     * 
     * @see #start(String, Attributes)
     * @see #register(Map)
     */
    public void end(String localName,Attributes attr) throws Exception;
  }
  
  /** Default {@link SAXlet} implementation that can be used to
   *  recognized one XML element. By default, this implementation does
   *  nothing.
   */
  public static class DefaultSAXlet implements SAXlet {
    /** Creates a saxlet that recognized the element named name.
     * @param name the name of the recognized element.
     */
    public DefaultSAXlet(String name) {
      this.name = name;
    }
    public void register(Map<String, SAXlet> map) {
      map.put(name, this);
    }
    public void unregister(Map<String, SAXlet> map) {
      map.remove(name);
    }
    public void start(String element, Attributes atts) throws Exception {
      // do nothing
    }
    public void end(String element, Attributes atts) throws Exception {
      // do nothing
    }
    
    /** Returns the name of the recognized element.
     * @return the name of the recognized element.
     */
    public String getName() {
      return name;
    }
    private final String name;
  }
}

/*
 * Created on 13 nov. 2005
 *
 */
package fr.umlv.tatoo.cc.main.xml;

import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import fr.umlv.tatoo.cc.common.xml.AbstractXMLDigester;
import fr.umlv.tatoo.cc.common.xml.XMLDigester;
import static fr.umlv.tatoo.cc.common.log.Level.*;

public class DispatchXMLDigester extends XMLDigester {
  public DispatchXMLDigester(AbstractXMLDigester... digesters) {
    HashMap<String,AbstractXMLDigester> map=new HashMap<String,AbstractXMLDigester>();
    for(AbstractXMLDigester digester:digesters)
      map.put(digester.getRootElementName(),digester);
    this.digesters=map;
  }
  
  @Override protected DefaultHandler handler() {
    return new DefaultHandler() {
      @Override
      public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        if (delegate==null) {
          AbstractXMLDigester digester = digesters.get(localName);
          if (digester==null) {
            reportError(ERROR,"unknown digester with root element "+localName);
            return;
          }
          
          delegate=digester;
          delegateHandler=digester.handler();
          delegateHandler.setDocumentLocator(getLocator());  
        }
        delegateHandler.startElement(uri,localName,qName,atts);
      }
      
      @Override
      public void endElement(String uri, String localName, String qName) throws SAXException {
        if (delegateHandler!=null)
          delegateHandler.endElement(uri, localName, qName);
      }
      
      @Override
      public void characters(char[] ch, int start, int length) throws SAXException {
        if (delegateHandler!=null)
          delegateHandler.characters(ch, start, length);
      }
      
      @Override public void setDocumentLocator(Locator locator) {
        setLocator(locator);
      }
      @Override public void error(SAXParseException e) throws SAXException {
        if (delegateHandler!=null)
          delegateHandler.error(e);
        else
          reportError(ERROR,findCause(e));
      }
      @Override public void warning(SAXParseException e) throws SAXException {
        if (delegateHandler!=null)
          delegateHandler.warning(e);
        else
          reportError(WARNING,findCause(e));
      }
      @Override public void fatalError(SAXParseException e) throws SAXException {
        if (delegateHandler!=null)
          delegateHandler.fatalError(e);
        else
          reportError(FATAL_ERROR,findCause(e));
      }
      private Throwable findCause(Throwable t) {
        Throwable cause=t.getCause();
        if (cause==null)
          return t;
        return cause;
      }
      
      private DefaultHandler delegateHandler;
    };
  }
  
  public AbstractXMLDigester getDelegate() {
    return delegate;
  }
  
  AbstractXMLDigester delegate;
  final HashMap<String,AbstractXMLDigester> digesters;
}

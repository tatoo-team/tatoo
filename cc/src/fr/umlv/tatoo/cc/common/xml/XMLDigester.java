package fr.umlv.tatoo.cc.common.xml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import fr.umlv.tatoo.cc.common.log.Info;
import fr.umlv.tatoo.cc.common.log.ReporterFactory;
import fr.umlv.tatoo.cc.common.log.Level;


/**
 * @author Remi Forax
 *
 */
public abstract class XMLDigester {
  protected XMLDigester() {
    // do nothing
  }
  
  public void parse(File file,boolean validating)
    throws ParserConfigurationException, SAXException, IOException {
    
    BufferedReader reader=new BufferedReader(new FileReader(file));
    try {
      parse(reader,file,validating);
    } finally {
      reader.close();
    }
  }
  
  public void parse(Reader reader,Object resource,boolean validating)
    throws ParserConfigurationException, SAXException, IOException {
      
    SAXParserFactory factory = SAXParserFactory.newInstance();
    factory.setNamespaceAware(true);
    factory.setValidating(validating);
    factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd",validating);
    SAXParser parser = factory.newSAXParser();
    
    Info info=ReporterFactory.getDefaultInfo();
    ReporterFactory.setAndSealDefaultInfo(info.file(resource));
    try {
      parser.parse(new InputSource(reader), handler());
    } finally {
      ReporterFactory.setAndSealDefaultInfo(info);
    }
  }
  
  protected abstract DefaultHandler handler();
  
  public void setLocator(Locator locator) {
    this.locator=locator;
  }
  public Locator getLocator() {
    return locator;
  }
  
  public void reportError(Level type,Throwable e) throws SAXException {
    //new Exception(e).printStackTrace(); 
    //System.exit(1);
    
    try {
      Info.derive(type).cause(e).
        line(locator.getLineNumber()).column(locator.getColumnNumber()).report();
    } catch(Exception reportException) {
      throw (SAXException)new SAXException().initCause(reportException);
    }
  }
  
  public void reportError(Level type,String message) throws SAXException {
    //new Exception("v2").printStackTrace(); 
    
    try {
      Info.derive(type,message).
        line(locator.getLineNumber()).column(locator.getColumnNumber()).report();
    } catch(Exception reportException) {
      throw (SAXException)new SAXException().initCause(reportException);
    }
  }
  
  public String computeId(Attributes atts) {
    return computeId(atts,true);
  }
  
  public String computeId(Attributes atts,boolean validate) {
    return computeId(atts,"id",validate);
  }
  
  public String computeId(Attributes atts,String attsName,boolean validate) {
    String id=convert(atts,attsName,String.class);
    if (validate && !JavaIds.validateId(id))
      throw new IllegalStateException("Value \""+id+"\" is invalid for attribute \""+attsName+"\"");
    return id;
  }
  
  public String computeId(Attributes atts,String attsName,String defaultValue) {
    String id=convert(atts,attsName,String.class,defaultValue);
    if (id==null)
      return null;
    if (!JavaIds.validateId(id))
      throw new IllegalStateException("Value \""+id+"\" is invalid for attribute \""+attsName+"\"");
    return id;
  }
  
  public <T> T convert(Attributes attrs,String name,Class<T> clazz) {
    String value=attrs.getValue(name);
    if (value==null)
      throw new IllegalArgumentException("No attribute with name "+name);
    return converter.convert(value,clazz);
  }
  
  public <T> T convert(Attributes attrs,String name,Class<T> clazz,T defaultValue) {
    String value=attrs.getValue(name);
    if (value==null)
      return defaultValue;
    return converter.convert(value,clazz);
  }
  
  private Locator locator;
  private final Converter converter=Converter.getDefaultConverter();
}

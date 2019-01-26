package fr.umlv.tatoo.cc.common.main;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class FileGuesser {
  private FileGuesser() {
    // no instance
  }
  
  private static class UnitException extends RuntimeException {
    private static final long serialVersionUID = 6573298817787401389L;
    private final Unit unit;

    public UnitException(Unit unit) {
      this.unit = unit;
    }
    
    public Unit getUnit() {
      return unit;
    }
  }
  static final UnitException[] EXCEPTIONS;
  static final UnitException NO_UNIT_EXCEPTION;
  static {
    Unit[] VALUES = Unit.values();
    UnitException[] exceptions =  new UnitException[VALUES.length];
    for(int i=0;i<VALUES.length;i++)
      exceptions[i]=new UnitException(VALUES[i]);
    EXCEPTIONS = exceptions;
    NO_UNIT_EXCEPTION = new UnitException(null);
  }
  
  private static final SAXParserFactory FACTORY;
  static {
    SAXParserFactory factory = SAXParserFactory.newInstance();
    factory.setNamespaceAware(true);
    FACTORY=factory;
  }
  
  public static Unit guessUnit(File file) throws IOException {
    if (file.getName().endsWith(".ebnf"))
      return Unit.ebnf;
    try {
    SAXParser parser = FACTORY.newSAXParser();
    parser.parse(file, new DefaultHandler() {
    
      @Override
      public void startElement(String uri, String localName, String name,
          Attributes attributes) throws SAXException {
        Unit ans = Unit.parse(localName);
        if (ans==null)
          throw NO_UNIT_EXCEPTION;
        throw EXCEPTIONS[ans.ordinal()];
      }      
    });
    }
    catch (UnitException u) {
      return u.getUnit();
    }
    catch (ParserConfigurationException e) {
      throw new AssertionError(e);
    }
    catch (SAXException e) {
      return null;
    }
    return null; /* empty file */
  }
  
  public static void main(String[] args) {
    if (args.length==0) {
      System.err.println("no input files");
      return;
    }
    for (String s : args) {
      if (args.length!=1)
        System.out.print(s+": ");
      try {
        System.out.println(guessUnit(new File(s)));
      }
      catch (IOException e) {
        System.out.println("failed: "+e.getClass().getSimpleName()+": "+e.getMessage());
      }
    }
  }
}

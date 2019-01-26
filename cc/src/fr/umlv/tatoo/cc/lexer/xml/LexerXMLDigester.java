/*
 * Created on 7 juil. 2005
 */
package fr.umlv.tatoo.cc.lexer.xml;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;

import fr.umlv.tatoo.cc.common.log.Info;
import fr.umlv.tatoo.cc.common.main.Unit;
import fr.umlv.tatoo.cc.common.xml.AbstractXMLDigester;
import fr.umlv.tatoo.cc.lexer.charset.encoding.Encoding;
import fr.umlv.tatoo.cc.lexer.lexer.RuleDecl;
import fr.umlv.tatoo.cc.lexer.lexer.RuleFactory;
import fr.umlv.tatoo.cc.lexer.regex.Regex;

public class LexerXMLDigester extends AbstractXMLDigester {
  public LexerXMLDigester(RuleFactory lexerFactory,Encoding charset) {
    this.lexerFactory = lexerFactory;
    this.charset=charset;
  }

  @Override
  public String getRootElementName() {
    return "lexer";
  }
  
  @Override
  public Unit getUnit() {
    return Unit.lexer;
  }

  @Override
  protected SAXlet[] getSAXLets() {
    return new SAXlet[] { 
        new DefaultSAXlet("lexer"),
        new DefaultSAXlet("define-macro-xml") {
          @Override
          public void start(String element, Attributes atts) {
            getXMLRuleCompiler().reset();
          }

          @Override
          public void end(String element, Attributes atts) throws Exception {
            macroes.put(atts.getValue("name"), getXMLRuleCompiler().getRegex());
          }
        }, 
        new DefaultSAXlet("define-macro") {
          @Override
          public void end(String element, Attributes atts) throws Exception {
            macroes.put(atts.getValue("name"), getPatternRuleCompiler().createMacro(atts
                .getValue("pattern")));
          }
        }, 
        new DefaultSAXlet("rule-xml") {
          @Override
          public void start(String element, Attributes atts) {
            getXMLRuleCompiler().reset();
          }

          @Override
          public void end(String element, Attributes atts) {
            String id = computeId(atts);
            boolean beginningOfLineRequired = convert(atts,
                "beginning-of-line", boolean.class, false);
            getXMLRuleCompiler().createRule(lexerFactory, id, beginningOfLineRequired);
          }
        }, 
        new DefaultSAXlet("rule") {
          @Override
          public void end(String element, Attributes atts) {
            String id = computeId(atts);
            String pattern = atts.getValue("pattern");
            getPatternRuleCompiler().createRule(lexerFactory, id, pattern);
          }
        } 
     };
  }

  public Map<String,Regex> getMacroes() {
    return macroes;
  }
  
  PatternRuleCompiler getPatternRuleCompiler() {
    if (patternRuleCompiler!=null)
      return patternRuleCompiler;
    return patternRuleCompiler=createPatternRuleCompiler(charset);
  }
  
  XMLRuleCompiler getXMLRuleCompiler() {
    if (xmlRuleCompiler!=null)
      return xmlRuleCompiler;
    return xmlRuleCompiler=new XMLRuleCompiler(this,charset);
  }

  private final Encoding charset;
  private PatternRuleCompiler patternRuleCompiler;
  private XMLRuleCompiler xmlRuleCompiler;

  final HashMap<String, Regex> macroes = new HashMap<String, Regex>();

  final RuleFactory lexerFactory;

  @SuppressWarnings("unchecked")
  private PatternRuleCompiler createPatternRuleCompiler(Encoding charset) {
    
    try {
      Class<? extends PatternRuleCompiler> clazz=(Class<? extends PatternRuleCompiler>)
        Class.forName("fr.umlv.tatoo.cc.lexer.regex.pattern.PatternRuleCompilerImpl");
    
      Constructor<? extends PatternRuleCompiler> c = clazz.getConstructor(
          Map.class, Encoding.class);
      return c.newInstance(macroes,charset);
    } catch (ClassNotFoundException e) {
      
      Info.warning("pattern rule compiler not available %s",e.getMessage()).cause(e).report();
      
      return new PatternRuleCompiler() {
        public Regex createMacro(String pattern) {
          throw new IllegalStateException("pattern rule compiler not available");
        }

        public RuleDecl createRule(RuleFactory lexerFactory, String id, String pattern) {
          throw new IllegalStateException("pattern rule compiler not available");
        }
      };
    
    } catch (InvocationTargetException e) {
      Throwable cause=e.getCause();
      if (cause instanceof RuntimeException) 
        throw (RuntimeException)cause;
      if (cause instanceof Error)
        throw (Error)cause;
      throw new UndeclaredThrowableException(cause);
      
    } catch(Exception e) {
      throw new AssertionError(e);
    }
  }
}

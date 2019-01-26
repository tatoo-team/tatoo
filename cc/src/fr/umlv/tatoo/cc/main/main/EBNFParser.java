package fr.umlv.tatoo.cc.main.main;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import fr.umlv.tatoo.cc.common.generator.Type;
import fr.umlv.tatoo.cc.lexer.charset.encoding.Encoding;
import fr.umlv.tatoo.cc.lexer.lexer.RuleFactory;
import fr.umlv.tatoo.cc.parser.grammar.EBNFSupport;
import fr.umlv.tatoo.cc.parser.grammar.GrammarFactory;
import fr.umlv.tatoo.cc.parser.grammar.ParserTableBuilder;
import fr.umlv.tatoo.cc.parser.grammar.TerminalDecl;
import fr.umlv.tatoo.cc.tools.tools.ToolsFactory;

/** Interface used to de-couple MainBatch from ebnf package.
 * 
 * @author Remi
 */
public abstract class EBNFParser {
  /** Parse an EBNF file.
   * @param source the EBNF file.
   * 
   * @return an object that implements ParserTableBuilder interface.
   * 
   * @throws IOException raised if an input error occurs.
   */
  public abstract ParserTableBuilder parse(File source) throws IOException;
  
  /** Parse an EBNF file.
   * @param reader the EBNF file reader.
   * 
   * @return an object that implements ParserTableBuilder interface.
   * 
   */
  public abstract ParserTableBuilder parse(Reader reader);
  
  @SuppressWarnings("unchecked")
  public static EBNFParser createEBNFParser(RuleFactory ruleFactory,Encoding encoding,GrammarFactory grammarFactory,EBNFSupport ebnfSupport,ToolsFactory toolsFactory,Map<String,Type> attributeMap) {
    try {
      Class<? extends EBNFParser> clazz=(Class<? extends EBNFParser>)Class.forName(
        "fr.umlv.tatoo.cc.ebnf.EBNFParserImpl");
    
      Constructor<? extends EBNFParser> constructor=clazz.getConstructor(
        RuleFactory.class,Encoding.class,GrammarFactory.class,EBNFSupport.class,ToolsFactory.class,Map.class);
      
      return constructor.newInstance(ruleFactory,encoding,grammarFactory,ebnfSupport,toolsFactory,attributeMap);
      
    } catch (ClassNotFoundException e) {
      return null;
    } catch (NoSuchMethodException e) {
      throw new AssertionError(e);
    } catch (InstantiationException e) {
      throw new AssertionError(e);
    } catch (IllegalAccessException e) {
      throw new AssertionError(e);
    } catch (InvocationTargetException e) {
      Throwable cause=e.getCause();
      if (cause instanceof RuntimeException)
        throw (RuntimeException)cause;
      if (cause instanceof Error)
        throw (Error)cause;
      throw new AssertionError(cause);
    }
  }
}
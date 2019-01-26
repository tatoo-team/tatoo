package fr.umlv.tatoo.cc.lexer.xml;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import fr.umlv.tatoo.cc.common.log.Level;
import fr.umlv.tatoo.cc.common.xml.AbstractXMLDigester.DefaultSAXlet;
import fr.umlv.tatoo.cc.common.xml.AbstractXMLDigester.SAXlet;
import fr.umlv.tatoo.cc.lexer.charset.CharacterInterval;
import fr.umlv.tatoo.cc.lexer.charset.encoding.Encoding;
import fr.umlv.tatoo.cc.lexer.lexer.RuleDecl;
import fr.umlv.tatoo.cc.lexer.lexer.RuleFactory;
import fr.umlv.tatoo.cc.lexer.regex.Leaf;
import fr.umlv.tatoo.cc.lexer.regex.Regex;
import fr.umlv.tatoo.cc.lexer.regex.RegexFactory;

public class XMLRuleCompiler {
  public XMLRuleCompiler(LexerXMLDigester digester, Encoding charset) {
    this.encoding = charset;
    this.digester = digester;
  }
  
  public void reset() {
    main=follow=null;
    digester.registerSaxlets(saxlets);
  }
  
  public Regex getRegex() {
    digester.unregisterSaxlets(saxlets);
    return pop();
  }
  
  public RuleDecl createRule(RuleFactory lexerFactory,String id,boolean beginningOfLineRequired) {
    digester.unregisterSaxlets(saxlets);
    return lexerFactory.createRule(id,
      main,follow,lexerFactory.getAllRules().size(),
      beginningOfLineRequired);
  }
  
  private final SAXlet[] saxlets=new SAXlet[] {
    new DefaultSAXlet("main") {
      @Override
      public void end(String element, Attributes atts) {
        main=pop();
      }
    },
    new DefaultSAXlet("follow") {
      @Override
      public void end(String element, Attributes atts) {
        follow=pop();
      }
    },
    new DefaultSAXlet("end-of-line") {
      @Override
      public void end(String element, Attributes atts) {
        follow=RegexFactory.createDollarRegex(encoding);
      }
    },
    new DefaultSAXlet("any") {
      @Override
      public void end(String element, Attributes atts) throws Exception {
        push(new Leaf(encoding.getAny()));
      }
    },
    new DefaultSAXlet("letter") {
      @Override
      public void end(String element, Attributes atts) throws Exception {
        char c = digester.convert(atts, "value", char.class);
        if (inSet)
          intervals.add(new CharacterInterval(c,c,encoding));
        else
          push(RegexFactory.letter(c,encoding));
      }
    },
    new DefaultSAXlet("interval") {
      @Override
      public void end(String element, Attributes atts) throws Exception {
        char to = digester.convert(atts, "to", char.class);
        char from = digester.convert(atts, "from", char.class);
        CharacterInterval interval = new CharacterInterval(from,to,encoding);
        if (!inSet) {
          push(RegexFactory.interval(from,to,encoding));
        }
        else {
          intervals.add(interval);
        }
      }
    },
    new DefaultSAXlet("string") {
      @Override
      public void end(String element, Attributes atts) throws Exception {
        boolean bol = digester.convert(atts, "ignore-case", boolean.class, false);
        String name = digester.convert(atts, "value", String.class);
        push(RegexFactory.characterSequence(name, bol,encoding));
      }
    },
    new DefaultSAXlet("star") {
      @Override
      public void end(String element, Attributes atts) throws Exception {
        push(RegexFactory.star(pop()));
      }
    },
    new DefaultSAXlet("plus") {
      @Override
      public void end(String element, Attributes atts) throws Exception {
        push(RegexFactory.plus(pop()));
      }
    },
    new DefaultSAXlet("optional") {
      @Override
      public void end(String element, Attributes atts) throws Exception {
        push(RegexFactory.optional(pop()));
      }
    },
    new DefaultSAXlet("range") {
      @Override
      public void end(String element, Attributes atts) throws Exception {
        int from = digester.convert(atts, "from", int.class);
        int to = digester.convert(atts, "to", int.class);
        push(RegexFactory.range(from, to, pop()));
      }
    },
    new DefaultSAXlet("at-least") {
      @Override
      public void end(String element, Attributes atts) throws Exception {
        int from = digester.convert(atts, "from", int.class);
        push(RegexFactory.atLeast(from, pop()));
      }
    },
    new DefaultSAXlet("times") {
      @Override
      public void end(String element, Attributes atts) throws Exception {
        int value = digester.convert(atts, "value", int.class);
        push(RegexFactory.times(value, pop()));
      }
    },
    new DefaultSAXlet("cat") {

      @Override
      public void start(String element, Attributes atts) throws Exception {
        push(MARKER);
      }
      @Override
      public void end(String element, Attributes atts) throws Exception {
        Regex deux = pop();
        Regex un;
        while ((un = pop())!= MARKER) {
          deux = RegexFactory.cat(un, deux);
        }
        push(deux);
      }
    },
    new DefaultSAXlet("or") {
      @Override
      public void start(String element, Attributes atts) throws Exception {
        push(MARKER);
      }
      @Override
      public void end(String element, Attributes atts) throws Exception {
        Regex deux = pop();
        Regex un;
        while ((un = pop())!= MARKER) {
          deux = RegexFactory.or(un, deux);
        }
        push(deux);
      }
    },
    new DefaultSAXlet("set") {
      @Override
      public void start(String element, Attributes atts) throws Exception {
        inSet=true;
      }
      @Override
      public void end(String element, Attributes atts) throws Exception {
        inSet=false;
        boolean bol = digester.convert(atts, "negate", boolean.class, false);
        push(RegexFactory.createSet(bol,intervals,encoding));
        intervals.clear();
      }
    },
    new DefaultSAXlet("macro") {
      @Override
      public void end(String element, Attributes atts) throws Exception {
        String name = digester.convert(atts, "name", String.class);
        Regex regex = digester.getMacroes().get(name);
        if (regex==null)
          throw new IllegalArgumentException("undefined macro "+name);
        push(RegexFactory.clone(regex));
      }
    }
  };
  
  Regex pop() {
    if (stack.isEmpty()) {
      try {
        digester.reportError(Level.ERROR,"no children element defined");
        throw new IllegalArgumentException("no children element defined");
      } catch (SAXException e) {
        Throwable cause=e.getCause();
        if (cause instanceof RuntimeException)
          throw (RuntimeException)cause;
        if (cause instanceof Error)
          throw (Error)cause;
        throw new IllegalArgumentException(cause);
      }
    }
    return stack.remove(stack.size()-1);
  }

  void push(Regex regex) {
    stack.add(regex);
  }

  boolean isEmpty() {
    return stack.isEmpty();
  }

  public Regex getMain() {
    return main;
  }
  public Regex getFollow() {
    return follow;
  }
  
  boolean inSet;
  Regex main,follow;
  final LexerXMLDigester digester;
  final Encoding encoding;
  final ArrayList<CharacterInterval> intervals = new ArrayList<CharacterInterval>();
  
  private final ArrayList<Regex> stack = new ArrayList<Regex>();
  final static Regex MARKER=null;
}

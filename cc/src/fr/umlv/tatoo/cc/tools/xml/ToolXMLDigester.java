package fr.umlv.tatoo.cc.tools.xml;


import java.util.HashMap;

import org.xml.sax.Attributes;

import fr.umlv.tatoo.cc.common.generator.IdMap;
import fr.umlv.tatoo.cc.common.generator.Type;
import fr.umlv.tatoo.cc.common.main.Unit;
import fr.umlv.tatoo.cc.common.xml.AbstractXMLDigester;
import fr.umlv.tatoo.cc.lexer.lexer.RuleDecl;
import fr.umlv.tatoo.cc.parser.grammar.NonTerminalDecl;
import fr.umlv.tatoo.cc.parser.grammar.TerminalDecl;
import fr.umlv.tatoo.cc.parser.grammar.VariableDecl;
import fr.umlv.tatoo.cc.tools.tools.ToolsFactory;

public class ToolXMLDigester extends AbstractXMLDigester {
  public ToolXMLDigester(IdMap<RuleDecl> ruleMap,IdMap<VariableDecl> variableMap,ToolsFactory toolsFactory) {
    this.ruleMap=ruleMap;
    this.variableMap=variableMap;
    this.toolsFactory=toolsFactory;
  }
  
  @Override public String getRootElementName() {
    return "tools";
  }
  
  @Override
  public Unit getUnit() {
    return Unit.tools;
  }
  
  @Override
  protected SAXlet[] getSAXLets() {
    return new SAXlet[] {
      new DefaultSAXlet("tools"),
      new DefaultSAXlet("rule") {
        @Override
        public void start(String element, Attributes atts) {
          String ruleId=computeId(atts);
          RuleDecl rule=ruleMap.get(RuleDecl.class,ruleId);
          if (rule==null)
            throw new IllegalStateException("no rule declaration in lexer file for "+ruleId);
          
          boolean discard=convert(atts,"discard",boolean.class,true);
          
          TerminalDecl terminal=null;
          String terminalId=computeId(atts,"terminal",null);
          if (terminalId!=null)
            terminal=variableMap.get(TerminalDecl.class,terminalId);
          
          // default value: false if there is a terminal, true otherwise
          boolean alwaysActive=convert(atts,"always-active",boolean.class,terminal==null);
          
          // default value: true if there is a terminal, false otherwise
          boolean spawn=convert(atts,"spawn",boolean.class,terminal!=null);
          
          TerminalDecl terminalPart=null; 
          String terminalPartId=computeId(atts,"terminal-part",null);
          if (terminalPartId!=null)
            terminalPart=variableMap.get(TerminalDecl.class,terminalPartId);
          
          toolsFactory.createRuleInfo(rule,terminal,terminalPart,discard,alwaysActive,spawn);
        }
      },
      new DefaultSAXlet("import") {
        @Override
        public void start(String element, Attributes atts) {
          String typeName=convert(atts,"type",String.class);
          int index=typeName.lastIndexOf('.');
          imports.put(typeName.substring(index+1),Type.createQualifiedType(typeName));
        }
      },
      new DefaultSAXlet("terminal") {
        @Override
        public void start(String element, Attributes atts) {
          String id=computeId(atts);
          
          Type type=convertType(atts);
          if (type==null)
            return;
          
          TerminalDecl terminal=variableMap.get(TerminalDecl.class,id);
          toolsFactory.declareTerminalType(terminal,type);
        }
      },
      new DefaultSAXlet("non-terminal") {
        @Override
        public void start(String element, Attributes atts) {
          String id=computeId(atts);
          Type type=convertType(atts);
          if (type==null)
            return;
          
          NonTerminalDecl nonTerminal=variableMap.get(NonTerminalDecl.class,id);
          toolsFactory.declareNonTerminalType(nonTerminal,type);
        }
      }
    };
  }
  
  Type convertType(Attributes atts) {
    String type=convert(atts,"type",String.class,null);
    if (type==null)
      return null;
    
    return Type.createType(type,imports);
  }
  
  final ToolsFactory toolsFactory;
  final IdMap<VariableDecl> variableMap;
  final IdMap<RuleDecl> ruleMap;
  
  final HashMap<String,Type> imports=new HashMap<String,Type>();
}

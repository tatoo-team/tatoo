package fr.umlv.tatoo.cc.parser.xml;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.xml.sax.Attributes;

import fr.umlv.tatoo.cc.common.extension.ExtensionBus;
import fr.umlv.tatoo.cc.common.main.Unit;
import fr.umlv.tatoo.cc.common.xml.AbstractXMLDigester;
import fr.umlv.tatoo.cc.common.xml.JavaIds;
import fr.umlv.tatoo.cc.parser.grammar.EBNFSupport;
import fr.umlv.tatoo.cc.parser.grammar.GrammarFactory;
import fr.umlv.tatoo.cc.parser.grammar.GrammarRepository;
import fr.umlv.tatoo.cc.parser.grammar.NonTerminalDecl;
import fr.umlv.tatoo.cc.parser.grammar.ParserTableBuilder;
import fr.umlv.tatoo.cc.parser.grammar.Priority;
import fr.umlv.tatoo.cc.parser.grammar.VariableDecl;
import fr.umlv.tatoo.cc.parser.grammar.VersionDecl;
import fr.umlv.tatoo.cc.parser.grammar.Priority.Associativity;
import fr.umlv.tatoo.cc.parser.parser.ActionDeclFactory;
import fr.umlv.tatoo.cc.parser.table.AbstractConflictDiagnosisReporter;
import fr.umlv.tatoo.cc.parser.table.ConflictResolverPolicy;
import fr.umlv.tatoo.cc.parser.table.ParserTableDecl;
import fr.umlv.tatoo.cc.parser.table.ParserTableDeclFactory;
import fr.umlv.tatoo.cc.parser.table.TableFactoryMethod;

public class ParserXMLDigester extends AbstractXMLDigester implements ParserTableBuilder {
  public ParserXMLDigester(GrammarFactory factory,EBNFSupport ebnfSupport) {
    this.factory=factory;
    this.ebnfSupport=ebnfSupport;
  }
  
  public ParserTableDecl createParserTableDecl(ExtensionBus extensionBus,ActionDeclFactory actionFactory,
      TableFactoryMethod<?> method, ConflictResolverPolicy conflictResolver,
      AbstractConflictDiagnosisReporter reporter,File log) {
    
    return ParserTableDeclFactory.buildTable(extensionBus,reporter,this.factory.getAllProductions(), 
        factory.getStartNonTerminalSet(), factory.getEof(), factory.getError(), factory.getVersionManager(),actionFactory, 
       method, conflictResolver, log);
  }
  
  public boolean isFatalError() {
    // FIXME Remi not implemented for now
    return false;
  }

  
  public EBNFSupport getEBNFSupport() {
    return ebnfSupport;
  }
  
  public GrammarRepository getGrammarItemsRepository() {
    return factory;
  }
  
  @Override public String getRootElementName() {
    return "parser";
  }
  
  @Override
  public Unit getUnit() {
    return Unit.parser;
  }
  
  @Override
  protected SAXlet[] getSAXLets() {
    return new SAXlet[] {
      new DefaultSAXlet("parser") {
        @Override
        public void end(String element, Attributes atts) throws Exception {
          //FIXME Remi perhaps not neccessary !!
          // if no version
          if (factory.getAllVersions().isEmpty())
            factory.createVersion("DEFAULT",null);
        }
      },
      new DefaultSAXlet("priority") {
        @Override
        public void start(String element, Attributes atts) {
          String id=computeId(atts);
          if (id.equals("DEFAULT")) {
            throw new IllegalArgumentException("Priority id (default) is reserved for default priority");
          }
          priorities.put(id,new Priority(
            id,
            convert(atts,"priority",double.class,Double.NaN),
            convert(
                atts,"associativity",Associativity.class,Associativity.NONE)
          ));
        }
      },
      new DefaultSAXlet("terminal") {
        @Override
        public void start(String element, Attributes atts) {
          String id=computeId(atts);
          factory.createTerminal(id,getPriority(atts),false);
        }
      },
      new DefaultSAXlet("branch") {
        @Override
        public void start(String element, Attributes atts) {
          String id=computeId(atts);
          factory.createTerminal(id,getPriority(atts),true);
        }
      },
      new DefaultSAXlet("version") {
        @Override
        public void start(String element, Attributes atts) {
          String id=computeId(atts);
          
          VersionDecl implies=getVersion(atts,"implies");
          factory.createVersion(id,implies);
        }
      },
      new DefaultSAXlet("start") {
        @Override
        public void start(String element, Attributes atts) {            
          String startId=computeId(atts);
          factory.addStartNonTerminal(getOrCreateVariable(NonTerminalDecl.class,startId));
        }
      },
      new DefaultSAXlet("error") {
        @Override
        public void start(String element, Attributes atts) {
          if (factory.getError()!=null) {
            throw new IllegalStateException("An error terminal is already declared");
          }
            
          String errorId=computeId(atts);
          factory.createError(errorId);
        }
      },
      new DefaultSAXlet("production") {
        @Override
        public void start(String element, Attributes atts) {
          left=null;
          rights.clear();
          
          productionVersion=getVersion(atts,"version");
        }
        @Override
        public void end(String element, Attributes atts) {
          String id=computeId(atts);
          if (left==null)
            throw new IllegalStateException("lhs non-terminal is not defined for production "+id);
          
          factory.createProduction(id,left,rights,
            createProductionPriority(id,rights,atts),productionVersion);
        }
      },
      new DefaultSAXlet("lhs") {
        @Override
        public void start(String element, Attributes atts) {
          String id=computeId(atts);
          left=getOrCreateVariable(NonTerminalDecl.class,id);
        }
      },
      new DefaultSAXlet("rhs"),
      new DefaultSAXlet("right") {
        @Override
        public void start(String element, Attributes atts) {
          String id=computeId(atts,false);
          
          VariableDecl right=getOrCreateVariable(VariableDecl.class,id);
          boolean isOptional=convert(atts,"optional",boolean.class,false);
          if (isOptional) 
            right = ebnfSupport.createOptionnalNonTerminal(id,right,productionVersion);
            
          rights.add(right);
        }
      },
      new DefaultSAXlet("list") {
        @Override
        public void start(String element, Attributes atts) {
          String id=computeId(atts,false);
          
          VariableDecl elementVar=getOrCreateVariable(VariableDecl.class,
            computeId(atts,"element",false));
          String separatorId=computeId(atts,"separator",null);
          VariableDecl separator;
          if (separatorId==null)
            separator=null;
          else
            separator=getOrCreateVariable(VariableDecl.class,separatorId);
          
          boolean isStar=convert(atts,"empty",boolean.class);
          
          Associativity assoc=convert(
              atts,"associativity",Associativity.class,Associativity.LEFT);
          if (assoc==Associativity.NONE || assoc==Associativity.NON_ASSOCIATIVE)
            throw new IllegalStateException("illegal associativity "+assoc);
          
          rights.add(ebnfSupport.createStarNonTerminal(id, elementVar,assoc,productionVersion,isStar,separator));
        }
      }
    };
  }
  
  <E extends VariableDecl> E getOrCreateVariable(Class<E> clazz,String id) {
    E value=factory.getVariableMap().get(clazz,id);
    if (value!=null)
      return value;
    
    //  here E could only be VariableDecl or NonTerminalDecl
    if (!JavaIds.validateId(id))
      throw new IllegalStateException("Invalid id ("+id+")");

    NonTerminalDecl nt=factory.createNonTerminal(id);
    return clazz.cast(nt);
  }
  
  Priority createProductionPriority(String id, ArrayList<VariableDecl> rights, Attributes atts) {
    if(atts.getValue("priority")!=null)
      return getPriority(atts);
    
    return factory.findProductionPriority(id,rights);
  }
  
  Priority getPriority(Attributes atts) {
    String priorityId=convert(atts,"priority",String.class,null);
    if (priorityId!=null && !priorityId.equals("default")) {
      Priority priority=priorities.get(priorityId);
      if (priority==null)
        throw new IllegalArgumentException("Unknown priority "+priorityId);
      return priority;
    }
   return Priority.getNoPriority();
  }
  
  VersionDecl getVersion(Attributes atts,String attributeName) {
    String value=convert(atts,attributeName,String.class,null);
    if (value==null)
      return null;
    
    VersionDecl version=factory.getVersion(value);
    if (version==null)
      throw new IllegalStateException("Unknown version "+value);
    return version;
  }
  
  NonTerminalDecl left;
  VersionDecl productionVersion;
  final EBNFSupport ebnfSupport;
  final ArrayList<VariableDecl> rights=new ArrayList<VariableDecl>();
  final HashMap<String,Priority> priorities= new HashMap<String,Priority>();
  
  final GrammarFactory factory;
}

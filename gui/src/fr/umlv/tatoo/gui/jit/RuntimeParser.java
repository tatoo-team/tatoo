package fr.umlv.tatoo.gui.jit;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import fr.umlv.tatoo.cc.parser.grammar.EBNFSupport;
import fr.umlv.tatoo.cc.parser.grammar.GrammarFactory;
import fr.umlv.tatoo.cc.parser.grammar.NonTerminalDecl;
import fr.umlv.tatoo.cc.parser.grammar.ProductionDecl;
import fr.umlv.tatoo.cc.parser.grammar.TerminalDecl;
import fr.umlv.tatoo.cc.parser.grammar.VariableDecl;
import fr.umlv.tatoo.cc.parser.grammar.VersionDecl;
import fr.umlv.tatoo.cc.parser.main.ConflictResolverType;
import fr.umlv.tatoo.cc.parser.main.ParserType;
import fr.umlv.tatoo.cc.parser.parser.AcceptActionDecl;
import fr.umlv.tatoo.cc.parser.parser.ActionDecl;
import fr.umlv.tatoo.cc.parser.parser.ActionDeclFactory;
import fr.umlv.tatoo.cc.parser.parser.BranchActionDecl;
import fr.umlv.tatoo.cc.parser.parser.EnterActionDecl;
import fr.umlv.tatoo.cc.parser.parser.ErrorActionDecl;
import fr.umlv.tatoo.cc.parser.parser.ExitActionDecl;
import fr.umlv.tatoo.cc.parser.parser.ReduceActionDecl;
import fr.umlv.tatoo.cc.parser.parser.RegularTableActionDecl;
import fr.umlv.tatoo.cc.parser.parser.ShiftActionDecl;
import fr.umlv.tatoo.cc.parser.parser.SimpleActionDeclVisitor;
import fr.umlv.tatoo.cc.parser.parser.VersionedActionDecl;
import fr.umlv.tatoo.cc.parser.table.LogInfoConflictDiagnosisReporter;
import fr.umlv.tatoo.cc.parser.table.ParserTableDecl;
import fr.umlv.tatoo.cc.parser.table.ParserTableDeclFactory;
import fr.umlv.tatoo.cc.parser.table.StateMetadataDecl;
import fr.umlv.tatoo.cc.parser.xml.ParserXMLDigester;
import fr.umlv.tatoo.gui.util.Mapper;
import fr.umlv.tatoo.gui.util.Mapper.Function;
import fr.umlv.tatoo.runtime.parser.AcceptAction;
import fr.umlv.tatoo.runtime.parser.Action;
import fr.umlv.tatoo.runtime.parser.BranchAction;
import fr.umlv.tatoo.runtime.parser.DefaultParserErrorRecoveryPolicy;
import fr.umlv.tatoo.runtime.parser.EnterAction;
import fr.umlv.tatoo.runtime.parser.ErrorAction;
import fr.umlv.tatoo.runtime.parser.ParserErrorRecoveryListener;
import fr.umlv.tatoo.runtime.parser.ExitAction;
import fr.umlv.tatoo.runtime.parser.Parser;
import fr.umlv.tatoo.runtime.parser.ParserListener;
import fr.umlv.tatoo.runtime.parser.ParserTable;
import fr.umlv.tatoo.runtime.parser.ReduceAction;
import fr.umlv.tatoo.runtime.parser.ShiftAction;
import fr.umlv.tatoo.runtime.parser.StateMetadata;
import fr.umlv.tatoo.runtime.parser.VersionedAction;
import fr.umlv.tatoo.runtime.tools.LookaheadMapFactory;

public class RuntimeParser {
  
  private final GrammarFactory factory;
  private final ParserXMLDigester digester;
  final Parser<NamedObject,NamedObject,Production,NamedObject> parser;
  private final  NamedObject[] versions;
  private final  NamedObject[] starts;
  final NamedObjectFactory objectFactory;
  
  public RuntimeParser(File xmlFile,
      ParserListener<? super NamedObject,? super NamedObject,? super Production> listener,
      ParserErrorRecoveryListener<? super NamedObject, ? super NamedObject> errorListener,
      ParserSettings settings) throws ParserConfigurationException, SAXException, IOException {
    
    factory = new GrammarFactory();
    EBNFSupport ebnfSupport=new EBNFSupport(factory); 
    digester = new ParserXMLDigester(factory,ebnfSupport);
    
    digester.parse(xmlFile, true);
    TerminalDecl eofDecl = factory.getEof();
    TerminalDecl errorDecl = factory.getError();
    Set<? extends NonTerminalDecl> startsDecl = factory.getStartNonTerminalSet();
    
    objectFactory = new NamedObjectFactory(eofDecl,errorDecl);
    
    versions = toArray(factory.getAllVersions(),
        objectFactory.versionFunction());
    
    starts = toArray(startsDecl,
        objectFactory.nonTerminalFunction());
    
    // generate table
    ActionDeclFactory actionFactory=new ActionDeclFactory();
    LogInfoConflictDiagnosisReporter reporter=new LogInfoConflictDiagnosisReporter(null);
    ParserTableDecl grammar = ParserTableDeclFactory.
      buildTable(null,reporter,factory.getAllProductions(), 
        startsDecl, eofDecl, errorDecl, 
        factory.getVersionManager(),actionFactory, 
        ParserType.lalr.getMethod(), ConflictResolverType.DEFAULT.getConflictResolver(),null);
    
    final Map<NonTerminalDecl, int[]> gotoes = grammar.getGotoes();
    
    HashMap<NamedObject,Action<NamedObject,Production,NamedObject>[]> runtimeTable =
      Mapper.apply(grammar.getActionMap(), objectFactory.terminalFunction(), 
          new Mapper.Function<RegularTableActionDecl[], Action<NamedObject,Production,NamedObject>[]>() {       
            public Action<NamedObject,Production, NamedObject>[] apply(
                RegularTableActionDecl[] actionDecls) {
              return runtimize(actionDecls,gotoes,objectFactory);
            }
          });
    
    //create branch array
    Action<NamedObject,Production,NamedObject>[] branchArray=
      runtimize(grammar.getBranchArray(),gotoes,objectFactory);
    
    // create metadata
    @SuppressWarnings("unchecked")
    StateMetadata<NamedObject,NamedObject,Production,NamedObject>[] metadatas=
      (StateMetadata<NamedObject,NamedObject,Production,NamedObject>[])new StateMetadata<?,?,?,?>[grammar.getStateCount()];
    
    StateMetadataDecl[] stateMetadata = grammar.getMetadata();
    for(int i=0;i<stateMetadata.length;i++)
      metadatas[i]=createMetadata(stateMetadata[i],versions.length,objectFactory);
    
    HashMap<NamedObject,Integer> startMap = Mapper.apply(grammar.getStartStateMap(),
        objectFactory.nonTerminalFunction(),
        Mapper.<Integer>id());
    
    // create parser table
    ParserTable<NamedObject,NamedObject,Production,NamedObject> parserTable =
      new ParserTable<NamedObject,NamedObject,Production,NamedObject>(
          runtimeTable,branchArray,metadatas,startMap,versions,
          grammar.getStateCount(),objectFactory.getEof(),objectFactory.getError());
    
    
    NamedObject version = objectFactory.get(factory.getAllVersions().iterator().next());
    NamedObject start = objectFactory.get(startsDecl.iterator().next());
    
    settings.setStart(start);
    settings.setVersion(version);
    
    parser = Parser.createParser(
        parserTable,listener,
        new DefaultParserErrorRecoveryPolicy<NamedObject, NamedObject, Production, NamedObject>(errorListener),
        start,version,LookaheadMapFactory.hashLookaheadMap(parserTable));
    
  }
  
  private static <T> NamedObject[] toArray(Collection<? extends T> source,
      Function<T, NamedObject> function) {
    return Mapper.toArray(source, new NamedObject[source.size()], function);
  }
  
  public GrammarFactory getFactory() {
    return factory;
  }
  
  public Parser<NamedObject, NamedObject, Production, NamedObject> getParser() {
    return parser;
  }
  
  
  void resetParser(ParserSettings settings) {
    parser.reset(settings.getStart());
    NamedObject version =  settings.getVersion();
    parser.setVersion(version);
  }
  
  private static StateMetadata<NamedObject,NamedObject,Production,NamedObject>
  createMetadata(StateMetadataDecl decl,int versionCount,NamedObjectFactory factory) {
    
    if (decl==null || decl.getAssociated()==null)
      return null;
    
    VariableDecl associated=decl.getAssociated();
    Set<? extends VersionDecl> compatibleVersionsDecl=decl.getCompatibleVersions();
    HashSet<NamedObject> compatibleVersions = null;
    if (compatibleVersionsDecl != null && compatibleVersionsDecl.size()!=versionCount) {
      compatibleVersions = Mapper.apply(compatibleVersionsDecl,factory.versionFunction());
    }
    
    if (associated.isTerminal()) {
      TerminalDecl terminal=(TerminalDecl)associated;  
      if (compatibleVersions==null)
        return StateMetadata.createAllVersionWithTerminal(factory.get(terminal),null);
      else
        return StateMetadata.createWithTerminal(compatibleVersions,factory.get(terminal),null);
    }
    else {
      NonTerminalDecl nonTerminal=(NonTerminalDecl)associated;
      if (compatibleVersions==null)
        return StateMetadata.createAllVersionWithNonTerminal(factory.get(nonTerminal),null);
      else
        return StateMetadata.createWithNonTerminal(compatibleVersions,factory.get(nonTerminal),null);
    }
  }
  
  static Action<NamedObject,Production, NamedObject>[] runtimize(ActionDecl[] actionDecls, Map<NonTerminalDecl, int[]> gotoes,
      NamedObjectFactory factory) {
    @SuppressWarnings("unchecked")
    Action<NamedObject,Production,NamedObject>[] actions=
      (Action<NamedObject,Production,NamedObject>[])new Action<?,?,?>[actionDecls.length];
    for(int i=0;i<actionDecls.length;i++)
      actions[i]=runtimize(gotoes,actionDecls[i],factory);
    return actions;
  }
  
  static Action<NamedObject,Production,NamedObject> runtimize(final Map<NonTerminalDecl,int[]> gotoes,
      ActionDecl decl,final NamedObjectFactory factory) {
    SimpleActionDeclVisitor<Action<NamedObject,Production,NamedObject>> visitor=
      new SimpleActionDeclVisitor<Action<NamedObject,Production,NamedObject>>() {
      @Override
      public Action<NamedObject,Production, NamedObject> visit(AcceptActionDecl accept) {
        return AcceptAction.getInstance();
      }
      @Override
      public Action<NamedObject,Production, NamedObject> visit(ShiftActionDecl shift) {
        return new ShiftAction<NamedObject,Production,NamedObject>(shift.getState().getStateNo());
      }
      @Override
      public Action<NamedObject,Production, NamedObject> visit(ReduceActionDecl reduce) {
        ProductionDecl productionDecl = reduce.getProduction();
        int rightSize = productionDecl.getRight().size();
        Production production = new Production(rightSize,factory.get(productionDecl.getLeft()));
        return new ReduceAction<NamedObject,Production,NamedObject>(production,rightSize,
            gotoes.get(reduce.getGotos()));
      }
      @Override
      public Action<NamedObject,Production, NamedObject> visit(ErrorActionDecl error) {
        return new ErrorAction<NamedObject,Production,NamedObject>(error.getMessage());
      }
      @Override
      public Action<NamedObject,Production, NamedObject> visit(BranchActionDecl error) {
        return new BranchAction<NamedObject,Production,NamedObject>(error.getMessage());
      }
      @Override
      public Action<NamedObject,Production,NamedObject> visit(VersionedActionDecl versioned) {
        HashMap<NamedObject,Action<NamedObject,Production,NamedObject>> map=
          new HashMap<NamedObject,Action<NamedObject,Production,NamedObject>>();
        
        for(Map.Entry<VersionDecl,? extends ActionDecl> entry:versioned.getActionMap().entrySet())
          map.put(factory.get(entry.getKey()),entry.getValue().accept(this));
        
        return new VersionedAction<NamedObject,Production,NamedObject>(map);
      }
      @Override
      public Action<NamedObject, Production, NamedObject> visit(EnterActionDecl enter) {
        return new EnterAction<NamedObject,Production,NamedObject>(
          new NamedObject(enter.getBranchingTerminal().getId()),
          enter.getState().getStateNo());
      }
      @Override
      public Action<NamedObject, Production, NamedObject> visit(ExitActionDecl exit) {
        return ExitAction.<NamedObject,Production,NamedObject>getInstance();
      }
    };
    return decl.accept(visitor);
  }
  
  public NamedObject[] getStarts() {
    return starts;
  }
  
  public NamedObject[] getVersions() {
    return versions;
  }
  
  public NamedObjectFactory getObjectFactory() {
    return objectFactory;
  }
  
  public void close() {
    parser.close();
  }
}

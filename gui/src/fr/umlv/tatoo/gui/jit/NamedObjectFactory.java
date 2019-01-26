package fr.umlv.tatoo.gui.jit;

import java.util.HashMap;

import fr.umlv.tatoo.cc.parser.grammar.NonTerminalDecl;
import fr.umlv.tatoo.cc.parser.grammar.TerminalDecl;
import fr.umlv.tatoo.cc.parser.grammar.VersionDecl;
import fr.umlv.tatoo.gui.util.Mapper.Function;

public class NamedObjectFactory {
 
  private final HashMap<String,NamedObject> terminalRegistry = new HashMap<String,NamedObject>();
  private final HashMap<String,NamedObject> nonTerminalRegistry = new HashMap<String,NamedObject>();
  private final HashMap<String,NamedObject> versionRegistry = new HashMap<String,NamedObject>();
  private final TerminalDecl eofDecl,errorDecl;
  private final NamedObject eof,error;
  
  public NamedObjectFactory(TerminalDecl eofDecl,TerminalDecl errorDecl) {
    this.eofDecl = eofDecl;
    this.errorDecl = errorDecl;
    this.eof = new NamedObject("$");
    this.error = new NamedObject("€");
  }
  
    private NamedObject get(String name,HashMap<String,NamedObject> registry) {
      NamedObject value = registry.get(name);
      if (value == null) {
        value = new NamedObject(name);
        registry.put(name,value);
      }
      return value;
    }
  
  /*public TerminalDecl getEofDecl() {
    return eofDecl;
  }*/
  
  public NamedObject getEof() {
    return eof;
  }
  
  /*public TerminalDecl getErrorDecl() {
    return errorDecl;
  }*/
  
  public NamedObject getError() {
    return error;
  }
  
  public NamedObject get(TerminalDecl name) {
    if (name == eofDecl)
      return eof;
    if (name == errorDecl)
      return error;
    return get(name.getId(),terminalRegistry);
  }
  
  public NamedObject get(NonTerminalDecl name) {
    return get(name.getId(),nonTerminalRegistry);
  }
  
  public NamedObject get(VersionDecl name) {
    return get(name.getId(),versionRegistry);
  }
  
  private final Function<TerminalDecl,NamedObject> terminalFunction = 
    new Function<TerminalDecl,NamedObject>() {
    
    public NamedObject apply(TerminalDecl entry) {
      return get(entry);
    }
  
  };
  
  public Function<TerminalDecl,NamedObject> terminalFunction() {
    return terminalFunction;
  }
  
  private final Function<NonTerminalDecl,NamedObject> nonTerminalFunction =
    new Function<NonTerminalDecl,NamedObject>() {
    
    public NamedObject apply(NonTerminalDecl entry) {
      return get(entry);
    }
  
  };
  
  public Function<NonTerminalDecl,NamedObject> nonTerminalFunction() {
    return nonTerminalFunction;
  }

  private final Function<VersionDecl,NamedObject> versionFunction =
    new Function<VersionDecl,NamedObject>() {
    
      public NamedObject apply(VersionDecl entry) {
        return get(entry);
      }
  
    };
  
  public Function<VersionDecl,NamedObject> versionFunction() {
    return versionFunction;
  }
  
}

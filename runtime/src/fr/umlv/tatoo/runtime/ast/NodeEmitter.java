package fr.umlv.tatoo.runtime.ast;

import fr.umlv.tatoo.runtime.buffer.impl.CharSequenceWrapper;
import fr.umlv.tatoo.runtime.lexer.Lexer;
import fr.umlv.tatoo.runtime.parser.Parser;
import fr.umlv.tatoo.runtime.tools.SemanticStack;
import fr.umlv.tatoo.runtime.util.IntArrayList;

public class NodeEmitter<T,N,P,V> {
  final NonTerminalGotoStateTable<N> nonTerminalGotoStateTable;
  final RootNodeProvider rootNodeProvider;
  final SemanticStack semanticStack;
  final Lexer<CharSequenceWrapper> lexer;
  final Parser<T,N,P,V> parser;
  
  protected NodeEmitter(Lexer<CharSequenceWrapper> lexer, Parser<T,N,P,V> parser, NonTerminalGotoStateTable<N> nonTerminalGotoStateTable, RootNodeProvider rootNodeProvider, SemanticStack semanticStack) {
    this.lexer=lexer;
    this.parser=parser;
    this.nonTerminalGotoStateTable=nonTerminalGotoStateTable;
    this.rootNodeProvider=rootNodeProvider;
    this.semanticStack=semanticStack;
  }
  
  public static <T,N,P,V> NodeEmitter<T,N,P,V> create(Lexer<CharSequenceWrapper> lexer, Parser<T,N,P,V> parser, NonTerminalGotoStateTable<N> nonTerminalGotoStateTable, RootNodeProvider rootNodeProvider, SemanticStack semanticStack) {
    return new NodeEmitter<T,N,P,V>(lexer, parser, nonTerminalGotoStateTable, rootNodeProvider, semanticStack);
  }
  
  public <A> NodeBuilder<A> to(N startNonTerminal, Class<A> nonTerminalType) {
    parser.reset(startNonTerminal);
    StringBuilder builder=new StringBuilder();
    CharSequenceWrapper buffer=new CharSequenceWrapper(builder, null);
    lexer.reset(buffer);
    
    return new NodeBuilder<A>(buffer, builder, nonTerminalType);
  }
  
  public class NodeBuilder<A> {
    private final CharSequenceWrapper buffer;
    private final StringBuilder builder;
    private final Class<A> nonTerminalType;
    
    NodeBuilder(CharSequenceWrapper buffer, StringBuilder builder, Class<A> nonTerminalType) {
      this.buffer=buffer;
      this.builder=builder;
      this.nonTerminalType=nonTerminalType;
    }
    
    public NodeBuilder<A> parse(CharSequence data) {
      builder.append(data);
      //System.out.println("stateStack reset "+parser.getStateStack());
      
      while(buffer.hasRemaining())
        lexer.step();
      
      //System.out.println("stateStack parse "+parser.getStateStack());
      return this;
    }
    
    public NodeBuilder<A> nonTerminal(N nonTerminal, Node node) {
      semanticStack.push_Object(node);
      
      //FIXME Hack, get parser state stack write access
      IntArrayList stateStack=(IntArrayList)parser.getStateStack();
      
      int currentState=stateStack.last();
      int state=nonTerminalGotoStateTable.getGotoState(currentState, nonTerminal);
      stateStack.add(state);
      
      return this;
    }
    
    public A emit() {
      lexer.close();
      return rootNodeProvider.getRootNode(nonTerminalType);
    }
  }
}

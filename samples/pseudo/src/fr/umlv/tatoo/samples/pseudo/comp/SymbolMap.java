package fr.umlv.tatoo.samples.pseudo.comp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fr.umlv.tatoo.samples.pseudo.ast.Node;

public class SymbolMap {
  private final HashMap<Node,Symbol> nodeToSymbolMap=
    new HashMap<Node,Symbol>();
  private final HashMap<Symbol,Binding> symbolToBindingMap=
    new HashMap<Symbol,Binding>();
  
  public static class Binding {
    private final Symbol symbol;
    private final Node definition;
    private final ArrayList<Node> referenceList=
      new ArrayList<Node>();
    
    /**
     * @param symbol
     * @param definition may be null if symbol is not defined by a script node
     */
    public Binding(Symbol symbol,Node definition) {
      this.symbol=symbol;
      this.definition=definition;
    }
    
    public Symbol getSymbol() {
      return symbol;
    }
    
    /** @return may return null if symbol is not defined by a script node
     */
    public Node getDefinition() {
      return definition;
    }
    public List<Node> getReferenceList() {
      return referenceList;
    }
  }
  
  /** put a node with no binding
   */
  public void putSymbol(Node node, Symbol symbol) {
    if (nodeToSymbolMap.put(node,symbol)!=null)
      throw new IllegalStateException("node "+node+" has already a symbol defined");
  }
  
  /** put a definition node
   * @param node can be null if symbol as node script node
   */
  public void putDefSymbol(/*may be null*/Node node, Symbol symbol) {
    Binding binding=new Binding(symbol,node);
    if (symbolToBindingMap.put(symbol,binding)!=null)
      throw new IllegalStateException("node "+node+" has already a binding defined");
    if (node!=null)
      putSymbol(node,symbol);
  }
  
  /** put a reference node
   */
  public void putRefSymbol(Node node, Symbol symbol) {
    Binding binding=getBinding(symbol);
    binding.getReferenceList().add(node);
    putSymbol(node,symbol);
  }
  
  public Symbol getSymbol(Node node) {
    return getSymbol(node,false);
  }
  
  public Symbol getSymbol(Node node,boolean allowNoSymbol) {
    Symbol symbol=nodeToSymbolMap.get(node);
    if (symbol==null && !allowNoSymbol)
      throw new IllegalStateException("no symbol for node "+node);
    return symbol;
  }
  
  public Binding getBinding(Symbol symbol) {
    Binding binding=symbolToBindingMap.get(symbol);
    if (binding==null)
      throw new IllegalStateException("no binding for symbol "+symbol);
    return binding;
  }
}

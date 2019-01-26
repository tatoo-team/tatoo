/*
 * Created on May 30, 2003
 */
package fr.umlv.tatoo.cc.parser.lr;

import fr.umlv.tatoo.cc.parser.grammar.ProductionDecl;
import fr.umlv.tatoo.cc.parser.grammar.TerminalDecl;
import fr.umlv.tatoo.cc.parser.table.NodeItem;

/**
 * @author jcervell
 */
public class LR1Item extends NodeItem<LR1Item> {
  public LR1Item(ProductionDecl p,int dotPlace,TerminalDecl lookahead) {
    super(p,dotPlace);
    this.lookahead=lookahead;
  }
  public LR1Item(ProductionDecl p,TerminalDecl lookahead) {
    this(p,0,lookahead);
  }
  
  @Override
  public String toString() {
    return super.toString()+" / "+lookahead;
  }
  
  @Override
  public boolean equals(Object o) {
    if (!(o instanceof LR1Item))
      return false;
    LR1Item itm=(LR1Item)o;
    return super.equals(itm)&&itm.getLookahead().equals(lookahead);
  }
  @Override
  public int hashCode() {
    return super.hashCode()^lookahead.hashCode();
  }
  
  public TerminalDecl getLookahead() {
    return lookahead;
  }
  @Override
  public LR1Item advance() {
    return new LR1Item(getProduction(),getDotPlace()+1,getLookahead());
  }
  private final TerminalDecl lookahead;
}

package fr.umlv.tatoo.cc.parser.slr;

import fr.umlv.tatoo.cc.parser.grammar.ProductionDecl;
import fr.umlv.tatoo.cc.parser.table.NodeItem;

/** Represents a LR0 item
 */
public class LR0Item extends NodeItem<LR0Item>{

  

  public LR0Item(ProductionDecl production, int dotPlace) {
    super(production,dotPlace);
  }

  public LR0Item(ProductionDecl p) {
    this(p,0);
  }

  @Override
  public LR0Item advance() {
    return new LR0Item(getProduction(),getDotPlace()+1);
  }
}

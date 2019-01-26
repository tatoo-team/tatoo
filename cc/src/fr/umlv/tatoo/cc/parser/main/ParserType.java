package fr.umlv.tatoo.cc.parser.main;

import fr.umlv.tatoo.cc.parser.lalr.LALRTableFactoryMethod;
import fr.umlv.tatoo.cc.parser.lr.LRTableFactoryMethod;
import fr.umlv.tatoo.cc.parser.slr.SLRTableFactoryMethod;
import fr.umlv.tatoo.cc.parser.table.TableFactoryMethod;

public enum ParserType {
  lr(new LRTableFactoryMethod()),
  lalr(new LALRTableFactoryMethod()),
  slr(new SLRTableFactoryMethod());
  
  private ParserType(TableFactoryMethod<?> method) {
    this.method=method;
  }
  public TableFactoryMethod<?> getMethod() {
    return method; 
  }
  private final TableFactoryMethod<?> method;
}

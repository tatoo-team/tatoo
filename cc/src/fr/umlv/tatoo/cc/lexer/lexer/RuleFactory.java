/*
 * Created on 21 juil. 2005
 *
 */
package fr.umlv.tatoo.cc.lexer.lexer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.umlv.tatoo.cc.common.generator.IdMap;
import fr.umlv.tatoo.cc.common.log.Info;
import fr.umlv.tatoo.cc.lexer.regex.Regex;

/** 
 * 
 * @author Remi
 */
public class RuleFactory {
  public RuleFactory() {
    this.registry = new IdMap<RuleDecl>();
  }

  public IdMap<RuleDecl> getRuleMap() {
    return registry;
  }
  
  public RuleDecl createRule(String id, Regex main, Regex follow,
      int priority, boolean beginningOfLineRequired) {
    
    if (follow != null && follow.nullable()) {
      Info.warning("follow regex of rule "+id+" accepts empty word and is ignored").report();
      follow=null;
    }
    
    RuleDecl rule=new RuleDecl(id,main,follow,priority,beginningOfLineRequired);
    rules.add(rule);
    registry.put(rule);
    return rule;
  }
  
  public List<RuleDecl> getAllRules() {
    return unmodifiableRules;
  }
  
  private final IdMap<RuleDecl> registry;
  private final ArrayList<RuleDecl> rules=
    new ArrayList<RuleDecl>();
  private final List<RuleDecl> unmodifiableRules=
    Collections.unmodifiableList(rules);
}
package fr.umlv.tatoo.runtime.lexer.nano;

import java.util.Map;

import fr.umlv.tatoo.runtime.buffer.LexerBuffer;
import fr.umlv.tatoo.runtime.lexer.LexerListener;
import fr.umlv.tatoo.runtime.lexer.LexerTable;
import fr.umlv.tatoo.runtime.lexer.RuleActivator;
import fr.umlv.tatoo.runtime.lexer.rules.RuleData;

public class SimpleRuleFeeder<R, B extends LexerBuffer> extends RuleFeeder<B> {
  private final Map<R,RuleData> ruleDataMap;
  private final LexerListener<R,B> lexerListener;
  private final RuleActivator<R> activator;
  private final SimpleRuleProcessor<B> ruleProcessor;
  
  public SimpleRuleFeeder(LexerTable<R> lexerTable, LexerListener<R,B> lexerListener, RuleActivator<R> activator) {
    this.ruleDataMap = lexerTable.getRuleDataMap();
    this.lexerListener = lexerListener;
    this.activator = activator;
    this.ruleProcessor = new SimpleRuleProcessor<B>(lexerTable.getRuleDataMap().size());
  }
  
  @Override
  public RuleProcessor getRuleProcessor() {
    return ruleProcessor;
  }

  @Override
  public void feedWithActiveRuleDatas(boolean previousWasNewLine) {
    R[] rules = activator.activeRules();  
  
    Map<R,RuleData> ruleDataMap = this.ruleDataMap;
    int ruleSize=rules.length;
    for (int i=0;i<ruleSize;i++) {
      R r = rules[i];
      RuleData ruleData = ruleDataMap.get(r);
      if (!previousWasNewLine && ruleData.beginningOfLineRequired())
        continue;
      ruleProcessor.registerRuleData(ruleData, r);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public void ruleVerified(B buffer) {
    RegexRuleAction winningRuleAction = ruleProcessor.winningRuleAction();
    int lastTokenLength = winningRuleAction.lastMatch();
    buffer.unwind(lastTokenLength);
    
    // cast to R is safe here because the only way to set an attachment
    // is by calling #registerRuleData wich always send a R.
    // see #feedWithActiveRuleDatas()
    lexerListener.ruleVerified((R)winningRuleAction.getAttachment(), lastTokenLength, buffer);
  }
}

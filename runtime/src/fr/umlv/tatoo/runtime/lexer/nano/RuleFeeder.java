package fr.umlv.tatoo.runtime.lexer.nano;

import fr.umlv.tatoo.runtime.buffer.LexerBuffer;

public abstract class RuleFeeder<B extends LexerBuffer> {
  public abstract RuleProcessor getRuleProcessor();
  
  public abstract void feedWithActiveRuleDatas(boolean previousWasNewLine);
  
  public abstract void ruleVerified(B buffer);
}

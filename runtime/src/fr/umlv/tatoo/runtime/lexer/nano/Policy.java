package fr.umlv.tatoo.runtime.lexer.nano;

import java.util.List;

public interface Policy {
  public RuleMatcher findBestMatcher(List<? extends RuleMatcher> ruleMatcherList);
}

package fr.umlv.tatoo.runtime.tools.nano;

import fr.umlv.tatoo.runtime.buffer.LexerBuffer;

public interface GrammarRegistry<R,B extends LexerBuffer> {
  Branchable<R,B> associatedGrammar(Object terminal);
}

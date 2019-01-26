package fr.umlv.tatoo.runtime.lexer;

public enum ForwardReturn {
  /**
   * Ask lexer to discard one character of input.
   */
  DISCARD,
  /**
   * Ask lexer to retry with the same input; if not used carefully this could lead
   * to endless loop.
   */
  RETRY
}

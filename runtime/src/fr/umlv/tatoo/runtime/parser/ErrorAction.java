package fr.umlv.tatoo.runtime.parser;

public class ErrorAction<T, P, V> extends AbstractErrorAction<T, P, V> {
  public ErrorAction(String message) {
    super(message);
  }

  @Override
  public <N> ActionReturn doPerform(Parser<T, N, P, V> parser, T terminal) {
    return parser.performError(this, terminal);
  }

  @Override
  public boolean isError(V version) {
    return true;
  }
}

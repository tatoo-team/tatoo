package fr.umlv.tatoo.runtime.parser;


public class BranchAction<T,P,V> extends AbstractErrorAction<T,P,V> {
  public BranchAction(String message) {
    super(message);
  }

  @Override
  public <N> ActionReturn doPerform(Parser<T,N,P,V> parser, T terminal) {
    return parser.performBranching(this,terminal);
  }
  
  @Override
  public boolean isError(V version) {
    return true;
  }
}

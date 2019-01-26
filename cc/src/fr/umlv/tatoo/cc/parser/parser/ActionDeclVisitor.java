package fr.umlv.tatoo.cc.parser.parser;

public interface ActionDeclVisitor<R,P> {
  public R visit(AcceptActionDecl accept,P parameter);
  public R visit(BranchActionDecl accept,P parameter);
  public R visit(ShiftActionDecl shift,P parameter);
  public R visit(ReduceActionDecl reduce,P parameter);
  public R visit(ErrorActionDecl error,P parameter);
  public R visit(VersionedActionDecl versioned,P parameter);
  public R visit(EnterActionDecl enter,P parameter);
  public R visit(ExitActionDecl exit,P parameter);
}

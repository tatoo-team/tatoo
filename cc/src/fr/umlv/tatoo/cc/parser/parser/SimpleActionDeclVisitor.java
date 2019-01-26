package fr.umlv.tatoo.cc.parser.parser;

/** A simple action visitor with no parameter.
 * 
 * @author Remi
 *
 * @param <R> the return type
 * 
 * @see ActionDeclVisitor
 */
public abstract class SimpleActionDeclVisitor<R> implements ActionDeclVisitor<R,Void> {
  public abstract R visit(AcceptActionDecl accept);
  public abstract R visit(BranchActionDecl accept);
  public abstract R visit(ShiftActionDecl shift);
  public abstract R visit(ReduceActionDecl reduce);
  public abstract R visit(ErrorActionDecl error);
  public abstract R visit(VersionedActionDecl versioned);
  public abstract R visit(EnterActionDecl enter);
  public abstract R visit(ExitActionDecl exit);
  
  public final R visit(AcceptActionDecl accept,Void notUsed) {
    return visit(accept);
  }
  public final R visit(BranchActionDecl accept,Void notUsed) {
    return visit(accept);
  }
  public final R visit(ShiftActionDecl shift,Void notUsed) {
    return visit(shift);
  }
  public final R visit(ReduceActionDecl reduce,Void notUsed) {
    return visit(reduce);
  }
  public final R visit(ErrorActionDecl error,Void notUsed) {
    return visit(error);
  }
  public final R visit(VersionedActionDecl versioned,Void notUsed) {
    return visit(versioned);
  }
  public final R visit(EnterActionDecl enter,Void notUsed) {
    return visit(enter);
  }
  public final R visit(ExitActionDecl exit,Void notUsed) {
    return visit(exit);
  }
}

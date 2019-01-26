package fr.umlv.tatoo.runtime.tools.nano;


import fr.umlv.tatoo.runtime.buffer.LexerBuffer;
import fr.umlv.tatoo.runtime.lexer.nano.RuleMatcher;

public abstract class Branchable<R,B extends LexerBuffer> {
  public abstract class BranchableRun {
    
    /* current parser has depth 0, calling depth -1, and other depth > 0,
       depth can be ignored for non-branching parser
       for depth 0 and -1, attachement is null
       for depth 1, attachement is branch non-terminal
       for depth >1, attachement is linked list of branch 
    */
    
    /* fill matcher with current state rules (remove eof token (if sensible) if isBranched is true) */
    public abstract void fill(boolean isBranched, boolean previousWasNewLine, Object attachment, int depth);
    
    /* true if analysis can exit this parser */
    public abstract boolean exits();
    
    /* fill matcher with corresponding rule to continue parsing after shifting branch */
    public abstract void fillUnbranch(Object branch, boolean previousWasNewLine, Object attachment, int depth);
    
    /* list of branching non-terminal available for shifting */ 
    public abstract Object[] branches();
    
    /* true if at initial state */
    public abstract boolean initial();
    
    public Branchable<R,B> branchable() {
      return Branchable.this;
    }
  }
  
  /* fill matcher with initial rules (+ special epsilon rule - eof token (if sensible) if isBranched is true and this branchable accepts epsilon) */
  public abstract void initialRules(boolean isBranched, boolean previousWasNewLine, Object attachment, int depth);
  
  /* list of branching non-terminal initially available for shifting */ 
  public abstract Object[] initialBranches();
  
  public abstract BranchableRun newRun();
  
  /* clean rule of matcher */
  public abstract void reset();
  
  /* null if non rule to apply */
  public abstract RuleMatcher matcher();
  
  /* unique small identifier of this branchable */
  public abstract int identifier();

}

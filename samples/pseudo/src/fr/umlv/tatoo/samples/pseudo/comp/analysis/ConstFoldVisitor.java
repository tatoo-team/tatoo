package fr.umlv.tatoo.samples.pseudo.comp.analysis;

import static fr.umlv.tatoo.samples.pseudo.comp.analysis.ConstantMap.NO_CONST;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import fr.umlv.tatoo.samples.pseudo.ast.DeclVarLet;
import fr.umlv.tatoo.samples.pseudo.ast.Expr;
import fr.umlv.tatoo.samples.pseudo.ast.ExprArrayIndex;
import fr.umlv.tatoo.samples.pseudo.ast.ExprBooleanConst;
import fr.umlv.tatoo.samples.pseudo.ast.ExprFieldAccess;
import fr.umlv.tatoo.samples.pseudo.ast.ExprNull;
import fr.umlv.tatoo.samples.pseudo.ast.ExprString;
import fr.umlv.tatoo.samples.pseudo.ast.ExprValue;
import fr.umlv.tatoo.samples.pseudo.ast.ExprVar;
import fr.umlv.tatoo.samples.pseudo.ast.IdToken;
import fr.umlv.tatoo.samples.pseudo.ast.Node;
import fr.umlv.tatoo.samples.pseudo.ast.Visitor;
import fr.umlv.tatoo.samples.pseudo.comp.BuiltInFunction;
import fr.umlv.tatoo.samples.pseudo.comp.LocalVar;
import fr.umlv.tatoo.samples.pseudo.comp.analysis.ConstantMap.Constant;

public class ConstFoldVisitor extends Visitor<Constant,ConstFoldEnv,RuntimeException> {
  private final ScriptEnv scriptEnv;
  
  public ConstFoldVisitor(ScriptEnv scriptEnv) {
    this.scriptEnv=scriptEnv;
  }
  
  private final Constant NO_RESULT=new Constant(null) {
    @Override
    public Object getConstant() {
      throw new IllegalStateException("NO_RESULT has no value");
    }
    @Override
    public String toString() {
      return "NO_RESULT";
    }
  };
  
  private Constant markIfConstant(Node node, ConstFoldEnv constFoldEnv, Constant value) {
    if (value!=NO_CONST) {
      constFoldEnv.getConstFoldMap().putConstant(node,value);
    }
    return value;
  }
  
  private Constant constFoldNode(Node node,ConstFoldEnv constFoldEnv) {
    Constant result=NO_RESULT;
    for(Node n:node.nodeList()) {
      if (n.isToken())
        continue;
      // must visit all nodes, even if intermediary result is NO_CONST
      Constant constValue=n.accept(this,constFoldEnv);
      if (result==NO_CONST)
        continue;
      if (constValue==NO_CONST) {
        result=NO_CONST;
        continue;
      }
      result=(result==NO_RESULT)?constValue:NO_CONST;
    }
    if (result==NO_CONST || result==NO_RESULT)
      return NO_CONST;
    return markIfConstant(node,constFoldEnv,result);
  }
  
  private Constant constFoldFun(Node node,BuiltInFunction builtInFunction,ConstFoldEnv constFoldEnv) {
    ArrayList<Object> args=new ArrayList<Object>();
    Constant result=NO_RESULT;
    for(Node n:node.nodeList()) {
      if (n.isToken())
        continue;
      // must visit all nodes, even if intermediary result is NO_CONST
      Constant constValue=n.accept(this,constFoldEnv);
      if (result==NO_CONST)
        continue;
      if (constValue==NO_CONST) {
        result=NO_CONST;
        continue;
      }
      args.add(constValue.getConstant());
    }
    
    if (result==NO_CONST) {
      return NO_CONST;
    }

    try {
      result=new Constant(builtInFunction.getReflectMethod().invoke(null,args.toArray()));
    } catch(IllegalAccessException e) {
      throw new AssertionError(e);
    } catch(InvocationTargetException e) {
      throw new AssertionError(e);
    }
    return markIfConstant(node,constFoldEnv,result);
  }
  
  // generic constant folding algorithm
  private Constant consFold(Node node,ConstFoldEnv constFoldEnv) {
    BuiltInFunction builtInFunction=(BuiltInFunction)scriptEnv.getSymbolMap().getSymbol(node,true);
    if (builtInFunction==null) {
      return constFoldNode(node,constFoldEnv);
    } 
    if (builtInFunction.hasSideEffect()) {
      return NO_CONST;
    }
    return constFoldFun(node,builtInFunction,constFoldEnv);
  }
  
  @Override
  protected Constant visit(Node node,ConstFoldEnv constFoldEnv) {
    for(Node n:node.nodeList()) {
      n.accept(this,constFoldEnv);
    }
    return NO_CONST;
  }
  
  @Override
  public Constant visit(DeclVarLet decl_var_let,ConstFoldEnv constFoldEnv) {
    Constant value=decl_var_let.getExpr().accept(this,constFoldEnv);
    if (value==NO_CONST) {
      return NO_CONST;
    }
    
    LocalVar localVar=(LocalVar)scriptEnv.getSymbolMap().getSymbol(decl_var_let);
    constFoldEnv.getConstFoldVarMap().putConstant(localVar,value);
    return NO_CONST;
  }
  
  @Override
  protected Constant visit(Expr expr,ConstFoldEnv constFoldEnv) {
    return consFold(expr,constFoldEnv);
  }
  
  @Override
  public Constant visit(ExprBooleanConst expr_boolean_const,ConstFoldEnv constFoldEnv) {
    return markIfConstant(expr_boolean_const,constFoldEnv,
      new Constant(expr_boolean_const.getBooleanConst().getValue()));
  }
  
  @Override
  public Constant visit(ExprVar expr_var,ConstFoldEnv constFoldEnv) {
    IdToken idToken=expr_var.getId();
    LocalVar localVar=(LocalVar)scriptEnv.getSymbolMap().getSymbol(idToken);
    
    return markIfConstant(idToken,constFoldEnv,
      constFoldEnv.getConstFoldVarMap().getConstant(localVar));
  }
  
  @Override
  public Constant visit(ExprFieldAccess expr_field_access,ConstFoldEnv constFoldEnv) {
    return visit((Node)expr_field_access,constFoldEnv);
  }
  
  @Override
  public Constant visit(ExprArrayIndex expr_array_index,ConstFoldEnv constFoldEnv) {
    return visit((Node)expr_array_index,constFoldEnv);
  }
  
  @Override
  public Constant visit(ExprNull expr_null,ConstFoldEnv constFoldEnv) {
    return markIfConstant(expr_null,constFoldEnv,
      new Constant(null));
  }
  
  @Override
  public Constant visit(ExprString expr_string,ConstFoldEnv constFoldEnv) {
    return markIfConstant(expr_string,constFoldEnv,
      new Constant(expr_string.getStringConst().getValue()));
  }
  
  @Override
  public Constant visit(ExprValue expr_value,ConstFoldEnv constFoldEnv) {
    return markIfConstant(expr_value,constFoldEnv,
      new Constant(expr_value.getValue().getValue()));
  }
}

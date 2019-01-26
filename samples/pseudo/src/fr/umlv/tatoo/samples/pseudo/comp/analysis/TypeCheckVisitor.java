package fr.umlv.tatoo.samples.pseudo.comp.analysis;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import fr.umlv.tatoo.samples.pseudo.ast.AllocationArray;
import fr.umlv.tatoo.samples.pseudo.ast.AllocationObject;
import fr.umlv.tatoo.samples.pseudo.ast.ArrayIndex;
import fr.umlv.tatoo.samples.pseudo.ast.Assignation;
import fr.umlv.tatoo.samples.pseudo.ast.Block;
import fr.umlv.tatoo.samples.pseudo.ast.Builtin;
import fr.umlv.tatoo.samples.pseudo.ast.ConditionalIf;
import fr.umlv.tatoo.samples.pseudo.ast.ConditionalIfElse;
import fr.umlv.tatoo.samples.pseudo.ast.DeclVarLet;
import fr.umlv.tatoo.samples.pseudo.ast.DeclVarType;
import fr.umlv.tatoo.samples.pseudo.ast.Expr;
import fr.umlv.tatoo.samples.pseudo.ast.ExprAllocation;
import fr.umlv.tatoo.samples.pseudo.ast.ExprArrayIndex;
import fr.umlv.tatoo.samples.pseudo.ast.ExprAs;
import fr.umlv.tatoo.samples.pseudo.ast.ExprBooleanConst;
import fr.umlv.tatoo.samples.pseudo.ast.ExprError;
import fr.umlv.tatoo.samples.pseudo.ast.ExprFieldAccess;
import fr.umlv.tatoo.samples.pseudo.ast.ExprFuncall;
import fr.umlv.tatoo.samples.pseudo.ast.ExprInstanceof;
import fr.umlv.tatoo.samples.pseudo.ast.ExprNull;
import fr.umlv.tatoo.samples.pseudo.ast.ExprParens;
import fr.umlv.tatoo.samples.pseudo.ast.ExprString;
import fr.umlv.tatoo.samples.pseudo.ast.ExprValue;
import fr.umlv.tatoo.samples.pseudo.ast.ExprVar;
import fr.umlv.tatoo.samples.pseudo.ast.ForLoopIncr;
import fr.umlv.tatoo.samples.pseudo.ast.ForLoopInit;
import fr.umlv.tatoo.samples.pseudo.ast.Funcall;
import fr.umlv.tatoo.samples.pseudo.ast.FunctionDecl;
import fr.umlv.tatoo.samples.pseudo.ast.IdToken;
import fr.umlv.tatoo.samples.pseudo.ast.Instr;
import fr.umlv.tatoo.samples.pseudo.ast.InstrBlock;
import fr.umlv.tatoo.samples.pseudo.ast.InstrBreak;
import fr.umlv.tatoo.samples.pseudo.ast.InstrContinue;
import fr.umlv.tatoo.samples.pseudo.ast.InstrError;
import fr.umlv.tatoo.samples.pseudo.ast.InstrLoop;
import fr.umlv.tatoo.samples.pseudo.ast.InstrReturn;
import fr.umlv.tatoo.samples.pseudo.ast.LoopFor;
import fr.umlv.tatoo.samples.pseudo.ast.LoopLabel;
import fr.umlv.tatoo.samples.pseudo.ast.LoopWhile;
import fr.umlv.tatoo.samples.pseudo.ast.LvalueArrayIndex;
import fr.umlv.tatoo.samples.pseudo.ast.LvalueField;
import fr.umlv.tatoo.samples.pseudo.ast.LvalueId;
import fr.umlv.tatoo.samples.pseudo.ast.Member;
import fr.umlv.tatoo.samples.pseudo.ast.Node;
import fr.umlv.tatoo.samples.pseudo.ast.Script;
import fr.umlv.tatoo.samples.pseudo.ast.TypeName;
import fr.umlv.tatoo.samples.pseudo.ast.VarInit;
import fr.umlv.tatoo.samples.pseudo.ast.Visitor;
import fr.umlv.tatoo.samples.pseudo.comp.BlockLocalVar;
import fr.umlv.tatoo.samples.pseudo.comp.Field;
import fr.umlv.tatoo.samples.pseudo.comp.Function;
import fr.umlv.tatoo.samples.pseudo.comp.LocalVar;
import fr.umlv.tatoo.samples.pseudo.comp.Scope;
import fr.umlv.tatoo.samples.pseudo.comp.Struct;
import fr.umlv.tatoo.samples.pseudo.comp.SymbolMap;
import fr.umlv.tatoo.samples.pseudo.comp.Type;
import fr.umlv.tatoo.samples.pseudo.comp.TypeScope;
import fr.umlv.tatoo.samples.pseudo.comp.Types;
import fr.umlv.tatoo.samples.pseudo.comp.UserFunction;
import fr.umlv.tatoo.samples.pseudo.comp.BuiltInFunction.OperatorKind;
import fr.umlv.tatoo.samples.pseudo.comp.Types.ArrayType;
import fr.umlv.tatoo.samples.pseudo.comp.Types.IntegerType;
import fr.umlv.tatoo.samples.pseudo.comp.Types.StructType;
import fr.umlv.tatoo.samples.pseudo.comp.UserFunction.Parameter;
import fr.umlv.tatoo.samples.pseudo.parser.ProductionEnum;

public class TypeCheckVisitor extends Visitor<Type,BlockEnv,RuntimeException> {
  final ScriptEnv scriptEnv;
  
  public TypeCheckVisitor(ScriptEnv scriptEnv) {
    this.scriptEnv=scriptEnv;
  }
  
  private boolean isAssignable(Type type1, Type type2) {
    return Types.isAssignable(scriptEnv.getTypeScope(),type1,type2);
  }
  
  private void checkIncompatible(Type type1, Type type2,Node node) {
    TypeScope typeScope=scriptEnv.getTypeScope();
    if (!Types.isAssignable(typeScope,type1,type2) &&
        !Types.isAssignable(typeScope,type2,type1)) {
      scriptEnv.error(node,type1+" and "+type2+" are incompatible");
    }
  }
  
  // should never returns type 'void'
  private Type errorRecoveryType(boolean leftHandSide) {
    TypeScope typeScope=scriptEnv.getTypeScope();
    return (leftHandSide)?typeScope.anyType():typeScope.nullType();
  }
  
  public Type typeCheck(Node node, BlockEnv blockEnv) {
    Type type=node.accept(this,blockEnv);
    if (type!=null) {
      scriptEnv.getExprTypeMap().put(node,type);
    }
    return type;
  }
  
  @Override
  protected Type visit(Node node, BlockEnv blockEnv) {
    for(Node n:node.nodeList()) {
      typeCheck(n,blockEnv);
    }
    return null;
  }
  
  @Override
  public Type visit(FunctionDecl functionDecl,BlockEnv unused) {
    UserFunction function=(UserFunction)scriptEnv.getSymbolMap().getSymbol(functionDecl);
    BlockEnv blockEnv=new BlockEnv(scriptEnv,function.getReturnType());
    
    // fill with parameters
    Scope<LocalVar> localVarScope=blockEnv.getLocalVarScope();
    for(Parameter parameter:function.getParameterList()) {
      localVarScope.add(parameter);
    }
    
    Block block=functionDecl.getBlock();
    scriptEnv.getSymbolMap().putSymbol(block,blockEnv);
    typeCheck(block,blockEnv);
    return null;
  }
  
  @Override
  public Type visit(Script script,BlockEnv unused) {
    for(Member member:script.getMemberStar())
      typeCheck(member,null);
    
    BlockEnv blockEnv=new BlockEnv(scriptEnv,scriptEnv.getTypeScope().voidType());
    Block block=script.getBlock();
    
    // insert a special parameter containing the command line arguments
    Parameter argsParameter=new Parameter("ARGS",scriptEnv.getTypeScope().stringType().getTypeArray());
    blockEnv.getLocalVarScope().add(argsParameter);
    //XXX: parameter def is null but defined
    scriptEnv.getSymbolMap().putDefSymbol(null,argsParameter);
    
    scriptEnv.getSymbolMap().putSymbol(block,blockEnv);
    typeCheck(block,blockEnv);
    return null;
  }
  
  @Override
  public Type visit(InstrBlock instr_block,BlockEnv parentBlockEnv) {
    BlockEnv blockEnv=new BlockEnv(scriptEnv,parentBlockEnv);
    Block block=instr_block.getBlock();
    scriptEnv.getSymbolMap().putSymbol(block,blockEnv);
    typeCheck(block,blockEnv);
    return null;
  }
  
  @Override
  public Type visit(InstrError instr_error,BlockEnv blockEnv) {
    scriptEnv.error(instr_error,"syntax error: invalid statement");
    return null;
  }
  
  @Override
  public Type visit(ConditionalIf conditional,BlockEnv blockEnv) {
    Type condType=typeCheck(conditional.getExpr(),blockEnv);
    if (scriptEnv.getTypeScope().booleanType()!=condType) {
      scriptEnv.error(conditional.getExpr(),"conditional type must be a boolean");
      // error recovery
    }
    typeCheck(conditional.getInstr(),blockEnv);
    return null;
  }
  
  @Override
  public Type visit(ConditionalIfElse conditional,BlockEnv blockEnv) {
    Type testType=typeCheck(conditional.getExpr(),blockEnv);
    if (scriptEnv.getTypeScope().booleanType()!=testType) {
      scriptEnv.error(conditional.getExpr(),"test type must be a boolean");
      // error recovery
    }
    typeCheck(conditional.getInstr(),blockEnv);
    typeCheck(conditional.getInstr2(),blockEnv);
    return null;
  }
  
  private void checkLoop(Expr bexpr,InstrLoop parent,Instr instr,BlockEnv blockEnv) {
    if (bexpr!=null) {
      Type conditionType=typeCheck(bexpr,blockEnv);
      if (scriptEnv.getTypeScope().booleanType()!=conditionType) {
        scriptEnv.error(bexpr,"condition type must be a boolean");
        // error recovery
      }
    }
    
    LoopLabel loopLabel=parent.getLoopLabelOptional();
    String label=(loopLabel==null)?null:loopLabel.getId().getValue();
    
    LoopStack<Boolean> loopStack=blockEnv.getLoopStack();
    loopStack.push(label,true);
    typeCheck(instr,blockEnv);
    loopStack.pop();
  }
  
  @Override
  public Type visit(LoopWhile loop_while,BlockEnv blockEnv) {
    checkLoop(loop_while.getExpr(),loop_while.getParent(),loop_while.getInstr(),blockEnv);
    return null;
  }
  
  @Override
  public Type visit(LoopFor loop_for, BlockEnv parentBlockEnv) {
    BlockEnv blockEnv=new BlockEnv(scriptEnv,parentBlockEnv);
    scriptEnv.getSymbolMap().putSymbol(loop_for,blockEnv);
    
    ForLoopInit forLoopInitOpt=loop_for.getForLoopInitOptional();
    if (forLoopInitOpt!=null)
      typeCheck(forLoopInitOpt,blockEnv);
    
    checkLoop(loop_for.getExprOptional(),loop_for.getParent(),loop_for.getInstr(),blockEnv);
    
    ForLoopIncr incrOpt=loop_for.getForLoopIncrOptional();
    if (incrOpt!=null)
      typeCheck(incrOpt,blockEnv);
    
    return null;
  }
  
  private void checkLoopContext(IdToken label,BlockEnv blockEnv, String kind, Node node) {
    LoopStack<Boolean> loopStack=blockEnv.getLoopStack();
    if (label==null) {
      // no label
      Boolean exist=loopStack.getCurrentLoopContext();
      if (exist==null) {
        scriptEnv.error(node,kind+" is called in a non loop context");
        // error recovery
      }
    } else {
      Boolean exist=loopStack.getLoopContext(label.getValue());
      if (exist==null) {
        scriptEnv.error(node,"there is no loop labeled "+label.getValue()+" in the current context");
        // error recovery
      }
    }
  }
  
  @Override
  public Type visit(InstrBreak loop_break,BlockEnv blockEnv) {
    checkLoopContext(loop_break.getIdOptional(),blockEnv,"break",loop_break);
    return null;
  }
  
  @Override
  public Type visit(InstrContinue loop_continue,BlockEnv blockEnv) {
    checkLoopContext(loop_continue.getIdOptional(),blockEnv,"continue",loop_continue);
    return null;
  }
  
  @Override
  public Type visit(InstrReturn instr_return,BlockEnv blockEnv) {
    TypeScope typeScope=scriptEnv.getTypeScope();
    
    Type returnType;
    Expr expr=instr_return.getExprOptional();
    if (expr==null) {
      returnType=typeScope.voidType();
    } else {
      returnType=typeCheck(expr,blockEnv);
    }
     
    if (!Types.isAssignable(typeScope,blockEnv.getExpectedReturnType(),returnType)) {
      scriptEnv.error(instr_return,"incompatible with return type: found "+returnType+
        " expect "+blockEnv.getExpectedReturnType()+" or a subtype");
      // error recovery
      return null;
    }
    
    // needed in genVisitor to get the expected return type 
    scriptEnv.getSymbolMap().putSymbol(instr_return,blockEnv);
    return null;
  }
  
  @Override
  public Type visit(AllocationObject allocation_object,BlockEnv blockEnv) {
    IdToken idToken=allocation_object.getId();
    Type type=TypeResolver.resolveType(scriptEnv,idToken);
    if (!(type instanceof Types.StructType)) {
      scriptEnv.error(idToken,"new can only be used on struct and array, not on "+idToken.getValue());
      // error recovery
    }
    return type;
  }
  
  @Override
  public Type visit(ArrayIndex array_index,BlockEnv blockEnv) {
    IntegerType integerType=scriptEnv.getTypeScope().integerType();
    Type exprType=typeCheck(array_index.getExpr(),blockEnv);
    if (exprType!=integerType) {
      scriptEnv.error(array_index.getExpr(),"array index must be an integer, found "+exprType);
      // error recovery
    }
    return integerType;
  }
  
  @Override
  public Type visit(AllocationArray allocation_array,BlockEnv blockEnv) {
    IdToken idToken=allocation_array.getId();
    Type type=TypeResolver.resolveType(scriptEnv,idToken);
    
    List<ArrayIndex> arrayIndexList=allocation_array.getArrayIndexPlus();
    for(ArrayIndex arrayIndex:arrayIndexList) {
      typeCheck(arrayIndex,blockEnv);
    }
    
    int dimension=allocation_array.getAngleBracketsStar().size()+
      arrayIndexList.size();
    return Types.arrayType(type,dimension);
  }
  
  @Override
  public Type visit(ExprAllocation expr_allocation,BlockEnv blockEnv) {
    return typeCheck(expr_allocation.getAllocation(),blockEnv);
  }
  
  private Type checkArrayType(Type varType,int dimension,Node node) {
    if (dimension==0)
      return varType;
    
    Type type=varType;
    for(int i=0;i<dimension;i++) {
      if (!(type instanceof ArrayType)) {
        scriptEnv.error(node,"try to use angle brackets ([]) on a non array type "+type);
        // error recovery
        return type;
      }
      type=((ArrayType)type).getComponentType();
    }
    return type;
  }
  
  @Override
  public Type visit(Assignation assignation,BlockEnv blockEnv) {
    Type type=typeCheck(assignation.getExpr(),blockEnv);
    Type lvalueType=typeCheck(assignation.getLvalue(),blockEnv);
    
    if (!isAssignable(lvalueType,type)) {
      scriptEnv.error(assignation,"Incompatible type: cannot assign "+type+" to "+lvalueType);
      // error recovery
    }
    return null;
  }
  
  @Override
  public Type visit(LvalueId lvalue_id,BlockEnv blockEnv) {
    IdToken idToken=lvalue_id.getId();
    String varName=idToken.getValue();
    LocalVar localVar=blockEnv.getLocalVarScope().lookupItem(varName);
    if (localVar==null) {
      scriptEnv.error(idToken,"no local variable "+varName+" declared");
      // error recovery
      return errorRecoveryType(true);
    }
    scriptEnv.getSymbolMap().putRefSymbol(idToken,localVar);
    return localVar.getType();
  }
  
  @Override
  public Type visit(LvalueField lvalue_field,BlockEnv blockEnv) {
    Type exprType=typeCheck(lvalue_field.getExpr(),blockEnv);  
    if (!(exprType instanceof StructType)) {
      scriptEnv.error(lvalue_field.getExpr(),"type "+exprType+" is not a struct hence it can not be dereferenced");
      // error recovery
      return errorRecoveryType(true);
    }
    
    Scope<Struct> structScope=scriptEnv.getStructScope();
    Struct struct=structScope.lookupItem(exprType.getName());
    IdToken idToken=lvalue_field.getId();
    String id=idToken.getValue();
    Field field=struct.getFieldDefScope().lookupItem(id);
    if (field==null) {
      scriptEnv.error(idToken,"struct "+struct+" doesn't declare a field "+id);
        // error recovery
        return errorRecoveryType(true);
    }
    scriptEnv.getSymbolMap().putRefSymbol(idToken,field);
    return field.getType();
  }
  
  @Override
  public Type visit(LvalueArrayIndex lvalue_array_index,BlockEnv blockEnv) {
    typeCheck(lvalue_array_index.getArrayIndex(),blockEnv);
    
    Type exprType=typeCheck(lvalue_array_index.getExpr(),blockEnv);
    return checkArrayType(exprType,1,lvalue_array_index.getExpr());
  }
  
  @Override
  public Type visit(DeclVarType decl_var_type,BlockEnv blockEnv) {
    String varName=decl_var_type.getId().getValue();
    Scope<LocalVar> localVarScope=blockEnv.getLocalVarScope();
    if (localVarScope.localExists(varName)) {
      scriptEnv.error(decl_var_type.getId(),"variable "+varName+" already declared");
      // error recovery
      return null;
    }
    
    TypeName typeName=decl_var_type.getTypeName();
    Type type=TypeResolver.resolveType(scriptEnv,typeName);
    
    VarInit init=decl_var_type.getVarInitOptional();
    if (init!=null) {
      Type initType=typeCheck(init,blockEnv);
      if (!isAssignable(type,initType)) {
        scriptEnv.error(typeName,"Incompatible type: try to assign "+initType+" to "+type);
        // error recovery
      }
    }
    
    BlockLocalVar localVar=new BlockLocalVar(varName,type,false,localVarScope);
    localVarScope.add(localVar);
    scriptEnv.getSymbolMap().putDefSymbol(decl_var_type,localVar);
    
    return type;
  }
  
  @Override
  public Type visit(VarInit init,BlockEnv blockEnv) {
    return typeCheck(init.getExpr(),blockEnv);
  }
  
  
  @Override
  public Type visit(DeclVarLet decl_var_let,BlockEnv blockEnv)  {
    String varName=decl_var_let.getId().getValue();
    Scope<LocalVar> localVarScope=blockEnv.getLocalVarScope();
    if (localVarScope.localExists(varName)) {
      scriptEnv.error(decl_var_let.getId(),"variable "+varName+" already declared");
      // error recovery
      return null;
    }
    
    Type type=typeCheck(decl_var_let.getExpr(),blockEnv);
    BlockLocalVar localVar=new BlockLocalVar(varName,type,true,localVarScope);
    localVarScope.add(localVar);
    scriptEnv.getSymbolMap().putDefSymbol(decl_var_let,localVar);
    
    return type;
  }
  
  private static final EnumMap<ProductionEnum,OperatorKind> productionToOperatorMap=
    new EnumMap<ProductionEnum,OperatorKind>(ProductionEnum.class);
  static {
    // boolean tests
    productionToOperatorMap.put(ProductionEnum.expr_eq,OperatorKind.eq);
    productionToOperatorMap.put(ProductionEnum.expr_neq,OperatorKind.neq);
    productionToOperatorMap.put(ProductionEnum.expr_lt,OperatorKind.lt);
    productionToOperatorMap.put(ProductionEnum.expr_le,OperatorKind.le);
    productionToOperatorMap.put(ProductionEnum.expr_gt,OperatorKind.gt);
    productionToOperatorMap.put(ProductionEnum.expr_ge,OperatorKind.ge);
    productionToOperatorMap.put(ProductionEnum.expr_neg,OperatorKind.neg);
    productionToOperatorMap.put(ProductionEnum.expr_bor,OperatorKind.bor);
    productionToOperatorMap.put(ProductionEnum.expr_band,OperatorKind.band);
    
    // numeric operators
    productionToOperatorMap.put(ProductionEnum.expr_plus,OperatorKind.plus);
    productionToOperatorMap.put(ProductionEnum.expr_minus,OperatorKind.minus);
    productionToOperatorMap.put(ProductionEnum.expr_star,OperatorKind.star);
    productionToOperatorMap.put(ProductionEnum.expr_slash,OperatorKind.slash);
    productionToOperatorMap.put(ProductionEnum.expr_mod,OperatorKind.mod);
    
    // top-level builtin
    productionToOperatorMap.put(ProductionEnum.builtin_print,OperatorKind.print);
  }
  
  // operatorNode is an expr or a boolean expr
  private Type builtinType(Node operatorNode,BlockEnv blockEnv) {
    ProductionEnum productionEnum=(ProductionEnum)operatorNode.getKind();
    OperatorKind operatorKind=productionToOperatorMap.get(productionEnum);
    if (operatorKind==null) {
      throw new AssertionError(operatorNode+" is not registered as a builtin function");
    }
    
    ArrayList<Type> parameterTypeList=new ArrayList<Type>();
    for(Node n:operatorNode.nodeList()) {
      if (n.isToken())
        continue;
      Type type=typeCheck(n,blockEnv);
      parameterTypeList.add(type);
    }
    
    Function function=FunctionResolver.resolveFunction(scriptEnv,
        operatorNode,operatorKind.getOperator(),parameterTypeList);
    
    if (function==null) {
      // error recovery
      return errorRecoveryType(false);
    }
    
    SymbolMap symbolMap=scriptEnv.getSymbolMap();
    if (function.isBuiltin()) {
      symbolMap.putSymbol(operatorNode,function);
    } else { 
      symbolMap.putRefSymbol(operatorNode,function);
    }
    return function.getReturnType();
  }
  
  @Override
  protected Type visit(Expr expr,BlockEnv blockEnv) {
    return builtinType(expr,blockEnv);
  }
  
  @Override
  protected Type visit(Builtin builtin,BlockEnv blockEnv) throws RuntimeException {
    return builtinType(builtin,blockEnv);
  }
  
  @Override
  public Type visit(Funcall funcall,BlockEnv blockEnv) {
    String name=funcall.getId().getValue();
    
    ArrayList<Type> parameterTypeList=new ArrayList<Type>();
    for(Expr expr:funcall.getExprStar()) {
      parameterTypeList.add(typeCheck(expr,blockEnv));
    }
    
    Function function=FunctionResolver.resolveFunction(scriptEnv,funcall,name,parameterTypeList);
    if (function==null) {
      // error recovery
      return errorRecoveryType(false);
    }
    scriptEnv.getSymbolMap().putRefSymbol(funcall,function);
    return function.getReturnType();
  }
  
  @Override
  public Type visit(ExprBooleanConst expr_const,BlockEnv unused) {
    return scriptEnv.getTypeScope().booleanType();
  }
  
  @Override
  public Type visit(ExprInstanceof expr_instanceof,BlockEnv blockEnv) {
    Type type=TypeResolver.resolveType(scriptEnv,expr_instanceof.getTypeName());
    Type exprType=typeCheck(expr_instanceof.getExpr(),blockEnv);
    checkIncompatible(type,exprType,expr_instanceof);
    return scriptEnv.getTypeScope().booleanType();
  }
  
  @Override
  public Type visit(ExprParens expr_parens,BlockEnv blockEnv)  {
    return typeCheck(expr_parens.getExpr(),blockEnv);
  }
  
  @Override
  public Type visit(ExprFuncall expr_funcall,BlockEnv blockEnv) {
    Funcall funcall=expr_funcall.getFuncall();
    Type type=typeCheck(funcall,blockEnv);
    if (type==scriptEnv.getTypeScope().voidType()) {
      Function function=(Function)scriptEnv.getSymbolMap().getSymbol(funcall);
      
      scriptEnv.error(expr_funcall,"function "+function+
        " can't be used as an expression because it returns void");
      // error recovery
      return errorRecoveryType(false);
    }
    return type;
  }
  
  @Override
  public Type visit(ExprVar expr_var,BlockEnv blockEnv) {
    IdToken idToken=expr_var.getId();
    String varName=idToken.getValue();
    LocalVar localVar=blockEnv.getLocalVarScope().lookupItem(varName);
    if (localVar==null) {
      scriptEnv.error(idToken,"no local variable "+varName+" declared");
      // error recovery
      return errorRecoveryType(false);
    }
    scriptEnv.getSymbolMap().putRefSymbol(idToken,localVar);
    return localVar.getType();
  }
  
  @Override
  public Type visit(ExprFieldAccess expr_field_access,BlockEnv blockEnv) {
    Type exprType=typeCheck(expr_field_access.getExpr(),blockEnv);
    if (!(exprType instanceof StructType)) {
      scriptEnv.error(expr_field_access.getExpr(),"type "+exprType+" is not a struct hence it can not be dereferenced");
      // error recovery
      return errorRecoveryType(false);
    }

    Struct struct=scriptEnv.getStructScope().lookupItem(exprType.getName());

    IdToken idToken=expr_field_access.getId();
    Field field=struct.getFieldDefScope().lookupItem(idToken.getValue());
    if (field==null) {
      scriptEnv.error(idToken,"struct "+exprType+" doesn't declare a field "+idToken.getValue());
      // error recovery
      return errorRecoveryType(false);
    }
    scriptEnv.getSymbolMap().putRefSymbol(idToken,field);
    return field.getType();
  }
  
  @Override
  public Type visit(ExprArrayIndex expr_array_index,BlockEnv blockEnv) {
    Type type=typeCheck(expr_array_index.getExpr(),blockEnv);
    typeCheck(expr_array_index.getArrayIndex(),blockEnv);
    return checkArrayType(type,1,expr_array_index);
  }
  
  @Override
  public Type visit(ExprAs expr_as,BlockEnv blockEnv) {
    Type type=typeCheck(expr_as.getExpr(),blockEnv);
    Type asType=TypeResolver.resolveType(scriptEnv,expr_as.getTypeName());
    checkIncompatible(type,asType,expr_as);
    // possible error recovery
    return asType;
  }
  
  @Override
  public Type visit(ExprError expr_error,BlockEnv unused) {
    // error recovery
    return errorRecoveryType(false);
  }
  
  @Override
  public Type visit(ExprValue expr_value,BlockEnv unused) {
    return scriptEnv.getTypeScope().integerType();
  }
  
  @Override
  public Type visit(ExprString expr_string,BlockEnv unused) {
    return scriptEnv.getTypeScope().stringType();
  }
  
  @Override
  public Type visit(ExprNull expr_null,BlockEnv unused) {
    return scriptEnv.getTypeScope().nullType();
  }
}

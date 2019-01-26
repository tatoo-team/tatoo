package fr.umlv.tatoo.samples.pseudo.comp.analysis;

import java.util.ArrayList;

import fr.umlv.tatoo.samples.pseudo.ast.FieldDeclError;
import fr.umlv.tatoo.samples.pseudo.ast.FieldDeclId;
import fr.umlv.tatoo.samples.pseudo.ast.FunctionDecl;
import fr.umlv.tatoo.samples.pseudo.ast.Param;
import fr.umlv.tatoo.samples.pseudo.ast.RtypeTypeName;
import fr.umlv.tatoo.samples.pseudo.ast.RtypeVoid;
import fr.umlv.tatoo.samples.pseudo.ast.StructDecl;
import fr.umlv.tatoo.samples.pseudo.ast.Visitor;
import fr.umlv.tatoo.samples.pseudo.comp.Field;
import fr.umlv.tatoo.samples.pseudo.comp.FunctionScope;
import fr.umlv.tatoo.samples.pseudo.comp.Scope;
import fr.umlv.tatoo.samples.pseudo.comp.Struct;
import fr.umlv.tatoo.samples.pseudo.comp.SymbolMap;
import fr.umlv.tatoo.samples.pseudo.comp.Type;
import fr.umlv.tatoo.samples.pseudo.comp.UserFunction;
import fr.umlv.tatoo.samples.pseudo.comp.UserFunction.Parameter;

public class FunctionAndFieldEnterVisitor extends TraversalVisitor<ScriptEnv> {
  @Override
  public Void visit(StructDecl struct_decl,ScriptEnv scriptEnv) {
    Struct struct=(Struct)scriptEnv.getSymbolMap().getSymbol(struct_decl,true);
    if (struct==null) {
      // error recovery
      return null;
    }
    
    // gather fields
    StructEnv structEnv=new StructEnv(scriptEnv,struct);
    struct_decl.accept(fieldVisitor, structEnv);
    
    return null;
  } // where
  private final TraversalVisitor<StructEnv> fieldVisitor=
    new TraversalVisitor<StructEnv>() {
    @Override
    public Void visit(FieldDeclId fieldDeclId,StructEnv structEnv) {
      String name=fieldDeclId.getId().getValue();
      
      ScriptEnv scriptEnv=structEnv.getScriptEnv();
      Struct struct=structEnv.getStruct();
      Scope<Field> fieldScope=struct.getFieldDefScope();
      if (fieldScope.localExists(name)) {
        scriptEnv.error(fieldDeclId,"duplicate field with same name "+name);
        return null;
      }
      
      Type type=TypeResolver.resolveType(scriptEnv,fieldDeclId.getTypeName());
      Field field=new Field(struct,name,type);
      fieldScope.add(field);
      
      scriptEnv.getSymbolMap().putDefSymbol(fieldDeclId,field);
      return null;
    }
    
    @Override
    public Void visit(FieldDeclError field_decl_error, StructEnv structEnv) {
      structEnv.getScriptEnv().error(field_decl_error,"syntax error: invalid field");
      return null;
    }
  };
  
  @Override
  public Void visit(FunctionDecl functionDecl,ScriptEnv scriptEnv) {
    String name=functionDecl.getId().getValue();
    SymbolMap symbolMap=scriptEnv.getSymbolMap();
    
    ArrayList<Parameter> parameterList=
      new ArrayList<Parameter>();
    for(Param param:functionDecl.getParamStar()) {
      String parameterName=param.getId().getValue();
      Type type=TypeResolver.resolveType(scriptEnv,param.getTypeName());
      Parameter parameter=new Parameter(parameterName,type);
      parameterList.add(parameter);
      symbolMap.putDefSymbol(param,parameter);
    }
    
    Type returnType=functionDecl.getRtype().accept(rTypeVisitor,scriptEnv);
    UserFunction function=new UserFunction(name,returnType,parameterList);
    
    FunctionScope functionScope=scriptEnv.getFunctionScope();
    if (functionScope.lookupFunction(name,function.getParameterTypeList())!=null) {
      scriptEnv.error(functionDecl,
        "duplicate method with same name and parameter types "+name+" "+function.getParameterTypeList());
      
      // error recovery
    } else {
      functionScope.add(function);
    }
    
    symbolMap.putDefSymbol(functionDecl,function);
    return null;
  } //where
  private static final Visitor<Type,ScriptEnv,RuntimeException> rTypeVisitor=
    new Visitor<Type,ScriptEnv,RuntimeException>() {
      @Override
      public Type visit(RtypeTypeName rtype_type_name,ScriptEnv scriptEnv) {
        return TypeResolver.resolveType(scriptEnv,rtype_type_name.getTypeName());
      }
      @Override
      public Type visit(RtypeVoid rtype_void, ScriptEnv scriptEnv) {
        return scriptEnv.getTypeScope().voidType();
      }
  };
}

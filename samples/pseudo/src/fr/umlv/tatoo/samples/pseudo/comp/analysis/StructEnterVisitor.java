package fr.umlv.tatoo.samples.pseudo.comp.analysis;

import fr.umlv.tatoo.samples.pseudo.ast.StructDecl;
import fr.umlv.tatoo.samples.pseudo.comp.Scope;
import fr.umlv.tatoo.samples.pseudo.comp.Struct;
import fr.umlv.tatoo.samples.pseudo.comp.TypeScope;
import fr.umlv.tatoo.samples.pseudo.comp.Types.StructType;

public class StructEnterVisitor extends TraversalVisitor<ScriptEnv> {
  @Override
  public Void visit(StructDecl struct_decl,ScriptEnv scriptEnv) {
    String name=struct_decl.getId().getValue();
    
    Scope<Struct> structScope=scriptEnv.getStructScope();
    if (structScope.localExists(name)) {
      scriptEnv.error(struct_decl,"duplicate struct with same name "+name);
      // error recovery
      return null;
    }
    
    TypeScope typeScope=scriptEnv.getTypeScope();
    if (typeScope.localExists(name)) {
      scriptEnv.error(struct_decl,"duplicate type with same name "+name);
      // error recovery
      return null;
    }
    
    StructType type=new StructType(name);
    typeScope.add(type);
    
    Struct struct=new Struct(name, type);
    structScope.add(struct);
    scriptEnv.getSymbolMap().putDefSymbol(struct_decl,struct);
    
    return null;
  }
}

package fr.umlv.tatoo.samples.pseudo.comp.analysis;

import fr.umlv.tatoo.samples.pseudo.ast.IdToken;
import fr.umlv.tatoo.samples.pseudo.ast.TypeName;
import fr.umlv.tatoo.samples.pseudo.comp.Type;
import fr.umlv.tatoo.samples.pseudo.comp.TypeScope;

public class TypeResolver {
  public static Type resolveType(ScriptEnv scriptEnv,IdToken idToken) {
    String typename=idToken.getValue();
    TypeScope typeScope=scriptEnv.getTypeScope();
    Type type=typeScope.lookupItem(typename);
    if (type==null) {
      scriptEnv.error(idToken,"Unknown type "+typename);
      // recover on error
      type=typeScope.anyType();
    }
    return type;
  }
  
  public static Type resolveType(ScriptEnv scriptEnv,TypeName typeName) {
    Type type=resolveType(scriptEnv,typeName.getId());
    for(int i=0;i<typeName.getAngleBracketsStar().size();i++)
      type=type.getTypeArray();
    return type;
  }
  
  public static String toString(TypeName typeName) {
    String typename=typeName.getId().getValue();
    
    StringBuilder builder=new StringBuilder();
    builder.append(typename);
    for(int i=0;i<typeName.getAngleBracketsStar().size();i++)
      builder.append("[]");
    
    return builder.toString();
  }
}

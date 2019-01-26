package fr.umlv.tatoo.cc.ebnf.ast.analysis;

import java.util.Map;

import fr.umlv.tatoo.cc.common.generator.Type;
import fr.umlv.tatoo.cc.ebnf.ast.BindingMap;
import fr.umlv.tatoo.cc.ebnf.ast.PriorityVarAST;
import fr.umlv.tatoo.cc.ebnf.ast.TypeVarAST;
import fr.umlv.tatoo.cc.ebnf.ast.VersionVarAST;
import fr.umlv.tatoo.cc.ebnf.ast.Bindings.PriorityBinding;
import fr.umlv.tatoo.cc.ebnf.ast.Bindings.TypeBinding;
import fr.umlv.tatoo.cc.ebnf.ast.Bindings.VersionBinding;
import fr.umlv.tatoo.cc.ebnf.ast.analysis.ASTDiagnosisReporter.ErrorKey;
import fr.umlv.tatoo.cc.ebnf.ast.analysis.ASTDiagnosisReporter.WarningKey;
import fr.umlv.tatoo.cc.parser.grammar.GrammarFactory;
import fr.umlv.tatoo.cc.parser.grammar.Priority;
import fr.umlv.tatoo.cc.parser.grammar.VersionDecl;

public class Commons {
  public static Object visit(PriorityVarAST node,GrammarFactory grammarFactory,BindingMap bindingMap,ASTDiagnosisReporter diagnostic) {
    String name=node.getName();
    Priority priority=grammarFactory.getPriority(name);
    
    // check unknown priority
    if (priority==null) {
      diagnostic.signal(ErrorKey.priority_var_unknown,node,name);
      return null;
    }
    
    node.setBinding(bindingMap.getBinding(priority,PriorityBinding.class,false));
    return priority;
  }
  
  public static Object visit(VersionVarAST node,GrammarFactory grammarFactory,BindingMap bindingMap,ASTDiagnosisReporter diagnostic) {
    String name=node.getName();
    VersionDecl version=grammarFactory.getVersion(name);
    
    // check if version exist
    if (version==null) {
      diagnostic.signal(ErrorKey.version_var_unknown,node,name);
      return null;
    }
    
    VersionBinding binding=bindingMap.getBinding(version,VersionBinding.class,false);
    node.setBinding(binding);
    
    return version;
  }
  
  public static Object visit(TypeVarAST node,Map<String,Type> importMap,TypeVerifier typeVerifier,BindingMap bindingMap,ASTDiagnosisReporter diagnostic) {
    String qualifiedId=node.getQualifiedId();
    Type type=Type.createType(qualifiedId,importMap);
    
    if (!typeVerifier.typeExist(type)) {
      diagnostic.signal(WarningKey.type_not_exist,node);
      return null;
    }
    
    // check is binding not already exist in case
    // of non imported type (primitive or class from java.lang.*)
    TypeBinding binding=bindingMap.getBinding(type,TypeBinding.class,true);
    if (binding==null) {
      binding=new TypeBinding(node,type);
      bindingMap.registerBinding(type,binding);
      node.setBinding(binding,false);
    } else {
      node.setBinding(binding,true);
    }
    
    return type;
  }
}

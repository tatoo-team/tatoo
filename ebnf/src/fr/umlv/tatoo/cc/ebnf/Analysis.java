package fr.umlv.tatoo.cc.ebnf;

import java.util.HashMap;
import java.util.Map;

import fr.umlv.tatoo.cc.common.generator.Type;
import fr.umlv.tatoo.cc.ebnf.ast.BindingMap;
import fr.umlv.tatoo.cc.ebnf.ast.Bindings.NonTerminalBinding;
import fr.umlv.tatoo.cc.ebnf.ast.Bindings.TerminalBinding;
import fr.umlv.tatoo.cc.ebnf.ast.Bindings.VariableBinding;
import fr.umlv.tatoo.cc.ebnf.ast.analysis.EnterPassOne;
import fr.umlv.tatoo.cc.ebnf.ast.analysis.EnterPassTwo;
import fr.umlv.tatoo.cc.ebnf.ast.analysis.TypeVerifier;
import fr.umlv.tatoo.cc.lexer.charset.encoding.Encoding;
import fr.umlv.tatoo.cc.lexer.lexer.RuleFactory;
import fr.umlv.tatoo.cc.parser.grammar.EBNFSupport;
import fr.umlv.tatoo.cc.parser.grammar.GrammarFactory;
import fr.umlv.tatoo.cc.parser.grammar.NonTerminalDecl;
import fr.umlv.tatoo.cc.parser.grammar.TerminalDecl;
import fr.umlv.tatoo.cc.parser.grammar.VariableDecl;
import fr.umlv.tatoo.cc.tools.tools.ToolsFactory;
import fr.umlv.tatoo.runtime.node.Binding;

public class Analysis {
  private final HashMap<String,TerminalDecl> aliases =
    new HashMap<String, TerminalDecl>();
  private final BindingMap bindingMap = new BindingMap();
  
  public boolean analyse(EBNFASTImpl ast,
      RuleFactory ruleFactory,Encoding encoding,GrammarFactory grammarFactory,
      EBNFSupport ebnfSupport,ToolsFactory toolsFactory,Map<String,Type> attributeMap) {
    
    // fake type verifier
    TypeVerifier typeVerifier=new TypeVerifier() {
      @Override
      public boolean typeExist(Type type) {
        return true;
      }
    };
    HashMap<String,Type> importMap=new HashMap<String,Type>();
    LogInfoASTDiagnosisReporter diagnosticReporter=new LogInfoASTDiagnosisReporter(null);
    
    updateBindingMap(grammarFactory, bindingMap);
    
    EnterPassOne passOne=new EnterPassOne(bindingMap,importMap,encoding,ruleFactory,aliases,grammarFactory,toolsFactory,attributeMap,typeVerifier,diagnosticReporter);
    ast.getRoot().accept(passOne,null);
    EnterPassTwo passTwo=passOne.createEnterPassTwo(ebnfSupport);
    ast.getRoot().accept(passTwo,null);
    
    return diagnosticReporter.isOnError();
  }
  
  
  private void updateBindingMap(GrammarFactory grammarFactory, BindingMap bindingMap) {
    for(VariableDecl variable:grammarFactory.getVariableMap().getAllValues()) {
      Binding binding = bindingMap.getBinding(variable, Binding.class, true);
      if (binding != null) {
        continue;   // binding already exists
      }
      
      VariableBinding<?> varBinding;
      if (variable.isTerminal()) {
        varBinding=new TerminalBinding(null,(TerminalDecl)variable);
      } else {
        varBinding=new NonTerminalBinding(null,(NonTerminalDecl)variable);
      }
      
      bindingMap.registerBinding(variable,varBinding);
    }
  }
}

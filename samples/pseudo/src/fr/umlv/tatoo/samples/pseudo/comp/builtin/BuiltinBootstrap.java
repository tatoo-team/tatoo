package fr.umlv.tatoo.samples.pseudo.comp.builtin;

import java.util.ArrayList;

import fr.umlv.tatoo.samples.pseudo.comp.BuiltInFunction;
import fr.umlv.tatoo.samples.pseudo.comp.FunctionScope;
import fr.umlv.tatoo.samples.pseudo.comp.SymbolMap;
import fr.umlv.tatoo.samples.pseudo.comp.TypeScope;
import fr.umlv.tatoo.samples.pseudo.comp.analysis.ScriptEnv;
import fr.umlv.tatoo.samples.pseudo.runtime.BuiltinIORuntime;
import fr.umlv.tatoo.samples.pseudo.runtime.BuiltinLangRuntime;
import fr.umlv.tatoo.samples.pseudo.runtime.BuiltinRuntime;

public class BuiltinBootstrap {
  public static void initScriptEnv(ScriptEnv scriptEnv) {
    TypeScope typeScope=scriptEnv.getTypeScope();
    ArrayList<BuiltInFunction> builtinFunctionList=
      new ArrayList<BuiltInFunction>();
    Builtins.appendBuiltinFunctions(builtinFunctionList,typeScope,BuiltinRuntime.class);
    Builtins.appendBuiltinFunctions(builtinFunctionList,typeScope,BuiltinLangRuntime.class);
    Builtins.appendBuiltinFunctions(builtinFunctionList,typeScope,BuiltinIORuntime.class);
    FunctionScope functionScope=scriptEnv.getFunctionScope();
    SymbolMap symbolMap=scriptEnv.getSymbolMap();
    for(BuiltInFunction function:builtinFunctionList) {
      functionScope.add(function);
      symbolMap.putDefSymbol(null,function);
    }
  }
}

package fr.umlv.tatoo.samples.pseudo.comp.main;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import fr.umlv.tatoo.runtime.buffer.impl.LocationTracker;
import fr.umlv.tatoo.runtime.buffer.impl.ReaderWrapper;
import fr.umlv.tatoo.samples.pseudo.ast.Script;
import fr.umlv.tatoo.samples.pseudo.comp.analysis.ConstFoldEnv;
import fr.umlv.tatoo.samples.pseudo.comp.analysis.ConstFoldVisitor;
import fr.umlv.tatoo.samples.pseudo.comp.analysis.CoverageVisitor;
import fr.umlv.tatoo.samples.pseudo.comp.analysis.FlowVisitor;
import fr.umlv.tatoo.samples.pseudo.comp.analysis.FunctionAndFieldEnterVisitor;
import fr.umlv.tatoo.samples.pseudo.comp.analysis.GenVisitor;
import fr.umlv.tatoo.samples.pseudo.comp.analysis.LivenessMap;
import fr.umlv.tatoo.samples.pseudo.comp.analysis.ScriptEnv;
import fr.umlv.tatoo.samples.pseudo.comp.analysis.StructEnterVisitor;
import fr.umlv.tatoo.samples.pseudo.comp.analysis.TypeCheckVisitor;
import fr.umlv.tatoo.samples.pseudo.comp.builtin.BuiltinBootstrap;
import fr.umlv.tatoo.samples.pseudo.parser.NonTerminalEnum;
import fr.umlv.tatoo.samples.pseudo.parser.VersionEnum;
import fr.umlv.tatoo.samples.pseudo.tools.Analyzers;

public class Main {
  public static void main(String[] args) throws IOException {
    File input=new File(args[0]);
    FileReader reader=new FileReader(input);
    
    LocationTracker locationTracker=new LocationTracker();
    ReaderWrapper readerWrapper=new ReaderWrapper(reader,locationTracker);
    
    LocationMap locationMap=new LocationMap();
    ASTTerminalEvaluator terminalEvaluator=new ASTTerminalEvaluator(locationTracker,locationMap);
    ASTGrammarEvaluator grammarEvaluator=new ASTGrammarEvaluator(locationTracker,locationMap);
    
    Analyzers.run(readerWrapper,terminalEvaluator,grammarEvaluator,NonTerminalEnum.script,VersionEnum.DEFAULT);
    
    Script script=grammarEvaluator.getScript();
    
    LocationErrorReporter locationErrorReporter=new LocationErrorReporter(locationMap);
    ScriptEnv scriptEnv=new ScriptEnv(locationErrorReporter);
    
    // insert builtins in function scope
    BuiltinBootstrap.initScriptEnv(scriptEnv);
    
    //pass 1: enter struct types
    StructEnterVisitor structEnterVisitor=new StructEnterVisitor();
    script.accept(structEnterVisitor,scriptEnv);
    
    //pass 2: enter functions and struct fields
    FunctionAndFieldEnterVisitor functionAndFieldEnterVisitor=new FunctionAndFieldEnterVisitor();
    script.accept(functionAndFieldEnterVisitor,scriptEnv);
    
    //pass 3: type checking
    TypeCheckVisitor typeCheckVisitor=new TypeCheckVisitor(scriptEnv);
    script.accept(typeCheckVisitor,null);
    
    if (locationErrorReporter.isOnError())
      return;
    
    //pass 4: constant folding
    ConstFoldEnv constFoldEnv=new ConstFoldEnv();
    ConstFoldVisitor constFoldVisitor=new ConstFoldVisitor(scriptEnv);
    script.accept(constFoldVisitor,constFoldEnv);
    
    //pass 5: variable definite assignment, dead code detection, unused variables
    LivenessMap livenessMap=new LivenessMap();
    FlowVisitor flowVisitor=new FlowVisitor(scriptEnv,constFoldEnv,livenessMap);
    flowVisitor.flow(script);
    
    //pass 6: test coverage, is script block tests all declared functions
    CoverageVisitor coverageVisitor=new CoverageVisitor(scriptEnv);
    script.accept(coverageVisitor,null);
    
    if (locationErrorReporter.isOnError())
      return;
    
    //pass 7: generation
    GenVisitor generation=new GenVisitor(input,scriptEnv,locationMap,constFoldEnv,livenessMap);
    generation.generate(script);
  }
}

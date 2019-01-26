package fr.umlv.tatoo.cc.tools.ast.generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.umlv.tatoo.cc.common.generator.Type;
import fr.umlv.tatoo.cc.parser.grammar.NonTerminalDecl;
import fr.umlv.tatoo.cc.parser.grammar.ProductionDecl;
import fr.umlv.tatoo.cc.parser.grammar.VariableDecl;

public class ASTGeneratorUtils {
  private ASTGeneratorUtils() {
    // no instanciation
  }
  
  public static List<NonTerminalDecl> filterOutTerminals(ProductionDecl production) {
    ArrayList<NonTerminalDecl> nonTerminalList=new ArrayList<NonTerminalDecl>();
    for(VariableDecl variable:production.getRight()) {
      if (!variable.isTerminal())
        nonTerminalList.add((NonTerminalDecl)variable);
    }
    return nonTerminalList;
  }
  
  public static Type computeVariableOrProductionType(String name, String astPackagePrefix, String suffix) {
    StringBuilder builder=new StringBuilder();
    builder.append(astPackagePrefix).append('.');
    for(String part:name.split("_")) {
      builder.append(toClassName(part));
    }
    builder.append(suffix);
    return Type.createType(builder.toString(),Collections.<String,Type>emptyMap());
  }
  
  private static String toClassName(String name) {
    if (name.length()==0)
      return "";
    return Character.toUpperCase(name.charAt(0))+
      name.substring(1);
  }  
  
  /*
  public long processSerialUID() {
    ByteArrayOutputStream arrayOut = new ByteArrayOutputStream();
    DataOutputStream out = new DataOutputStream(arrayOut);
    
    try {
      serialUIDAsStream(out);
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    
    MessageDigest md;
    try {
      md = MessageDigest.getInstance("SHA");
    } catch (NoSuchAlgorithmException e) {
      throw new AssertionError(e);
    }
    
    long hash=0;
    byte[] bytes = md.digest(arrayOut.toByteArray());
    for (byte b:bytes)
      hash = (hash << 8) | (b & 0xFF);
    
    return hash;
  }*/
}

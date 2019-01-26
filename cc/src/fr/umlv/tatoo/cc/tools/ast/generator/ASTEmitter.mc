<%@page import="fr.umlv.tatoo.cc.common.generator.Type"%>
<%@page import="fr.umlv.tatoo.cc.tools.generator.ToolsGeneratorUtils"%>
<%@page import="java.util.Collection"%>
<%@page import="java.util.Map"%>
<%@page import="fr.umlv.tatoo.cc.parser.grammar.EBNFSupport"%>
<%@page import="fr.umlv.tatoo.cc.parser.grammar.NonTerminalDecl"%>
<%@page import="fr.umlv.tatoo.cc.parser.grammar.VariableDecl"%>
<%!/*PARAM*/
Type astEmitter;
Type terminalEvaluator;
Type astGrammarEvaluator;
Type analyzerProcessor;
Type analyzer;

Type nonTerminalGotoStateDataTable;

Type T;
Type N;
Type P;
Type V;

EBNFSupport ebnfSupport;
Collection<? extends NonTerminalDecl> starts;
Collection<Type> variableImports;
Map<VariableDecl, Type> variableTypeMap;
%>
<%!/*COMMENT*/
java.io.PrintWriter out;%>
<%!
  private static String capitalize(String name) {
    String[] subNames=name.split("_");
    StringBuilder builder=new StringBuilder();
    for(String subName:subNames) {
      switch(subName.length()) {
        case 0:
          break;
        case 1:
          builder.append(Character.toUpperCase(subName.charAt(0)));
          break;
        default:
          builder.append(Character.toUpperCase(subName.charAt(0))).append(subName.substring(1));    
      }
    }
    return builder.toString();
  }
%>
package <%=astGrammarEvaluator.getPackageName()%>;

import fr.umlv.tatoo.runtime.ast.NodeEmitter;
import fr.umlv.tatoo.runtime.buffer.impl.CharSequenceWrapper;
import fr.umlv.tatoo.runtime.tools.DataViewer;
import fr.umlv.tatoo.runtime.tools.SemanticStack;
import fr.umlv.tatoo.runtime.tools.builder.LexerAndParser;

import <%=T.getRawName()%>;
import <%=N.getRawName()%>;
import <%=P.getRawName()%>;
import <%=V.getRawName()%>;
import <%=terminalEvaluator.getRawName()%>;
import <%=astGrammarEvaluator.getRawName()%>;
import <%=analyzerProcessor.getRawName()%>;
import <%=analyzer.getRawName()%>;

/** 
 *  This class is generated - please do not edit it 
 */
public class <%=astEmitter.getSimpleName()%> {
private final NodeEmitter<<%=T.getSimpleName()%>, <%=N.getSimpleName()%>, <%=P.getSimpleName()%>, <%=V.getSimpleName()%>> nodeEmitter;
  
  protected ASTEmitter(NodeEmitter<<%=T.getSimpleName()%>, <%=N.getSimpleName()%>, <%=P.getSimpleName()%>, <%=V.getSimpleName()%>> nodeEmitter) {
    this.nodeEmitter=nodeEmitter;
  }
  
  public static ASTEmitter create(<%=terminalEvaluator.getSimpleName()%><CharSequence> terminalEvaluator, <%=astGrammarEvaluator.getSimpleName()%> grammarEvaluator) {
    SemanticStack semanticStack=new SemanticStack();
    <%=analyzerProcessor.getSimpleName()%><CharSequenceWrapper, CharSequence> processor=<%=analyzerProcessor.getSimpleName()%>.create<%=analyzerProcessor.getSimpleName()%>(
        terminalEvaluator,
        grammarEvaluator,
        DataViewer.<CharSequence>getTokenBufferViewer(),
        semanticStack);

    LexerAndParser<CharSequenceWrapper, <%=T.getSimpleName()%>, <%=N.getSimpleName()%>, <%=P.getSimpleName()%>, <%=V.getSimpleName()%>> lexerAndParser =
      <%=analyzer.getSimpleName()%>.analyzerBuilder().buffer((CharSequenceWrapper)null).listener(processor).createAnalyzer();

    return new ASTEmitter(NodeEmitter.create(lexerAndParser.getLexer(),
        lexerAndParser.getParser(),
        <%=nonTerminalGotoStateDataTable.getSimpleName()%>.create(),
        grammarEvaluator,
        semanticStack));
  }
  
<%
  for(NonTerminalDecl nonTerminal:starts) { 
     Type type = variableTypeMap.get(nonTerminal);
     String upperName=ToolsGeneratorUtils.toUpperCase(nonTerminal);
%>  
  public ASTBuilder<<%=type.getSimpleName()%>> to<%=upperName%>() {
    return new ASTBuilder<<%=type.getSimpleName()%>>(nodeEmitter.to(<%=N.getSimpleName()%>.<%=nonTerminal.getId()%>, <%=type.getSimpleName()%>.class));
  }
<%
  }
%> 
  
  public static class ASTBuilder<A> {
    private final NodeEmitter<<%=T.getSimpleName()%>, <%=N.getSimpleName()%>, <%=P.getSimpleName()%>, <%=V.getSimpleName()%>>.NodeBuilder<A> nodeBuilder;
    
    protected ASTBuilder(NodeEmitter<<%=T.getSimpleName()%>, <%=N.getSimpleName()%>, <%=P.getSimpleName()%>, <%=V.getSimpleName()%>>.NodeBuilder<A> nodeBuilder) {
      this.nodeBuilder=nodeBuilder;
    }
    
    public ASTBuilder<A> parse(CharSequence data) {
      nodeBuilder.parse(data);
      return this;
    }

<% for(Map.Entry<VariableDecl, Type> entry:variableTypeMap.entrySet()) {
     VariableDecl variable=entry.getKey();
     Type type=entry.getValue();
     if (!variable.isTerminal() &&
         !ebnfSupport.getStarNonTerminals().contains(variable) &&
         !ebnfSupport.getOptionalNonTerminals().contains(variable)) {
%>    
    public ASTBuilder<A> <%=capitalize(variable.getId())%>(<%=type.getSimpleName()%> <%=variable.getId()%>) {
      nodeBuilder.nonTerminal(<%=N.getSimpleName()%>.<%=variable.getId()%>, <%=variable.getId()%>);
      return this;
    }
<%   }
   }  
%>
    
    public A emit() {
      return nodeBuilder.emit();
    }
  }
}

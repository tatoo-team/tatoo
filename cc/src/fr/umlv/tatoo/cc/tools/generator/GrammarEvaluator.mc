<%@page import="fr.umlv.tatoo.cc.common.generator.Type"%>
<%@page import="fr.umlv.tatoo.cc.tools.generator.ToolsGeneratorUtils"%>
<%@page import="fr.umlv.tatoo.cc.parser.grammar.NonTerminalDecl"%>
<%@page import="fr.umlv.tatoo.cc.tools.generator.Naming"%>
<%@page import="java.util.Map"%>
<%@page import="fr.umlv.tatoo.cc.parser.grammar.EBNFSupport"%>
<%@page import="fr.umlv.tatoo.cc.parser.grammar.VariableDecl"%>
<%@page import="fr.umlv.tatoo.cc.parser.grammar.ProductionDecl"%>
<%@page import="java.util.Collection"%>
<%@page import="java.util.Iterator"%>
<%!/*PARAM*/
Type grammarEvaluator;
Collection<Type> variableImports;
Collection<? extends ProductionDecl> productions;
EBNFSupport ebnfSupport;
Map<VariableDecl, Type> variableTypeMap;
Collection<? extends NonTerminalDecl> starts;%>
<%!/*COMMENT*/
java.io.PrintWriter out;%>
<%!
%>
package <%=grammarEvaluator.getPackageName()%>;

<%
  for(Type iMport : variableImports) { %>
import <%=iMport.getRawName()%>;
<%
  }
%>

/** 
 *  This class is generated - please do not edit it 
 */
public interface <%=grammarEvaluator.getSimpleName()%> {
<%
  for(ProductionDecl production:productions) { 
     Type returnType = variableTypeMap.get(production.getLeft());
     if (!ebnfSupport.getEBNFTypeMap().containsKey(production) 
     && (returnType!=null
     || ToolsGeneratorUtils.notAllNull(production.getRight(),variableTypeMap))) {
%>
  /** This methods is called after the reduction of the non terminal <%=production.getLeft()%>
   *  by the grammar production <%=production.getId()%>.
   *  <%="<"%>code><%=production.getLeft().getId()%> ::=<%
  for(VariableDecl right : production.getRight())
   out.append(' ').append(right.getId());
%><%="<"%>/code>
   */
  public <%=returnType==null?"void":returnType.getSimpleName()%> <%=production.getId()%>(<%
      ToolsGeneratorUtils.foreachNonNull(production.getRight(),variableTypeMap, new ToolsGeneratorUtils.VarParamClosure<Boolean>(ebnfSupport) {
        public Boolean apply(Type type,String name,Boolean initial) {
          %><%=(initial)?"":","%><%=type.getSimpleName()%> <%=name%><%     
          return false;
        }
      },true);
 %>);
<%
   }
   }
 %>

<%
  for(NonTerminalDecl nonTerminal:starts) { 
     Type type = variableTypeMap.get(nonTerminal);
     String acceptName=ToolsGeneratorUtils.toUpperCase(nonTerminal);
     String param= type==null||type.isVoid()?"":type.getSimpleName()+" "+nonTerminal.getId();
%>
  public void accept<%=acceptName%>(<%=param%>);
<% } %>
}

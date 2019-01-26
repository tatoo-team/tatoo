<%@page import="java.util.Iterator"%>
<%@page import="java.util.Collection"%>
<%@page import="fr.umlv.tatoo.cc.parser.grammar.NonTerminalDecl"%>
<%@page import="fr.umlv.tatoo.cc.common.generator.Type"%>
<%!/*PARAM*/
Type nonTerminalEnum;
Collection<? extends NonTerminalDecl> nonTerminals;
%>

package <%=nonTerminalEnum.getPackageName()%>;

/** 
 *  This class is generated - please do not edit it 
 */
public enum <%=nonTerminalEnum.getSimpleName() %> {
  <%
Iterator<? extends NonTerminalDecl> iterator = nonTerminals.iterator();
out.print(iterator.next().getId());
while(iterator.hasNext()) {
  out.println(',');
  out.print(iterator.next().getId());
}
out.println();
%>;
}
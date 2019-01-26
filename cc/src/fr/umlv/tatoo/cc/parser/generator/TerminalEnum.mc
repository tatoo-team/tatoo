<%@page import="java.util.Iterator"%>
<%@page import="fr.umlv.tatoo.cc.parser.grammar.TerminalDecl"%>
<%@page import="java.util.Collection"%>
<%@page import="fr.umlv.tatoo.cc.common.generator.Type"%>
<%!/*PARAM*/
Type terminalEnum;
Collection<? extends TerminalDecl> terminals;
%>
package <%=terminalEnum.getPackageName()%>;

/** 
 *  This class is generated - please do not edit it 
 */
public enum <%=terminalEnum.getSimpleName()%> {
  <%
  Iterator<? extends TerminalDecl> iterator = terminals.iterator();
  out.print(iterator.next().getId());
  while(iterator.hasNext()) {
    out.println(',');
    out.print(iterator.next().getId());
  }
  out.println();
%>;
}
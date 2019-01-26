<%@page import="java.util.Iterator"%>
<%@page import="fr.umlv.tatoo.cc.parser.grammar.ProductionDecl"%>
<%@page import="java.util.Collection"%>
<%@page import="fr.umlv.tatoo.cc.common.generator.Type"%>
<%!/*PARAM*/
Type productionEnum;
Collection<? extends ProductionDecl> productions;
%>
package <%=productionEnum.getPackageName()%>;

/** 
 *  This class is generated - please do not edit it 
 */
public enum <%=productionEnum.getSimpleName()%> {
  <%
  Iterator<? extends ProductionDecl> iterator = productions.iterator();
  out.print(iterator.next().getId());
  while(iterator.hasNext()) {
    out.println(',');
    out.print(iterator.next().getId());
  }
  out.println();
%>;
}